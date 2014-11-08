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
package com.baidu.rigel.biplatform.tesseract.dataquery.service.impl;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baidu.rigel.biplatform.tesseract.dataquery.service.DataQueryService;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.SqlQuery;
import com.baidu.rigel.biplatform.tesseract.resultset.TesseractResultSet;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.ResultRecord;
import com.baidu.rigel.biplatform.tesseract.resultset.isservice.SearchResultSet;
import com.baidu.rigel.biplatform.tesseract.util.isservice.LogInfoConstants;

/**
 * 
 * SQLDataQueryService的实现类
 * 
 * @author lijin
 *
 */
@Service("sqlDataQueryService")
public class SqlDataQueryServiceImpl implements DataQueryService {
    /**
     * LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlDataQueryServiceImpl.class);
    /**
     * jdbcTemplate
     */
    private JdbcTemplate jdbcTemplate = null;
    
    /**
     * 
     * initJdbcTemplate
     * 
     * @param dataSource
     *            dataSource
     */
    private void initJdbcTemplate(DataSource dataSource) {
        if (jdbcTemplate == null || !this.jdbcTemplate.getDataSource().equals(dataSource)) {
            this.jdbcTemplate = null;
            jdbcTemplate = new JdbcTemplate(dataSource);
        }
        
    }
    
    @Override
    public List<Map<String, Object>> queryForListWithSql(String sql, DataSource dataSource) {
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_BEGIN,
            "queryForListWithSql", "[sql:" + sql + "][dataSource:" + dataSource + "]"));
        initJdbcTemplate(dataSource);
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_END,
            "queryForListWithSql", "[sql:" + sql + "][dataSource:" + dataSource + "]"));
        return this.jdbcTemplate.queryForList(sql);
        
    }
    
    /**
     * getter method for property jdbcTemplate
     * 
     * @param dataSource
     *            获取某数据源下的jdbcTemplate
     * @return the jdbcTemplate
     */
    @Override
    public JdbcTemplate getJdbcTemplate(DataSource dataSource) {
        this.initJdbcTemplate(dataSource);
        return jdbcTemplate;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.biplatform.tesseract.dataquery.service.DataQueryService
     * #queryForDocListWithSQLQuery
     * (com.baidu.rigel.biplatform.tesseract.isservice.meta.SQLQuery,
     * javax.sql.DataSource, long, long)
     */
    @Override
    public TesseractResultSet queryForDocListWithSQLQuery(SqlQuery sqlQuery, DataSource dataSource,
        long limitStart, long limitEnd) {
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_BEGIN,
            "queryForDocListWithSQLQuery", "[sqlQuery:" + sqlQuery + "][dataSource:" + dataSource
                + "][limitStart:" + limitStart + "][limitEnd:" + limitEnd + "]"));
        if (sqlQuery == null || dataSource == null || limitEnd < 0) {
            throw new IllegalArgumentException();
        }
        
        sqlQuery.setLimitMap(limitStart, limitEnd);
        
        this.initJdbcTemplate(dataSource);
        
        LinkedList<ResultRecord> resultList = new LinkedList<ResultRecord>();
        
        jdbcTemplate.query(sqlQuery.toSql(), new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                
                /**
                 * QUESTION：？？？
                 * TODO ResultSet指针不移动，如何取得已经查询的结果
                 * 如果selectList 大于一个值，单一维度数组能够支持？如Select a， b from c
                 * 原有逻辑如下：
                 * for (String select : sqlQuery.getSelectList()) {
                 *      fieldValues.add(rs.getObject(select));
                 *   }
                 *   ResultRecord record = new ResultRecord(fieldValues.toArray(new Serializable[0]),
                 *   sqlQuery.getSelectList().toArray(new String[0]));
                 *   resultList.add(record);
                 */
                List<Object> fieldValues = new ArrayList<Object>();
//                while (rs.next()) {
//                    for (String select : sqlQuery.getSelectList()) {
//                        fieldValues.add(rs.getObject(select));
//                    }
//                    ResultRecord record = new ResultRecord(fieldValues.toArray(new Serializable[0]),
//                            sqlQuery.getSelectList().toArray(new String[0]));
//                    resultList.add(record);
//                }
                String groupBy = "";
                for (String select : sqlQuery.getSelectList()) {
                    fieldValues.add(rs.getObject(select));
                    if (sqlQuery.getGroupBy() != null && sqlQuery.getGroupBy().contains(select)) {
                        groupBy += rs.getString(select) + ",";
                    }
                }
                
                ResultRecord record = new ResultRecord(fieldValues.toArray(new Serializable[0]),
                        sqlQuery.getSelectList().toArray(new String[0]));
                record.setGroupBy(groupBy);
                resultList.add(record);
                // }
            }
        });
        TesseractResultSet result = new SearchResultSet(resultList);
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_END,
            "queryForDocListWithSQLQuery", "[sqlQuery:" + sqlQuery + "][dataSource:" + dataSource
                + "][limitStart:" + limitStart + "][limitEnd:" + limitEnd + "]"));
        return result;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.biplatform.tesseract.dataquery.service.DataQueryService
     * #queryForLongWithSql(java.lang.String, javax.sql.DataSource)
     */
    @Override
    public long queryForLongWithSql(String sql, DataSource dataSource) {
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_BEGIN,
            "queryForListWithSql", "[sql:" + sql + "][dataSource:" + dataSource + "]"));
        long result = -1;
        if (StringUtils.isEmpty(sql) || dataSource == null) {
            throw new IllegalArgumentException();
        }
        this.initJdbcTemplate(dataSource);
        
        result = this.jdbcTemplate.queryForObject(sql, Long.class);
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_END,
            "queryForListWithSql", "[sql:" + sql + "][dataSource:" + dataSource + "]"));
        return result;
    }
    
}
