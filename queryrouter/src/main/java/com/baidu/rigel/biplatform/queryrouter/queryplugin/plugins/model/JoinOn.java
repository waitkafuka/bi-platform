package com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model;

/**
 * sql JoinOn
 * 
 * @author luowenlei
 *
 */
public class JoinOn extends SqlSegment {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6180677386102168725L;

    /**
     * joinTableFieldName
     */
    private String joinTableFieldName;

    /**
     * complexOperator
     */
    private String facttableColumnName;

    /**
     * default generate get joinTableFieldName
     * @return the joinTableFieldName
     */
    public String getJoinTableFieldName() {
        return joinTableFieldName;
    }

    /**
     * default generate set joinTableFieldName
     * @param joinTableFieldName the joinTableFieldName to set
     */
    public void setJoinTableFieldName(String joinTableFieldName) {
        this.joinTableFieldName = joinTableFieldName;
    }

    /**
     * default generate get facttableColumnName
     * @return the facttableColumnName
     */
    public String getFacttableColumnName() {
        return facttableColumnName;
    }

    /**
     * default generate set facttableColumnName
     * @param facttableColumnName the facttableColumnName to set
     */
    public void setFacttableColumnName(String facttableColumnName) {
        this.facttableColumnName = facttableColumnName;
    }
}
