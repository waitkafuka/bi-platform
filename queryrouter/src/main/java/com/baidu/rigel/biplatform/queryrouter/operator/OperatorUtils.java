package com.baidu.rigel.biplatform.queryrouter.operator;

import java.util.HashSet;
import java.util.Set;

import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.Column;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlColumn;

/**
 * 算子工具类
 * 
 * @author luowenlei
 *
 */
public class OperatorUtils {

    /**
     * aggList
     */
    private static Set<String> aggSet = new HashSet<String>();

    static {
        aggSet.add(Operator.SUM.getName());
        aggSet.add(Operator.COUNT.getName());
        aggSet.add(Operator.DISTINCT_COUNT.getName());
    }

    /**
     * 获取判断此字段是否为AGG计算
     * 
     * @return OperatorType
     */
    public static OperatorType getOperatorType(Column column) {
        if (aggSet.contains(column.getOperator())) {
            return OperatorType.AGG;
        } else {
            return OperatorType.OTHER;
        }
    }

    /**
     * 获取判断此字段是否为AGG计算
     * 
     * @return OperatorType
     */
    public static OperatorType getOperatorType(SqlColumn column) {
        if (aggSet.contains(column.getOperator())) {
            return OperatorType.AGG;
        } else {
            return OperatorType.OTHER;
        }
    }
}
