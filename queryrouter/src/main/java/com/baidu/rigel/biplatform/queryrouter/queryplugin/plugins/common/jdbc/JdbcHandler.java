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

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.queryrouter.handle.QueryRouterContext;
import com.baidu.rigel.biplatform.tesseract.datasource.DataSourcePoolService;
import com.baidu.rigel.biplatform.tesseract.datasource.impl.SqlDataSourceWrap;
import com.baidu.rigel.biplatform.tesseract.exception.DataSourceException;

/**
 * 
 * 处理Jdbc sql query请求
 * 
 * @author luowenlei
 *
 */
@Service("jdbcHandler")
@Scope("prototype")
public class JdbcHandler {

    /**
     * Logger
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * jdbcTemplate
     */
    private JdbcTemplate jdbcTemplate = null;

    /**
     * dataSourcePoolService
     */
    @Resource
    private DataSourcePoolService dataSourcePoolService;

    /**
     * 
     * initJdbcTemplate
     * 
     * @param dataSource
     *            dataSource
     */
    private synchronized void initJdbcTemplate(DataSourceInfo dataSourceInfo) {
        long begin = System.currentTimeMillis();
        try {
            DataSource dataSource = (SqlDataSourceWrap) this.dataSourcePoolService
                    .getDataSourceByKey(dataSourceInfo);

            if (jdbcTemplate == null
                    || !this.jdbcTemplate.getDataSource().equals(dataSource)) {
                this.jdbcTemplate = null;
                jdbcTemplate = new JdbcTemplate(dataSource);
            }
        } catch (DataSourceException e) {
            e.printStackTrace();
            logger.error("getDataSource error:" + e.getCause().getMessage());
        }
        logger.info("initJdbcTemplate cost:"
                + (System.currentTimeMillis() - begin) + "ms");
    }

    /**
     * 通过sql查询数据库中的数据
     * 
     * @param sqlQuery
     *            sql
     * @param dataSourceInfo
     *            dataSourceInfo
     * @return List<Map<String, Object>> formd tableresult data
     */
    public List<Map<String, Object>> queryForList(String sql,
            List<Object> whereValues, DataSourceInfo dataSourceInfo) {
        initJdbcTemplate(dataSourceInfo);
        long begin = System.currentTimeMillis();
        List<Map<String, Object>> result = null;
        try {
            logger.info("queryId:{} sql: {}", QueryRouterContext
                    .getQueryId(), this.toPrintString(sql, whereValues));
            result = this.jdbcTemplate.queryForList(sql, whereValues.toArray());
        } catch (Exception e) {
            logger.error("queryId:{} select sql error:{}",
                    QueryRouterContext.getQueryId(), e.getCause().getMessage());
            throw e;
        } finally {
            logger.info("queryId:{} select sql cost:{} ms",
                    QueryRouterContext.getQueryId(), System.currentTimeMillis() - begin);
        }
        return result;
    }

    /**
     * queryForInt
     * 
     * @param sql
     *            sql
     * @param whereValues
     *            whereValues
     * @param dataSourceInfo
     *            dataSourceInfo
     * @return int count
     */
    public int queryForInt(String sql, List<Object> whereValues,
            DataSourceInfo dataSourceInfo) {
        initJdbcTemplate(dataSourceInfo);
        long begin = System.currentTimeMillis();
        Map<String, Object> result = null;
        int count = 0;
        try {
            logger.info("queryId:{} count sql: {}", QueryRouterContext
                    .getQueryId(), this.toPrintString(sql, whereValues));
            result = this.jdbcTemplate.queryForMap(sql, whereValues.toArray());
            count = Integer.valueOf(result.values().toArray()[0].toString())
                    .intValue();
        } catch (Exception e) {
            logger.error("queryId:{} select sql error:{}",
                    QueryRouterContext.getQueryId(), e.getCause().getMessage());
            throw e;
        } finally {
            logger.info("queryId:{} select count sql cost:{} ms, result: {}",
                    QueryRouterContext.getQueryId(), System.currentTimeMillis()
                            - begin, count);
        }
        return count;
    }

    /**
     * toPrintString
     * 
     * @param sql
     *            sql
     * @param objects
     *            objects
     * @return sql String
     */
    public String toPrintString(String sql, List<Object> objects) {
        if (CollectionUtils.isEmpty(objects)) {
            return sql;
        }
        String printSql = new String(sql);
        int valuesCount = 0;
        if (!StringUtils.isEmpty(printSql)) {
            for (Object value : objects) {
                valuesCount ++;
                if (value instanceof String) {
                    printSql = StringUtils.replaceOnce(printSql, "?", "'"
                            + value.toString() + "'");
                } else {
                    printSql = StringUtils.replaceOnce(printSql, "?",
                            value.toString());
                }
                if (valuesCount > 2000) {
                    return printSql;
                }
            }
            return printSql;
        } else {
            return "";
        }
    }
}
