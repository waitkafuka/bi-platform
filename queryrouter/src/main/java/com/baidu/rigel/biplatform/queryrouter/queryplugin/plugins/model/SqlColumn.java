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

import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.Level;
import com.baidu.rigel.biplatform.ac.model.Measure;
import com.baidu.rigel.biplatform.ac.query.model.AxisMeta.AxisType;

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
     * name
     */
    public String name;
    
    /**
     * sql查询的唯一的列名标示
     */
    public String sqlUniqueColumn;
    
    /**
     * tableFieldName
     */
    public String tableFieldName;
    
    /**
     * caption
     */
    public String caption;
    
    /**
     * tableName
     */
    public String tableName;
    
    /**
     * AxisType
     */
    public AxisType type;
    
    /**
     * dimension
     */
    public Dimension dimension;
    
    /**
     * Level
     */
    public Level level;
    
    /**
     * measure
     */
    public Measure measure;
    
    /**
     * factTableName
     */
    public String factTableName;
    
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
     * getFactTableName
     * 
     * @return String factTableName
     */
    public String getFactTableName() {
        return factTableName;
    }
    
    /**
     * setFactTableName
     * 
     * @param String
     *            factTableName
     */
    public void setFactTableName(String factTableName) {
        this.factTableName = factTableName;
    }
    
    /**
     * getLevel
     * 
     * @return Level level
     */
    public Level getLevel() {
        return level;
    }
    
    /**
     * setLevel
     * 
     * @param Level
     *            level
     */
    public void setLevel(Level level) {
        this.level = level;
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
    
    /**
     * getType
     * 
     * @return AxisType type
     */
    public AxisType getType() {
        return type;
    }
    
    /**
     * setType
     * 
     * @param AxisType
     *            type
     */
    public void setType(AxisType type) {
        this.type = type;
    }
    
    /**
     * getDimension
     * 
     * @return dimension dimension
     */
    public Dimension getDimension() {
        return dimension;
    }
    
    /**
     * setDimension
     * 
     * @param Dimension
     *            dimension
     */
    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }
    
    /**
     * getMeasure
     * 
     * @return measure measure
     */
    public Measure getMeasure() {
        return measure;
    }
    
    /**
     * setMeasure
     * 
     * @param measure
     *            measure
     */
    public void setMeasure(Measure measure) {
        this.measure = measure;
    }
    
}