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

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.model.ConfigQuestionModel;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.common.QuestionModel4TableDataUtils;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlColumn;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.QuestionModelTransformationException;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlExpression;

/**
 * 
 * JdbcQuestionModelUtil的转换类
 * 
 * @author luowenlei
 *
 */
@Service("jdbcQuestionModelUtil")
public class JdbcQuestionModelUtil {
    
    /**
     * convertQuestionModel2Sql
     * 
     * @param questionModel
     *            questionModel
     * @return String sql str
     */
    public SqlExpression convertQuestionModel2Sql(QuestionModel questionModel)
            throws QuestionModelTransformationException {
        ConfigQuestionModel configQuestionModel = (ConfigQuestionModel) questionModel;
        MiniCube cube = (MiniCube) configQuestionModel.getCube();
        questionModel.setUseIndex(false);
        HashMap<String, SqlColumn> allColums = QuestionModel4TableDataUtils
                .getAllCubeColumns(configQuestionModel.getCube());
        List<SqlColumn> needColums = QuestionModel4TableDataUtils.getNeedColumns(allColums,
                configQuestionModel.getAxisMetas(), cube);
        SqlDataSourceInfo sqlDataSource = (SqlDataSourceInfo) configQuestionModel
                .getDataSourceInfo();
        SqlExpression sqlExpression = new SqlExpression(sqlDataSource.getDataBase().getDriver());
        sqlExpression.generateSql(configQuestionModel, allColums, needColums);
        return sqlExpression;
    }
    
}
