package com.baidu.rigel.biplatform.ma.model.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.rigel.biplatform.ac.minicube.DivideTableStrategyVo;
import com.baidu.rigel.biplatform.ac.util.AesUtil;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceService;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceType;
import com.baidu.rigel.biplatform.ma.model.meta.ColumnMetaDefine;
import com.baidu.rigel.biplatform.ma.model.meta.FactTableMetaDefine;
import com.baidu.rigel.biplatform.ma.model.meta.TableInfo;
import com.baidu.rigel.biplatform.ma.model.service.CubeMetaBuildService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
            String sql = "drop table if exists testTable; create table testTable(col1 int)";
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
        // 获取该数据源
        Mockito.when(dsService.getDsDefine(Mockito.anyString())).thenReturn(dataSourceDefine);

        List<String> tableId = Lists.newArrayList();
        tableId.add("TESTTABLE");

        List<String> regex = Lists.newArrayList();
        regex.add("^[A-Za-z0-9]+$");

        // 构建策略
        DivideTableStrategyVo strategy = this.buildDivideTableStrategy("yyyy");
        FactTableMetaDefine factTableMetaDefine = this.buildFactTable("^[A-Za-z0-9]+$", "^[A-Za-z0-9]+$",
                "^[A-Za-z0-9]+$", true, strategy);
        factTableMetaDefine.addColumn(this.buildColumn("COL1", "COL1"));
        
        List<FactTableMetaDefine> expectFactTable = Lists.newArrayList();
        expectFactTable.add(factTableMetaDefine);
        
        List<FactTableMetaDefine> actualFactTable = cubeService.initCubeTables("", tableId, regex, securityKey);
        Assert.assertNotNull(actualFactTable);
        Assert.assertEquals(expectFactTable, actualFactTable);
        
        // other Key
        expectFactTable = Lists.newArrayList();
        List<String> regexNew = Lists.newArrayList();
        regexNew.add("^[0-9]*$");
        factTableMetaDefine = this.buildFactTable("TESTTABLE", "TESTTABLE", null, false, strategy);
        factTableMetaDefine.addColumn(this.buildColumn("COL1", "COL1"));
        factTableMetaDefine.setDivideTableStrategyVo(null);
        expectFactTable.add(factTableMetaDefine);
        actualFactTable = cubeService.initCubeTables("", tableId, regexNew, securityKey);
        
        Assert.assertNotNull(actualFactTable);
        Assert.assertEquals(expectFactTable, actualFactTable);
    }
    
    /**
     * 测试分表情况下的初始化cube表
     * testInitCubeTablesForDivideTable
     */
    @Test
    public void testInitCubeTablesForDivideTable() throws Exception {
        // 获取该数据源
        Mockito.when(dsService.getDsDefine(Mockito.anyString())).thenReturn(dataSourceDefine);
        DivideTableStrategyVo strategy = this.buildDivideTableStrategy("yyyy");
        FactTableMetaDefine factTableMetaDefine = this.buildFactTable("TESTTABLE", "TESTTABLE", null, true, strategy);
        factTableMetaDefine.addColumn(this.buildColumn("COL1", "COL1"));
        List<FactTableMetaDefine> expectFactTable = Lists.newArrayList();
        expectFactTable.add(factTableMetaDefine);
        List<String> tableId = Lists.newArrayList();
        tableId.add("TESTTABLE");
        Map<String, DivideTableStrategyVo> strategys = Maps.newHashMap();
        strategys.put("TESTTABLE", strategy);
        List<FactTableMetaDefine> actualFactTable = cubeService.initCubeTables("", tableId, strategys, securityKey);
        Assert.assertEquals(expectFactTable, actualFactTable);
        
        factTableMetaDefine.setDivideTableStrategyVo(null);
        factTableMetaDefine.setMutilple(false);
        strategys.clear();
        actualFactTable = cubeService.initCubeTables("", tableId, strategys, securityKey);
        Assert.assertEquals(expectFactTable, actualFactTable);
    }
    
    @After
    public void after() {
        // 设置正确的数据库前缀
        DataSourceType.H2.setPrefix("jdbc:h2:tcp://");  
        DataSourceType.H2.setDriver("org.h2.Driver");
    }
    /**
     * 构建事实表
     * buildFactTable
     * @param cubeId
     * @param name
     * @param regexp
     * @param isMultiple
     * @param strategy
     * @return
     */
    private FactTableMetaDefine buildFactTable(String cubeId, String name, 
            String regexp, boolean isMultiple, DivideTableStrategyVo strategy) {
        FactTableMetaDefine factTableMetaDefine = new FactTableMetaDefine();
        factTableMetaDefine.setCubeId(cubeId);
        factTableMetaDefine.setMutilple(isMultiple);
        factTableMetaDefine.setName(name);
        factTableMetaDefine.setRegExp(regexp);
        factTableMetaDefine.setRegExpTables(null);
        factTableMetaDefine.setSchemaId(null);
        factTableMetaDefine.setDivideTableStrategyVo(strategy);
        return factTableMetaDefine;
    }
    /**
     * 构建事实表列
     * buildColumn
     * @param caption
     * @param name
     * @return
     */
    private ColumnMetaDefine buildColumn(String caption, String name) {
        ColumnMetaDefine column = new ColumnMetaDefine();
        column.setCaption(caption);
        column.setName(name);
        return column;
    }
    /**
     * 构建时间分表策略
     * buildDivideTableStrategy
     * @param granularity粒度
     * @return
     */
    private DivideTableStrategyVo buildDivideTableStrategy(String granularity) {
        DivideTableStrategyVo strategy = new DivideTableStrategyVo();
        strategy.setType("time");
        strategy.setPrefix("table_");
        strategy.setCondition(granularity);
        return strategy;
    }
}
