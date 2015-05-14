package com.baidu.rigel.biplatform.ma.ds.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.rigel.biplatform.ac.util.AesUtil;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceInfoReaderService;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceType;
import com.baidu.rigel.biplatform.ma.model.meta.ColumnInfo;
import com.baidu.rigel.biplatform.ma.model.meta.TableInfo;
import com.google.common.collect.Lists;

/**
 * 测试RelationDBInfoReaderServiceImpl
 * @author jiangyichao
 *
 */
public class RelationDBInfoReaderServiceImplTest {

	/**
	 * 数据源信息读取服务
	 */
	public DataSourceInfoReaderService dsInfoReader = new RelationDBInfoReaderServiceImpl();
	/**
	 * 
	 */
	@Test
	public void test() throws Exception {
    	// 数据库连接基本信息，并构建内存数据库H2
    	String username = "test";
    	String password = "test";
    	String dbInstance = "testDB";
    	String name = "testDB";
    	String securityKey = "0000000000000000";
    	String passwordEncrypt = AesUtil.getInstance().encryptAndUrlEncoding(password, securityKey);
    	String url = "jdbc:h2:mem://127.0.0.1:3306/" + dbInstance + ";";
    	try {
    		Class.forName("org.h2.Driver");
    		Connection conn = DriverManager.getConnection(url,username,password); 
    		if (conn != null) {
    			System.out.println("get H2 datasource by username:" + username);    			
    		} 
    		// 创建表
    		String sql = "create table testTable(col1 int)";
    		PreparedStatement pt = conn.prepareStatement(sql);
    		pt.execute();
    		
        	// 创建数据源定义对象
        	DataSourceDefine dataSourceDefine = new DataSourceDefine();
        	DataSourceType.H2.setPrefix("jdbc:h2:mem://");
        	dataSourceDefine.setDataSourceType(DataSourceType.H2);
        	dataSourceDefine.setDbUser(username);
        	dataSourceDefine.setDbPwd(passwordEncrypt);
        	dataSourceDefine.setDbInstance(dbInstance);
        	dataSourceDefine.setName(name);
        	dataSourceDefine.setHostAndPort("127.0.0.1:3306");
        	
        	
    		List<TableInfo> tables = Lists.newArrayList();
    		TableInfo table = new TableInfo();
    		table.setId("TESTTABLE");
    		table.setName("TESTTABLE");
    		tables.add(table);
    		
    		Assert.assertEquals(tables, dsInfoReader.getAllTableInfos(dataSourceDefine, securityKey));
    		
    		List<ColumnInfo> columns = Lists.newArrayList();
    		ColumnInfo col1 = new ColumnInfo();
    		col1.setId("COL1");
    		col1.setName("COL1");
    		columns.add(col1);
    		
    		Assert.assertEquals(columns, dsInfoReader.getAllColumnInfos(dataSourceDefine, securityKey, "TESTTABLE"));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}

}
