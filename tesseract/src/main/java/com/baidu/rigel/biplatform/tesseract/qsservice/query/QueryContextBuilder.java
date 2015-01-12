
/**
 * Copyright (c) 2014 Baidu, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baidu.rigel.biplatform.tesseract.qsservice.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.ac.exception.MiniCubeQueryException;
import com.baidu.rigel.biplatform.ac.minicube.CallbackLevel;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMeasure;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMember;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.LevelType;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.model.AxisMeta;
import com.baidu.rigel.biplatform.ac.query.model.AxisMeta.AxisType;
import com.baidu.rigel.biplatform.ac.query.model.DimensionCondition;
import com.baidu.rigel.biplatform.ac.query.model.MeasureCondition;
import com.baidu.rigel.biplatform.ac.query.model.MetaCondition;
import com.baidu.rigel.biplatform.ac.query.model.QueryData;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.ac.util.DeepcopyUtils;
import com.baidu.rigel.biplatform.ac.util.MetaNameUtil;
import com.baidu.rigel.biplatform.tesseract.exception.MetaException;
import com.baidu.rigel.biplatform.tesseract.meta.MetaDataService;
import com.baidu.rigel.biplatform.tesseract.model.MemberNodeTree;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/** 
 *  
 * @author xiaoming.chen
 * @version  2014年12月26日 
 * @since jdk 1.8 or after
 */
@Service
public class QueryContextBuilder {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    
    @Resource
    private MetaDataService metaDataService;
    
    
    /**
     * 构建查询上下文
     * 
     * @param questionModel 问题模型
     * @param dsInfo 数据源信息
     * @param cube cube模型
     * @param queryContext 查询上下文
     * @return 根据问题模型构建的查询上下文
     * @throws MiniCubeQueryException 查询维值异常
     * @throws MetaException
     */
    public QueryContext buildQueryContext(QuestionModel questionModel, DataSourceInfo dsInfo, Cube cube,
            QueryContext queryContext) throws MiniCubeQueryException, MetaException {
        if (queryContext == null) {
            queryContext = new QueryContext();
            QuestionModel cloneQuestionModel = DeepcopyUtils.deepCopy(questionModel);
            long current = System.currentTimeMillis();
            AxisMeta axisMeta = null;
            AxisType axisType = AxisType.COLUMN;
            while (axisType != null && (axisMeta = cloneQuestionModel.getAxisMetas().get(axisType)) != null) {
                if (CollectionUtils.isNotEmpty(axisMeta.getCrossjoinDims())) {
                    int i = 0;
                    for (String dimName : axisMeta.getCrossjoinDims()) {
                        DimensionCondition dimCondition =
                                (DimensionCondition) cloneQuestionModel.getQueryConditions().remove(dimName);
                        if (dimCondition == null) {
                            dimCondition = new DimensionCondition(dimName);
                        }
                        queryContext.addMemberNodeTreeByAxisType(axisType,
                                buildQueryMemberTree(dsInfo, cube, dimCondition, i == 0, questionModel.getRequestParams()));
                        i++;
                    }
                }
                logger.info("cost:{}ms in build axisTye:{},axisMeta:{}",System.currentTimeMillis() - current,axisType,axisMeta);
                current = System.currentTimeMillis();
                if (CollectionUtils.isNotEmpty(axisMeta.getQueryMeasures())) {
                    for (String measureName : axisMeta.getQueryMeasures()) {
                        if (cube.getMeasures().containsKey(measureName)) {
                            queryContext.getQueryMeasures().add((MiniCubeMeasure) cube.getMeasures().get(measureName));
                        }
                        // 需要判断，如果cube里面不包含的话，那么这个名称可能是个计算公式，需要进行构造一个虚拟的名称扔进去
                    }
                }
                logger.info("cost:{}ms in build axisTye:{},axisMeta:{}",System.currentTimeMillis() - current,axisType,axisMeta);
                current = System.currentTimeMillis();
                if (axisType.equals(AxisType.ROW)) {
                    axisType = null;
                } else {
                    axisType = AxisType.ROW;
                }
            }
            
            if (!cloneQuestionModel.getQueryConditions().isEmpty()) {
                for (MetaCondition condition : cloneQuestionModel.getQueryConditions().values()) {
                    if (condition == null) {
                        logger.warn("meta condition is null,skip.");
                        continue;
                    }
                    if (condition instanceof DimensionCondition) {
                        DimensionCondition dimCondition = (DimensionCondition) condition;
                        Map<String, Set<String>> filterCondition = buildFilterCondition(dsInfo, cube, dimCondition, questionModel.getRequestParams());
                        if (MapUtils.isNotEmpty(filterCondition)) {
                            queryContext.getFilterMemberValues().putAll(filterCondition);
                        }
                    } else {
                        MeasureCondition measureCon = (MeasureCondition) condition;
                        // 暂时这个还不会生效
                        queryContext.getFilterExpression().put(measureCon.getMetaName(),
                                measureCon.getMeasureConditions());
                    }
                    logger.info("cost:{}ms,in build filter conditon:{}",System.currentTimeMillis() - current,condition);
                    current = System.currentTimeMillis();
                }
            }
        }
        return queryContext;
    }
    
    /**
     * 构造过滤条件
     * 
     * @param dataSourceInfo 数据源信息
     * @param cube cube信息
     * @param dimCondition 维度信息
     * @param params 查询条件
     * @return 过滤条件
     * @throws MiniCubeQueryException 查询异常
     * @throws MetaException 元数据异常信息
     */
    public Map<String, Set<String>> buildFilterCondition(DataSourceInfo dataSourceInfo, Cube cube,
            DimensionCondition dimCondition, Map<String, String> params) throws MiniCubeQueryException, MetaException {
        if (dimCondition == null) {
            throw new IllegalArgumentException("dimension condition is null");
        }
        if (dimCondition.getQueryDataNodes().isEmpty()) {
            logger.info("filter axises ignore all member filter");
            return null;
        }
        Map<String, Set<String>> filterValues = new HashMap<>();
        for (QueryData queryData : dimCondition.getQueryDataNodes()) {
            if (MetaNameUtil.isAllMemberUniqueName(queryData.getUniqueName())) {
                logger.info("filter axises ignore all member filter");
                return null;
            }
            MiniCubeMember member = metaDataService.lookUp(dataSourceInfo, cube, queryData.getUniqueName(), params);
            if (member != null) {
                String querySource = member.getLevel().getFactTableColumn();
                Set<String> nodes =
                        CollectionUtils.isEmpty(member.getQueryNodes()) ? Sets.newHashSet(member.getName()) : member
                                .getQueryNodes();
                if (filterValues.containsKey(querySource)) {
                    filterValues.get(querySource).addAll(nodes);
                } else {
                    filterValues.put(querySource, nodes);
                }
            } else {
                logger.warn("can not found member by query data:{}", queryData);
            }
        }

        return filterValues;
    }
    
    
    /**
     * 根据维值选中条件构造维值查询的树
     * 
     * @param dataSourceInfo 数据源信息
     * @param cube cube模型
     * @param dimCondition 维值查询条件
     * @param isFirstInRow 是否是行上的第一个维度
     * @return 维值树
     * @throws MiniCubeQueryException 查询维值异常
     * @throws MetaException
     */
    public MemberNodeTree buildQueryMemberTree(DataSourceInfo dataSourceInfo, Cube cube,
            DimensionCondition dimCondition, boolean isFirstInRow, Map<String, String> params)
            throws MiniCubeQueryException, MetaException {
        if (dimCondition == null) {
            throw new IllegalArgumentException("dimension condition is null");
        }
        long current = System.currentTimeMillis();
        MemberNodeTree nodeTree = new MemberNodeTree(null);
        if (dimCondition.getQueryDataNodes().isEmpty()) {
            String allMemberUniqueName =
                    cube.getDimensions().get(dimCondition.getMetaName()).getAllMember().getUniqueName();
            QueryData queryData = new QueryData(allMemberUniqueName);
            queryData.setExpand(isFirstInRow);
            queryData.setShow(true);
            dimCondition.getQueryDataNodes().add(queryData);
            logger.info("cost:{}ms,in build default member:{}",System.currentTimeMillis() - current, dimCondition);
            current = System.currentTimeMillis();
        }
        for (QueryData queryData : dimCondition.getQueryDataNodes()) {
            MiniCubeMember member = metaDataService.lookUp(dataSourceInfo, cube, queryData.getUniqueName(), params);

            MemberNodeTree memberNode = new MemberNodeTree(nodeTree);
            List<MemberNodeTree> childNodes = new ArrayList<MemberNodeTree>();
            boolean isCallBack = member.getLevel().getType().equals(LevelType.CALL_BACK);
            // 如果接到设置了下钻 或者 当前维度在行上第一个并且只有一个选中节点
            if (queryData.isExpand() || isCallBack) {
                List<MiniCubeMember> children = metaDataService.getChildren(dataSourceInfo, cube, member, params);
                if (CollectionUtils.isNotEmpty(children)) {
                    memberNode.setSummary(true);
                    children.forEach((child) -> {
                        MemberNodeTree childNode = new MemberNodeTree(nodeTree);
                        buildMemberNodeByMember(childNode, child);
                        childNodes.add(childNode);
//                        member.getQueryNodes().addAll(child.getQueryNodes());
                    });
                }
            }
            // 如果当前孩子为空或者当前节点是要展现，那么直接把本身扔到要展现列表中
            if (queryData.isShow() || CollectionUtils.isEmpty(childNodes)) {
                buildMemberNodeByMember(memberNode, member);
                memberNode.setChildren(childNodes);
                nodeTree.getChildren().add(memberNode);
//                return memberNode;
            } else {
                nodeTree.getChildren().addAll(childNodes);
            }
            logger.info("cost:{}ms,in build query data:{}",System.currentTimeMillis() - current, queryData);
            current = System.currentTimeMillis();
        }
        // 非DESC的都按ASC排序。
        nodeTree.sort(dimCondition.getMemberSortType());
        return nodeTree;
    }

    /**
     * 根据维值创建查询树的节点
     * @param node 查询节点
     * @param member 维值
     */
    private void buildMemberNodeByMember(MemberNodeTree node, MiniCubeMember member) {
        node.setCaption(member.getCaption());
        if (CollectionUtils.isNotEmpty(member.getQueryNodes())) {
            node.setLeafIds(member.getQueryNodes());
        } else {
            node.getLeafIds().add(member.getName());
        }
        node.setName(member.getName());
        node.setUniqueName(member.getUniqueName());
        node.setOrdinal(member.getName());
        // 设置查询的来源，如事实表的字段
        node.setQuerySource(member.getLevel().getFactTableColumn());

        // 后续需要对孩子节点进行下查询，本次对是否有孩子的判断只是按照是否有下一个层级
        if (member.isAll()) {
            node.setHasChildren(true);
        } else if (member.getLevel() instanceof CallbackLevel) {
            if (CollectionUtils.isNotEmpty(member.getQueryNodes())) {
                node.setHasChildren(true);
            }
        } else {
            Dimension dim = member.getLevel().getDimension();
            List<String> levelNames = Lists.newArrayList(dim.getLevels().keySet());
            for (int i = 0; i < levelNames.size(); i++) {
                if (member.getLevel().getName().equals(levelNames.get(i))) {
                    if (i < levelNames.size() - 1) {
                        node.setHasChildren(true);
                    }
                    break;
                }
            }
        }

    }

}

