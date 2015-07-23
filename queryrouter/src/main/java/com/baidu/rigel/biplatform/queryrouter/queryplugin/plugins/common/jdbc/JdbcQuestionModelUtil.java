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
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.common.PlaneTableQuestionModel2SqlColumnUtils;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.common.SqlExpression;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.common.jdbc.parsecheck.TableExistCheckService;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.PlaneTableQuestionModel;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.QuestionModelTransformationException;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlColumn;

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
     * 将questionModel转换成Sql对象
     * 
     * @param questionModel
     *            questionModel
     * @return String sql str
     */
    public SqlExpression convertQuestionModel2Sql(PlaneTableQuestionModel planeTableQuestionModel,
            Map<String, SqlColumn> allColumns, String tableName) throws QuestionModelTransformationException {
        SqlDataSourceInfo sqlDataSource = (SqlDataSourceInfo) planeTableQuestionModel
                .getDataSourceInfo();
        List<SqlColumn> needColums = PlaneTableQuestionModel2SqlColumnUtils
                .getNeedColumns(allColumns, planeTableQuestionModel.getSelection());
        SqlExpression sqlExpression = new SqlExpression(sqlDataSource.getDataBase().getDriver());
        sqlExpression.setTableName(tableName);
        sqlExpression.generateSql(planeTableQuestionModel, allColumns, needColums);
        return sqlExpression;
    }
    
}
