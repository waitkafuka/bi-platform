package com.baidu.rigel.biplatform.queryrouter.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.baidu.rigel.biplatform.queryrouter.handle.QueryRouterContext;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.Column;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.ColumnCondition;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.ColumnType;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.JoinOn;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.JoinTable;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.PlaneTableQuestionModel;

/**
 * PlantTableUtils
 * 
 * @author luowenlei
 *
 */
public class PlaneTableUtils {
    
    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(PlaneTableUtils.class);
    
    /**
     * CUBE_TIME_TABLE_NAME
     */
    public static final String CUBE_TIME_TABLE_NAME = "ownertable";
    
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
        planeTableQuestionModel.setDataSourceInfo(configQuestionModel.getDataSourceInfo());
        Map<String, Column> allColumns = PlaneTableUtils.getAllColumns(configQuestionModel, isAgg);
        planeTableQuestionModel.setMetaMap(PlaneTableUtils
                .getAllColumns(configQuestionModel, isAgg));
        planeTableQuestionModel.setSelection(PlaneTableUtils.getSelection(configQuestionModel,
                planeTableQuestionModel.getMetaMap()));
        planeTableQuestionModel.setQueryConditions(PlaneTableUtils.convertQueryConditions(miniCube,
                allColumns, configQuestionModel.getQueryConditions()));
        planeTableQuestionModel.setRequestParams(configQuestionModel.getRequestParams());
        planeTableQuestionModel.setPageInfo(configQuestionModel.getPageInfo());
        planeTableQuestionModel.setSortRecord(configQuestionModel.getSortRecord());
        planeTableQuestionModel
                .setQueryConditionLimit(configQuestionModel.getQueryConditionLimit());
        planeTableQuestionModel.setSource(miniCube.getSource());
        return planeTableQuestionModel;
    }
    
    /**
     * 设置有序的选择查询的列
     * 
     * @param configQuestionModel
     *            configQuestionModel
     * @param metaMap
     *            metaMap
     * @return List<String> SelectionList
     */
    public static List<String> getSelection(ConfigQuestionModel configQuestionModel,
            Map<String, Column> metaMap) {
        List<String> result = new ArrayList<String>();
        if (CollectionUtils.isEmpty(metaMap)) {
            return result;
        }
        if (MapUtils.isEmpty(configQuestionModel.getAxisMetas())) {
            return result;
        }
        // 如果axisMetaMeasures.getQueryItemsOrder()没有值，那么返回无序的List
        if (configQuestionModel.getAxisMetas().get(AxisType.COLUMN) != null) {
            AxisMeta axisMetaMeasures = (AxisMeta) configQuestionModel.getAxisMetas().get(
                    AxisType.COLUMN);
            if (CollectionUtils.isEmpty(axisMetaMeasures.getQueryItemsOrder())) {
                return getSelectionNotOrdered(configQuestionModel, metaMap);
            } else {
                return axisMetaMeasures.getQueryItemsOrder();
            }
        } else {
            return getSelectionNotOrdered(configQuestionModel, metaMap);
        }
    }
    
    /**
     * 设置无序的选择查询的列
     * 
     * @param configQuestionModel
     *            configQuestionModel
     * @param metaMap
     *            metaMap
     * @return List<String> SelectionList
     */
    public static List<String> getSelectionNotOrdered(ConfigQuestionModel configQuestionModel,
            Map<String, Column> metaMap) {
        List<String> result = new ArrayList<String>();
        if (configQuestionModel.getAxisMetas().get(AxisType.COLUMN) != null) {
            AxisMeta axisMetaMeasures = (AxisMeta) configQuestionModel.getAxisMetas().get(
                    AxisType.COLUMN);
            // 获取指标元数据
            if (axisMetaMeasures.getQueryMeasures() != null) {
                for (String measureName : axisMetaMeasures.getQueryMeasures()) {
                    result.add("[Measure].[" + measureName + "]");
                }
            }

            // 获取指标元数据
            if (axisMetaMeasures.getCrossjoinDims() != null) {
                for (String dimName : axisMetaMeasures.getCrossjoinDims()) {
                    result.add("[Dimension].[" + dimName + "]");
                }
            }
        }

        if (configQuestionModel.getAxisMetas().get(AxisType.ROW) != null) {
            // 获取维度元数据
            AxisMeta axisMetaDims = (AxisMeta) configQuestionModel.getAxisMetas().get(AxisType.ROW);
            if (axisMetaDims.getCrossjoinDims() != null) {
                for (String dimName : axisMetaDims.getCrossjoinDims()) {
                    result.add("[Dimension].[" + dimName + "]");
                }
            }

            // 获取维度元数据
            if (axisMetaDims.getQueryMeasures() != null) {
                for (String measureName : axisMetaDims.getQueryMeasures()) {
                    result.add("[Measure].[" + measureName + "]");
                }
            }
        }
        return result;
    }
    
    /**
     * 获取所有列的元数据
     * 
     * @param configQuestionModel
     *            configQuestionModel
     * @return Map<String, Column> 所有列的元数据
     */
    public static Map<String, Column> getAllColumns(ConfigQuestionModel configQuestionModel,
            boolean isAgg) {
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
        
        // 获取基本维度元数据
        for (String k : miniCube.getDimensions().keySet()) {
            Dimension v = miniCube.getDimensions().get(k);
            if (v.getType() == DimensionType.GROUP_DIMENSION) {
                continue;
            } else {
                String dimKey = "[Dimension].[" + k + "]";
                Column column = new Column();
                setSingleCommonDimensionData(column, v, miniCube.getSource());
                allColumns.put(dimKey, column);
            }
        }
        return allColumns;
    }
    
    /**
     * 设置一个普通维度的基本信息
     *
     * @param column
     *            column
     * @param v
     *            dimension
     * @param source
     *            source
     */
    private static void setSingleCommonDimensionData(Column column, Dimension v, String source) {
        Level oneDimensionSource = null;
        Level[] levels = v.getLevels().values().toArray(new Level[0]);
        oneDimensionSource = levels[levels.length - 1];
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
            column.setJoinTable(convertLevels2JoinList(levels));
        }
        column.setName(oneDimensionSource.getName());
        column.setCaption(v.getCaption());
        column.setFacttableName(source);
        column.setFacttableColumnName(oneDimensionSource.getFactTableColumn());
        column.setFacttableColumnName(v.getFacttableColumn());
    }
    
    /**
     * convertLevels2JoinList
     *
     * @param levels
     * @return List<Join>
     */
    private static JoinTable convertLevels2JoinList(Level[] levels) {
        JoinTable join = new JoinTable();
        if (levels == null) {
            return null;
        }
        join.setTableName(levels[0].getDimTable());
        JoinOn joinOn = new JoinOn();
        Level level = levels[0];
        joinOn.setFacttableColumnName(level.getFactTableColumn());
        joinOn.setJoinTableFieldName(level.getPrimaryKey());
        join.getJoinOnList().add(joinOn);
        return join;
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
    public static Map<String, MetaCondition> convertQueryConditions(MiniCube miniCube,
            Map<String, Column> allColums, Map<String, MetaCondition> metaConditionMap) {
        Map<String, MetaCondition> result = new HashMap<String, MetaCondition>();
        for (String k : metaConditionMap.keySet()) {
            
            MetaCondition metaCondition = metaConditionMap.get(k);
            if (metaCondition instanceof MeasureCondition) {
                // 判断是指标查询
                
                String key = "[Measure].[" + k + "]";
                MeasureCondition measureCondition = (MeasureCondition) metaCondition;
                if (measureCondition.getMeasureConditions() != null) {
                    ColumnCondition columnCondition = new ColumnCondition(key);
                    columnCondition.setColumnConditions(measureCondition.getMeasureConditions());
                    result.put(key, columnCondition);
                }
            } else if (metaCondition instanceof DimensionCondition) {
                // 判断条件为维度组查询
                
                Dimension d = miniCube.getDimensions().get(k);
                if (d.getType() != DimensionType.GROUP_DIMENSION) {
                    // 普通维度条件
                    
                    String key = "[Dimension].[" + k + "]";
                    ColumnCondition columnCondition = getDimensionColumnCondition(metaCondition,
                            key, -1);
                    if (columnCondition != null) {
                        result.put(key, columnCondition);
                    }
                } else {
                    // 维度组条件，此情况下目前只能处理维度中in查询
                    
                    Dimension dimensionGroup = d;
                    Object[] levels = dimensionGroup.getLevels().values().toArray();
                    for (int i = 0; i < levels.length; i++) {
                        Level level = (Level) levels[i];
                        String printKeys = "";
                        Column column = null;
                        if (CUBE_TIME_TABLE_NAME.equals(level.getDimTable())
                                || miniCube.getSource().equals(level.getDimTable())) {
                            logger.warn(
                                    "queryId:{} cann't handle 'column of source table,time,"
                                    + "callback in dimension group now.',"
                                    + "allcolumn keys:{}", QueryRouterContext.getQueryId(), printKeys);
                            continue;
                        }
                        // 查找对应的字段信息
                        String currentKey = "";
                        for (String key : allColums.keySet()) {
                            printKeys = printKeys + key + ",";
                            String name = key.substring(key.lastIndexOf("[") + 1, key.lastIndexOf("]"));
                            // 目前获取uniquename的方式
                            String lvlName = level.getDimTable() + "_" + level.getName();
                            if (lvlName.equals(name)) {
                                currentKey = key;
                                column = allColums.get(key);
                                break;
                            }
                        }
                        if (column == null) {
                            // 此处如果出现证明cube不正确
                            logger.warn(
                                    "queryId:{} group dimension:{} levels:{} can not find in allColumns,"
                                            + "allcolumn keys:{}", QueryRouterContext.getQueryId(),
                                                    d.getName(), level.getName(), printKeys);
                            continue;
                        }
                        // 在cube找到对应的column
                        
                        // 此处目前只处理维度
                        // 根据level中的index与condition中unique 那么的数据作为in条件
                        ColumnCondition columnCondition = getDimensionColumnCondition(
                                metaCondition, currentKey, i);
                        if (columnCondition != null) {
                            result.put(currentKey, columnCondition);
                        }
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * getDimensionColumnCondition
     *
     * @param metacondition
     * @param allColumnMapkey
     * @param dimensionGroupIndex
     *            level中对应的维度index与metacondition众uniquename中的 []index位置一样
     *            如果index为-1或大于(uniquename[].length - 1)，则去最后一个uniquename []中的值
     * @return columnCondition
     */
    private static ColumnCondition getDimensionColumnCondition(MetaCondition metacondition,
            String allColumnMapkey, int dimensionGroupIndex) {
        DimensionCondition dimensionCondition = (DimensionCondition) metacondition;
        if (
        // 如果节点为空，不需要组织条件
        !CollectionUtils.isEmpty(dimensionCondition.getQueryDataNodes())
        // 如果节点为所有条件，不需要组织条件
                && !MetaNameUtil.isAllMemberUniqueName(dimensionCondition.getQueryDataNodes()
                        .get(0).getUniqueName())) {
            
            ColumnCondition columnCondition = new ColumnCondition(allColumnMapkey);
            SQLCondition sqlCondition = new SQLCondition();
            // 目前维度只有in的情况。
            sqlCondition.setCondition(SQLConditionType.IN);
            sqlCondition.setConditionValues(new ArrayList<String>());
            columnCondition.setColumnConditions(sqlCondition);
            
            for (QueryData queryData : dimensionCondition.getQueryDataNodes()) {
                String[] str = MetaNameUtil.parseUnique2NameArray(queryData.getUniqueName());
                String value = null;
                if (str.length <= 1) {
                    // cube 不正确
                    logger.warn("queryId:{} length of uniquename:{} is not right.", QueryRouterContext.getQueryId(),
                            queryData.getUniqueName());
                    return null;
                }
                if (dimensionGroupIndex <= -1 || dimensionGroupIndex > (str.length - 1)) {
                    value = str[str.length - 1];
                } else {
                    value = str[dimensionGroupIndex + 1];
                }
                sqlCondition.getConditionValues().add(value);
            }
            return columnCondition;
        }
        return null;
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
