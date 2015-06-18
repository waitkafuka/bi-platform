package com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model;

import java.math.BigDecimal;
import java.util.ArrayList;
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
     * @param values
     *            where values
     * @return String where expression
     */
    public static String getWhereExpression(SQLCondition sqlCondition, List<Object> values) {
        SQLConditionType conditionType = sqlCondition.getCondition();
        values = SqlWhereExpressionFactory.convertSqlConditionValue2SqlValue(sqlCondition.getConditionValues());
        switch (conditionType) {
            // 等于
            case EQ: {
                values.add(sqlCondition.getConditionValues().get(0));
                return " = " + SqlConstants.PARAM;
            }
            // 不等于
            case NOT_EQ: {
                values.add(sqlCondition.getConditionValues().get(0));
                return " <> " + SqlConstants.PARAM;
            }
            // 小于
            case LT: {
                values.add(sqlCondition.getConditionValues().get(0));
                return " < " + SqlConstants.PARAM;
            }
            // 小于等于
            case LT_EQ: {
                values.add(sqlCondition.getConditionValues().get(0));
                return " <= " + SqlConstants.PARAM;
            }
            // 大于
            case GT: {
                values.add(sqlCondition.getConditionValues().get(0));
                return " > " + SqlConstants.PARAM;
            }
            // 大于等于
            case GT_EQ: {
                values.add(sqlCondition.getConditionValues().get(0));
                return " >= " + SqlConstants.PARAM;
            }
            // between and
            case BETWEEN_AND: {
                if (StringUtils.isNumeric(sqlCondition.getConditionValues().get(0))
                        && StringUtils.isNumeric(sqlCondition.getConditionValues().get(0))) {
                    BigDecimal front = new BigDecimal(sqlCondition.getConditionValues().get(0));
                    BigDecimal back = new BigDecimal(sqlCondition.getConditionValues().get(1));
                    if (front.compareTo(back) >= 0) {
                        return " between " + back + " and " + front;
                    } else {
                        return " between " + front + " and " + back;
                    }
                } else {
                    return " between " + sqlCondition.getConditionValues().get(0)
                            + " and " + sqlCondition.getConditionValues().get(1);
                }
            }
            // in
            case IN: {
                StringBuffer inExpression = new StringBuffer(" in (");
                for (String value : sqlCondition.getConditionValues()) {
                    values.add(value);
                    inExpression.append(SqlConstants.PARAM + SqlConstants.COMMA);
                }
                return inExpression.toString().substring(0,
                        inExpression.toString().lastIndexOf(SqlConstants.COMMA))
                        + ")";
            }
            // like
            case LIKE: {
                values.add(sqlCondition.getConditionValues().get(0));
                return " like " + SqlConstants.PARAM;
            }
            default: {
                return "";
            }
        }
    }

    /**
     * 转换sqlcondition为sql where中的字符串或数字，加引号或不加引号
     * @param values sq param values
     * @return List<Object> 转换后的参数列表
     */
    private static List<Object> convertSqlConditionValue2SqlValue(List<String> values) {
        List<Object> result = new ArrayList<Object>();
        String value = null;
        for (int i = 0 ; i < values.size(); i++) {
            value = values.get(i).toString();
            if (StringUtils.isNumeric(value)) {
            // 数字
                result.add(new BigDecimal(value));
            } else {
            // 字符串
                result.add(value);
            }
        }
        return result;
    }

    /**
     * SqlWhereExpressionFactory
     */
    private SqlWhereExpressionFactory() {
        
    }
}
