package com.baidu.rigel.biplatform.ma.ds.service;

import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ma.ds.exception.DataSourceConnectionException;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;

/**
 * 数据源连接服务接口
 * @author jiangyichao
 *
 */
public interface DataSourceConnectionService <T> {
	
	/**
	 * 获取数据源连接信息
	 * @param ds 数据源定义
	 * @param securityKey AES加密的密钥
	 * @return 数据源连接
	 * @throws DataSourceConnectionException 数据连接异常
	 */
	public T createConnection(DataSourceDefine ds, String securityKey) throws DataSourceConnectionException;
	
	/**
	 * 关闭数据源连接信息
	 * @param conn 数据源连接信息
	 * @return 若正常关闭，返回true，否则返回false
	 * @throws DataSourceConnectionException 数据连接异常
	 */
	public boolean closeConnection(T conn) throws DataSourceConnectionException;
	
	/**
	 * 获取数据源连接url
	 * @param ds 数据源定义
	 * @return 数据源连接url
	 * @throws DataSourceConnectionException 数据连接异常
	 */
	public String getDataSourceConnUrl(DataSourceDefine ds) throws DataSourceConnectionException;
	
	/**
	 * 判断数据源是否有效
	 * @param ds 数据源定义
	 * @param securityKey 密钥
	 * @return 数据源有效返回true，否则返回false
	 * @throws DataSourceConnectionException 数据连接异常
	 */
	public boolean isValidateDataSource(DataSourceDefine ds, String securityKey) throws DataSourceConnectionException;
	/**
	 * 将数据源信息由silkroad层向tesseract层转换
	 * @param ds 数据源定义
	 * @param securityKey AES加密的密钥
	 * @return tesseract层所需数据源信息
	 * @throws DataSourceConnectionException 数据连接异常
	 */
	public  DataSourceInfo parseToDataSourceInfo(DataSourceDefine ds, String securityKey) throws DataSourceConnectionException;
}
