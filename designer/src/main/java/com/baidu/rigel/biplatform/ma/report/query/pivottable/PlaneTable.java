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
	 * 平面表列上的属性定义
	 */
	private List<PlaneTableColDefine> colDefs = Lists.newArrayList();
		
	/**
	 * 表格列头定义
	 */
	private List<List<ColField>> colFields = Lists.newArrayList();
	
	/**
	 * 基于列的指标数据信息
	 */
	private List<List<CellData>> dataSourceColumnBased = Lists.newArrayList();
	
	/**
	 * 基于列的维度数据信息
	 */
	private List<List<String>> dimDataColumnBased = Lists.newArrayList();
	
	/**
	 * 列上的高度
	 */
	private int colHeadHeight;
	
	/**
	 * 总的数据条数
	 */
	private int totalRecordSize;
	
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
	public List<PlaneTableColDefine> getColDefines() {
		return colDefs;
	}

	/**
	 * @param colDefs the colDefs to set
	 */
	public void setColDefines(List<PlaneTableColDefine> colDefs) {
		this.colDefs = colDefs;
	}

	/**
	 * @return the colFields
	 */
	public List<List<ColField>> getColFields() {
		return colFields;
	}

	/**
	 * @param colFields the colFields to set
	 */
	public void setColFields(List<List<ColField>> colFields) {
		this.colFields = colFields;
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

	/**
	 * @return the colHeadHight
	 */
	public int getColHeadHeight() {
		return colHeadHeight;
	}

	/**
	 * @param colHeadHight the colHeadHight to set
	 */
	public void setColHeadHight(int colHeadHeight) {
		this.colHeadHeight = colHeadHeight;
	}

	/**
	 * @param colHeadHeight the colHeadHeight to set
	 */
	public void setColHeadHeight(int colHeadHeight) {
		this.colHeadHeight = colHeadHeight;
	}
	
	/**
	 * @return the dimDataColumnBased
	 */
	public List<List<String>> getDimDataColumnBased() {
		return dimDataColumnBased;
	}

	/**
	 * @param dimDataColumnBased the dimDataColumnBased to set
	 */
	public void setDimDataColumnBased(List<List<String>> dimDataColumnBased) {
		this.dimDataColumnBased = dimDataColumnBased;
	}
	
	/**
	 * @return the totalRecordSize
	 */
	public int getTotalRecordSize() {
		return totalRecordSize;
	}

	/**
	 * @param totalRecordSize the totalRecordSize to set
	 */
	public void setTotalRecordSize(int totalRecordSize) {
		this.totalRecordSize = totalRecordSize;
	}
		
}
