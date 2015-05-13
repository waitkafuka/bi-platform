package com.baidu.rigel.biplatform.ma.ds.service.impl;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.rigel.biplatform.ma.ds.exception.DataSourceOperationException;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceInfoReaderServiceFactory;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceType;

/**
 * 数据源信息读取工厂类
 * 
 * @author jiangyichao
 *
 */
public class DataSourceInfoReaderServiceFactoryTest {
    
    /**
	 * 
	 */
    @Test
    public void test() throws Exception {
        DataSourceDefine ds = new DataSourceDefine ();
        ds.setDataSourceType (DataSourceType.MYSQL);
        ds.setDbInstance ("testDB");
        ds.setDbPwd ("test");
        ds.setDbUser ("test");
        ds.setHostAndPort ("127.0.0.1:3306");
        
        Assert.assertNotNull (DataSourceInfoReaderServiceFactory
                .getDataSourceInfoReaderServiceInstance (ds
                .getDataSourceType ().name ()));
        
        try {
            ds.setDataSourceType (DataSourceType.CSV);
            DataSourceInfoReaderServiceFactory
                    .getDataSourceInfoReaderServiceInstance (ds
                    .getDataSourceType ().name ());
        } catch (DataSourceOperationException e) {
            Assert.assertNotNull (e);
        }
    }
}
