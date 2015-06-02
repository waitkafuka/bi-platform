package com.baidu.rigel.biplatform.ma.model.utils;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceType;
import com.baidu.rigel.biplatform.ma.model.exception.DBInfoReadException;

public class DBUrlGeneratorUtilsTest {

	/**
	 * 
	 */
	@Test
	public void testDsNull() {
		try {
			DBUrlGeneratorUtils.getConnUrl(null);
		} catch(DBInfoReadException e) {
			Assert.assertNotNull(e);
		}
	}
	
	/**
	 * 
	 */
	@Test
	public void testMYSQLDsWithEncoding() {
    	// 创建数据源定义对象
    	DataSourceDefine dataSourceDefine = new DataSourceDefine();
    	dataSourceDefine.setDataSourceType(DataSourceType.MYSQL);
    	dataSourceDefine.setHostAndPort("127.0.0.1:3306");
    	dataSourceDefine.setEncoding("utf8");
    	dataSourceDefine.setDbInstance("testDB");
    	String connUrl = "jdbc:mysql://127.0.0.1:3306/testDB?useUniCode=true&characterEncoding=utf8";
    	Assert.assertEquals(connUrl, DBUrlGeneratorUtils.getConnUrl(dataSourceDefine));
	}
}
