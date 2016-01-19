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
package com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.mysql;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.ac.query.data.DataModel;
import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo.DataBase;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.queryrouter.handle.model.QueryHandler;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.QueryPlugin;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.convert.DataModelConvertService;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.convert.SqlColumnUtils;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.jdbc.service.impl.JdbcCountNumServiceImpl;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.sql.model.QuestionModelTransformationException;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.sql.model.SqlQuestionModel;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.utils.QueryHandlerBuilder;
import com.google.common.collect.Lists;

/**
 * mysql查询的插件
 * 
 * @author luowenlei
 *
 */
@Service("mySqlSqlPlugin")
@Scope("prototype")
public class MySqlSqlPlugin implements QueryPlugin {
    
    /**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(MySqlSqlPlugin.class);
    
    /**
     * dataModelConvertService
     */
    @Resource(name = "dataModelConvertService")
    private DataModelConvertService dataModelConvertService;
    
    /**
     * jdbcCountNumServiceImpl
     */
    @Resource(name = "jdbcCountNumServiceImpl")
    private JdbcCountNumServiceImpl jdbcCountNumService;
    
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.biplatform.querybus.queryplugin.QueryPlugin#query(com
     * .baidu.rigel.biplatform.ac.query.model.QuestionModel)
     */
    @Override
    public DataModel query(QuestionModel questionModel) throws QuestionModelTransformationException {
        SqlQuestionModel sqlQuestionModel = (SqlQuestionModel) questionModel;
        QueryHandler queryHandler = QueryHandlerBuilder.buildQueryHandler(questionModel);
        List<Map<String, Object>> rowBasedList = queryHandler.getJdbcHandler()
                .queryForList(sqlQuestionModel.getSql(), Lists.newArrayList());
        // convert data to datamodel
        DataModel dataModel = dataModelConvertService.convert(
                SqlColumnUtils.getNeedSqlColumns(sqlQuestionModel.getSql()), rowBasedList);
        return dataModel;
    }
    
    @Override
    public boolean isSuitable(QuestionModel questionModel)
            throws QuestionModelTransformationException {
        if (questionModel instanceof SqlQuestionModel) {
            SqlQuestionModel sqlQuestionModel = (SqlQuestionModel) questionModel;
            SqlDataSourceInfo sqlDataSourceInfo = ((SqlDataSourceInfo) sqlQuestionModel
                    .getDataSourceInfo());
            if (sqlDataSourceInfo.getDataBase() == DataBase.PALO
                    || sqlDataSourceInfo.getDataBase() == DataBase.MYSQL) {
                logger.info("queryId:{} QuerySqlPlugin find this questionModel isSuitable!",
                        questionModel.getQueryId());
                return true;
            }
        }
        return false;
    }
}
