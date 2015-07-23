package com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model;

import java.io.Serializable;

/**
 * 
 * Description: 平面表数据表列元数据信息描述
 * @author 罗文磊
 *
 */
public class Column implements Serializable {
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3151301875582323397L;

    /**
     * name
     */
    private String name;
    
    /**
     * joinTableFieldName
     */
    private String joinTableFieldName;
    
    /**
     * columnType
     */
    private ColumnType columnType;
    
    /**
     * operator
     */
    private String operator;
    
    /**
     * complexOperator
     */
    private String facttableName;
    
    /**
     * complexOperator
     */
    private String facttableColumnName;
    
    /**
     * key
     */
    private String key;
    
    /**
     * caption
     */
    private String caption;
    
    /**
     * tableName
     */
    private String tableName;
    
    /**
     * 保留字段
     */
    private String dbName;

    /**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}

	/**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
	 * @return the columnType
	 */
	public ColumnType getColumnType() {
		return columnType;
	}

	/**
	 * @param columnType the columnType to set
	 */
	public void setColumnType(ColumnType columnType) {
		this.columnType = columnType;
	}

	/**
	 * @return the joinTableFieldName
	 */
	public String getJoinTableFieldName() {
		return joinTableFieldName;
	}

	/**
	 * @param joinTableFieldName the joinTableFieldName to set
	 */
	public void setJoinTableFieldName(String joinTableFieldName) {
		this.joinTableFieldName = joinTableFieldName;
	}

	/**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the operator
     */
    public String operator() {
        return operator;
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * @return the facttableName
     */
    public String getFacttableName() {
        return facttableName;
    }

    /**
     * @param facttableName the facttableName to set
     */
    public void setFacttableName(String facttableName) {
        this.facttableName = facttableName;
    }

    /**
     * @return the facttableColumnName
     */
    public String getFacttableColumnName() {
        return facttableColumnName;
    }

    /**
     * @param facttableColumnName the facttableColumnName to set
     */
    public void setFacttableColumnName(String facttableColumnName) {
        this.facttableColumnName = facttableColumnName;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * @param caption the caption to set
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the dbName
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * @param dbName the dbName to set
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Column(String key, String name, String caption, String tableName) {
        super ();
        this.key = key;
        this.name = name;
        this.caption = caption;
        this.tableName = tableName;
    }
    
    public Column() {
        super ();
    }
}