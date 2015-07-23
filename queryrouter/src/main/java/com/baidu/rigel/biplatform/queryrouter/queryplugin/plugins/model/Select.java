package com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model;

import java.util.List;

/**
 * sql Select
 * 
 * @author luowenlei
 *
 */
public class Select extends SqlSegment {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1938118460092726099L;

    /**
     * selectList
     */
    private List<SqlColumn> selectList;

    /**
     * getSelectList
     * 
     * @return the selectList
     */
    public List<SqlColumn> getSelectList() {
        return selectList;
    }

    /**
     * setSelectList
     * 
     * @param selectList
     *            the selectList to set
     */
    public void setSelectList(List<SqlColumn> selectList) {
        this.selectList = selectList;
    }
}
