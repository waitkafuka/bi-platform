package com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.rigel.biplatform.ac.query.model.PageInfo;
import com.baidu.rigel.biplatform.queryrouter.operator.OperatorType;
import com.baidu.rigel.biplatform.queryrouter.operator.OperatorUtils;

/**
 * sql select
 * 
 * @author luowenlei
 *
 */
public class SqlQuery implements Serializable {

    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(SqlQuery.class);

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5653873904654535901L;

    /**
     * select
     */
    private Select select = new Select();
    /**
     * from
     */
    private From from = new From();
    /**
     * where
     */
    private Where where = new Where();
    /**
     * join
     */
    private Join join = new Join();
    /**
     * orderBy
     */
    private OrderBy orderBy = new OrderBy();
    /**
     * pageInfo
     */
    private PageInfo pageInfo;

    /**
     * driver
     */
    private String driver;

    /**
     * getSelect
     * 
     * @return the select
     */
    public Select getSelect() {
        return select;
    }

    /**
     * setSelect
     * 
     * @param select
     *            the select to set
     */
    public void setSelect(Select select) {
        this.select = select;
    }

    /**
     * getFrom
     * 
     * @return the from
     */
    public From getFrom() {
        return from;
    }

    /**
     * setFrom
     * 
     * @param from
     *            the from to set
     */
    public void setFrom(From from) {
        this.from = from;
    }

    /**
     * getWhere
     * 
     * @return the where
     */
    public Where getWhere() {
        return where;
    }

    /**
     * setWhere
     * 
     * @param where
     *            the where to set
     */
    public void setWhere(Where where) {
        this.where = where;
    }

    /**
     * getJoin
     * 
     * @return the join
     */
    public Join getJoin() {
        return join;
    }

    /**
     * setJoin
     * 
     * @param join
     *            the join to set
     */
    public void setJoin(Join join) {
        this.join = join;
    }

    /**
     * getOrderBy
     * 
     * @return the orderBy
     */
    public OrderBy getOrderBy() {
        return orderBy;
    }

    /**
     * setOrderBy
     * 
     * @param orderBy
     *            the orderBy to set
     */
    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * getPageInfo
     * 
     * @return the pageInfo
     */
    public PageInfo getPageInfo() {
        return pageInfo;
    }

    /**
     * setPageInfo
     * 
     * @param pageInfo
     *            the pageInfo to set
     */
    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    /**
     * toSql
     * 
     * @return
     */
    public String toSql() {
        StringBuffer sqlExpression = new StringBuffer();
        if (select == null || CollectionUtils.isEmpty(select.getSelectList())) {
            return "";
        }
        try {
            sqlExpression.append(select.getSql());
            sqlExpression.append(from.getSql());
            sqlExpression.append(join.getSql());
            sqlExpression.append(where.getSql());
            sqlExpression.append(this.generateGroupBy());
            sqlExpression.append(orderBy.getSql());
            return this.generatePageInfo(sqlExpression.toString());
        } catch (Exception e) {
            logger.error("occur sql exception:{}", e.getMessage());
            throw e;
        }
    }

    /**
     * toCountSql
     * 
     * @return
     */
    public String toCountSql() {
        StringBuffer sqlExpression = new StringBuffer();
        if (select == null || CollectionUtils.isEmpty(select.getSelectList())) {
            return "";
        }
        try {
            sqlExpression.append(select.getSql());
            sqlExpression.append(from.getSql());
            sqlExpression.append(where.getSql());
            sqlExpression.append(this.generateGroupBy());
        } catch (Exception e) {
            logger.error("occur sql exception:{}", e.getMessage());
            throw e;
        }
        return sqlExpression.toString();
    }

    public String generateGroupBy() {
        String sqlExpression = "";
        // generate GroupBy 根据select自己计算是否需要groupby
        boolean needGroupBy = false;
        StringBuffer groupbyTmpExpression = new StringBuffer("");
        StringBuffer groupbyExpression = new StringBuffer(" group by ");
        for (SqlColumn colum : select.getSelectList()) {
            if (!StringUtils.isEmpty(colum.getOperator())
                    && OperatorUtils.getOperatorType(colum) == OperatorType.AGG) {
                needGroupBy = true;
            } else {
                if (ColumnType.JOIN == colum.getType()) {
                    // 如果为Join字段
                    String columName = colum.getTableName();
                    groupbyTmpExpression.append(columName + SqlConstants.DOT
                            + colum.getTableFieldName() + SqlConstants.COMMA);
                } else {
                    // 如果为其他字段
                    String columName = SqlConstants.SOURCE_TABLE_ALIAS_NAME
                            + SqlConstants.DOT + colum.getFactTableFieldName();
                    groupbyTmpExpression.append(columName + SqlConstants.COMMA);
                }
            }
        }
        if (needGroupBy) {
            groupbyExpression.append(groupbyTmpExpression.toString());
        }
        if (!" group by ".equals(groupbyExpression.toString())) {
            sqlExpression = groupbyExpression.toString().substring(
                    0,
                    groupbyExpression.toString()
                            .lastIndexOf(SqlConstants.COMMA));
        }
        return sqlExpression;
    }

    /**
     * 生成mysql sql语句
     * 
     * @param String
     *            finallySql
     * 
     */
    public String generatePageInfo(String finallySql) {
        StringBuffer limitStringBuffer = new StringBuffer();
        int start = 0;
        int size = -1;

        if (pageInfo == null) {
            return finallySql;
        } else {
            if (pageInfo.getCurrentPage() < 0) {
                pageInfo.setCurrentPage(0);
            }
            if (pageInfo.getPageSize() < 0) {
                pageInfo.setPageSize(0);
            }
            start = pageInfo.getCurrentPage() * pageInfo.getPageSize();
            size = pageInfo.getPageSize();
        }
        switch (driver) {
            case "com.mysql.jdbc.Driver": {
                limitStringBuffer.append(" limit ");
                limitStringBuffer.append(start);
                limitStringBuffer.append(SqlConstants.COMMA);
                limitStringBuffer.append(size);
                return finallySql + limitStringBuffer.toString();
            }
            case "Oracle.jdbc.driver.OracleDriver": {
                int end = -1;
                start = pageInfo.getCurrentPage() * pageInfo.getPageSize();
                end = start + pageInfo.getPageSize();
                StringBuffer pageString = new StringBuffer();
                pageString.append("SELECT * FROM (");
                pageString.append("SELECT A.*, ROWNUM RN");
                pageString
                        .append(" FROM (SELECT * FROM ( " + finallySql + " )) A ");
                pageString.append(" WHERE ROWNUM <= " + end);
                pageString.append(") WHERE RN >=  " + start);
                return pageString.toString();
            }
            default: {
                return finallySql;
            }
        }

    }

    /**
     * toPrintSql
     * 
     * @return sql String
     */
    public String toPrintSql() {
        if (this.where == null
                || CollectionUtils.isEmpty(this.where.getValues())) {
            return this.toSql();
        }
        String sql = this.toSql();
        List<Object> objects = this.where.getValues();
        String printSql = new String(sql);
        if (!StringUtils.isEmpty(printSql)) {
            for (Object value : objects) {
                if (value instanceof String) {
                    printSql = StringUtils.replaceOnce(printSql, "?", "'"
                            + value.toString() + "'");
                } else {
                    printSql = StringUtils.replaceOnce(printSql, "?",
                            value.toString());
                }
            }
            return printSql;
        } else {
            return "";
        }
    }

    /**
     * @return the driver
     */
    public String getDriver() {
        return driver;
    }

    /**
     * @param driver
     *            the driver to set
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * driver
     * 
     * @param driver
     */
    public SqlQuery(String driver) {
        this.driver = driver;
    }

    /**
     * driver
     * 
     * @param driver
     */
    public SqlQuery() {
        this.driver = "com.mysql.jdbc.Driver";
    }
}
