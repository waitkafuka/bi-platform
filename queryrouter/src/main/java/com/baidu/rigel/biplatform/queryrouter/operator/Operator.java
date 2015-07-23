package com.baidu.rigel.biplatform.queryrouter.operator;

/**
 * 算子枚举
 * 
 * @author luowenlei
 *
 */
public enum Operator {
    /**
     * SUM
     */
    SUM("SUM"),
    /**
     * COUNT
     */
    COUNT("COUNT"),
    /**
     * DISTINCT_COUNT
     */
    DISTINCT_COUNT("DISTINCT_COUNT");

    private String name;

    /**
     * getName
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    private Operator(String name) {
        this.name = name;
    }
}
