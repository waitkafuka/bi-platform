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

/**
 * 
 * Description: sql数据表列元数据信息描述
 * 
 * @author 罗文磊
 *
 */
public class SqlColumn implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3151301875582323397L;

    /**
     * columnKey
     */
    public String columnKey;

    /**
     * name
     */
    public String name;
    
    /**
     * operator
     */
    public String operator;
    
    /**
     * type
     */
    public ColumnType type;
    
    /**
     * dataType
     */
    public String dataType;

    /**
     * sql查询的唯一的列名标示
     */
    public String sqlUniqueColumn;

    /**
     * tableFieldName
     */
    public String tableFieldName;
    
    /**
     * joinTableFieldName
     */
    public String joinTableFieldName;
    
    /**
     * factTableFieldName
     */
    public String factTableFieldName;

    /**
     * caption
     */
    public String caption;

    /**
     * tableName
     */
    public String tableName;

    /**
     * tableNames
     */
    public String sourceTableName;

    /**
     * columnCondition
     */
    public ColumnCondition columnCondition;

    
    /**
     * getDataType
     * 
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * setDataType
     * 
     * @param dataType the dataType to set
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * getJoinTableFieldName
     * 
     * @return the joinTableFieldName
     */
    public String getJoinTableFieldName() {
        return joinTableFieldName;
    }

    /**
     * setJoinTableFieldName
     * 
     * @param joinTableFieldName the joinTableFieldName to set
     */
    public void setJoinTableFieldName(String joinTableFieldName) {
        this.joinTableFieldName = joinTableFieldName;
    }

    /**
     * getFactTableFieldName
     * 
     * @return the factTableFieldName
     */
    public String getFactTableFieldName() {
        return factTableFieldName;
    }

    /**
     * setFactTableFieldName
     * 
     * @param factTableFieldName the factTableFieldName to set
     */
    public void setFactTableFieldName(String factTableFieldName) {
        this.factTableFieldName = factTableFieldName;
    }

    /**
     * getOperator
     * 
     * @return the operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * setOperator
     * 
     * @param operator the operator to set
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * getType
     * 
     * @return the type
     */
    public ColumnType getType() {
        return type;
    }

    /**
     * setType
     * 
     * @param type the type to set
     */
    public void setType(ColumnType type) {
        this.type = type;
    }

    /**
     * columnCondition
     * 
     * @return the columnCondition to get
     */
    public ColumnCondition getColumnCondition() {
        return columnCondition;
    }

    /**
     * setColumnCondition
     * 
     * @param columnCondition
     *            the columnCondition to set
     */
    public void setColumnCondition(ColumnCondition columnCondition) {
        this.columnCondition = columnCondition;
    }

    /**
     * getSourceTableName
     * 
     * @return the sourceTableName to get
     */
    public String getSourceTableName() {
        return sourceTableName;
    }

    /**
     * setSourceTableName
     * 
     * @param sourceTableName
     *            the sourceTableName to set
     */
    public void setSourceTableName(String sourceTableName) {
        this.sourceTableName = sourceTableName;
    }

    

    /**
     * getColumnKey
     * 
     * @return the columnKey
     */
    public String getColumnKey() {
        return columnKey;
    }

    /**
     * setColumnKey
     * 
     * @param columnKey
     *            the columnKey to set
     */
    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }

    /**
     * getSqlUniqueColumn
     * 
     * @return String sqlUniqueColumn
     */
    public String getSqlUniqueColumn() {
        return sqlUniqueColumn;
    }

    /**
     * setSqlUniqueColumn
     * 
     * @param String
     *            sqlUniqueColumn
     */
    public void setSqlUniqueColumn(String sqlUniqueColumn) {
        this.sqlUniqueColumn = sqlUniqueColumn;
    }

    /**
     * getName
     * 
     * @return name name
     */
    public String getName() {
        return name;
    }

    /**
     * setName
     * 
     * @param name
     *            name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getTableFieldName
     * 
     * @return tableFieldName tableFieldName
     */
    public String getTableFieldName() {
        return tableFieldName;
    }

    /**
     * setTableFieldName
     * 
     * @param tableFieldName
     *            tableFieldName
     */
    public void setTableFieldName(String tableFieldName) {
        this.tableFieldName = tableFieldName;
    }

    /**
     * getCaption
     * 
     * @return caption caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * setCaption
     * 
     * @param caption
     *            caption
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * getTableName
     * 
     * @return tableName tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * setTableName
     * 
     * @param tableName
     *            tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}