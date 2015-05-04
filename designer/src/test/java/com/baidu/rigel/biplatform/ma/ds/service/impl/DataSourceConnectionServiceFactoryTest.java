package com.baidu.rigel.biplatform.ma.ds.service.impl;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.rigel.biplatform.ma.ds.exception.DataSourceOperationException;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceConnectionServiceFactory;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceType;

/**
 * 测试数据源连接工厂类
 * @author jiangyichao
 *
 */
public class DataSourceConnectionServiceFactoryTest {

	/**
	 * 
	 */
	@Test
	public void test() throws Exception {
    	DataSourceDefine ds = new DataSourceDefine();
    	ds.setDataSourceType(DataSourceType.MYSQL);
    	ds.setDbInstance("testDB");
    	ds.setDbPwd("test");
    	ds.setDbUser("test");
    	ds.setHostAndPort("127.0.0.1:3306");
    	
    	Assert.assertNotNull(DataSourceConnectionServiceFactory.
    			getDataSourceConnectionServiceInstance(ds.getDataSourceType()));
    	
    	try {
    		ds.setDataSourceType(DataSourceType.CSV);
    		DataSourceConnectionServiceFactory.getDataSourceConnectionServiceInstance(ds.getDataSourceType());
    	} catch (DataSourceOperationException e) {
    		Assert.assertNotNull(e);
    	}
	}
}
