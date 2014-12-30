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
package com.baidu.rigel.biplatform.tesseract.qsservice.query.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.ac.minicube.ExtendMinicubeMeasure;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMeasure;
import com.baidu.rigel.biplatform.ac.model.Aggregator;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.query.data.DataModel;
import com.baidu.rigel.biplatform.ac.query.data.DataModel.FillDataType;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.data.HeadField;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.ac.util.DataModelUtils;
import com.baidu.rigel.biplatform.ac.util.DeepcopyUtils;
import com.baidu.rigel.biplatform.ac.util.MetaNameUtil;
import com.baidu.rigel.biplatform.ac.util.PlaceHolderUtils;
import com.baidu.rigel.biplatform.parser.CompileExpression;
import com.baidu.rigel.biplatform.parser.context.CompileContext;
import com.baidu.rigel.biplatform.parser.context.Condition;
import com.baidu.rigel.biplatform.parser.context.Condition.ConditionType;
import com.baidu.rigel.biplatform.parser.context.EmptyCondition;
import com.baidu.rigel.biplatform.parser.result.ComputeResult;
import com.baidu.rigel.biplatform.parser.result.ListComputeResult;
import com.baidu.rigel.biplatform.parser.util.ConditionUtil;
import com.baidu.rigel.biplatform.tesseract.dataquery.udf.condition.ParseCoditionUtils;
import com.baidu.rigel.biplatform.tesseract.exception.IllegalSplitResultException;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.QueryContextBuilder;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.QueryContextSplitService;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryContext;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryContextSplitResult;
import com.baidu.rigel.biplatform.tesseract.util.DataModelBuilder;



/**
 * 按照查询上下文自动拆分实现
 * 
 * @author xiaoming.chen
 *
 */
@Service
public class QueryContextSplitServiceImpl implements QueryContextSplitService {
    
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String NONE = "NONE";
    
    @Resource
    private QueryContextBuilder queryContextBuilder;
    

    @Override
    public QueryContextSplitResult split(QuestionModel question, DataSourceInfo dsInfo, Cube cube, QueryContext queryContext,
            QueryContextSplitStrategy preSplitStrategy) {
        QueryContextSplitStrategy splitStrategy = QueryContextSplitStrategy.getNextStrategy(preSplitStrategy);
        // 如果下一次拆分已经没有可拆分的了，那么说明已经不需要再进行拆分了
        if (splitStrategy != null) {
            if (splitStrategy.equals(QueryContextSplitStrategy.MeasureType)) {
                return splitByMeasureTypeStrategy(question, dsInfo, cube, queryContext);
//            } else if (splitStrategy.equals(QueryContextSplitStrategy.Column)) {
//                return splitByColumnStrategy(queryContext);
//            } else {
//                return splitByRowStrategy(queryContext);
            }
        }
        return null;
    }

    /**
     * @param cube
     * @param queryContext
     * @return
     */
    private QueryContextSplitResult splitByMeasureTypeStrategy(QuestionModel question, DataSourceInfo dsInfo, Cube cube, QueryContext queryContext) {
        QueryContextSplitResult result = new QueryContextSplitResult(QueryContextSplitStrategy.MeasureType, queryContext);
        // 按照指标类型拆分，只考虑指标类型
        if (CollectionUtils.isNotEmpty(queryContext.getQueryMeasures())) {
            
            CompileContext compileContext = null;
            for(Iterator<MiniCubeMeasure> it = queryContext.getQueryMeasures().iterator(); it.hasNext();) {
                MiniCubeMeasure measure = it.next();
                // 取出所有的计算列指标
                if (measure.getAggregator().equals(Aggregator.CALCULATED)) {
                    ExtendMinicubeMeasure extendMeasure = (ExtendMinicubeMeasure) measure;
                    compileContext = CompileExpression.compile(extendMeasure.getFormula());
                    result.getCompileContexts().put(measure.getUniqueName(), compileContext);
                    it.remove();
                }
            }
            Map<Condition, Set<String>> conditions = ConditionUtil.simpleMergeContexsCondition(result.getCompileContexts().values());
            if(MapUtils.isNotEmpty(conditions)) {
                conditions.forEach((con, vars) -> {
                    // TODO 这里先这么写，无法执行，等雨学提交代码再修改
                    QueryContext context = con.processCondition(ParseCoditionUtils.decorateQueryContext(DeepcopyUtils.deepCopy(queryContext), question, cube, dsInfo, queryContextBuilder));
                    // 是否需要清理，到时候在讨论
                    context.getQueryMeasures().clear();
                    for(String var : vars) {
                        MiniCubeMeasure measure = (MiniCubeMeasure) cube.getMeasures().get(PlaceHolderUtils.getKeyFromPlaceHolder(var));
                        if(!context.getQueryMeasures().contains(measure)) {
                            context.getQueryMeasures().add(measure);
                        }
                    }
                    if(con.getConditionType().equals(ConditionType.None) && CollectionUtils.isNotEmpty(queryContext.getQueryMeasures())) {
                        for(MiniCubeMeasure m : queryContext.getQueryMeasures()) {
                            if(!context.getQueryMeasures().contains(m)) {
                                context.getQueryMeasures().add(m);
                            }
                        }
                    }
                    result.getConditionQueryContext().put(con, context);
                    
                });
            }
        }

        return result;
    }

    /**
     * @param queryContext
     * @return
     */
//    private QueryContextSplitResult splitByColumnStrategy(QueryContext queryContext) {
//        QueryContextSplitResult result = new QueryContextSplitResult(QueryContextSplitStrategy.Column, queryContext);
//        
//        if (CollectionUtils.isNotEmpty(queryContext.getColumnMemberTrees())) {
//            for (int i = 0; i < queryContext.getColumnMemberTrees().size(); i++) {
//                // TODO 按照查询的节点数进行拆分，按照拆分的节点封装QueryContext
//            }
//        }
//        return result;
//    }
//
//    /**
//     * @param queryContext
//     * @return
//     */
//    private QueryContextSplitResult splitByRowStrategy(QueryContext queryContext) {
//        QueryContextSplitResult result = new QueryContextSplitResult(QueryContextSplitStrategy.Row, queryContext);
//
//        if (CollectionUtils.isNotEmpty(queryContext.getRowMemberTrees())) {
//            for (int i = 0; i < queryContext.getRowMemberTrees().size(); i++) {
//                // TODO 按照查询的节点数进行拆分，按照拆分的节点封装QueryContext
//            }
//        }
//        return result;
//    }

    @Override
    public DataModel mergeDataModel(QueryContextSplitResult splitResult) {
        long current = System.currentTimeMillis();
        if(splitResult == null || splitResult.getConditionQueryContext().size() != splitResult.getDataModels().size()) {
            throw new IllegalSplitResultException(splitResult, "splitResult is null or condition size not equal", "MERGE_MODEL");
        }
        
        DataModel dataModel = new DataModel();
        dataModel.setColumnHeadFields(DataModelBuilder.buildAxisHeadFields(splitResult.getOriQueryContext().getColumnMemberTrees(),
                splitResult.getOriQueryContext().getQueryMeasures()));
        dataModel.setRowHeadFields(DataModelBuilder.buildAxisHeadFields(splitResult.getOriQueryContext().getRowMemberTrees(), null));
        
        
        
        // 条件，指标UniqueName，父节点UniqueName，数据
        Map<Condition, Map<String, Map<String, List<BigDecimal>>>> dataModelDatas = new HashMap<Condition, Map<String,Map<String,List<BigDecimal>>>>(splitResult.getDataModels().size());
        splitResult.getDataModels().forEach((con, dm) -> {
            // 先把数据按照列封装了
            DataModelUtils.fillFieldData(dm, FillDataType.COLUMN);
            List<HeadField> columnFields = DataModelUtils.getLeafNodeList(dm.getColumnHeadFields());
            Map<String, Map<String, List<BigDecimal>>> dataModelData = new HashMap<String, Map<String, List<BigDecimal>>>();
            // 存放叶子节点信息
            for(HeadField field : columnFields) {
                if(!dataModelData.containsKey(field.getValue())) {
                    dataModelData.put(field.getValue(), new HashMap<>());
                }
                String parentKey = NONE;
                if (field.getParentLevelField() != null) {
                    parentKey = field.getParentLevelField().getNodeUniqueName();
                }
                dataModelData.get(field.getValue()).put(parentKey, field.getCompareDatas());
            }
            dataModelDatas.put(con, dataModelData);
            
        });
        if(!dataModelDatas.containsKey(EmptyCondition.getInstance())) {
            dataModelDatas.put(EmptyCondition.getInstance(), new HashMap<>());
        }
        // TODO 优化循环策略
        splitResult.getCompileContexts().forEach((measureName,compileContext) -> {
           Map<String, List<BigDecimal>> calCulateDatas = new HashMap<>();
           Map<String, Map<String, ComputeResult>> categoryVariableVal = new HashMap<>();
           for(Entry<Condition,Set<String>> entry : compileContext.getConditionVariables().entrySet()) {
               Map<String, Map<String, List<BigDecimal>>> dataModelData = dataModelDatas.get(entry.getKey());
               if(dataModelData == null) {
                   throw new IllegalSplitResultException(splitResult, "dataModel is null by condition" + entry.getKey(), "MERGE_MODEL");
               }
               // parentNode uniqueName, varName, data
               for(String var : entry.getValue()) {
                   String name = MetaNameUtil.generateMeasureUniqueName(PlaceHolderUtils.getKeyFromPlaceHolder(var));
                   if (!dataModelData.containsKey(name)) {
                       throw new IllegalSplitResultException(splitResult, "miss variable:" + var, "MERGE_MODEL");
                   }
                   for(String parentNodeUniqueName : dataModelData.get(name).keySet()) {
                       if(!categoryVariableVal.containsKey(parentNodeUniqueName)) {
                           categoryVariableVal.put(parentNodeUniqueName, new HashMap<>());
                       }
                       categoryVariableVal.get(parentNodeUniqueName).put(var, new ListComputeResult(dataModelData.get(name).get(parentNodeUniqueName)));
                   }
               }
               
               for(String parentName : categoryVariableVal.keySet()) {
                   // 清理一下，避免对后续造成影响
                   compileContext.getVariablesResult().clear();
                   compileContext.getVariablesResult().put(entry.getKey(), categoryVariableVal.get(parentName));
                   calCulateDatas.put(parentName, ((ListComputeResult)compileContext.getNode().getResult(compileContext)).getData());
               }
           }
           dataModelDatas.get(EmptyCondition.getInstance()).put(measureName, calCulateDatas);
        });
        mergeDataModelDatas(dataModel, dataModelDatas.get(EmptyCondition.getInstance()));
        log.info("merge datamodel cost:{}ms",System.currentTimeMillis() - current);
        return dataModel;
    }
    
    
    private void mergeDataModelDatas(DataModel dataModel, Map<String, Map<String, List<BigDecimal>>> datas){
        List<HeadField> oriColumnFields = DataModelUtils.getLeafNodeList(dataModel.getColumnHeadFields());
        oriColumnFields.forEach(field -> {
            String pName = NONE;
            if(field.getParentLevelField() != null) {
                pName = field.getParentLevelField().getNodeUniqueName();
            }
            field.setCompareDatas(datas.get(field.getValue()).get(pName));
        });
        DataModelUtils.fillColumnData(dataModel, FillDataType.COLUMN);
    }

}
