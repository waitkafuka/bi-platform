package com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.baidu.rigel.biplatform.ac.query.model.SQLCondition;
import com.baidu.rigel.biplatform.ac.query.model.SQLCondition.SQLConditionType;

/**
 * 组织sql where 表达式的Factory
 * 
 * @author luowenlei
 *
 */
public class SqlWhereExpressionFactory {

    /**
     * 根据条件获取where expression
     * 
     * @param sqlCondition
     *            sqlCondition
     * @return String where expression
     */
    public static String getWhereExpression(SQLCondition sqlCondition) {
        SQLConditionType conditionType = sqlCondition.getCondition();
        SqlWhereExpressionFactory.convertSqlConditionValue2SqlValue(sqlCondition.getConditionValues());
        switch (conditionType) {
            // 等于
            case EQ: {
                return " = " + sqlCondition.getConditionValues().get(0);
            }
            // 不等于
            case NOT_EQ: {
                return " <> " + sqlCondition.getConditionValues().get(0);
            }
            // 小于
            case LT: {
                return " < " + sqlCondition.getConditionValues().get(0);
            }
            // 小于等于
            case LT_EQ: {
                return " <= " + sqlCondition.getConditionValues().get(0);
            }
            // 大于
            case GT: {
                return " > " + sqlCondition.getConditionValues().get(0);
            }
            // 大于等于
            case GT_EQ: {
                return " >= " + sqlCondition.getConditionValues().get(0);
            }
            // between and
            case BETWEEN_AND: {
                return " between " + sqlCondition.getConditionValues().get(0)
                        + " and " + sqlCondition.getConditionValues().get(1);
            }
            // in
            case IN: {
                StringBuffer inExpression = new StringBuffer(" in (");
                for (String value : sqlCondition.getConditionValues()) {
                    inExpression.append(value + SqlConstants.COMMA);
                }
                return inExpression.toString().substring(0,
                        inExpression.toString().lastIndexOf(SqlConstants.COMMA))
                        + ")";
            }
            // like
            case LIKE: {
                return " like " + sqlCondition.getConditionValues().get(0);
            }
            default: {
                return "";
            }
        }
    }
    
    /**
     * 转换sqlcondition为sql where中的字符串或数字，加引号或不加引号
     * @param values sqlvalues
     */
    private static void convertSqlConditionValue2SqlValue(List<String> values) {
        String value = null;
        for (int i = 0 ; i < values.size(); i++) {
            value = values.get(i);
            if (StringUtils.isNumeric(value)) {
            // 数字
                values.set(i, value);
            } else {
            // 字符串
                values.set(i, "'" + value + "'");
            }
        }
    }
}
