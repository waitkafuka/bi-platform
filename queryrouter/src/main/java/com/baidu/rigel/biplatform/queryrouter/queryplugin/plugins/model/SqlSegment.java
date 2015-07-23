package com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model;

import java.io.Serializable;

/**
 * sql Segment
 * 
 * @author luowenlei
 *
 */
public class SqlSegment implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8185627906626742508L;

    /**
     * sql
     */
    private String sql;

    /**
     * getSql
     * 
     * @return the sql
     */
    public String getSql() {
        if (sql == null) {
            sql = "";
        }
        return sql;
    }

    /**
     * setSql
     * 
     * @param sql
     *            the sql to set
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

}
