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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import com.baidu.rigel.biplatform.ac.minicube.ExtendMinicubeMeasure;
import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeDimension;
import com.baidu.rigel.biplatform.ac.minicube.TimeDimension;
import com.baidu.rigel.biplatform.ac.model.Aggregator;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.Level;
import com.baidu.rigel.biplatform.ac.model.Measure;
import com.baidu.rigel.biplatform.ac.model.MeasureType;
import com.baidu.rigel.biplatform.ac.model.OlapElement;
import com.baidu.rigel.biplatform.ac.model.Schema;
import com.baidu.rigel.biplatform.ac.query.model.AxisMeta;
import com.baidu.rigel.biplatform.ac.query.model.AxisMeta.AxisType;
import com.baidu.rigel.biplatform.ac.query.model.ConfigQuestionModel;
import com.baidu.rigel.biplatform.ac.query.model.DimensionCondition;
import com.baidu.rigel.biplatform.ac.query.model.MetaCondition;
import com.baidu.rigel.biplatform.ac.query.model.QueryData;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.ac.util.DeepcopyUtils;
import com.baidu.rigel.biplatform.ac.util.PlaceHolderUtils;
import com.baidu.rigel.biplatform.ma.model.service.PositionType;
import com.baidu.rigel.biplatform.ma.report.exception.QueryModelBuildException;
import com.baidu.rigel.biplatform.ma.report.model.ExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.ExtendAreaType;
import com.baidu.rigel.biplatform.ma.report.model.Item;
import com.baidu.rigel.biplatform.ma.report.model.LiteOlapExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.report.utils.ReportDesignModelUtils;
import com.baidu.rigel.biplatform.ma.rt.query.model.QueryAction;
import com.baidu.rigel.biplatform.ma.rt.query.model.QueryStrategy;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * QueryUtils
 * @author david.wang
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
        Map<String, MetaCondition> rs = Maps.newConcurrentMap();
        Map<Item, Object> items = collectQueryItems(action);
        final Cube cube = action.getCube();
        items.keySet().parallelStream().filter(item -> {
            OlapElement olapElement = OlapElementQueryUtils.queryElementById(cube,  item.getOlapElementId());
            return olapElement != null && olapElement instanceof Dimension;
        }).forEach(item -> {
            Dimension dim = (Dimension) OlapElementQueryUtils.queryElementById(cube,  item.getOlapElementId());
            DimensionCondition condition = buildDimCondition(action, items.get(item), item, dim);
            rs.put(condition.getMetaName(), condition);
        });
        return rs;
    }

    /**
     * 构建维度查询条件
     * @param action 查询请求
     * @param filterVal 过滤条件
     * @param item 当前查询条目
     * @param dim 当前查询维度
     * @return DimensionCondition 维度查询条件
     */
    private static DimensionCondition buildDimCondition(QueryAction action, 
        Object filterVal, Item item, Dimension dim) {
        final boolean chartQuery = action.getQueryStrategy() == QueryStrategy.CHART_QUERY;
        DimensionCondition condition = new DimensionCondition(dim.getName());
        List<QueryData> datas = Lists.newArrayList();
        if (filterVal != null) {
            List<String> values = collectFilterValues(filterVal);
            boolean isDrilledItem = filterVal.equals(action.getDrillDimValues().get(item));
            boolean changeStatus = (item.getPositionType() == PositionType.X) && chartQuery;
            datas = buildQueryDatas(action, chartQuery, changeStatus, isDrilledItem, values);
        } else {
            datas = genDefaultCondition(chartQuery, item, dim);
        }
        condition.setQueryDataNodes(datas);
        return condition;
    }

    /**
     * @param action  QueryAction
     * @param chartQuery 是否是图查询
     * @param changeStatus 是否是图查询并且查询条件在x轴
     * @param isDrilledItem 是否是下钻的条目
     * @param values 过滤条件集合
     * @return List<QueryData> 查询条件
     */
    private static List<QueryData> buildQueryDatas(QueryAction action, boolean chartQuery, boolean changeStatus, 
                boolean isDrilledItem, List<String> values) {
        List<QueryData> datas = Lists.newArrayList();
        for (String value : values) {
            if (!chartQuery && value.toLowerCase().contains("all")) {
                datas.clear();
                break;
            }
            QueryData data = new QueryData(value);
            if (isDrilledItem) {
                data.setExpand(true);
            } else if (changeStatus) {
                data.setExpand(true);
                data.setShow(false);
            }
            datas.add(data);
        }
        return datas;
    }

    /**
     * @param valueObject
     * @return List<String>
     */
    private static List<String> collectFilterValues(Object valueObject) {
        List<String> values = Lists.newArrayList();
        if (valueObject instanceof String[]) {
            CollectionUtils.addAll(values, (String[]) valueObject); 
        } else {
            values.add(valueObject.toString());
        }
        return values;
    }

    /**
     * 构建查询请求数据以及节点状态
     * @param chartQuery 是否是图表查询
     * @param item 查询条目
     * @param dim 查询维度
     * @return List<QueryData> 查询请求数据状态以及节点状态
     */
    private static List<QueryData> genDefaultCondition(boolean chartQuery, Item item, Dimension dim) {
        List<QueryData> datas = new ArrayList<QueryData>();
        if (item.getPositionType() == PositionType.X && chartQuery) {
            QueryData data = new QueryData(dim.getAllMember().getUniqueName());
            data.setExpand(true);
            data.setShow(false);
            datas.add(data);
        }
        return datas;
    }

    /**
     * 
     * 收集查询条目
     * @param action 查询动作
     * @return Map<Item, Object> 查询条目集合
     * 
     */
    private static Map<Item, Object> collectQueryItems(QueryAction action) {
        Map<Item, Object> items = new HashMap<Item, Object>();
        Map<Item, Object> cols = action.getColumns();
        Map<Item, Object> rows = action.getRows();
        Map<Item, Object> slices = action.getSlices();
        action.getColumns().keySet().forEach(item -> {
            items.put(item, cols.get(item));
        });
        action.getRows().keySet().forEach(item -> {
            items.put(item, rows.get(item));
        });
        action.getSlices().keySet().forEach(item -> {
            items.put(item, slices.get(item));
        });
//        items.putAll(action.getColumns());
//        items.putAll(action.getRows());
//        items.putAll(action.getSlices());
        return Collections.synchronizedMap(items);
    }

    /**
     * 
     * 根据查询请求构建问题模型查询原数据信息
     * @param action 查询动作
     * @return Map<AxisType, AxisMeta> 查询问题模型元数据
     * 
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
    
    /**
     * 
     * 依据区域的类型，生成查询策略
     * 注意：此方法不能覆盖所有查询策略，比如：下钻，上卷等，此类查询策略需要针对具体业务具体处理
     * @param type ExtendAreaType 查询区域类型
     * @return QueryStrategy 查询策略
     */
    public static QueryStrategy genQueryStrategyWithAreaType(ExtendAreaType type) {
        switch(type) {
            case TABLE:
                return QueryStrategy.TABLE_QUERY;
            case CHART:
                return QueryStrategy.CHART_QUERY;
            case LITEOLAP_TABLE:
                return QueryStrategy.LITE_OLAP_TABLE_QUERY;
            case LITEOLAP_CHART:
                return QueryStrategy.LITE_OLAP_CHART_QUERY;
            case TIME_COMP:
            case QUERY_COMP:
                return QueryStrategy.CONTEXT_UPDATE;
            default:
                return QueryStrategy.UNKNOW;
        }
    }
    
    /**
     * 获取扩展区域包含的立方体定义
     * 
     * @param reportModel
     *            报表模型
     * @param area
     *            扩展区域
     * @return 立方体定义
     * @throws QueryModelBuildException
     */
    public static Cube getCubeWithExtendArea(ReportDesignModel reportModel, ExtendArea area) 
            throws QueryModelBuildException {
        Cube oriCube = getCubeFromReportModel(reportModel, area);
        Map<String, List<Dimension>> filterDims = collectFilterDim(reportModel);
        MiniCube cube = new MiniCube(area.getCubeId());
        String areaId = area.getId();
        LogicModel logicModel = area.getLogicModel();
        if (area.getType() == ExtendAreaType.SELECTION_AREA
                || area.getType() == ExtendAreaType.LITEOLAP_CHART 
                || area.getType() == ExtendAreaType.LITEOLAP_TABLE) {
            LiteOlapExtendArea liteOlapArea = (LiteOlapExtendArea) reportModel.getExtendById(area.getReferenceAreaId());
            logicModel = liteOlapArea.getLogicModel();
            areaId = area.getReferenceAreaId();
        }
        if (logicModel == null) {
            throw new QueryModelBuildException("logic model is empty");
        }
        Item[] items = logicModel.getItems();
        Map<String, Dimension> dimensions = new HashMap<String, Dimension>();
        Map<String, Measure> measures = new HashMap<String, Measure>();
        
        for (Item item : items) {
            OlapElement olapElement = oriCube.getDimensions().get(item.getOlapElementId());
            if (olapElement == null) { // 维度不存在或者可能是指标信息
                olapElement = oriCube.getMeasures().get(item.getOlapElementId());
                if (olapElement != null) {
                    Measure measure = (Measure) olapElement;
                    measures.put(measure.getName(), measure);
                } 
            } else {
                MiniCubeDimension dim = (MiniCubeDimension) DeepcopyUtils.deepCopy(olapElement);
                dim.setLevels(Maps.newLinkedHashMap());;
                ((Dimension) olapElement).getLevels().values().forEach(level ->{
                    dim.getLevels().put(level.getName(), level);
                });
                dimensions.put(dim.getName(), dim);
            }
        }
        if (area.getType() == ExtendAreaType.LITEOLAP) {
            /**
             * TODO 把liteOlap中候选的维度和指标加入到items里面
             */
            Map<String, Item> candDims = ((LiteOlapExtendArea) area).getCandDims();
            for (String elementId : candDims.keySet()) {
                OlapElement element = ReportDesignModelUtils.getDimOrIndDefineWithId(reportModel.getSchema(),
                        area.getCubeId(), elementId);
                MiniCubeDimension dim = (MiniCubeDimension) DeepcopyUtils.deepCopy(element);
                dim.setLevels(Maps.newLinkedHashMap());
                ((Dimension) element).getLevels().values().forEach(level ->{
                    dim.getLevels().put(level.getName(), level);
                });
                dimensions.put(element.getName(), (Dimension) element);
            }
            Map<String, Item> candInds = ((LiteOlapExtendArea) area).getCandInds();
            for (String elementId : candInds.keySet()) {
                OlapElement element = ReportDesignModelUtils.getDimOrIndDefineWithId(reportModel.getSchema(),
                        area.getCubeId(), elementId);
                measures.put(element.getName(), (Measure) element);
            }
        }
        if (filterDims != null) { //&& filterDims.get(area.getCubeId()) != null) {
            List<Dimension> dims = filterDims.get(area.getCubeId());
            if (dims != null) {
                for(Dimension dim : dims) {
                    if (dim != null) {
                        dimensions.put(dim.getName(), dim);
                    }
                }
            }
            
            // TODO 处理不同cube共用同一查询条件情况
            filterDims.forEach((key, dimArray) -> {
                if (!key.equals(area.getCubeId())) {
                    dimArray.stream().filter(dim -> {
                        return dim instanceof TimeDimension;
                    }).forEach(dim -> {
                        for (Dimension tmp : oriCube.getDimensions().values()) {
                            if (dim.getName().equals(tmp.getName())) {
                                MiniCubeDimension tmpDim = (MiniCubeDimension) DeepcopyUtils.deepCopy(dim);
                                tmpDim.setLevels((LinkedHashMap<String, Level>) tmp.getLevels());
                                tmpDim.setFacttableColumn(tmp.getFacttableColumn());
                                tmpDim.setFacttableCaption(tmp.getFacttableCaption());
                                dimensions.put(tmpDim.getName(), tmpDim);
                            }
                        }
                    });
                }
            });
        }
        cube.setDimensions(dimensions);
        modifyMeasures(measures, oriCube);
        cube.setMeasures(measures);
        cube.setSource(((MiniCube) oriCube).getSource());
        cube.setPrimaryKey(((MiniCube) oriCube).getPrimaryKey());
        cube.setId(oriCube.getId() + "_" + areaId);
        return cube;
    }
    
    /**
     * 
     * @param reportModel
     * @param area
     * @return
     * @throws QueryModelBuildException
     */
    private static Cube getCubeFromReportModel(ReportDesignModel reportModel, ExtendArea area)
            throws QueryModelBuildException {
        String cubeId = area.getCubeId();
        if (StringUtils.isEmpty(cubeId)) {
            throw new QueryModelBuildException("cube id is empty");
        }
        Schema schema = reportModel.getSchema();
        if (schema == null) {
            throw new QueryModelBuildException("schema is not define");
        }
        Map<String, ? extends Cube> cubes = schema.getCubes();
        if (cubes == null) {
            throw new QueryModelBuildException("can not get cube define from schema : " + schema.getId());
        }
        Cube oriCube = cubes.get(area.getCubeId());
        if (oriCube == null) {
            throw new QueryModelBuildException("can not get cube define from schema : " + area.getCubeId());
        }
        return oriCube;
    }
    
    /**
     * 修正measure，将measure引用的measure放到cube中
     * @param measures
     * @param oriCube
     */
    private static void modifyMeasures(Map<String, Measure> measures, Cube oriCube) {
        Set<String> refMeasuers = Sets.newHashSet();
        measures.values().stream().filter(m -> {
            return m.getType() == MeasureType.CAL || m.getType() == MeasureType.RR || m.getType() == MeasureType.SR;
        }).forEach(m -> {
            ExtendMinicubeMeasure tmp = (ExtendMinicubeMeasure) m;
            if (m.getType() == MeasureType.CAL) {
                refMeasuers.addAll(PlaceHolderUtils.getPlaceHolderKeys(tmp.getFormula()));
            } else {
                final String refName = m.getName().substring(0, m.getName().length() - 3);
                refMeasuers.add(refName);
                if (m.getType() == MeasureType.RR) {
                    tmp.setFormula("rRate(${" + refName + "})");
                } else if (m.getType() == MeasureType.SR) {
                    tmp.setFormula("sRate(${" + refName + "})");
                }
            }
            tmp.setAggregator(Aggregator.CALCULATED);
        });
        refMeasuers.stream().filter(str -> {
            return !measures.containsKey(str);
        }).map(str -> {
            Set<Map.Entry<String, Measure>> entry = oriCube.getMeasures().entrySet();
            for (Map.Entry<String, Measure> tmp : entry) {
                if (str.equals(tmp.getValue().getName())) {
                    return tmp.getValue();
                }
            }
            return null;
        }).forEach(m -> {
            if (m != null) {
                measures.put(m.getName(), m);
            }
        });
    }
    
    /**
     * 
     * @param model
     * @return Map<String, List<Dimension>>
     */
    private static Map<String, List<Dimension>> collectFilterDim(ReportDesignModel model) {
        Map<String, List<Dimension>> rs = Maps.newHashMap();
        for (ExtendArea area : model.getExtendAreaList()) {
            if (isFilterArea(area.getType())) {
                Cube cube = model.getSchema().getCubes().get(area.getCubeId());
                if (rs.get(area.getCubeId()) == null) {
                    List<Dimension> dims = Lists.newArrayList();
                    area.listAllItems().values().forEach(key -> {
                        MiniCubeDimension dim = (MiniCubeDimension) 
                                DeepcopyUtils.deepCopy(cube.getDimensions().get(key.getId()));
                        dim.setLevels(Maps.newLinkedHashMap());;
                        cube.getDimensions().get(key.getId()).getLevels().values().forEach(level ->{
                            dim.getLevels().put(level.getName(), level);
                        });
                        dims.add(dim);
                    });
                    rs.put(area.getCubeId(), dims);
                } else {
                    area.listAllItems().values().forEach(key -> {
                        MiniCubeDimension dim = (MiniCubeDimension) 
                                DeepcopyUtils.deepCopy(cube.getDimensions().get(key.getId()));
                        dim.setLevels(Maps.newLinkedHashMap());;
                        cube.getDimensions().get(key.getId()).getLevels().values().forEach(level ->{
                            dim.getLevels().put(level.getName(), level);
                        });
                        rs.get(area.getCubeId()).add(dim);
                    });
                }
            } 
        }
        return rs;
    }
    
    /**
     * 
     * @param type
     * @return boolean
     * 
     */
    public static boolean isFilterArea(ExtendAreaType type) {
        return type == ExtendAreaType.TIME_COMP 
                || type == ExtendAreaType.SELECT 
                || type == ExtendAreaType.MULTISELECT;
    }
}
