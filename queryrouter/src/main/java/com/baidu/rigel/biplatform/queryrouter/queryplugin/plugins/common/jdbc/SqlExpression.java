/**
 * Copyright (c) 2014 Baidu, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.common.jdbc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.rigel.biplatform.ac.query.model.MetaCondition;
import com.baidu.rigel.biplatform.ac.query.model.SQLCondition;
import com.baidu.rigel.biplatform.ac.query.model.SQLCondition.SQLConditionType;
import com.baidu.rigel.biplatform.ac.util.MetaNameUtil;
import com.baidu.rigel.biplatform.queryrouter.handle.QueryRouterResource;
import com.baidu.rigel.biplatform.queryrouter.operator.OperatorType;
import com.baidu.rigel.biplatform.queryrouter.operator.OperatorUtils;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.common.PlaneTableQuestionModel2SqlColumnUtils;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.ColumnCondition;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.ColumnType;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.PlaneTableQuestionModel;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.QuestionModelTransformationException;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlColumn;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlConstants;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.SqlQuery;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model.Where;

/**
 * 
 * Description: sql数据组织类
 * 
 * @author 罗文磊
 *
 */
public class SqlExpression implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3151301875582323398L;

    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(QueryRouterResource.class);

    /**
     * where 1=2
     */
    private static final String WHERE_NO_DATA = " 1=2 ";
    
    /**
     * sqlQuery
     */
    private SqlQuery sqlQuery;
    
    /**
     * sqlQuery
     */
    private SqlQuery countSqlQuery;
    
    /**
     * allColumns
     */
    private Map<String, SqlColumn> allColumns;
    
    /**
     * jdbc driver
     */
    private String driver;

    /**
     * 事实表alias
     */
    private String facttableAlias;

    /**
     * 事实表名称
     */
    private String tableName;

    /**
     * 生成组织sql string
     * 
     * @param questionModel
     *            questionModel
     * @param allColums
     *            allColums
     * @param needColums
     *            needColums
     * @return String sql
     */
    public void generateSql(PlaneTableQuestionModel questionModel,
            Map<String, SqlColumn> allColums, List<SqlColumn> needColums)
            throws QuestionModelTransformationException {
        if (CollectionUtils.isEmpty(needColums)) {
            throw new QuestionModelTransformationException(
                    "List needColums is empty, there is no SqlColum object available to generate.");
        }
        try {
            sqlQuery.getSelect().setSql(generateSelectExpression(sqlQuery, needColums, true));
            sqlQuery.getFrom().setSql(generateFromExpression(questionModel, sqlQuery, allColums));
            sqlQuery.getJoin().setSql(generateLeftOuterJoinExpression(
                    questionModel, allColums, needColums));
            sqlQuery.getWhere().setSql(generateTotalWhereExpression(
                    questionModel, sqlQuery, allColums));
            sqlQuery.getOrderBy().setSql(generateOrderByExpression(questionModel, allColums));
            sqlQuery.setPageInfo(questionModel.getPageInfo());
            // set no paged sql
        } catch (Exception e) {
            logger.error("occur sql exception:{}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 生成组织sql count string
     * 
     * @param questionModel
     *            questionModel
     * @param allColums
     *            allColums
     * @param needColums
     *            needColums
     * @param whereData
     *            whereData
     * @return String sql
     */
    public void generateCountSql(PlaneTableQuestionModel questionModel,
            Map<String, SqlColumn> allColums, List<SqlColumn> needColums, Map<String, List<Object>> whereData)
            throws QuestionModelTransformationException {
        if (CollectionUtils.isEmpty(needColums)) {
            throw new QuestionModelTransformationException(
                    "List needColums is empty, there is no SqlColum object available to generate.");
        }
        try {
            countSqlQuery.getSelect().setSql(" select 1 ");
            countSqlQuery.getSelect().setSelectList(needColums);
            countSqlQuery.getFrom().setSql(generateFromExpression(questionModel, countSqlQuery, allColums));
            countSqlQuery.getWhere().setSql(
                    generateCountTotalWhereExpression(questionModel, allColums, SQLConditionType.IN, whereData));
        } catch (Exception e) {
            logger.error("occur sql exception:{}", e.getMessage());
            throw e;
        }
    }

    /**
     * 生成select sql语句
     * 
     * @param HashMap
     *            allColums
     * @param hasAlias
     *            是否添加alias
     * @param onlyFacttableColumns
     *            是否只需要事实表的字段
     * @return String sql
     */
    public String generateSelectExpression(SqlQuery sqlQuery, List<SqlColumn> needColums, boolean contentJoinSelect) throws QuestionModelTransformationException {
        sqlQuery.getSelect().setSelectList(needColums);
        StringBuffer select = new StringBuffer("select ");
        for (SqlColumn colum : needColums) {
            if (ColumnType.JOIN == colum.getType() && contentJoinSelect) {
                // join表字段
                select.append(colum.getTableName() + SqlConstants.DOT + colum.getTableFieldName()
                        + " as " + colum.getSqlUniqueColumn() + SqlConstants.COMMA);
            } else if (ColumnType.TIME == colum.getType() || ColumnType.CALLBACK == colum.getType()){
                // 如果为时间或cb字段
                select.append(facttableAlias + SqlConstants.DOT + colum.getFactTableFieldName()
                        + " as " + colum.getSqlUniqueColumn() + SqlConstants.COMMA);
            } else {
                // 普通事实表字段
                if (!StringUtils.isEmpty(colum.getOperator())
                        && OperatorUtils.getOperatorType(colum) == OperatorType.AGG) {
                    // 需要聚合
                    select.append(colum.getOperator() + "(" + facttableAlias + SqlConstants.DOT + colum.getTableFieldName()
                            + ") as " + colum.getSqlUniqueColumn() + SqlConstants.COMMA);
                } else {
                    select.append(facttableAlias + SqlConstants.DOT + colum.getTableFieldName()
                            + " as " + colum.getSqlUniqueColumn() + SqlConstants.COMMA);
                }
            }
        }
        return select.toString().substring(0,
                select.toString().lastIndexOf(SqlConstants.COMMA))
                + SqlConstants.SPACE;
    }

    /**
     * 生成from sql语句
     * 
     * @param MiniCube
     *            cube
     * @return String sql
     */
    public String generateFromExpression(
            PlaneTableQuestionModel planeTableQuestionModel, SqlQuery sqlQuery, Map<String, SqlColumn> allColums)
            throws QuestionModelTransformationException {
        if (StringUtils.isEmpty(tableName)) {
            throw new QuestionModelTransformationException(
                    "cube.getSource() can not be empty");
        }
        String[] tableNames = tableName.split(",");
        if (tableNames.length == 1) {
        // 为单表
            String from = " from " + tableNames[0] + SqlConstants.SPACE
                    + this.facttableAlias + SqlConstants.SPACE;
            return from;
        }
        // 此处处理多表的情况
        StringBuffer sqlFrom = new StringBuffer("");
        for (int i = 0; i < tableNames.length; i++) {
            StringBuffer tmpSqlFrom = new StringBuffer(" select * from ");
            tmpSqlFrom.append(tableNames[i]);
            tmpSqlFrom.append(" where 1=1 ");
            tmpSqlFrom.append(this.generateSourceWhereExpression(planeTableQuestionModel, sqlQuery, allColums));
            if (i == 0) {
                sqlFrom.append(tmpSqlFrom.toString());
            } else {
                sqlFrom.append(" union all " + tmpSqlFrom.toString());
            }
        }
        return " from (" + sqlFrom.toString() + ")" + SqlConstants.SPACE
                + this.facttableAlias + SqlConstants.SPACE;
    }

    /**
     * 生成leftouterjoin sql语句
     * 
     * @param HashMap
     *            allColums
     * @param HashMap
     *            needColums
     * 
     * @return String sql
     */
    public String generateLeftOuterJoinExpression(
            PlaneTableQuestionModel planeTableQuestionModel,
            Map<String, SqlColumn> allColums, List<SqlColumn> needColums)
            throws QuestionModelTransformationException {
        Set<SqlColumn> needJoinColumns = new HashSet<SqlColumn>();
        needJoinColumns.addAll(needColums);
        StringBuffer leftOuterJoinExpressions = new StringBuffer();
        HashSet<String> joinTables = new HashSet<String>();
        Map<String, MetaCondition> metaConditionMap = planeTableQuestionModel
                .getQueryConditions();
        // 添加Where中有字段，而select中没有的sqlcolumns
        metaConditionMap.forEach((k, v) -> {
            SqlColumn column = allColums.get(k);
            if (ColumnType.TIME != column.getType() 
                    // 如果不为TIME CALLBACK字段
                    && ColumnType.CALLBACK != column.getType()
                    // 如果不为事实表join
                    && !column.getTableName().equals(this.facttableAlias)) {
                needJoinColumns.add(column);
            }
        });
        for (SqlColumn colum : needJoinColumns) {
            String joinTable = colum.getTableName();
            if (joinTables.contains(joinTable)) {
                // 如果join的dimTableName已在sql中存在，
                continue;
            }
            if (ColumnType.JOIN == colum.getType()) {
                // 如果为JOIN 字段
                 joinTables.add(joinTable);
                 StringBuffer oneLeftOuterJoinExpression = new StringBuffer(" left outer join ");
                 oneLeftOuterJoinExpression.append(joinTable
                         + SqlConstants.SPACE + joinTable
                         + SqlConstants.SPACE);
                 String tableFiled = joinTable + SqlConstants.DOT
                         + colum.joinTableFieldName;
                 String sourceTableFiled = this.facttableAlias
                         + SqlConstants.DOT
                         + colum.factTableFieldName;
                 oneLeftOuterJoinExpression.append(" on " + tableFiled
                         + " = " + sourceTableFiled + SqlConstants.SPACE);
                 leftOuterJoinExpressions.append(oneLeftOuterJoinExpression);
            }
        }
        return leftOuterJoinExpressions.toString();
    }

    /** 
     * 生成generateTotalWhereExpression ,Dimension only where sql语句,如果维度表为事实表，将会过滤
     * 
     * @param ConfigQuestionModel
     *            configQuestionModel
     * @return String sql
     */
    public String generateTotalWhereExpression(
            PlaneTableQuestionModel planeTableQuestionModel, SqlQuery sqlQuery,
            Map<String, SqlColumn> allColums) {
        StringBuffer whereExpressions = new StringBuffer(" where 1=1 ");
        planeTableQuestionModel
                .getQueryConditions()
                .forEach((k, v) -> {
                    MetaCondition metaCondition = v;
                    if (metaCondition instanceof ColumnCondition) {
                        // 判断是维度查询
                        ColumnCondition columnCondition = (ColumnCondition) metaCondition;
                        SqlColumn sqlColumn = allColums.get(columnCondition
                                .getMetaName());
                        if (sqlColumn.getType() == ColumnType.JOIN) {
                            whereExpressions.append(this.generateSqlWhereOneCondition(sqlColumn, sqlQuery, true));
                        }
                    }
                });
        whereExpressions.append(this.generateSingleTableSourceWhere(planeTableQuestionModel, sqlQuery, allColums));
        return whereExpressions.toString();
    }

    /**
     * 生成generateCountTotalWhereExpression
     * 
     * @param SQLConditionType
     *            SQLConditionType
     * @return String sql
     */
    public String generateCountTotalWhereExpression(PlaneTableQuestionModel planeTableQuestionModel,
            Map<String, SqlColumn> allColums, SQLConditionType sqlConditionType,
                    Map<String, List<Object>> whereData) {
        StringBuffer whereExpressions = new StringBuffer(" where 1=1 ");
        whereData.forEach((k, v) -> {
            Where where = this.getWhereEquals(sqlConditionType, v);
            if (StringUtils.isNotEmpty(where.getSql())) {
                whereExpressions.append(" and " + k + " ");
                whereExpressions.append(where.getSql());
            } else {
                whereExpressions.append(" and " + WHERE_NO_DATA);
            }
            this.countSqlQuery.getWhere().getValues().addAll(where.getValues());
        });
        whereExpressions.append(this.generateSingleTableSourceWhere(
                planeTableQuestionModel, this.countSqlQuery, allColums));
        return whereExpressions.toString();
    }

    /**
     * 查询单个表的where
     * 
     * @param planeTableQuestionModel planeTableQuestionModel
     * @param allColums allColums
     * @return where sql
     */
    public String generateSingleTableSourceWhere(PlaneTableQuestionModel planeTableQuestionModel,
            SqlQuery sqlQuery, Map<String, SqlColumn> allColums) {
        if (StringUtils.isNotEmpty(tableName)) {
            // 为单表的情况下需要添加where
            String[] tableNames = tableName.split(",");
            if (tableNames.length == 1) {
                return this
                        .generateSourceWhereExpression(planeTableQuestionModel, sqlQuery, allColums);
            }
        }
        return "";
    }

    /**
     * 生成Source(Form里面的where sql) where sql语句
     * 
     * @param ConfigQuestionModel
     *            configQuestionModel
     * @return String sql
     */
    public String generateSourceWhereExpression(
            PlaneTableQuestionModel planeTableQuestionModel, SqlQuery sqlQuery,
            Map<String, SqlColumn> allColums) {
        StringBuffer whereExpressions = new StringBuffer("");
        
        planeTableQuestionModel.getQueryConditions()
                .forEach((k, v) -> {
                    ColumnCondition columnCondition = (ColumnCondition) v;
                    SqlColumn sqlColumn = allColums.get(k);
                    if (ColumnType.COMMON == sqlColumn.getType()
                            || ColumnType.TIME == sqlColumn.getType()
                            || ColumnType.CALLBACK == sqlColumn.getType()) {
                        // 判断是事实表查询
                        if (columnCondition != null) {
                            whereExpressions.append(this.generateSqlWhereOneCondition(sqlColumn, sqlQuery, false));
                        }
                    }
                });
        return whereExpressions.toString();
    }

    /**
     * 生成维度sqlwhere表达式
     * 
     * @param dimensionCondition
     *            dimensionCondition
     * @param sqlColumn
     *            sqlColumn
     * @return dimensionCondition where sql
     */
    public String generateSqlWhereOneCondition(SqlColumn sqlColumn, SqlQuery sqlQuery,
            boolean gengerateTableAlias) {
        ColumnCondition columnCondition = sqlColumn.getColumnCondition();
        if (columnCondition != null) {
        // 判断是或否有添加查询
            SQLCondition sqlCondition = columnCondition.getColumnConditions();
            String columnSqName = sqlColumn.getTableFieldName();
            if (gengerateTableAlias) {
                if (ColumnType.TIME == sqlColumn.getType()) {
                    columnSqName = this.facttableAlias + SqlConstants.DOT + sqlColumn.getFactTableFieldName();
                } else {
                    columnSqName = sqlColumn.getTableName() + SqlConstants.DOT + columnSqName;
                }
            }
            String whereEquals = this.generateWhereEquals(sqlCondition, sqlQuery, sqlColumn.getDataType());
            if (!StringUtils.isEmpty(whereEquals)) {
                if (WHERE_NO_DATA == whereEquals) {
                    return " and " + WHERE_NO_DATA;
                }
                if (ColumnType.TIME == sqlColumn.getType()) {
                    return " and " + sqlColumn.getFactTableFieldName() + whereEquals + SqlConstants.SPACE;
                } else {
                    return " and " + columnSqName + whereEquals + SqlConstants.SPACE;
                }
            }
        }
        return "";
    }

    /**
     * 获取where equals等式
     * 
     * @param sqlCondition
     *            sqlCondition
     * @return where equals sting
     */
    public String generateWhereEquals(SQLCondition sqlCondition, SqlQuery sqlQuery, String dateType) {
        if (sqlCondition == null || CollectionUtils.isEmpty(sqlCondition.getConditionValues())) {
            return "";
        }
        List<Object> values = new ArrayList<Object>();
        for (String value : sqlCondition.getConditionValues()) {
            if (StringUtils.isEmpty(dateType)
                    || dateType.toLowerCase().startsWith("varchar")
                    || dateType.toLowerCase().startsWith("char")) {
                values.add(value);
            } else {
                try {
                    values.add(Double.valueOf(value));
                } catch (Exception e) {
                    return WHERE_NO_DATA;
                }
            }
        }
        Where where = this.getWhereEquals(sqlCondition.getCondition(), values);
        sqlQuery.getWhere().getValues().addAll(where.getValues());
        return where.getSql();
    }

    /**
     * 生成one where
     * 
     * @param conditionType conditionType
     * @param values values
     * @return sql string
     */
    public Where getWhereEquals(SQLConditionType conditionType, List<Object> values){
        Where where = new Where();
        if (CollectionUtils.isEmpty(values)) {
            return where;
        }
        switch (conditionType) {
        // 等于
        case EQ: {
            where.getValues().add(values.get(0));
            where.setSql(" = " + SqlConstants.PARAM);
            break;
        }
        // 不等于
        case NOT_EQ: {
            where.getValues().add(values.get(0));
            where.setSql(" <> " + SqlConstants.PARAM);
            break;
        }
        // 小于
        case LT: {
            where.getValues().add(values.get(0));
            where.setSql(" < " + SqlConstants.PARAM);
            break;
        }
        // 小于等于
        case LT_EQ: {
            where.getValues().add(values.get(0));
            where.setSql(" <= " + SqlConstants.PARAM);
            break;
        }
        // 大于
        case GT: {
            where.getValues().add(values.get(0));
            where.setSql(" > " + SqlConstants.PARAM);
            break;
        }
        // 大于等于
        case GT_EQ: {
            where.getValues().add(values.get(0));
            where.setSql(" >= " + SqlConstants.PARAM);
            break;
        }
        // between and
        case BETWEEN_AND: {
            if (StringUtils.isNumeric(values.get(0).toString())
                    && StringUtils.isNumeric(values
                            .get(1).toString())) {
                BigDecimal front = new BigDecimal(values.get(0).toString());
                BigDecimal back = new BigDecimal(values.get(1).toString());
                if (front.compareTo(back) >= 0) {
                    where.getValues().add(back);
                    where.getValues().add(front);
                } else {
                    where.getValues().add(front);
                    where.getValues().add(back);
                }
                where.setSql(" between ? and ?");
                break;
            } else {
                where.getValues().add(values.get(0));
                where.getValues().add(values.get(1));
            }
            where.setSql(" between " + SqlConstants.PARAM + " and "
                    + SqlConstants.PARAM);
            break;
        }
        // in
        case IN: {
            StringBuffer inExpression = new StringBuffer(" in (");
            for (Object value : values) {
                where.getValues().add(value);
                inExpression.append(SqlConstants.PARAM + SqlConstants.COMMA);
            }
            where.setSql(inExpression.toString().substring(0,
                    inExpression.toString().lastIndexOf(SqlConstants.COMMA))
                    + ")");
            break;
        }
        // like
        case LIKE: {
            if (SqlConstants.LIKE_ALL.equals(values.get(0))) {
                where.setSql("");
                break;
            }
            where.getValues().add(values.get(0));
            where.setSql(" like " + SqlConstants.PARAM);
            break;
        }
        default: {
            where.setSql("");
        }
        }
        return where;
    }

    /**
     * 生成groupby sql语句
     * 
     * @param HashMap
     *            needColums
     * @return String sql
     */
    public String generateGroupByExpression(List<SqlColumn> needColums) {
        boolean needGroupBy = false;
        StringBuffer groupbyTmpExpression = new StringBuffer("");
        StringBuffer groupbyExpression = new StringBuffer(" group by ");
        for (SqlColumn colum : needColums) {
            if (!StringUtils.isEmpty(colum.getOperator())
                    && OperatorUtils.getOperatorType(colum) == OperatorType.AGG) {
                needGroupBy = true;    
            } else {
                if (ColumnType.JOIN == colum.getType()) {
                    // 如果为时间字段
                    String columName = colum.getTableName();
                    groupbyTmpExpression.append(columName + SqlConstants.DOT
                            + colum.getTableFieldName() + SqlConstants.COMMA);
                } else {
                    // 如果为其他字段
                    String columName = this.facttableAlias + SqlConstants.DOT
                            + colum.getFactTableFieldName();
                    groupbyTmpExpression.append(columName + SqlConstants.COMMA);
                } 
            }
        }
        if (needGroupBy) {
            groupbyExpression.append(groupbyTmpExpression.toString());
        }
        if (" group by ".equals(groupbyExpression.toString())) {
            return "";
        } else {
            return groupbyExpression.toString().substring(
                    0,
                    groupbyExpression.toString()
                            .lastIndexOf(SqlConstants.COMMA));
        }

    }

    /**
     * 生成orderby sql语句
     * 
     * @param ConfigQuestionModel
     *            configQuestionModel
     * @param HashMap
     *            allColums
     * @return String orderby sql
     */
    public String generateOrderByExpression(
            PlaneTableQuestionModel planeTableQuestionModel,
            Map<String, SqlColumn> allColums)
            throws QuestionModelTransformationException {
        if (planeTableQuestionModel.getSortRecord() == null) {
            return "";
        }
        String orderColumnNameTmp = planeTableQuestionModel.getSortRecord()
                .getSortColumnUniquename();
        if (!MetaNameUtil.isUniqueName(orderColumnNameTmp)) {
            throw new QuestionModelTransformationException(
                    "string of 'SortColumnUniquename' isn't well fromed as [data].[data] .");
        }
        SqlColumn sqlColumn = allColums.get(orderColumnNameTmp);
        String orderByType = "";
        if (SqlConstants.DESC.equals(planeTableQuestionModel.getSortRecord()
                .getSortType().name())) {
            orderByType = SqlConstants.DESC;
        }
        String tableName = this.facttableAlias;
        String fieldName = sqlColumn.getTableFieldName();
        if (ColumnType.JOIN == sqlColumn.getType()) {
            tableName = sqlColumn.getTableName();
        }
        if (ColumnType.TIME == sqlColumn.getType()) {
            fieldName = sqlColumn.getFactTableFieldName();
        }
        return " order by " + tableName + SqlConstants.DOT
                + fieldName + SqlConstants.SPACE
                + orderByType + SqlConstants.SPACE;
    }

    /**
     * @param driver
     *            jdbc driver
     * @param facttableAlias
     *            facttableAlias facttableAlias
     */
    public SqlExpression(String driver) {
        this.setDriver(driver);
        sqlQuery = new SqlQuery(this.driver);
        countSqlQuery = new SqlQuery(this.driver);
        this.facttableAlias = PlaneTableQuestionModel2SqlColumnUtils
                .getFactTableAliasName();
    }


    /**
     * @param tableName
     *            the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the sqlQuery
     */
    public SqlQuery getSqlQuery() {
        return sqlQuery;
    }

    /**
     * @param sqlQuery the sqlQuery to set
     */
    public void setSqlQuery(SqlQuery sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    /**
     * @return the countSqlQuery
     */
    public SqlQuery getCountSqlQuery() {
        return countSqlQuery;
    }

    /**
     * @param countSqlQuery the countSqlQuery to set
     */
    public void setCountSqlQuery(SqlQuery countSqlQuery) {
        this.countSqlQuery = countSqlQuery;
    }

    /**
     * getAllColumns
     * 
     * @return the allColumns
     */
    public Map<String, SqlColumn> getAllColumns() {
        return allColumns;
    }

    /**
     * setDriver
     * 
     * @param driver the driver to set
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * setAllColumns
     * 
     * @param allColumns the allColumns to set
     */
    public void setAllColumns(Map<String, SqlColumn> allColumns) {
        this.allColumns = allColumns;
    }
}