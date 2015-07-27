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
package com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.common.jdbc.meta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.common.jdbc.JdbcHandler;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.ColumnType;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlColumn;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlConstants;

/**
 * 
 * Description: 查询数据库中的表的元数据信息
 * 
 * @author 罗文磊
 *
 */
@Service("tableMetaService")
@Scope("prototype")
public class TableMetaService {
    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * JdbcConnectionPool
     */
    @Resource(name = "jdbcHandler")
    private JdbcHandler jdbcHandler;

    /**
     * 查询所有字段在数据库中的数据类型并添入到sqlColumnMap中
     * 
     * @param sqlColumnMap
     *            所有的字段
     * @param SqlDataSourceInfo
     *            dataSourceInfo
     * @return List<String> 存在的表
     */
    public Map<String, SqlColumn> generateColumnDataType(
            Map<String, SqlColumn> sqlColumnMap,
            SqlDataSourceInfo dataSourceInfo) {
        Set<String> tableNameSet = new HashSet<String>();
        Set<String> columnNameSet = new HashSet<String>();
        for (SqlColumn sqlColumn : sqlColumnMap.values()) {
            String tableNameTmp = "";
            String columnNameTmp = "";
            if (sqlColumn.getType() == ColumnType.TIME) {
                tableNameTmp = sqlColumn.getSourceTableName();
                columnNameTmp = sqlColumn.getFactTableFieldName();
            } else if (sqlColumn.getType() == ColumnType.CALLBACK) {
                tableNameTmp = sqlColumn.getSourceTableName();
                columnNameTmp = sqlColumn.getFactTableFieldName();
            } else {
                tableNameTmp = sqlColumn.getTableName();
                columnNameTmp = sqlColumn.getTableFieldName();
            }
            if (StringUtils.isNotEmpty(tableNameTmp)
                    && tableNameTmp.indexOf(",") >= 0) {
                // 如果为多事实表，那么取一个表的数据类型
                tableNameTmp = tableNameTmp.substring(0,
                        tableNameTmp.indexOf(","));
            }
            tableNameSet.add(tableNameTmp);
            columnNameSet.add(columnNameTmp);
        }
        String tableNameStringIn = convertSet2InString(tableNameSet);
        String columnNameStringIn = convertSet2InString(columnNameSet);
        String dataType = "";
        if (SqlConstants.DRIVER_MYSQL.equals(dataSourceInfo.getDataBase()
                .getDriver())) {
            String sql = "select column_name,table_name,data_type from information_schema.`columns` "
                    + "where table_name in ("
                    + tableNameStringIn
                    + ")"
                    + "and column_name in (" + columnNameStringIn + ")";
            List<Map<String, Object>> datas = jdbcHandler.queryForList(sql,
                    new ArrayList<Object>(), dataSourceInfo);
            if (CollectionUtils.isEmpty(datas)) {
                logger.warn(" can not found tablenames:" + tableNameStringIn
                        + " and columnNames:" + columnNameStringIn
                        + " in database.");
                return sqlColumnMap;
            }
            for (SqlColumn sqlColumn : sqlColumnMap.values()) {
                String tableNameTmp1 = "";
                String columnNameTmp1 = "";
                if (sqlColumn.getType() == ColumnType.TIME) {
                    tableNameTmp1 = sqlColumn.getSourceTableName();
                    columnNameTmp1 = sqlColumn.getFactTableFieldName();
                } else if (sqlColumn.getType() == ColumnType.CALLBACK) {
                    tableNameTmp1 = sqlColumn.getSourceTableName();
                    columnNameTmp1 = sqlColumn.getFactTableFieldName();
                } else {
                    tableNameTmp1 = sqlColumn.getTableName();
                    columnNameTmp1 = sqlColumn.getTableFieldName();
                }
                if (StringUtils.isNotEmpty(tableNameTmp1)
                        && tableNameTmp1.indexOf(",") >= 0) {
                    // 为多事实表的情况
                    tableNameTmp1 = tableNameTmp1.substring(0,
                            tableNameTmp1.indexOf(","));
                }
                for (Map<String, Object> data : datas) {
                    String tableNameTmp = data.get("table_name").toString();
                    String columnNameTmp = data.get("column_name").toString();
                    String dataTypeTmp = data.get("data_type").toString();
                    if (tableNameTmp1.equals(tableNameTmp)
                            && columnNameTmp1.equals(columnNameTmp)) {
                        sqlColumn.setDataType(dataTypeTmp);
                        break;
                    }
                }
            }
        } else {
            logger.info("no available driver handler match:"
                    + dataSourceInfo.getDataBase().getDriver());
        }
        logger.info("found table column datatype is:" + dataType
                + "in tablenames:" + tableNameStringIn + " and columnNames:" + columnNameStringIn);
        return sqlColumnMap;
    }

    private static String convertSet2InString(Set<String> set) {
        String result = "";
        for (String name : set) {
            result = result + "'" + name + "',";
        }
        if (StringUtils.isEmpty(result)) {
            return "";
        } else {
            return result.substring(0, result.lastIndexOf(","));
        }
    }
}