package com.baidu.rigel.biplatform.ma.model.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.rigel.biplatform.ac.util.AesUtil;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceService;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceType;
import com.baidu.rigel.biplatform.ma.model.meta.ColumnMetaDefine;
import com.baidu.rigel.biplatform.ma.model.meta.FactTableMetaDefine;
import com.baidu.rigel.biplatform.ma.model.meta.TableInfo;
import com.baidu.rigel.biplatform.ma.model.service.CubeMetaBuildService;
import com.google.common.collect.Lists;

/**
 * CubeMetaBuildServiceImpl测试
 * @author yichao.jiang 2015年6月1日 上午9:51:47
 */
public class CubeMetaBuildServiceImplTest {

    
    /**
     * 日志对象
     */
    private static final Logger LOG = LoggerFactory.getLogger(CubeMetaBuildServiceImplTest.class);
    
    /**
     * 加密串
     */
    private String securityKey = "0000000000000000";
    
    /**
     * 数据源信息
     */
    private DataSourceDefine dataSourceDefine;
    
    @InjectMocks
    private CubeMetaBuildService cubeService = new CubeMetaBuildServiceImpl();
    @Mock
    private DataSourceService dsService;
    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        // 数据库连接基本信息，并构建内存数据库H2
        String username = "test";
        String password = "test";
        String dbInstance = "testDB";
        String name = "testDB";
        String securityKey = "0000000000000000";
        String passwordEncrypt = AesUtil.getInstance().encryptAndUrlEncoding(password, securityKey);
        String url = "jdbc:h2:mem://127.0.0.1:3306/" + dbInstance + ";";
        
        
        // 创建数据源定义对象
        dataSourceDefine = new DataSourceDefine();
        DataSourceType.H2.setPrefix("jdbc:h2:mem://");
        dataSourceDefine.setDataSourceType(DataSourceType.H2);
        dataSourceDefine.setDbUser(username);
        dataSourceDefine.setDbPwd(passwordEncrypt);
        dataSourceDefine.setDbInstance(dbInstance);
        dataSourceDefine.setName(name);
        dataSourceDefine.setHostAndPort("127.0.0.1:3306");

        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection(url,username,password); 
            if (conn != null) {
                LOG.info("get H2 datasource by username:" + username);                
            } 
            // 创建表
            String sql = "create table testTable(col1 int)";
            PreparedStatement pt = conn.prepareStatement(sql);
            pt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     */
    @Test
    public void testGetAllTable() throws Exception {

        List<TableInfo> tables = Lists.newArrayList();
        TableInfo table = new TableInfo();
        table.setId("TESTTABLE");
        table.setName("TESTTABLE");
        tables.add(table);

        // 获取该数据源
        Mockito.when(dsService.getDsDefine(Mockito.anyString())).thenReturn(dataSourceDefine);
        List<TableInfo> actualTables = cubeService.getAllTable("", securityKey);
        Assert.assertEquals(tables, actualTables);

        // 判断抛出异常
        try {
            Mockito.when(dsService.getDsDefine(Mockito.anyString())).thenReturn(null);
            cubeService.getAllTable("", securityKey);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }
    
    /**
     * 
     */
    @Test
    public void testInitCubeTables() throws Exception {

        List<TableInfo> tables = Lists.newArrayList();
        TableInfo table = new TableInfo();
        table.setId("TESTTABLE");
        table.setName("TESTTABLE");
        tables.add(table);

        // 获取该数据源
        Mockito.when(dsService.getDsDefine(Mockito.anyString())).thenReturn(dataSourceDefine);

        List<String> tableId = Lists.newArrayList();
        tableId.add("TESTTABLE");

        List<String> regex = Lists.newArrayList();
        regex.add("^[A-Za-z0-9]+$");

        FactTableMetaDefine factTableMetaDefine = new FactTableMetaDefine();
        factTableMetaDefine.setCubeId("^[A-Za-z0-9]+$");
        factTableMetaDefine.setMutilple(true);
        factTableMetaDefine.setName("^[A-Za-z0-9]+$");
        factTableMetaDefine.setRegExp("^[A-Za-z0-9]+$");
        factTableMetaDefine.setRegExpTables(null);
        factTableMetaDefine.setSchemaId(null);
        
        ColumnMetaDefine column = new ColumnMetaDefine();
        column.setCaption("COL1");
        column.setName("COL1");
        factTableMetaDefine.addColumn(column);
        
        List<FactTableMetaDefine> expectFactTable = Lists.newArrayList();
        expectFactTable.add(factTableMetaDefine);
        
        List<FactTableMetaDefine> actualFactTable = cubeService.initCubeTables("", tableId, regex, securityKey);
        Assert.assertNotNull(actualFactTable);
        Assert.assertEquals(expectFactTable, actualFactTable);
        
        // other Key
        expectFactTable = Lists.newArrayList();
        List<String> regexNew = Lists.newArrayList();
        regexNew.add("^[0-9]*$");
        factTableMetaDefine.setCubeId("TESTTABLE");
        factTableMetaDefine.setMutilple(false);
        factTableMetaDefine.setName("TESTTABLE");
        factTableMetaDefine.setRegExp(null);
        expectFactTable.add(factTableMetaDefine);
        actualFactTable = cubeService.initCubeTables("", tableId, regexNew, securityKey);
        Assert.assertNotNull(actualFactTable);
        Assert.assertEquals(expectFactTable, actualFactTable);
        
        
        LOG.info(actualFactTable.toString());
    }
}
