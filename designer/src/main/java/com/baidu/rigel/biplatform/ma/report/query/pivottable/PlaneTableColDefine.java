package com.baidu.rigel.biplatform.ma.report.query.pivottable;

import java.io.Serializable;

/**
 * 平面表列属性
 * 
 * @author yichao.jiang 2015年5月26日 上午11:00:34
 */
public class PlaneTableColDefine implements Serializable{

	/**
	 * 序列id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 表名_列名
	 */
	private String field;
	
	/**
	 * 表头title
	 */
	private String title;
	
	/**
	 * 排序字段
	 */
	private String orderby;
	
	/**
	 * 样式
	 */
	private String format = "I,III.DD";
	
	/**
	 * 对齐方式
	 */
	private String align;
	
	/**
	 * 列头提示信息
	 */
	private String tips;
	/**
	 * 设置该列属性是否为指标,默认为指标
	 */
	private Boolean isMeasure = true;

	
	/**
     * default generate get field
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * default generate set field
     * @param field the field to set
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * default generate get title
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * default generate set title
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * default generate get orderby
     * @return the orderby
     */
    public String getOrderby() {
        return orderby;
    }

    /**
     * default generate set orderby
     * @param orderby the orderby to set
     */
    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    /**
     * default generate get format
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * default generate set format
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * default generate get align
     * @return the align
     */
    public String getAlign() {
        return align;
    }

    /**
     * default generate set align
     * @param align the align to set
     */
    public void setAlign(String align) {
        this.align = align;
    }

    /**
     * default generate get tips
     * @return the tips
     */
    public String getTips() {
        return tips;
    }

    /**
     * default generate set tips
     * @param tips the tips to set
     */
    public void setTips(String tips) {
        this.tips = tips;
    }

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
