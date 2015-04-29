package com.baidu.rigel.biplatform.ma.datasource.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.rigel.biplatform.ac.util.AesUtil;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceInfoReaderService;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceInfoReaderServiceFactory;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceType;
import com.baidu.rigel.biplatform.ma.model.meta.TableInfo;
/**
 * 关系型数据库读取信息测试
 * @author jiangyichao
 *
 */
public class RelationDBInfoReaderServiceImplTest {
	/**
	 * 日志对象
	 */
	private static final Logger LOG = LoggerFactory.getLogger(
			RelationDBInfoReaderServiceImplTest.class);
	
	/**
	 * 测试H2数据库
	 */
	@Test
	public void testH2DB() throws Exception {
    	// 数据库连接基本信息，并构建内存数据库H2
    	String username = "test";
    	String password = "test";
    	String dbInstance = "testDB";
    	String name = "testDB";
    	String securityKey = "0000000000000000";
    	String passwordEncrypt = AesUtil.getInstance().encryptAndUrlEncoding(password, securityKey);
    	String url = "jdbc:h2:tcp://localhost/mem:" + dbInstance;
    	
       	try {
    		Class.forName("org.h2.Driver");
    		Connection conn = DriverManager.getConnection(url,username,password); 
    		if (conn != null) {
    			LOG.info("get H2 datasource by username:" + username);    			
    		}
    	} catch (Exception e) {
    		LOG.error("error:", e.getMessage());
    	}
       	
       	
    	// 创建数据源定义对象
    	DataSourceDefine dataSourceDefine = new DataSourceDefine();
    	dataSourceDefine.setDataSourceType(DataSourceType.H2);
    	dataSourceDefine.setDbUser(username);
    	dataSourceDefine.setDbPwd(passwordEncrypt);
    	dataSourceDefine.setDbInstance(dbInstance);
    	dataSourceDefine.setName(name);
    	dataSourceDefine.setHostAndPort("127.0.0.1:3306");
    	
    	DataSourceInfoReaderService dsInfoReaderService = DataSourceInfoReaderServiceFactory.
    			getDataSourceInfoReaderServiceInstance(DataSourceType.H2);
    	List<TableInfo> tables = dsInfoReaderService.getAllTableInfos(dataSourceDefine, securityKey);
    	
    	
    	
	}
}
