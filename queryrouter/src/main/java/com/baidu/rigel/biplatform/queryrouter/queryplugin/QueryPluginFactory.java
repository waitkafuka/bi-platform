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
package com.baidu.rigel.biplatform.queryrouter.queryplugin;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.ac.query.MiniCubeConnection.DataSourceType;
import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo.DataBase;
import com.baidu.rigel.biplatform.ac.query.model.ConfigQuestionModel;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.QuerySqlPlugin;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.QueryTessractPlugin;

/**
 * 查询插件获取类
 * 
 * @author luowenlei
 *
 */
@Service
public class QueryPluginFactory {

    /**
     * TessractPlugin
     */
    @Resource(name = "queryTessractPlugin")
    private QueryTessractPlugin queryTessractPlugin;

    /**
     * queryMysqlPlugin
     */
    @Resource(name = "querySqlPlugin")
    private QuerySqlPlugin querySqlPlugin;

    /**
     * 获取查询插件
     *
     * @param strategy
     *            认证策略
     * @return QueryPlugin 认证服务实例
     */
    public QueryPlugin getPlugin(QuestionModel questionModel) {
        if (questionModel instanceof ConfigQuestionModel) {
            ConfigQuestionModel configQuestionModel = (ConfigQuestionModel) questionModel;
            if (configQuestionModel.getDataSourceInfo().getDataSourceType() == DataSourceType.SQL) {
                SqlDataSourceInfo sqlDataSourceInfo = (SqlDataSourceInfo) configQuestionModel
                        .getDataSourceInfo();
                if (sqlDataSourceInfo.getDataBase() == DataBase.MYSQL) {
                    // 临时方案 通过querysource判断
                    if ("TESSERACT"
                            .equals(configQuestionModel.getQuerySource())) {
                        return queryTessractPlugin;
                    } else if ("SQL".equals(configQuestionModel
                            .getQuerySource())) {
                        return querySqlPlugin;
                    }
                } else if (sqlDataSourceInfo.getDataBase() == DataBase.PALO) {
                    // 临时方案 通过querysource判断
                    if ("TESSERACT"
                            .equals(configQuestionModel.getQuerySource())) {
                        return queryTessractPlugin;
                    } else if ("SQL".equals(configQuestionModel
                            .getQuerySource())) {
                        return querySqlPlugin;
                    }
                }
            }
        }
        return null;
    }
}
