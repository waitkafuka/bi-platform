package com.baidu.rigel.biplatform.ma.report.query.pivottable;

import java.io.Serializable;

public class PlaneTableColDefine extends ColDefine implements Serializable{

	/**
	 * 序列id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 设置该列属性是否为指标
	 */
	private Boolean isMeasure;

	/**
	 * @return the isMeasure
	 */
	public Boolean getIsMeasure() {
		return isMeasure;
	}

	/**
	 * @param isMeasure the isMeasure to set
	 */
	public void setIsMeasure(Boolean isMeasure) {
		this.isMeasure = isMeasure;
	}
}
