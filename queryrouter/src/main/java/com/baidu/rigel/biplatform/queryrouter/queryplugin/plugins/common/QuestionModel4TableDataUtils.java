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
package com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.DimensionType;
import com.baidu.rigel.biplatform.ac.model.Level;
import com.baidu.rigel.biplatform.ac.model.Measure;
import com.baidu.rigel.biplatform.ac.query.model.AxisMeta;
import com.baidu.rigel.biplatform.ac.query.model.AxisMeta.AxisType;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlColumn;

/**
 * QuestionModel to TableData的工具类
 * 
 * @author luowenlei
 *
 */
public class QuestionModel4TableDataUtils {
    
    /**
     * 获取questionModel中需要查询的Columns
     * 
     * @param AxisMetas
     *        axisMetas map
     * 
     * @return List needcolumns hashmap
     */
    public static List<SqlColumn> getNeedColumns(HashMap<String, SqlColumn> allColums,
        Map<AxisType, AxisMeta> axisMetas, MiniCube cube) {
        Set<SqlColumn> needColumns = new HashSet<SqlColumn>();
        
        // 获取指标元数据
        AxisMeta axisMetaMeasures = (AxisMeta) axisMetas.get(AxisType.COLUMN);
        for (String measureName : axisMetaMeasures.getQueryMeasures()) {
            needColumns.add(allColums.get(measureName));
        }
        
        // 获取指标元数据
        for (String dimName : axisMetaMeasures.getCrossjoinDims()) {
            Dimension dimension = cube.getDimensions().get(dimName);
            if (dimension.getType() == DimensionType.TIME_DIMENSION) {
            // 如果为时间维度，转换成事实表的时间字段
            dimName = cube.getDimensions().get(dimName).getFacttableColumn();
            }
            needColumns.add(allColums.get(dimName));
        }
        
        // 获取维度元数据
        AxisMeta axisMetaDims = (AxisMeta) axisMetas.get(AxisType.ROW);
        for (String dimName : axisMetaDims.getCrossjoinDims()) {
            Dimension dimension = cube.getDimensions().get(dimName);
            if (dimension.getType() == DimensionType.TIME_DIMENSION) {
            // 如果为时间维度，转换成事实表的时间字段
            dimName = cube.getDimensions().get(dimName).getFacttableColumn();
            }
            needColumns.add(allColums.get(dimName));
        }
        return new ArrayList<SqlColumn>(needColumns);
    }
    
    /**
     * 获取指标及维度中所有的字段信息Formcube
     * 
     * @param cube
     *        cube
     * @return HashMap allcolumns hashmap
     */
    public static HashMap<String, SqlColumn> getAllCubeColumns(Cube cube) {
        MiniCube miniCube = (MiniCube) cube;
        HashMap<String, SqlColumn> allColumns = new HashMap<String, SqlColumn>();
        // 获取指标元数据
        for (Entry<String, Measure> entry : miniCube.getMeasures().entrySet()) {
            String measureName = entry.getKey();
            if (allColumns.get(measureName) == null) {
            allColumns.put(measureName, new SqlColumn());
            }
            SqlColumn oneMeasure = allColumns.get(measureName);
            oneMeasure.setName(entry.getKey());
            oneMeasure.setTableFieldName(entry.getValue().getDefine());
            oneMeasure.setCaption(entry.getValue().getCaption());
            oneMeasure.setTableName(miniCube.getSource());
            oneMeasure.setType(AxisType.COLUMN);
            oneMeasure.setSqlUniqueColumn(miniCube.getSource() + entry.getValue().getDefine());
            oneMeasure.setMeasure(entry.getValue());
            oneMeasure.setFactTableName(miniCube.getSource());
        }
        
        // 获取维度元数据
        for (Entry<String, Dimension> entry : miniCube.getDimensions().entrySet()) {
            Level oneDimensionSource = (Level) entry.getValue().getLevels().values().toArray()[0];
            String dimensionName = entry.getKey();
            String tableFieldName = oneDimensionSource.getName();
            String tableName = oneDimensionSource.getDimTable();
            Dimension dimension = cube.getDimensions().get(dimensionName);
            if (dimension.getType() == DimensionType.TIME_DIMENSION) {
            // 如果为时间维度，转换成事实表的时间字段
            dimensionName = oneDimensionSource.getFactTableColumn();
            tableFieldName = oneDimensionSource.getFactTableColumn();
            tableName = miniCube.getSource();
            }
            if (allColumns.get(dimensionName) == null) {
            allColumns.put(dimensionName, new SqlColumn());
            }
            SqlColumn oneDimensionTarget = allColumns.get(dimensionName);
            oneDimensionTarget.setName(dimensionName);
            oneDimensionTarget.setTableFieldName(tableFieldName);
            oneDimensionTarget.setCaption(oneDimensionSource.getCaption());
            oneDimensionTarget.setTableName(tableName);
            oneDimensionTarget.setType(AxisType.ROW);
            oneDimensionTarget.setDimension(entry.getValue());
            oneDimensionTarget.setLevel(oneDimensionSource);
            oneDimensionTarget.setSqlUniqueColumn(tableName + tableFieldName);
            oneDimensionTarget.setFactTableName(miniCube.getSource());
        }
        
        return allColumns;
    }
}
