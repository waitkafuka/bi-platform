package com.baidu.rigel.biplatform.ma.model.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.rigel.biplatform.ac.util.AesUtil;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceType;
import com.baidu.rigel.biplatform.ma.model.meta.ColumnInfo;
import com.baidu.rigel.biplatform.ma.model.meta.TableInfo;
import com.google.common.collect.Lists;
public class DBInfoReaderTest {

    /**
     * LOG
     */
    private static final Logger LOG  = LoggerFactory.getLogger(DBInfoReaderTest.class);
    
    @Test
    public void testDBInfoReaderTest() throws Exception {
        // 数据库连接基本信息，并构建内存数据库H2
        String username = "test";
        String password = "test";
        String dbInstance = "testDB";
        String securityKey = "0000000000000000";
        String passwordEncrypt = AesUtil.getInstance().encryptAndUrlEncoding(password, securityKey);
        String url = "jdbc:h2:mem:" + dbInstance + ";";
        try {
            // 创建数据库
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection(url, username, password); 
            if (conn != null) {
                LOG.info("get H2 datasource by username:" + username);                
            }
            // 创建数据表
            String sql = "create table testTable (id int, col1 int, col2 int)";
            PreparedStatement st = conn.prepareStatement(sql);
            try {
                st.execute();
            } catch (Exception e) {
                LOG.error(e.getMessage());
            } 
            
            DBInfoReader dbInfoReader = null;
            // 使用错误用户名和密码
            try {
                dbInfoReader = 
                    DBInfoReader.build(DataSourceType.H2, "wrongName", passwordEncrypt, url, securityKey);
            } catch (Exception e) {
                Assert.assertNotNull(e);
            }
            
            // 使用正确的用户名和密码
            dbInfoReader = DBInfoReader.build(DataSourceType.H2, username, passwordEncrypt, url, securityKey);
            dbInfoReader.getDataBaseInformations();
            Assert.assertNotNull(dbInfoReader);
            
            // 获取所有数据表
            List<TableInfo> allTables = dbInfoReader.getAllTableInfos();
            List<TableInfo> expectTables = Lists.newArrayList();
            TableInfo testTable = new TableInfo();
            testTable.setName("TESTTABLE");
            testTable.setId("TESTTABLE");
            expectTables.add(testTable);
            Assert.assertEquals(expectTables, allTables);
            
            // 获取表中所有的列
            List<ColumnInfo> allColumns = dbInfoReader.getColumnInfos("TESTTABLE");
            List<ColumnInfo> expectColumns = Lists.newArrayList();
            ColumnInfo colID = new ColumnInfo();
            colID.setId("ID");
            colID.setName("ID");
            ColumnInfo col1 = new ColumnInfo();
            col1.setId("COL1");
            col1.setName("COL1");            
            ColumnInfo col2 = new ColumnInfo();
            col2.setId("COL2");
            col2.setName("COL2");
            expectColumns.add(colID);
            expectColumns.add(col1);
            expectColumns.add(col2);
            Assert.assertEquals(expectColumns, allColumns);
            
            // 获取数据库连接的元数据和连接信息
            Assert.assertNotNull(dbInfoReader.getCon());
            Assert.assertNotNull(dbInfoReader.getDbMetaData());
            
            // 关闭数据库连接
            if (dbInfoReader != null) {
                dbInfoReader.closeConn();
            }
            // 修改驱动名称，使其报异常
            DataSourceType.H2.setDriver("this is not the right driver");
            try {
                dbInfoReader = DBInfoReader.build(DataSourceType.H2, username, passwordEncrypt, url, securityKey);
            } catch (Exception e) {
                Assert.assertNotNull(e);
            }
            // 重新设置正确的driver
            DataSourceType.H2.setDriver("org.h2.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
