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
package com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.common.jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.common.SqlExpression;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.PlaneTableQuestionModel;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlColumn;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlConstants;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlQuery;

/**
 * 
 * Description: 查询数据库中需要join的表的数据
 * 
 * @author 罗文磊
 *
 */
@Service("joinTableDataService")
@Scope("prototype")
public class JoinTableDataService {
    /**
     * JdbcConnectionPool
     */
    @Resource(name = "jdbcHandler")
    private JdbcHandler jdbcHandler;

    /**
     * 查询tableName中的jointable的数据id
     * 
     * @param planeTableQuestionModel
     *            所有的字段
     * @param allColums
     *            dataSourceInfo
     * @param tables
     *            key为tablename的结构存储
     * @return Map<String, List<Object>> key为主表中的字段，value为where的id数据
     */
    public Map<String, List<Object>> getJoinTableData(PlaneTableQuestionModel planeTableQuestionModel,
            Map<String, SqlColumn> allColums, HashMap<String, List<SqlColumn>> tables) {
        Map<String, List<Object>> result = new HashMap<String, List<Object>>();
        SqlDataSourceInfo dataSourceInfo = (SqlDataSourceInfo) planeTableQuestionModel
                .getDataSourceInfo();
        for (String tableName : tables.keySet()) {
            SqlQuery sqlQuery = new SqlQuery();
            SqlExpression sqlExpression = new SqlExpression(dataSourceInfo
                    .getDataBase().getDriver());
            List<SqlColumn> selectColumns = tables.get(tableName);
            String whereSql = SqlConstants.WHERE_TRUE;
            String joinTableFieldId = "";
            String factTableColumnName = "";
            for (SqlColumn sqlColumn : selectColumns) {
                joinTableFieldId = sqlColumn.getJoinTableFieldName();
                factTableColumnName = sqlColumn.getFactTableFieldName();
                whereSql = whereSql
                        + sqlExpression.generateSqlWhereOneCondition(sqlColumn,
                                sqlQuery, false);
            }
            if (SqlConstants.WHERE_TRUE.equals(whereSql)) {
                // 如果搜索为所有条件，则不组织wheresql
                break;
            }
            String sql = "select " + joinTableFieldId + " from " + tableName
                    + whereSql;
            List<Map<String, Object>> datas = jdbcHandler.queryForList(sql,
                    sqlQuery.getWhere().getValues(), dataSourceInfo);
            for (Map<String, Object> data : datas) {
                if (result.get(factTableColumnName) == null) {
                    result.put(factTableColumnName, new ArrayList<Object>());
                }
                result.get(factTableColumnName).add(data.get(joinTableFieldId));
            }
        }
        return result;
    }
}