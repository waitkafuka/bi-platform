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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.DimensionType;
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

/**
 * 
 * Description: sql数据组织类
 * 
 * @author 罗文磊
 *
 */
public class SqlExpression implements Serializable {
    
    @SuppressWarnings("unused")
    private SqlExpression() {
    }
    
    /**
     * @param driver
     *            jdbc driver
     */
    public SqlExpression(String driver) {
        this.driver = driver;
    }
    
    /**
     * select sql
     */
    private String sql;
    
    /**
     * countSql
     */
    private String countSql;
    
    /**
     * jdbc driver
     */
    private String driver;
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3151301875582323398L;
    
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
    public void generateSql(QuestionModel questionModel, HashMap<String, SqlColumn> allColums,
            List<SqlColumn> needColums) {
        ConfigQuestionModel configQuestionModel = (ConfigQuestionModel) questionModel;
        MiniCube cube = (MiniCube) configQuestionModel.getCube();
        StringBuffer sqlExpression = new StringBuffer();
        sqlExpression.append(generateSelectExpression(needColums));
        sqlExpression.append(generateFromExpression(cube));
        sqlExpression.append(generateLeftOuterJoinExpression(needColums));
        sqlExpression.append(generateWhereExpression(configQuestionModel, allColums));
        sqlExpression.append(generateGroupByExpression(needColums));
        sqlExpression.append(generateOrderByExpression(configQuestionModel, allColums));
        // set no paged sql
        this.setSql(sqlExpression.toString());
        // set total count sql
        this.setCountSql(" select count(1) as count from (" + this.getSql() + ") t");
        generateTotalExpression(configQuestionModel.getPageInfo());
    }
    
    /**
     * 事实表物理表名
     * 
     * @param cube
     *            cube
     * @return String tablename
     */
    public String getFactTableName(MiniCube cube) {
        return cube.getSource();
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
    public String generateSelectExpression(List<SqlColumn> needColums) {
        StringBuffer select = new StringBuffer("select ");
        for (SqlColumn colum : needColums) {
            String columName = colum.getTableName() + "." + colum.getTableFieldName();
            String columNameSqlAlias = colum.getSqlUniqueColumn();
            if (AxisType.COLUMN == colum.getType()) {
                // TODO
                // if (StringUtils.isEmpty(colum.getMeasure().getAggregator()))
                // {
                if (false) {
                    String aggregator = colum.getMeasure().getAggregator().toString();
                    columName = aggregator + "(" + columName + ")";
                }
                select.append(columName + " as " + columNameSqlAlias + ",");
            } else if (AxisType.ROW == colum.getType()) {
                if (colum.getDimension().getType() == DimensionType.TIME_DIMENSION) {
                    // 如果为时间维度
                    columName = colum.getFactTableName() + "."
                            + colum.getDimension().getFacttableColumn();
                }
                select.append(columName + " as " + columNameSqlAlias + ",");
            }
        }
        return select.toString().substring(0, select.toString().lastIndexOf(",")) + " ";
    }
    
    /**
     * 生成from sql语句
     * 
     * @param MiniCube
     *            cube
     * @return String sql
     */
    public String generateFromExpression(MiniCube cube) {
        String tableName = this.getFactTableName(cube);
        return " from " + tableName + " " + tableName + " ";
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
    public String generateLeftOuterJoinExpression(List<SqlColumn> needColums) {
        StringBuffer leftOuterJoinExpressions = new StringBuffer();
        for (SqlColumn colum : needColums) {
            if (AxisType.ROW == colum.getType()) {
                if (colum.getDimension().getType() == DimensionType.TIME_DIMENSION) {
                    // 如果为时间维度，不需要leftouterjoin
                    continue;
                }
                StringBuffer oneLeftOuterJoinExpression = new StringBuffer(" left outer join ");
                String dimTableName = colum.getTableName();
                oneLeftOuterJoinExpression.append(dimTableName + " " + dimTableName + " ");
                String dimTableFiled = dimTableName + "." + colum.getLevel().getPrimaryKey();
                String measureTableFiled = colum.getFactTableName() + "."
                        + colum.getLevel().getFactTableColumn();
                oneLeftOuterJoinExpression.append(" on " + dimTableFiled + " = " + measureTableFiled
                        + " ");
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
    public String generateWhereExpression(ConfigQuestionModel configQuestionModel,
            HashMap<String, SqlColumn> allColums) {
        
        Set<Map.Entry<String, MetaCondition>> metaConditionSet = configQuestionModel
                .getQueryConditions().entrySet();
        MiniCube cube = (MiniCube) configQuestionModel.getCube();
        StringBuffer whereExpressions = new StringBuffer(" where 1=1 ");
        for (Entry<String, MetaCondition> entry : metaConditionSet) {
            MetaCondition metaCondition = entry.getValue();
            if (entry.getValue() instanceof DimensionCondition) {
                // 判断是维度查询
                DimensionCondition dimensionCondition = (DimensionCondition) metaCondition;
                Dimension dimension = cube.getDimensions().get(dimensionCondition.getMetaName());
                String demensionName = dimensionCondition.getMetaName();
                if (dimensionCondition.getQueryDataNodes() == null
                        || dimensionCondition.getQueryDataNodes().isEmpty()) {
                    // 如果节点为空，不需要组织条件
                    continue;
                }
                if (MetaNameUtil.isAllMemberUniqueName(dimensionCondition.getQueryDataNodes()
                        .get(0).getUniqueName())) {
                    // 如果节点为所有条件，不需要组织条件
                    continue;
                }
                if (dimension.getType() == DimensionType.TIME_DIMENSION) {
                    // 如果为TIME_DIMENSION，name为指标字段
                    demensionName = dimension.getFacttableColumn();
                }
                // 判断是或否有添加查询
                SqlColumn sqlColumn = allColums.get(demensionName);
                String equals = "";
                String inExpression = "";
                for (QueryData queryData : dimensionCondition.getQueryDataNodes()) {
                    inExpression = inExpression
                            + "\'"
                            + queryData.getUniqueName().substring(
                                    queryData.getUniqueName().lastIndexOf("[") + 1,
                                    queryData.getUniqueName().lastIndexOf("]")) + "\',";
                }
                if (!inExpression.isEmpty()) {
                    // 如果inExpression没有值，证明没有此条件
                    String columnSqName = sqlColumn.getTableName() + "."
                            + sqlColumn.getTableFieldName();
                    equals = " in (" + inExpression.substring(0, inExpression.lastIndexOf(",")) + ")";
                    whereExpressions.append(" and " + columnSqName + equals + " ");
                }
                
            } else if (entry.getValue() instanceof MeasureCondition) {
                // 判断是指标查询
                MeasureCondition measureCondition = (MeasureCondition) metaCondition;
                if (measureCondition.getMeasureConditions() != null) {
                    // 判断是或否有添加查询
                    SqlColumn sqlColumn = allColums.get(measureCondition.getMetaName());
                    SQLCondition sqlCondition = measureCondition.getMeasureConditions();
                    String columnSqName = sqlColumn.getTableName() + "."
                            + sqlColumn.getTableFieldName();
                    whereExpressions.append(" and " + columnSqName
                            + this.generateWhereEquals(sqlCondition) + " ");
                }
            }
        }
        return whereExpressions.toString();
    }
    
    /**
     * 获取where equals等式
     * 
     * @param sqlCondition
     *            sqlCondition
     * @return where equals sting
     */
    private String generateWhereEquals(SQLCondition sqlCondition) {
        SQLConditionType conditionType = sqlCondition.getCondition();
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
            return " between " + sqlCondition.getConditionValues().get(0) + " and "
                    + sqlCondition.getConditionValues().get(1);
        }
        // in
        case IN: {
            StringBuffer inExpression = new StringBuffer(" in (");
            for (String value : sqlCondition.getConditionValues()) {
                inExpression.append(value + ",");
            }
            return inExpression.toString().substring(0, inExpression.toString().lastIndexOf(",")) + ")";
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
                if (colum.getDimension().getType() == DimensionType.TIME_DIMENSION) {
                    // 如果为时间维度
                    columName = colum.getFactTableName() + "."
                            + colum.getDimension().getFacttableColumn();
                    groupbyExpression.append(columName + ",");
                } else {
                    // 如果为其他维度
                    String dimTableName = colum.getTableName();
                    groupbyExpression.append(dimTableName + "." + colum.getTableFieldName() + ",");
                }
            } else if (AxisType.COLUMN == colum.getType()) {
                // TODO
                // if (StringUtils.isEmpty(colum.getMeasure().getAggregator()))
                // {
                if (true) {
                    return "";
                }
            }
        }
        if (" group by ".equals(groupbyExpression.toString())) {
            return "";
        } else {
            return groupbyExpression.toString().substring(0, groupbyExpression.toString().lastIndexOf(","));
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
    public String generateOrderByExpression(ConfigQuestionModel configQuestionModel,
            HashMap<String, SqlColumn> allColums) {
        String orderColumnNameTmp = configQuestionModel.getSortRecord().getSortColumnUniquename();
        String orderColumnName = orderColumnNameTmp.substring(
                orderColumnNameTmp.lastIndexOf("[") + 1, orderColumnNameTmp.lastIndexOf("]"));
        SqlColumn sqlColumn = allColums.get(orderColumnName);
        return " order by " + sqlColumn.getTableName() + "." + sqlColumn.getTableFieldName() + " "
                + configQuestionModel.getSortRecord().getSortType() + " ";
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
                if (start >= 0 && size >= 0) {
                    limitStringBuffer.append(" limit ");
                    limitStringBuffer.append(start);
                    limitStringBuffer.append(",");
                    limitStringBuffer.append(size);
                    this.setSql(this.getSql() + limitStringBuffer.toString());
                }
                return;
            }
            case "Oracle.jdbc.driver.OracleDriver": {
                int end = -1;
                start = pageInfo.getCurrentPage() * pageInfo.getPageSize();
                end = start + pageInfo.getPageSize();
                if (start >= 0 && end >= 0) {
                    StringBuffer pageString = new StringBuffer();
                    pageString.append("SELECT * FROM (");
                    pageString.append("SELECT A.*, ROWNUM RN");
                    pageString.append("FROM (SELECT * FROM ( " + this.getSql() + " )) A ");
                    pageString.append(" WHERE ROWNUM <= " + end);
                    pageString.append(") WHERE RN >=  " + start);
                    this.setSql(pageString.toString());
                }
                return;
            }
            default: {
                return;
            }
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
}