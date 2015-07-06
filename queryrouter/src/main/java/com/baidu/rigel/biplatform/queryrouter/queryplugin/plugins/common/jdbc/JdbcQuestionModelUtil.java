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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.model.ConfigQuestionModel;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.common.QuestionModel4TableDataUtils;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.common.jdbc.parsecheck.TableExistCheckService;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.QuestionModelTransformationException;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlColumn;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlExpression;

/**
 * 
 * JdbcQuestionModelUtil的转换类
 * 
 * @author luowenlei
 *
 */
@Service("jdbcQuestionModelUtil")
@Scope("prototype")
public class JdbcQuestionModelUtil {
    
    /**
     * TableExistCheck
     */
    @Resource(name = "tableExistCheckService")
    private TableExistCheckService tableExistCheckService;

    /**
     * convertQuestionModel2Sql
     * 
     * @param questionModel
     *            questionModel
     * @return String sql str
     */
    public SqlExpression convertQuestionModel2Sql(QuestionModel questionModel, String tableName)
            throws QuestionModelTransformationException {
        ConfigQuestionModel configQuestionModel = (ConfigQuestionModel) questionModel;
        Map<String, SqlColumn> allColums = QuestionModel4TableDataUtils.getAllCubeColumns(questionModel);
        List<SqlColumn> needColums = QuestionModel4TableDataUtils.getNeedColumns(questionModel);
        SqlDataSourceInfo sqlDataSource = (SqlDataSourceInfo) configQuestionModel
                .getDataSourceInfo();
        SqlExpression sqlExpression = new SqlExpression(sqlDataSource.getDataBase().getDriver());
        sqlExpression.setTableName(tableName);
        sqlExpression.generateSql(configQuestionModel, allColums, needColums);
        return sqlExpression;
    }
    
}
