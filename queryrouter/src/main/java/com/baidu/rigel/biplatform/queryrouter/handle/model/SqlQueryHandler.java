package com.baidu.rigel.biplatform.queryrouter.handle.model;

import com.baidu.rigel.biplatform.queryrouter.queryplugin.service.JdbcHandler;

/**
 * 类QueryRequest.java的实现描述：Sql query request 类实现描述 
 * @author luowenlei 2015年9月22日 下午6:11:06
 */
public class SqlQueryHandler extends QueryHandler{
    /**
     * sqls
     */
    private String sql;

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

    /**
     * @param sql
     * @param jdbcHandler
     */
    public SqlQueryHandler(String sql, JdbcHandler jdbcHandler) {
        super(null, jdbcHandler);
        this.sql = sql;
    }

}
