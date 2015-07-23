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
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.baidu.rigel.biplatform.queryrouter.operator.OperatorType;
import com.baidu.rigel.biplatform.queryrouter.operator.OperatorUtils;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.ColumnCondition;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.ColumnType;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.PlaneTableQuestionModel;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlColumn;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlConstants;

/**
 * QuestionModel to TableData的工具类
 * 
 * @author luowenlei
 *
 */
public class PlaneTableQuestionModel2SqlColumnUtils {

    /**
     * 获取questionModel中需要查询的Columns
     * 
     * @param Map<String, SqlColumn> allColums 元数据信息
     * @param List<String> selecions 选取的字符串信息
     * 
     * @return List needcolumns hashmap
     */
    public static List<SqlColumn> getNeedColumns(
            Map<String, SqlColumn> allColums, List<String> selections) {
        List<SqlColumn> needColumns = new ArrayList<SqlColumn>();
        if (CollectionUtils.isEmpty(allColums)) {
            return needColumns;
        }
        // 获取列元数据
        selections.forEach((selectName) -> {
            needColumns.add(allColums.get(selectName));
        });
        return needColumns;
    }

    /**
     * 获取指标及维度中所有的字段信息Formcube
     * 
     * @param cube
     *            cube
     * @return HashMap allcolumns hashmap
     */
    public static HashMap<String, SqlColumn> getAllColumns(
            PlaneTableQuestionModel planeTableQuestionModel) {
        HashMap<String, SqlColumn> allColumns = new HashMap<String, SqlColumn>();
        if (CollectionUtils.isEmpty(planeTableQuestionModel.getMetaMap())) { 
            return allColumns;
        }
        planeTableQuestionModel
                .getMetaMap()
                .forEach(
                        (k, v) -> {
                            allColumns.put(k, new SqlColumn());
                            SqlColumn sqlColumn = allColumns.get(k);
                            sqlColumn.setName(v.getName());
                            if (OperatorType.AGG == OperatorUtils.getOperatorType(v)) {
                                sqlColumn.setOperator(v.getOperator());
                            }
                            sqlColumn.setTableFieldName(v.getName());
                            sqlColumn.setTableName(v.getTableName());
                            sqlColumn.setSourceTableName(planeTableQuestionModel.getSource());
                            sqlColumn.setFactTableFieldName(v.getFacttableColumnName());
                            sqlColumn.setCaption(v.getCaption());
                            if (ColumnType.JOIN == v.getColumnType()) {
                                sqlColumn.setSqlUniqueColumn(v.getTableName() + v.getName());
                            } else {
                                sqlColumn.setSqlUniqueColumn(getFactTableAliasName() + v.getName());
                            }
                            sqlColumn.setJoinTableFieldName(v.getJoinTableFieldName());
                            sqlColumn.setType(v.getColumnType());
                            sqlColumn.setColumnKey(k);
                            sqlColumn.setColumnCondition((ColumnCondition)planeTableQuestionModel
                                    .getQueryConditions().get(k));
                        });
        return allColumns;
    }

    /**
     * 获取事实表
     * 
     * @return
     */
    public static String getFactTableAliasName() {
        return SqlConstants.SOURCE_TABLE_ALIAS_NAME;
    }
}
