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
package com.baidu.rigel.biplatform.queryrouter.queryplugin.sql.model;

import java.io.Serializable;

import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;

/**
 * 平面表问题模型
 * 
 * @author 罗文磊
 *
 */
public class SqlQuestionModel extends QuestionModel implements Serializable {

    /**
     * default generate id
     */
    private static final long serialVersionUID = 1576666141762101245L;
    
    /**
     * dataSourceInfo 问题模型对应的数据源信息（有数据源的key优先从缓存取）
     */
    private DataSourceInfo dataSourceInfo;
    
    /**
     * sql,查询 sql
     */
    private String sql;

    /**
     * default generate get dataSourceInfo
     * @return the dataSourceInfo
     */
    public DataSourceInfo getDataSourceInfo() {
        return dataSourceInfo;
    }

    /**
     * default generate set dataSourceInfo
     * @param dataSourceInfo the dataSourceInfo to set
     */
    public void setDataSourceInfo(DataSourceInfo dataSourceInfo) {
        this.dataSourceInfo = dataSourceInfo;
    }

    /**
     * default generate get sql
     * @return the sql
     */
    public String getSql() {
        return sql;
    }

    /**
     * default generate set sql
     * @param sql the sql to set
     */
    public void setSql(String sql) {
        this.sql = sql;
    }
}