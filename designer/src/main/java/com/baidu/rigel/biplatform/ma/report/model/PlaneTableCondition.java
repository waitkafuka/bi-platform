package com.baidu.rigel.biplatform.ma.report.model;

import java.io.Serializable;

/**
 * 平面表条件类
 * @author yichao.jiang
 *
 */
public class PlaneTableCondition implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 条件对应的elementId
	 */
	private String elementId;
	
	/**
	 * 条件名称
	 */
	private String name;
	
	/**
	 * 当前查询条件 
	 */
	private String sqlCondition = "=";
	
	/**
	 * 条件默认值
	 */
	private String defaultValue;

	/**
	 * @return the elementId
	 */
	public String getElementId() {
		return elementId;
	}

	/**
	 * @param elementId the elementId to set
	 */
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the condition
	 */
	public String getSQLCondition() {
		return sqlCondition;
	}

	/**
	 * @param condition the condition to set
	 */
	public void setSQLCondition(String sqlCondition) {
		this.sqlCondition = sqlCondition;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@Override
	public String toString() {
	    return "[ name: " + this.getName() + ", elementId: " + this.getElementId() + ", condition: " + this.getSQLCondition() + "]";
	}
}
