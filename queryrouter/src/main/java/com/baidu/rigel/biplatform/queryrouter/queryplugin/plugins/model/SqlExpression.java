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
package com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.query.model.AxisMeta.AxisType;
import com.baidu.rigel.biplatform.ac.query.model.ConfigQuestionModel;
import com.baidu.rigel.biplatform.ac.query.model.DimensionCondition;
import com.baidu.rigel.biplatform.ac.query.model.MeasureCondition;
import com.baidu.rigel.biplatform.ac.query.model.MetaCondition;
import com.baidu.rigel.biplatform.ac.query.model.PageInfo;
import com.baidu.rigel.biplatform.ac.query.model.QueryData;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.ac.query.model.SQLCondition;
import com.baidu.rigel.biplatform.ac.query.model.SQLCondition.SQLConditionType;
import com.baidu.rigel.biplatform.ac.util.MetaNameUtil;
import com.baidu.rigel.biplatform.queryrouter.handle.QueryRouterResource;
import com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.common.QuestionModel4TableDataUtils;

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
     * select sql
     */
    private String sql;

    /**
     * countSql
     */
    private String countSql;

    /**
     * whereValues
     */
    private List<Object> whereValues = new ArrayList<Object>();

    /**
     * jdbc driver
     */
    private String driver;

    /**
     * 事实表alias
     */
    private String facttableAlias;
    
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
    public void generateSql(QuestionModel questionModel,
            Map<String, SqlColumn> allColums, List<SqlColumn> needColums) throws QuestionModelTransformationException {
        if (CollectionUtils.isEmpty(needColums)) {
            throw new QuestionModelTransformationException("List needColums is empty, there is no SqlColum object available to generate.");
        }
        ConfigQuestionModel configQuestionModel = (ConfigQuestionModel) questionModel;
        MiniCube cube = (MiniCube) configQuestionModel.getCube();
        StringBuffer sqlExpression = new StringBuffer();
        try {
            sqlExpression.append(generateSelectExpression(needColums));
            sqlExpression.append(generateFromExpression(cube));
            sqlExpression.append(generateLeftOuterJoinExpression(
                    configQuestionModel, allColums, needColums));
            sqlExpression.append(generateWhereExpression(configQuestionModel,
                    allColums, whereValues));
            sqlExpression.append(generateOrderByExpression(configQuestionModel,
                    allColums));
            // set no paged sql
            this.setSql(sqlExpression.toString());
            // set total count sql
            this.setCountSql(" select count(1) as count from (" + this.getSql()
                    + ") t");
            generateTotalExpression(configQuestionModel.getPageInfo());
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
     * @param HashMap
     *            needColums
     * @return String sql
     */
    public String generateSelectExpression(List<SqlColumn> needColums) throws QuestionModelTransformationException {
        StringBuffer select = new StringBuffer("select ");
        for (SqlColumn colum : needColums) {
            String columName = colum.getTableName() + SqlConstants.DOT
                    + colum.getTableFieldName();
            String columNameSqlAlias = colum.getSqlUniqueColumn();
            if (AxisType.COLUMN == colum.getType()) {
                select.append(columName + " as " + columNameSqlAlias + SqlConstants.COMMA);
            } else if (AxisType.ROW == colum.getType()) {
                if (QuestionModel4TableDataUtils.isTimeOrCallbackDimension(colum.getDimension())) {
                    // 如果为时间维度
                    columName = this.facttableAlias + SqlConstants.DOT
                            + colum.getDimension().getFacttableColumn();
                }
                select.append(columName + " as " + columNameSqlAlias + SqlConstants.COMMA);
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
    public String generateFromExpression(MiniCube cube) throws QuestionModelTransformationException {
        String tableName = cube.getSource();
        if (StringUtils.isEmpty(tableName)) {
            throw new QuestionModelTransformationException("cube.getSource() can not be empty");
        }
        String[] tableNames =  tableName.split(",");
        String sqlFrom = "";
        if (tableNames.length == 1) {
        // 一个表
            sqlFrom = tableName;
        } else {
        // 多个表union的情况
            for (int i = 0; i < tableNames.length; i++) {
                if (i == 0) {
                    sqlFrom = " select * from " + tableNames[i];
                } else {
                    sqlFrom = sqlFrom + " union all select * from " + tableNames[i];
                }
            }
            sqlFrom = "(" + sqlFrom + ")";
        }
        return " from " + sqlFrom + SqlConstants.SPACE + this.facttableAlias + SqlConstants.SPACE;
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
            ConfigQuestionModel configQuestionModel,
            Map<String, SqlColumn> allColums, List<SqlColumn> needColums)
                    throws QuestionModelTransformationException {
        List<SqlColumn> needJoinColumns = new ArrayList<SqlColumn>();
        needJoinColumns.addAll(needColums);
        StringBuffer leftOuterJoinExpressions = new StringBuffer();
        HashSet<String> joinTables = new HashSet<String>();
        Set<Map.Entry<String, MetaCondition>> metaConditionSet = configQuestionModel
                .getQueryConditions().entrySet();
        MiniCube cube = (MiniCube) configQuestionModel.getCube();
        for (Entry<String, MetaCondition> entry : metaConditionSet) {
            MetaCondition metaCondition = entry.getValue();
            if (entry.getValue() instanceof DimensionCondition) {
                // 判断是维度查询
                DimensionCondition dimensionCondition = (DimensionCondition) metaCondition;
                String demensionName = dimensionCondition.getMetaName();
                if (dimensionCondition.getQueryDataNodes().isEmpty()) {
                    // 如果节点为空，不需要组织条件
                    continue;
                }
                Dimension dimension = cube.getDimensions().get(
                        dimensionCondition.getMetaName());
                if (QuestionModel4TableDataUtils.isTimeOrCallbackDimension(dimension)) {
                    // 如果为TIME_DIMENSION，name为指标字段
                    continue;
                }
                // 判断是或否有添加查询
                SqlColumn sqlColumn = allColums.get(demensionName);
                if (sqlColumn.getTableName().equals(this.facttableAlias)) {
                    // 如果为事实表不添加到jointable中
                    continue;
                }
                if (MetaNameUtil.isAllMemberUniqueName(dimensionCondition
                        .getQueryDataNodes().get(0).getUniqueName())) {
                    // 如果节点为所有条件，不需要组织条件
                    continue;
                }
                needJoinColumns.add(sqlColumn);
            }
        }
        for (SqlColumn colum : needJoinColumns) {
            if (AxisType.ROW == colum.getType()) {
                String dimTableName = colum.getTableName();
                if (QuestionModel4TableDataUtils.isTimeOrCallbackDimension(colum.getDimension())) {
                    // 如果为时间维度或callback维护，不需要leftouterjoin
                    continue;
                }
                if (this.facttableAlias.equals(dimTableName)) {
                    // 如果join table为facttable,跳过
                    continue;
                }
                if (joinTables.contains(dimTableName)) {
                    // 如果join的dimTableName已在sql中存在，
                    continue;
                }
                joinTables.add(dimTableName);
                StringBuffer oneLeftOuterJoinExpression = new StringBuffer(
                        " left outer join ");
                oneLeftOuterJoinExpression.append(dimTableName + SqlConstants.SPACE
                        + dimTableName + SqlConstants.SPACE);
                String dimTableFiled = dimTableName + SqlConstants.DOT
                        + colum.getLevel().getPrimaryKey();
                String measureTableFiled = this.facttableAlias + SqlConstants.DOT
                        + colum.getLevel().getFactTableColumn();
                oneLeftOuterJoinExpression.append(" on " + dimTableFiled
                        + " = " + measureTableFiled + SqlConstants.SPACE);
                leftOuterJoinExpressions.append(oneLeftOuterJoinExpression);
            }
        }
        return leftOuterJoinExpressions.toString();
    }

    /**
     * 生成where sql语句
     * 
     * @param ConfigQuestionModel
     *            configQuestionModel
     * @return String sql
     */
    public String generateWhereExpression(
            ConfigQuestionModel configQuestionModel,
            Map<String, SqlColumn> allColums, List<Object> whereValues) {
        StringBuffer whereExpressions = new StringBuffer(" where 1=1 ");
        configQuestionModel.getQueryConditions().forEach((k, v) -> {
            MetaCondition metaCondition = v;
            if (metaCondition instanceof DimensionCondition) {
                // 判断是维度查询
                DimensionCondition dimensionCondition = (DimensionCondition) metaCondition;
                if (// 如果节点为空，不需要组织条件
                    !CollectionUtils.isEmpty(dimensionCondition.getQueryDataNodes())
                    // 如果节点为所有条件，不需要组织条件
                    && !MetaNameUtil.isAllMemberUniqueName(dimensionCondition.getQueryDataNodes()
                                .get(0).getUniqueName())) {
                    // 判断是或否有添加查询
                    SqlColumn sqlColumn = allColums.get(dimensionCondition.getMetaName());
                    String equals = "";
                    String inExpression = "";
                    for (QueryData queryData : dimensionCondition.getQueryDataNodes()) {
                        String[] str = MetaNameUtil.parseUnique2NameArray(queryData.getUniqueName());
                        String value = str[str.length - 1];
                        inExpression = inExpression + SqlConstants.PARAM + ",";
                        this.whereValues.add(value);
                    }
                    if (!inExpression.isEmpty()) {
                        // 如果inExpression没有值，证明没有此条件
                        String columnSqName = sqlColumn.getTableName() + SqlConstants.DOT
                                + sqlColumn.getTableFieldName();
                        equals = " in ("
                                + inExpression.substring(0, inExpression.lastIndexOf(SqlConstants.COMMA)) + ")";
                        whereExpressions.append(" and " + columnSqName + equals + SqlConstants.SPACE);
                    }
                }
            } else if (metaCondition instanceof MeasureCondition) {
                // 判断是指标查询
                MeasureCondition measureCondition = (MeasureCondition) metaCondition;
                if (measureCondition.getMeasureConditions() != null) {
                    // 判断是或否有添加查询
                    SqlColumn sqlColumn = allColums.get(measureCondition
                            .getMetaName());
                    SQLCondition sqlCondition = measureCondition
                            .getMeasureConditions();
                    String columnSqName = sqlColumn.getTableName() + SqlConstants.DOT
                            + sqlColumn.getTableFieldName();
                    String whereEquals = this.generateWhereEquals(sqlCondition);
                    if (!StringUtils.isEmpty(whereEquals)) {
                        whereExpressions.append(" and " + columnSqName + whereEquals + SqlConstants.SPACE);
                    }
                }
            }
        });
        return whereExpressions.toString();
    }
    
    /**
     * 获取where equals等式
     * 
     * @param sqlCondition
     *            sqlCondition
     * @return where equals sting
     */
    public String generateWhereEquals(SQLCondition sqlCondition) {
        if (sqlCondition == null || sqlCondition.getConditionValues() == null
                || sqlCondition.getConditionValues().size() == 0) {
            return "";
        }
        SQLConditionType conditionType = sqlCondition.getCondition();
        switch (conditionType) {
            // 等于
            case EQ: {
                this.whereValues.add(sqlCondition.getConditionValues().get(0));
                return " = " + SqlConstants.PARAM;
            }
            // 不等于
            case NOT_EQ: {
                this.whereValues.add(sqlCondition.getConditionValues().get(0));
                return " <> " + SqlConstants.PARAM;
            }
            // 小于
            case LT: {
                this.whereValues.add(sqlCondition.getConditionValues().get(0));
                return " < " + SqlConstants.PARAM;
            }
            // 小于等于
            case LT_EQ: {
                this.whereValues.add(sqlCondition.getConditionValues().get(0));
                return " <= " + SqlConstants.PARAM;
            }
            // 大于
            case GT: {
                this.whereValues.add(sqlCondition.getConditionValues().get(0));
                return " > " + SqlConstants.PARAM;
            }
            // 大于等于
            case GT_EQ: {
                this.whereValues.add(sqlCondition.getConditionValues().get(0));
                return " >= " + SqlConstants.PARAM;
            }
            // between and
            case BETWEEN_AND: {
                if (StringUtils.isNumeric(sqlCondition.getConditionValues().get(0))
                        && StringUtils.isNumeric(sqlCondition.getConditionValues().get(1))) {
                    BigDecimal front = new BigDecimal(sqlCondition.getConditionValues().get(0));
                    BigDecimal back = new BigDecimal(sqlCondition.getConditionValues().get(1));
                    if (front.compareTo(back) >= 0) {
                        this.whereValues.add(back);
                        this.whereValues.add(front);
                    } else {
                        this.whereValues.add(front);
                        this.whereValues.add(back);
                    }
                    return " between ? and ?";
                } else {
                    this.whereValues.add(sqlCondition.getConditionValues().get(0));
                    this.whereValues.add(sqlCondition.getConditionValues().get(1));
                }
                return " between " + SqlConstants.PARAM + " and " + SqlConstants.PARAM;
            }
            // in
            case IN: {
                StringBuffer inExpression = new StringBuffer(" in (");
                for (String value : sqlCondition.getConditionValues()) {
                    this.whereValues.add(value);
                    inExpression.append(SqlConstants.PARAM + SqlConstants.COMMA);
                }
                return inExpression.toString().substring(0,
                        inExpression.toString().lastIndexOf(SqlConstants.COMMA))
                        + ")";
            }
            // like
            case LIKE: {
                if (SqlConstants.LIKE_ALL.equals(sqlCondition.getConditionValues().get(0))) {
                    return "";
                }
                this.whereValues.add(sqlCondition.getConditionValues().get(0));
                return " like " + SqlConstants.PARAM;
            }
            default: {
                return "";
            }
        }
    }

    /**
     * 生成groupby sql语句
     * 
     * @param HashMap
     *            allColums
     * @param HashMap
     *            needColums
     * @return String sql
     */
    public String generateGroupByExpression(List<SqlColumn> needColums) {
        StringBuffer groupbyExpression = new StringBuffer(" group by ");
        for (SqlColumn colum : needColums) {
            if (AxisType.ROW == colum.getType()) {
                String columName = "";
                if (QuestionModel4TableDataUtils.isTimeOrCallbackDimension(colum.getDimension())) {
                    // 如果为时间维度
                    columName = this.facttableAlias + SqlConstants.DOT
                            + colum.getDimension().getFacttableColumn();
                    groupbyExpression.append(columName + SqlConstants.COMMA);
                } else {
                    // 如果为其他维度
                    String dimTableName = colum.getTableName();
                    groupbyExpression.append(dimTableName + SqlConstants.DOT
                            + colum.getTableFieldName() + SqlConstants.COMMA);
                }
            } else if (AxisType.COLUMN == colum.getType()) {
                return "";
            }
        }
        if (" group by ".equals(groupbyExpression.toString())) {
            return "";
        } else {
            return groupbyExpression.toString().substring(0,
                    groupbyExpression.toString().lastIndexOf(SqlConstants.COMMA));
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
            ConfigQuestionModel configQuestionModel,
            Map<String, SqlColumn> allColums) throws QuestionModelTransformationException {
        if (configQuestionModel.getSortRecord() == null) {
            return "";
        }
        String orderColumnNameTmp = configQuestionModel.getSortRecord()
                .getSortColumnUniquename();
        if (StringUtils.isEmpty(orderColumnNameTmp) && MetaNameUtil.isUniqueName(orderColumnNameTmp)) {
            throw new QuestionModelTransformationException("string of 'SortColumnUniquename' isn't well fromed as [data].[data] .");
        }
        String[] str = MetaNameUtil.parseUnique2NameArray(orderColumnNameTmp);
        String orderColumnName = str[str.length - 1];
        SqlColumn sqlColumn = allColums.get(orderColumnName);
        String orderByType = "";
        if (SqlConstants.DESC.equals(configQuestionModel.getSortRecord().getSortType().name())) {
            orderByType = SqlConstants.DESC;
        }
        return " order by " + sqlColumn.getTableName() + SqlConstants.DOT
                + sqlColumn.getTableFieldName() + SqlConstants.SPACE
                + orderByType + SqlConstants.SPACE;
    }

    /**
     * 生成mysql sql语句
     * 
     * @param PageInfo
     *            pageInfo
     * 
     */
    public void generateTotalExpression(PageInfo pageInfo) {
        StringBuffer limitStringBuffer = new StringBuffer();
        int start = 0;
        int size = -1;

        if (pageInfo == null) {
            return;
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
                this.setSql(this.getSql() + limitStringBuffer.toString());
                return;
            }
            case "Oracle.jdbc.driver.OracleDriver": {
                int end = -1;
                start = pageInfo.getCurrentPage() * pageInfo.getPageSize();
                end = start + pageInfo.getPageSize();
                StringBuffer pageString = new StringBuffer();
                pageString.append("SELECT * FROM (");
                pageString.append("SELECT A.*, ROWNUM RN");
                pageString.append(" FROM (SELECT * FROM ( " + this.getSql()
                            + " )) A ");
                pageString.append(" WHERE ROWNUM <= " + end);
                pageString.append(") WHERE RN >=  " + start);
                this.setSql(pageString.toString());
                return;
            }
            default: {
                return;
            }
        }

    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString(){
        String sql = this.getSql();
        if (!StringUtils.isEmpty(this.getSql())) {
            for(Object value : this.whereValues){
                sql = StringUtils.replaceOnce(sql, "?", "'" + value.toString() + "'");
            }
            return sql;
        } else {
            return "";
        }    
    }

    /**
     * getSql
     * 
     * @return sql sql
     */
    public String getSql() {
        return sql;
    }

    /**
     * setSql
     * 
     * @param sql
     *            sql
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * getCountSql
     * 
     * @return countSql countSql
     */
    public String getCountSql() {
        return countSql;
    }

    /**
     * countSql
     * 
     * @param countSql
     *            countSql
     */
    public void setCountSql(String countSql) {
        this.countSql = countSql;
    }

    /**
     * getDriver
     * 
     * @return String String
     */
    public String getDriver() {
        return driver;
    }

    /**
     * setDriver
     * 
     * @param driver
     *            driver
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * @param driver
     *            jdbc driver
     * @param facttableAlias
     *            facttableAlias facttableAlias
     */
    public SqlExpression(String driver) {
        this.driver = driver;
        this.facttableAlias = QuestionModel4TableDataUtils.getFactTableAliasName();
    }

    /**
     * @return the whereValues
     */
    public List<Object> getWhereValues() {
        return whereValues;
    }
}