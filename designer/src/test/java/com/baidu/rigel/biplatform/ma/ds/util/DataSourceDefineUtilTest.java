package com.baidu.rigel.biplatform.ma.ds.util;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.data.impl.SqlDataSourceInfo.DataBase;
import com.baidu.rigel.biplatform.ac.util.AesUtil;
import com.baidu.rigel.biplatform.ma.comm.util.ConfigUtil;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DataSourceDefineUtilTest {

	/**
	 * 
	 */
	@Test
	public void testGetDsFileDir() {
		Assert.assertEquals("null" + File.separator +
				ConfigUtil.getDsBaseDir(), DataSourceDefineUtil.getDsFileStoreDir());
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetDsFileName() {
    	// 创建数据源定义对象
    	DataSourceDefine dataSourceDefine = new DataSourceDefine();
    	dataSourceDefine.setId("test");
    	dataSourceDefine.setName("test");
    	Assert.assertEquals("null" + File.separator +
				ConfigUtil.getDsBaseDir() + File.separator + 
				dataSourceDefine.getId() + "_" + dataSourceDefine.getName(), 
				DataSourceDefineUtil.getDsFileName(dataSourceDefine));
	}
	
	/**
	 * 
	 */
	@Test
	public void testParseToSqlDataSourceInfo() throws Exception {
		String security = "0000000000000000";
		String id = "id";
        SqlDataSourceInfo sqlDataSource = new SqlDataSourceInfo(id);
        sqlDataSource.setHosts(Lists.newArrayList("127.0.0.1:3306"));
        sqlDataSource.setUsername("user");
        sqlDataSource.setPassword("pass");
        sqlDataSource.setDBProxy(true);
        sqlDataSource.setInstanceName("db");
        sqlDataSource.setProductLine("productline");
        sqlDataSource.setJdbcUrls(Lists.newArrayList("jdbc:mysql://127.0.0.1:3306/db"));
        sqlDataSource.setDbPoolInfo(Maps.newHashMap());
        sqlDataSource.setDataBase(DataBase.MYSQL);
        
        DataSourceDefine ds = new DataSourceDefine();
        ds.setId(id);
        ds.setDbUser("user");
        ds.setDbPwd("pass");
        ds.setProductLine("productline");
        ds.setHostAndPort("127.0.0.1:3306");
        ds.setDbInstance("db");
        ds.setDataSourceType(DataSourceType.MYSQL);
        // 使用未加密密码，导致抛出异常
        try {
        	DataSourceDefineUtil.parseToDataSourceInfo(ds, security);
        } catch(Exception e) {
        	Assert.assertNotNull(e);
        }
        String pwd = AesUtil.getInstance().encryptAndUrlEncoding("pass", security);
        // sqlDataSource.setPassword(pwd);
        ds.setDbPwd(pwd);
        Assert.assertEquals(sqlDataSource, DataSourceDefineUtil.parseToDataSourceInfo(ds, security));
        
        // Oracle数据库
        sqlDataSource.setDataBase(DataBase.ORACLE);
        ds.setDataSourceType(DataSourceType.ORACLE);
        sqlDataSource.setJdbcUrls(Lists.newArrayList("jdbc:oracle:thin:127.0.0.1:3306:db"));
        Assert.assertEquals(sqlDataSource, DataSourceDefineUtil.parseToDataSourceInfo(ds, security));
        
        // H2数据库
        sqlDataSource.setDataBase(DataBase.H2);
        ds.setDataSourceType(DataSourceType.H2);
        sqlDataSource.setJdbcUrls(Lists.newArrayList("jdbc:h2:tcp://127.0.0.1:3306/db"));
        Assert.assertEquals(sqlDataSource, DataSourceDefineUtil.parseToDataSourceInfo(ds, security));
        
	}
}
