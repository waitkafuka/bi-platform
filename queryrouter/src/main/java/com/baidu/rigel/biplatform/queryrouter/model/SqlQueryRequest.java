package com.baidu.rigel.biplatform.queryrouter.model;

import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlQuery;

/**
 * 类QueryRequest.java的实现描述：Sql query request 类实现描述 
 * @author luowenlei 2015年9月22日 下午6:11:06
 */
public class SqlQueryRequest {

	/**
     * dataSourceInfo 数据源信息
     */
    private DataSourceInfo dataSourceInfo;
    
    /**
     * sqlQuery
     */
    private SqlQuery sqlQuery;

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
     * default generate get sqlQuery
     * @return the sqlQuery
     */
    public SqlQuery getSqlQuery() {
        return sqlQuery;
    }

    /**
     * default generate set sqlQuery
     * @param sqlQuery the sqlQuery to set
     */
    public void setSqlQuery(SqlQuery sqlQuery) {
        this.sqlQuery = sqlQuery;
    }
    
    
}
