package com.baidu.rigel.biplatform.ma.report.query.pivottable;

import java.util.List;

import com.baidu.rigel.biplatform.ac.query.model.PageInfo;
import com.google.common.collect.Lists;

/**
 * 平面表模型定义
 * @author yichao.jiang
 *
 */
public class PlaneTable extends BaseTable {

	/**
	 * 序列id
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 分页信息
	 */
	private PageInfo pageInfo;
	
	/**
	 * 列上的属性定义
	 */
	private List<ColDefine> colDefs = Lists.newArrayList();
	
	/**
	 * 表格列头定义
	 */
	private List<List<ColField>> colHeadFields = Lists.newArrayList();
	
	/**
	 * 基于行的数据信息
	 */
	private List<List<CellData>> dataSourceRowBased = Lists.newArrayList();
	
	/**
	 * 基于列的数据信息
	 */
	private List<List<CellData>> dataSourceColumnBased = Lists.newArrayList();

	/**
	 * 
	 */
	private int recordSize;
	/**
	 * @return the pageInfo
	 */
	public PageInfo getPageInfo() {
		return pageInfo;
	}

	/**
	 * @param pageInfo the pageInfo to set
	 */
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	/**
	 * @return the colDefs
	 */
	public List<ColDefine> getColDefs() {
		return colDefs;
	}

	/**
	 * @param colDefs the colDefs to set
	 */
	public void setColDefs(List<ColDefine> colDefs) {
		this.colDefs = colDefs;
	}

	/**
	 * @return the colHeadFields
	 */
	public List<List<ColField>> getColHeadFields() {
		return colHeadFields;
	}

	/**
	 * @param colHeadFields the colHeadFields to set
	 */
	public void setColHeadFields(List<List<ColField>> colHeadFields) {
		this.colHeadFields = colHeadFields;
	}

	/**
	 * @return the dataSourceRowBased
	 */
	public List<List<CellData>> getDataSourceRowBased() {
		return dataSourceRowBased;
	}

	/**
	 * @param dataSourceRowBased the dataSourceRowBased to set
	 */
	public void setDataSourceRowBased(List<List<CellData>> dataSourceRowBased) {
		this.dataSourceRowBased = dataSourceRowBased;
	}

	/**
	 * @return the dataSourceColumnBased
	 */
	public List<List<CellData>> getDataSourceColumnBased() {
		return dataSourceColumnBased;
	}

	/**
	 * @param dataSourceColumnBased the dataSourceColumnBased to set
	 */
	public void setDataSourceColumnBased(List<List<CellData>> dataSourceColumnBased) {
		this.dataSourceColumnBased = dataSourceColumnBased;
	}	
}
