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
package com.baidu.rigel.biplatform.ma.rt.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.OlapElement;
import com.baidu.rigel.biplatform.ac.query.model.AxisMeta;
import com.baidu.rigel.biplatform.ac.query.model.AxisMeta.AxisType;
import com.baidu.rigel.biplatform.ac.query.model.ConfigQuestionModel;
import com.baidu.rigel.biplatform.ac.query.model.DimensionCondition;
import com.baidu.rigel.biplatform.ac.query.model.MetaCondition;
import com.baidu.rigel.biplatform.ac.query.model.QueryData;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.ma.model.service.PositionType;
import com.baidu.rigel.biplatform.ma.report.model.Item;
import com.baidu.rigel.biplatform.ma.report.utils.ReportDesignModelUtils;
import com.baidu.rigel.biplatform.ma.rt.query.service.QueryAction;
import com.baidu.rigel.biplatform.ma.rt.query.service.QueryStrategy;
import com.google.common.collect.Lists;

/**
 * 
 * QueryUtils
 * @author wangyuxue
 * @version 1.0.0.1
 */
public final class QueryUtils {
    
    /**
     * QueryUtils
     */
    private QueryUtils() {
    }

    /**
     * convert query action to question model
     * @param action QueryAction
     * @return Question Model
     */
    public static QuestionModel convert2QuestionModel(QueryAction action) {
        if (action == null) {
            throw new IllegalArgumentException("查询请求为空");
        }
        ConfigQuestionModel questionModel = new ConfigQuestionModel();
        Cube cube = action.getCube();
        if (cube == null) {
            throw new IllegalArgumentException("未找到与查询请求相关的cube信息");
        }
        // 设置轴信息
        questionModel.setAxisMetas(buildAxisMeta(action));
        // 构建查询信息
        questionModel.setQueryConditions(buildQueryConditions(action));
        questionModel.setCubeId(cube.getId());
        ((MiniCube) cube).setProductLine(action.getDataSource().getProductLine());
        // TODO 动态更新cube 针对查询过程中动态添加的属性 需要仔细考虑此处逻辑
//        updateLogicCubeWithSlices(cube, queryAction.getSlices().keySet(),
//                reportModel.getSchema().getCubes().get(area.getCubeId()));
        questionModel.setCube(cube);
        questionModel.setDataSourceInfo(action.getDataSource());
        return questionModel;
    }

    /**
     * 根据查询请求构建查询条件
     * @param action 查询请求
     * @return Map<String, MetaCondition> 查询条件
     */
    private static Map<String, MetaCondition> buildQueryConditions(QueryAction action) {
        Map<String, MetaCondition> rs = new HashMap<String, MetaCondition>();
        Map<Item, Object> items = new HashMap<Item, Object>();
        boolean chartQuery = action.getQueryStrategy() == QueryStrategy.CHART_QUERY;
        items.putAll(action.getColumns());
        items.putAll(action.getRows());
        items.putAll(action.getSlices());
        Cube cube = action.getCube();
//        items.keySet().stream().filter(item -> {
//           return  OlapElementQueryUtils.queryElementById(cube, item.getOlapElementId()) != null;
//        }).
//        for (Map.Entry<Item, Object> entry : items.entrySet()) {
//            Item item = entry.getKey();
//            OlapElement olapElement = OlapElementQueryUtils.queryElementById(cube, item.getOlapElementId());
//            if (olapElement == null) {
//                continue;
//            }
//            
//            if (olapElement instanceof Dimension) {
//                DimensionCondition condition = new DimensionCondition(olapElement.getName());
//                Object valueObj = entry.getValue();
//                if (valueObj != null) {
//                    List<String> values = Lists.newArrayList();
//                    if (valueObj instanceof String[]) {
//                        values = Lists.newArrayList();
//                        CollectionUtils.addAll(values, (String[]) valueObj);
//                    } else {
//                        values.add(valueObj.toString());
//                    }
//                   
//                    List<QueryData> datas = Lists.newArrayList();
//                    // TODO QeuryData value如何处理
//                    for (String value : values) {
//                        if (!chartQuery && value.toLowerCase().contains("all")) {
//                            datas.clear();
//                            break;
//                        }
//                        QueryData data = new QueryData(value);
//                        Object drillValue = queryAction.getDrillDimValues().get(item);
//                        if (drillValue != null && valueObj.equals(drillValue)) {
//                            data.setExpand(true);
//                        } else if (item.getPositionType() == PositionType.X && chartQuery) {
//                            data.setExpand(true);
//                            data.setShow(false);
//                        }
//                        datas.add(data);
//                    }
//                    condition.setQueryDataNodes(datas);
//                } else {
//                    List<QueryData> datas = new ArrayList<QueryData>();
//                    Dimension dim = (Dimension) olapElement;
//                    if (item.getPositionType() == PositionType.X && chartQuery) {
//                        QueryData data = new QueryData(dim.getAllMember().getUniqueName());
//                        data.setExpand(true);
//                        data.setShow(false);
//                        datas.add(data);
//                    }
//                    condition.setQueryDataNodes(datas);
//                }
//                rs.put(condition.getMetaName(), condition);
//            }
//        }
        return rs;
    }

    /**
     * 根据查询请求构建问题模型查询原数据信息
     * @param action 查询动作
     * @return Map<AxisType, AxisMeta> 查询问题模型元数据
     */
    private static Map<AxisType, AxisMeta> buildAxisMeta(QueryAction action) {
        Map<Item, Object> columns = action.getColumns();
        Map<AxisType, AxisMeta> rs = new HashMap<AxisType, AxisMeta>();
        AxisMeta columnMeta = buildAxisMeta(action.getCube(), columns, AxisType.COLUMN);
        rs.put(columnMeta.getAxisType(), columnMeta);
        
        Map<Item, Object> rows = action.getRows();
        AxisMeta rowMeta = buildAxisMeta(action.getCube(), rows, AxisType.ROW);
        rs.put(rowMeta.getAxisType(), rowMeta);
        
        AxisMeta filterMeta = buildAxisMeta(action.getCube(),  action.getSlices(), AxisType.FILTER);
        rs.put(filterMeta.getAxisType(), filterMeta);
        return rs;
    }

    /**
     * 根据查询条目、立方体信息构建查询轴元定义信息
     * @param cube 立方体定义
     * @param items  条目信息
     * @param type 轴类型
     * @return AxisMeta 轴元数据信息
     */
    private static AxisMeta buildAxisMeta(Cube cube, Map<Item, Object> items, AxisType type) {
        AxisMeta meta = new AxisMeta(type);
        for (Map.Entry<Item, Object> entry : items.entrySet()) {
            Item item = entry.getKey();
            OlapElement olapElement = OlapElementQueryUtils.queryElementById(cube, item.getOlapElementId());
            if (olapElement == null) {
                continue;
            }
            if (olapElement instanceof Dimension) {
                meta.getCrossjoinDims().add(olapElement.getName());
            } else {
                meta.getQueryMeasures().add(olapElement.getName());
            }
        }
        return meta;
    }
    
    
}
