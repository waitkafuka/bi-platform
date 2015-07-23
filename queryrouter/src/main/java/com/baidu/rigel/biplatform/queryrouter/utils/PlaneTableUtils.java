package com.baidu.rigel.biplatform.queryrouter.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.baidu.rigel.biplatform.ac.minicube.CallbackLevel;
import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.DimensionType;
import com.baidu.rigel.biplatform.ac.model.Level;
import com.baidu.rigel.biplatform.ac.query.model.AxisMeta;
import com.baidu.rigel.biplatform.ac.query.model.AxisMeta.AxisType;
import com.baidu.rigel.biplatform.ac.query.model.ConfigQuestionModel;
import com.baidu.rigel.biplatform.ac.query.model.DimensionCondition;
import com.baidu.rigel.biplatform.ac.query.model.MeasureCondition;
import com.baidu.rigel.biplatform.ac.query.model.MetaCondition;
import com.baidu.rigel.biplatform.ac.query.model.QueryData;
import com.baidu.rigel.biplatform.ac.query.model.SQLCondition;
import com.baidu.rigel.biplatform.ac.query.model.SQLCondition.SQLConditionType;
import com.baidu.rigel.biplatform.ac.util.MetaNameUtil;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.Column;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.ColumnCondition;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.ColumnType;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.PlaneTableQuestionModel;

/**
 * PlantTableUtils
 * 
 * @author luowenlei
 *
 */
public class PlaneTableUtils {

    /**
     * 通过多维表查询对象转换成平面表查询对象
     * 
     * @param configQuestionModel
     * @return PlaneTableQuestionModel 平面表查询对象
     */
    public static PlaneTableQuestionModel convertConfigQuestionModel2PtQuestionModel(
            ConfigQuestionModel configQuestionModel, boolean isAgg) {
        PlaneTableQuestionModel planeTableQuestionModel = new PlaneTableQuestionModel();
        MiniCube miniCube = (MiniCube) configQuestionModel.getCube();
        planeTableQuestionModel.setDataSourceInfo(configQuestionModel
                .getDataSourceInfo());
        Map<String, Column> allColumns = PlaneTableUtils.getAllColumns(configQuestionModel, isAgg);
        planeTableQuestionModel.setMetaMap(PlaneTableUtils
                .getAllColumns(configQuestionModel, isAgg));
        planeTableQuestionModel.setSelection(PlaneTableUtils.getSelection(
                configQuestionModel, planeTableQuestionModel.getMetaMap()));
        planeTableQuestionModel.setQueryConditions(PlaneTableUtils.convertQueryConditions(allColumns,
                configQuestionModel.getQueryConditions()));
        planeTableQuestionModel.setRequestParams(configQuestionModel
                .getRequestParams());
        planeTableQuestionModel.setPageInfo(configQuestionModel.getPageInfo());
        planeTableQuestionModel.setSortRecord(configQuestionModel
                .getSortRecord());
        planeTableQuestionModel.setQueryConditionLimit(configQuestionModel
                .getQueryConditionLimit());
        planeTableQuestionModel.setSource(miniCube.getSource());
        return planeTableQuestionModel;
    }

    /**
     * 设置选择查询的列
     * 
     * @param configQuestionModel
     *            configQuestionModel
     * @param metaMap
     *            metaMap
     * @return List<String> SelectionList
     */
    public static List<String> getSelection(
            ConfigQuestionModel configQuestionModel, Map<String, Column> metaMap) {
        List<String> result = new ArrayList<String>();
        if (CollectionUtils.isEmpty(metaMap)) {
            return result;
        }
        // 获取指标元数据
        AxisMeta axisMetaMeasures = (AxisMeta) configQuestionModel
                .getAxisMetas().get(AxisType.COLUMN);
        axisMetaMeasures.getQueryMeasures().forEach((measureName) -> {
            result.add("[Measure].[" + measureName + "]");
        });

        // 获取指标元数据
        axisMetaMeasures.getCrossjoinDims().forEach((dimName) -> {
            result.add("[Dimension].[" + dimName + "]");
        });

        // 获取维度元数据
        AxisMeta axisMetaDims = (AxisMeta) configQuestionModel.getAxisMetas()
                .get(AxisType.ROW);
        axisMetaDims.getCrossjoinDims().forEach((dimName) -> {
            result.add("[Dimension].[" + dimName + "]");
        });
        
        // 获取维度元数据
        axisMetaDims.getQueryMeasures().forEach((measureName) -> {
            result.add("[Measure].[" + measureName + "]");
        });
        return result;
    }

    /**
     * 获取所有列的元数据
     * 
     * @param configQuestionModel
     *            configQuestionModel
     * @return Map<String, Column> 所有列的元数据
     */
    public static Map<String, Column> getAllColumns(
            ConfigQuestionModel configQuestionModel, boolean isAgg) {
        MiniCube miniCube = (MiniCube) configQuestionModel.getCube();
        HashMap<String, Column> allColumns = new HashMap<String, Column>();
        // 获取指标元数据
        miniCube.getMeasures().forEach((k, v) -> {
            String measureKey = "[Measure].[" + k + "]";
            allColumns.put(measureKey, new Column());
            Column oneMeasure = allColumns.get(measureKey);
            oneMeasure.setName(v.getDefine());
            oneMeasure.setColumnType(ColumnType.COMMON);
            oneMeasure.setTableName(miniCube.getSource());
            oneMeasure.setFacttableName(miniCube.getSource());
            oneMeasure.setFacttableColumnName(v.getDefine());
            oneMeasure.setCaption(v.getCaption());
            if (isAgg) {
                oneMeasure.setOperator(v.getAggregator().name());
            }
        });

        // 获取维度元数据
        miniCube.getDimensions().forEach(
                (k, v) -> {
                    Level oneDimensionSource = (Level) v.getLevels().values()
                            .toArray()[0];
                    String dimKey = "[Dimension].[" + k + "]";
                    allColumns.put(dimKey, new Column());
                    Column column = allColumns.get(dimKey);
                    if (PlaneTableUtils.isTimeDimension(v)) {
                        // 如果为时间维度，转换成事实表的时间字段
                        column.setColumnType(ColumnType.TIME);
                        column.setTableName(oneDimensionSource.getDimTable());
                    } else if (PlaneTableUtils.isCallbackDimension(v)) {
                        // 如果为callback维度，转换成事实表的时间字段
                        column.setColumnType(ColumnType.CALLBACK);
                    } else {
                        column.setColumnType(ColumnType.JOIN);
                        column.setTableName(oneDimensionSource.getDimTable());
                    }
                    column.setName(oneDimensionSource.getName());
                    column.setCaption(oneDimensionSource.getCaption());
                    column.setFacttableName(miniCube.getSource());
                    column.setFacttableColumnName(oneDimensionSource.getFactTableColumn());
                    column.setJoinTableFieldName(oneDimensionSource.getPrimaryKey());
                });
        return allColumns;
    }

    /**
     * 转换QueryCondition
     * 
     * @param allColums
     *            allColums
     * @param metaConditionMap
     *            metaConditionMap
     * 
     * @return Map<String, MetaCondition> 转换后带有columncondition的metacondition
     */
    public static Map<String, MetaCondition> convertQueryConditions(
            Map<String, Column> allColums,
            Map<String, MetaCondition> metaConditionMap) {
        Map<String, MetaCondition> result = new HashMap<String, MetaCondition>();
        metaConditionMap.forEach((k, v) -> {
            MetaCondition metaCondition = v;
            if (metaCondition instanceof MeasureCondition) {
                // 判断是指标查询
                String key = "[Measure].[" + k + "]";
                MeasureCondition measureCondition = (MeasureCondition) metaCondition;
                if (measureCondition.getMeasureConditions() != null) {
                    ColumnCondition columnCondition = new ColumnCondition(key);
                    columnCondition.setColumnConditions(measureCondition
                            .getMeasureConditions());
                    result.put(key, columnCondition);
                }
            } else if (metaCondition instanceof DimensionCondition) {
                // 判断是维度查询
                String key = "[Dimension].[" + k + "]";
                DimensionCondition dimensionCondition = (DimensionCondition) metaCondition;
                if (
                // 如果节点为空，不需要组织条件
                !CollectionUtils.isEmpty(dimensionCondition.getQueryDataNodes())
                        // 如果节点为所有条件，不需要组织条件
                        && !MetaNameUtil
                                .isAllMemberUniqueName(dimensionCondition
                                        .getQueryDataNodes().get(0)
                                        .getUniqueName())) {

                    ColumnCondition columnCondition = new ColumnCondition(key);
                    SQLCondition sqlCondition = new SQLCondition();
                    // 目前维度只有in的情况。
                    sqlCondition.setCondition(SQLConditionType.IN);
                    sqlCondition.setConditionValues(new ArrayList<String>());
                    columnCondition.setColumnConditions(sqlCondition);

                    for (QueryData queryData : dimensionCondition
                            .getQueryDataNodes()) {
                        String[] str = MetaNameUtil
                                .parseUnique2NameArray(queryData
                                        .getUniqueName());
                        String value = str[str.length - 1];
                        sqlCondition.getConditionValues().add(value);
                    }
                    result.put(key, columnCondition);
                }
            }
        });
        return result;
    }

    /**
     * isTimeDimension
     * 
     * @param dimension
     *            dimension
     * @return isTimeDimension
     */
    public static boolean isTimeDimension(Dimension dimension) {
        if (dimension.getType() == DimensionType.TIME_DIMENSION) {
            // 判断是否是timedimension维度
            return true;
        }
        return false;
    }

    /**
     * isCallbackDimension
     * 
     * @param dimension
     *            dimension
     * @return isCallbackDimension
     */
    public static boolean isCallbackDimension(Dimension dimension) {
        if (!dimension.getLevels().isEmpty()) {
            if (dimension.getLevels().entrySet().iterator().next().getValue() instanceof CallbackLevel) {
                // 判断是否是callback维度
                return true;
            }
        }
        return false;
    }

    /**
     * PlantTableUtils
     */
    private PlaneTableUtils() {

    }
}
