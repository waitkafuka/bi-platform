package com.baidu.rigel.biplatform.ma.ds.service.impl;



import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import com.baidu.rigel.biplatform.ac.util.AesUtil;
import com.baidu.rigel.biplatform.api.client.service.FileService;
import com.baidu.rigel.biplatform.api.client.service.FileServiceException;
import com.baidu.rigel.biplatform.ma.ds.exception.DataSourceOperationException;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceService;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceGroupDefine;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceType;
/**
 * 
 * @author jiangyichao
 * DataSourceGroupServiceImpl测试
 */
public class DataSourceGroupServiceImplTest {
	
	/**
	 * 日志对象
	 */
	private static final Logger LOG = LoggerFactory.getLogger(DataSourceGroupServiceImplTest.class);
	   /**
     * dataSourceGroupService
     */
	@InjectMocks
    private DataSourceGroupServiceImpl dsGService = new DataSourceGroupServiceImpl();
    
    /**
     * FileService
     */
    @Mock
    private FileService fileService;
    
    /**
     * 
     */
    @Mock
	private DataSourceService dsService;
    /**
     * 
     */
    @Before
    public void before() {
    	MockitoAnnotations.initMocks(this);
    }
    
    /**
     * 测试返回数组length为0
     */
    @Test
    public void testIsNameExistWithEmptyDir() throws Exception {
        Mockito.doReturn(new String[] {}).when(fileService).ls(Mockito.anyString());
        try {
            Assert.assertFalse(dsGService.isNameExist("test"));
        } catch (Exception e) {
            Assert.assertNotNull(e);
            Assert.fail();
        }
    }
    
    /**
     * 测试ls抛出异常
     */
    @Test
	public void testIsNameExistThrowException() throws Exception {
		Mockito.when(fileService.ls(Mockito.anyString())).thenThrow(
				new FileServiceException(""));
		try {
			dsGService.isNameExist("test");
		} catch (DataSourceOperationException e) {
			Assert.assertNotNull(e);
		} catch (Exception e) {
			Assert.fail();
		}
    }
    /**
     * 测试文件夹内容为null
     */
    @Test
    public void testIsNameExistWithNullDir() throws Exception {
        Mockito.doReturn(null).when(fileService).ls(Mockito.anyString());
        try {
            Assert.assertFalse(dsGService.isNameExist("test"));
        } catch (Exception e) {
            Assert.assertNotNull(e);
            Assert.fail();
        }
    }
    
    /**
     * 测试名称不存在
     */
    @Test
    public void testIsNameExistWithDir() throws Exception {
        Mockito.doReturn(new String[] { "a", "b" }).when(fileService).ls(Mockito.anyString());
        try {
            Assert.assertFalse(dsGService.isNameExist("test"));
        } catch (Exception e) {
            Assert.assertNotNull(e);
            Assert.fail();
        }
    }
    
    /**
     * 测试 名称存在
     */
    @Test
    public void testIsNameExist() throws Exception {
        Mockito.doReturn(new String[] { "test", "abcdefg" }).when(fileService).ls(Mockito.anyString());
        try {
            Assert.assertTrue(dsGService.isNameExist("test"));
        } catch (Exception e) {
            Assert.assertNotNull(e);
            Assert.fail();
        }
    }
    
    /**
     * 获取数据源组定义，目录为空
     */
    @Test
    public void testGetDsGDefineWithEmptyDir() throws Exception {
		Mockito.doReturn(new String[] {}).when(fileService).ls(Mockito.anyString());		
		Assert.assertNull(dsGService.getDataSourceGroupDefine("test"));
	}
    
    /**
     * 获取数据源组定义，目录为null
     */
    @Test
    public void testGetDsGDefineWithNullDir() throws Exception {
        Mockito.doReturn(null).when(fileService).ls(Mockito.anyString());
        Assert.assertNull(dsGService.getDataSourceGroupDefine("test"));
    }
    
    /**
     * 获取数据源组定义，抛出异常
     */
    @Test
    public void testGetDsGDefineThrowException() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenThrow(
    			new FileServiceException("FileServiceException"));
    	dsGService.getDataSourceGroupDefine("test");
    }
    
    /**
     * 文件不存在
     */
    @Test
    public void testGetDsGDefineFileNotExist() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_test", "abc_abc"});
    	Assert.assertNull(dsGService.getDataSourceGroupDefine("cde"));
    }
    
    /**
     * 文件存在
     */
    @Test
    public void testGetDGDefineWithNameExist() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_test", "abc_abc"});
    	Mockito.when(fileService.read(Mockito.anyString())).thenReturn(
    			SerializationUtils.serialize(new DataSourceDefine()));
    	Assert.assertNotNull(dsGService.getDataSourceGroupDefine("test"));
    }
    
    
    /**
     * 增加产品线参数后的调用
     */
    @Test
    public void testGetDsGDefineWithEmptyDirUnderProductLine() throws Exception {
		Mockito.doReturn(new String[] {}).when(fileService).ls(Mockito.anyString());		
		Assert.assertNull(dsGService.getDataSourceGroupDefine("productLine", "test"));
	}
    
    /**
     * 
     */
    @Test
    public void testGetDsGDefineWithNullDirUnderProductLine() throws Exception {
        Mockito.doReturn(null).when(fileService).ls(Mockito.anyString());
        Assert.assertNull(dsGService.getDataSourceGroupDefine("productLine", "test"));
    }
    
    /**
     * 
     */
    @Test
    public void testGetDsGDefineThrowExceptionUnderProductLine() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] {"test_test"});
    	Mockito.when(fileService.read(Mockito.anyString())).thenThrow(
    			new FileServiceException("FileServiceException"));
    	try {
    		dsGService.getDataSourceGroupDefine("productLine", "test");    		
    	} catch (Exception e) {
    		Assert.assertNotNull(e);
    	}
    }
    
    /**
     * 
     */
    @Test
    public void testGetDefineFileNotExistUnderProductLine() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_test", "abc_abc"});
    	Assert.assertNull(dsGService.getDataSourceGroupDefine("productLine", "cde_cde"));
    }
    
    /**
     * 
     */
    @Test
    public void testGetDsGDefineWithNameExistUnderProductLine() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_test", "abc_abc"});
    	Mockito.when(fileService.read(Mockito.anyString())).thenReturn(
    			SerializationUtils.serialize(new DataSourceDefine()));
    	Assert.assertNotNull(dsGService.getDataSourceGroupDefine("productLine", "test"));
    }
    
    /**
     * 
     */
    @Test
    public void testListAllWithNullDir() throws Exception {
        Mockito.doReturn(null).when(fileService).ls("null/null");
        Assert.assertEquals(0, dsGService.listAll().length);
    }
    
    /**
     * 
     */
    @Test
    public void testListAllWithEmptyDir() throws Exception {
        Mockito.doReturn(new String[] {}).when(fileService).ls("null/null");
        Assert.assertEquals(0, dsGService.listAll().length);
    }
    
    /**
     * listAll()，获取所有数据源定义
     */
	@Test
    public void testListAllWithNullDataSourceDefine() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(null);
    	Assert.assertArrayEquals(new DataSourceGroupDefine[0], dsGService.listAll());
    }
    
    /**
     * 测试listAll
     */
	@Test
    public void testListAllWithFileReadException() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] {"testListAll"});
    	Mockito.when(fileService.read(Mockito.anyString())).thenThrow(new FileServiceException(""));
    	Assert.assertArrayEquals(new DataSourceGroupDefine[0], dsGService.listAll());
    }
    
    /**
     * 测试listAll抛出异常
     */
    @Test
    public void testListAllThrowException() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenThrow(
    			new FileServiceException(""));
    	try {
    		dsGService.listAll();
    	} catch(DataSourceOperationException e) {
    		Assert.assertNotNull(e);
    	} catch(Exception e) {
    		Assert.fail();
    	}
    }
    /**
     * 测试ListAll
     */
    @Test
    public void testListAll() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"testList1", "testList2"});
    	Mockito.when(fileService.read(Mockito.anyString())).thenReturn(
    			SerializationUtils.serialize(new DataSourceGroupDefine()));
    	Assert.assertNotNull(dsGService.listAll());
    	
    }
    /**
     * 
     */
    @Test
    public void testIsValidateConnWithNull() throws Exception {
        Assert.assertFalse(dsGService.isValidate(null, null));
    }
    
    /**
     * 测试数据库连接
     */
    @Test
    public void testIsValidateConn() throws Exception {
    	String securityKey = "0000000000000000";
    	Mockito.when(dsService.isValidateConn(Mockito.anyObject(), Mockito.anyString())).thenReturn(false);
    	// 测试空数据源定义对象
    	try {
    		dsGService.isValidate(null, securityKey);    		
    	} catch(Exception e) {
    		Assert.assertNotNull(e);
    	}

    	DataSourceGroupDefine dsG = new DataSourceGroupDefine();
    	dsG.setId("id");
    	dsG.setName("name");
    	dsG.setProductLine("productLine");
    	DataSourceDefine ds = new DataSourceDefine();
    	dsG.addDataSourceDefine(ds);
    	dsG.setActiveDataSource(ds);
    	Mockito.when(dsService.isValidateConn(Mockito.anyObject(), Mockito.anyString())).thenReturn(true);
    	Assert.assertTrue(dsGService.isValidate(dsG, securityKey));
    	Mockito.when(dsService.isValidateConn(Mockito.anyObject(), Mockito.anyString())).thenReturn(false);
    	dsGService.isValidate(dsG, securityKey);    		   	
    }
    
    /**
     * 
     */
    @Test
    public void testRemoveDsGWithEmptyDir() throws Exception {
    	// 空数据源定义
    	Mockito.when(dsGService.getDataSourceGroupDefine(Mockito.anyString())).thenReturn(
    			null);
    	try {
    		dsGService.removeDataSourceGroup("test");
    	} catch (Exception e) {
    		Assert.assertNotNull(e);
    	}
    	// 数据源组删除成功
    	DataSourceGroupDefine dsG = new DataSourceGroupDefine();
    	dsG.setId("test");
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_test", "abc_abc"});
    	Mockito.when(fileService.read(Mockito.anyString())).thenReturn(
    			SerializationUtils.serialize(dsG));
    	Mockito.when(fileService.rm(Mockito.anyString())).thenReturn(true);
    	Assert.assertTrue(dsGService.removeDataSourceGroup("test"));
    	
    	// 数据源组删除失败
    	dsG.setId("test");
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_test", "abc_abc"});
    	Mockito.when(fileService.read(Mockito.anyString())).thenReturn(
    			SerializationUtils.serialize(dsG));
    	Mockito.when(fileService.rm(Mockito.anyString())).thenReturn(false);
    	Assert.assertFalse(dsGService.removeDataSourceGroup("test"));
    	
    	// 删除数据源抛出异常
    	dsG.setId("test");
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_test", "abc_abc"});
    	Mockito.when(fileService.read(Mockito.anyString())).thenReturn(
    			SerializationUtils.serialize(dsG));
    	Mockito.when(fileService.rm(Mockito.anyString())).thenThrow(
    			new FileServiceException(""));
    	try {
    		Assert.assertTrue(dsGService.removeDataSourceGroup("test"));    		
    	} catch (Exception e) {
    		Assert.assertNotNull(e);
    	}
    	
    	// 测试read抛出异常
    	dsG.setId("test");
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_test", "abc_abc"});
    	Mockito.when(fileService.read(Mockito.anyString())).thenThrow(
    			new FileServiceException(""));
    	try {
    		Assert.assertTrue(dsGService.removeDataSourceGroup("test"));    		
    	} catch (Exception e) {
    		Assert.assertNotNull(e);
    	}
    }
        
    /**
     * 
     */
    @Test
    public void testSaveOrUpdateDs() throws Exception {   	
    	// 测试空数据源对象
    	try {
    		dsGService.saveOrUpdateDataSourceGroup(null, null);
    	} catch (DataSourceOperationException e) {
    		Assert.assertNotNull(e);
    	}
    	// 测试数据源对象不含有产品线
    	try {
    		dsGService.saveOrUpdateDataSourceGroup(new DataSourceGroupDefine(), null);
    	} catch (DataSourceOperationException e) {
    		Assert.assertNotNull(e);
    	}
    	// 数据库连接基本信息，并构建内存数据库H2
    	String dbID = "dbID";
    	String username = "test";
    	String password = "test";
    	String dbInstance = "test";
    	String name = "test";
    	String securityKey = "0000000000000000";
    	String passwordEncrypt = AesUtil.getInstance().encryptAndUrlEncoding(password, securityKey);
    	String url = "jdbc:h2:mem:" + dbInstance + ";";
    	try {
    		Class.forName("org.h2.Driver");
    		Connection conn = DriverManager.getConnection(url,username,password); 
    		if (conn != null) {
    			LOG.info("get H2 datasource by username:" + username);    			
    		}
    	} catch (Exception e) {
    		LOG.error(e.getMessage(), e);
    	}
    	
    	// 创建数据源定义对象
    	DataSourceDefine dataSourceDefine = new DataSourceDefine();
    	DataSourceType.H2.setPrefix("jdbc:h2:mem://");
    	dataSourceDefine.setDataSourceType(DataSourceType.H2);
    	dataSourceDefine.setDbUser(username);
    	dataSourceDefine.setId(dbID);
    	dataSourceDefine.setDbPwd(passwordEncrypt);
    	dataSourceDefine.setDbInstance(dbInstance);
    	dataSourceDefine.setName(name);
    	dataSourceDefine.setHostAndPort("127.0.0.1:3306");
    	dataSourceDefine.setProductLine("productLine");
    	
    	DataSourceGroupDefine dsG = new DataSourceGroupDefine();
    	dsG.addDataSourceDefine(dataSourceDefine);
    	dsG.setActiveDataSource(dataSourceDefine);
    	dsG.setId(dbID);
    	dsG.setProductLine("productLine");
    	dsG.setName(username);
    	// 测试数据源组名称已经存在
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test", "abc"});
    	try {
    		dsGService.saveOrUpdateDataSourceGroup(dsG, securityKey);    		
    	} catch (DataSourceOperationException e) {
    		Assert.assertNotNull(e);
    	}
    	
    	// 测试数据源组无效，抛出异常
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"abc_abc"});
    	Mockito.when(dsService.isValidateConn(Mockito.anyObject(), Mockito.anyString())).thenReturn(false);   	
    	try {
    		Assert.assertEquals(dsG, dsGService.saveOrUpdateDataSourceGroup(dsG, securityKey));     		
    	} catch (Exception e) {
    		Assert.assertNotNull(e);
    	}
    	
    	
    	// 测试数据源组有效，并执行保存操作
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"abc_abc"});
    	Mockito.when(dsService.isValidateConn(Mockito.anyObject(), Mockito.anyString())).thenReturn(true);
    	
    	Assert.assertEquals(dsG, dsGService.saveOrUpdateDataSourceGroup(dsG, securityKey)); 
    	
    	// 测试数据源有效，并执行更新操作
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_abc"});
    	dsG.setId("test");
    	Mockito.when(fileService.read(Mockito.anyString())).thenReturn(SerializationUtils.serialize(dsG));
    	
    	dsG.setName("newName");
    	Assert.assertEquals(dsG, dsGService.saveOrUpdateDataSourceGroup(dsG, securityKey)); 
    	
    	// 测试数据源有效，并执行更新操作
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_abc", "newName_newName"});
    	dsG.setId("test");
    	dsG.setName(username);
    	Mockito.when(fileService.read(Mockito.anyString())).thenReturn(SerializationUtils.serialize(dsG));
    	
    	dsG.setName("newName");
    	try {
    		dsGService.saveOrUpdateDataSourceGroup(dsG, securityKey);    		
    	} catch(Exception e) {
    		Assert.assertNotNull(e);
    	}
    	
    	// 修改数据源，抛出异常
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_abc", "newName_newName"});
    	Mockito.when(fileService.write(Mockito.anyString(), 
    			Mockito.anyObject())).thenThrow(new FileServiceException(""));
    	dsG.setId("test");
    	dsG.setName("abc");
    	Mockito.when(fileService.read(Mockito.anyString())).thenReturn(
    			SerializationUtils.serialize(dsG));
    	Mockito.when(dsService.isValidateConn(Mockito.anyObject(), Mockito.anyString())).thenReturn(true);
    	try {
    		dsGService.saveOrUpdateDataSourceGroup(dsG, securityKey);     		
    	} catch(Exception e) {
    		Assert.assertNotNull(e);
    	}
        // 设置正确的数据库前缀
        DataSourceType.H2.setPrefix("jdbc:h2:tcp://");  
        DataSourceType.H2.setDriver("org.h2.Driver");
    }
    
    
    
    @Test
    public void testGetDsDefine() throws Exception {
    	// 数据库连接基本信息，并构建内存数据库H2
    	String dbID = "test";
    	String username = "test";
    	
    	// 创建数据源定义对象
    	DataSourceDefine dataSourceDefine = new DataSourceDefine();
    	dataSourceDefine.setId(dbID);
    	dataSourceDefine.setName(username);
    	dataSourceDefine.setProductLine("productLine");
    	// 定义数据源组
    	DataSourceGroupDefine dsG = new DataSourceGroupDefine();
    	dsG.addDataSourceDefine(dataSourceDefine);
    	dsG.setActiveDataSource(dataSourceDefine);
    	dsG.setId(dbID);
    	dsG.setProductLine("productLine");
    	dsG.setName(username);
    	
    	// 
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[]{"test_test"});
    	// 无法获取数据源组对象
    	Assert.assertNull(dsGService.getDataSourceDefine(dbID, dbID));
    	// 假定可以读取数据源组对象
    	Mockito.when(fileService.read(Mockito.anyString())).thenReturn(SerializationUtils.serialize(dsG));
    	// 获取数据源组的返回结果
    	DataSourceDefine actual = dsGService.getDataSourceDefine(dbID, dbID);
    	// 因为DataSourceDefine未覆盖equals方法，故使用此方法验证返回结果
    	Assert.assertEquals(dataSourceDefine.getId(), actual.getId());
    	Assert.assertEquals(dataSourceDefine.getName(), actual.getName());
    	Assert.assertEquals(dataSourceDefine.getProductLine(), actual.getProductLine());
    	// 验证不存在的子id
    	Assert.assertNull(dsGService.getDataSourceDefine(dbID, "notexists"));
    	
    }
    
    /**
     * 测试使用中文存储数据源名称
     */
    @Test
    public void testDsNameWithChinese() throws Exception {
        String securityKey = "0000000000000000";
        // 数据库连接基本信息
        String dsID = "数据源ID";
        String dsName = "数据源名称";
        
        // 创建数据源定义对象
        DataSourceDefine dataSourceDefine = new DataSourceDefine();
        dataSourceDefine.setId(dsID);
        dataSourceDefine.setName(dsName);
        dataSourceDefine.setProductLine("productLine");
        
        // 定义数据源组
        String dsGID = "数据源组ID";
        String dsGName = "数据源组名称";
        DataSourceGroupDefine dsG = new DataSourceGroupDefine();
        dsG.addDataSourceDefine(dataSourceDefine);
        dsG.setActiveDataSource(dataSourceDefine);
        dsG.setId(dsGID);
        dsG.setProductLine("productLine");
        dsG.setName(dsGName);
        
        // 保证当前目录下没有上述数据源组
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
                new String[] {"test_abc", "newName_newName"});
        Mockito.when(dsService.isValidateConn(Mockito.anyObject(), Mockito.anyString())).thenReturn(true);
        dsG = dsGService.saveOrUpdateDataSourceGroup(dsG, securityKey);
        
        // 假定已经写入上述数据源组
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
                new String[] {"test_abc", "newName_newName", "数据源组ID_数据组名称"});
        // 假定可以读取数据源组对象
        Mockito.when(fileService.read(Mockito.anyString())).thenReturn(
                SerializationUtils.serialize(dsG));
        DataSourceGroupDefine dsGNew = dsGService.getDataSourceGroupDefine("数据源组ID");
        
        Assert.assertEquals(dsG.getName(), dsGNew.getName());
        Assert.assertEquals(dsG.getId(), dsGNew.getId());
        Assert.assertEquals(dsG.getProductLine(), dsGNew.getProductLine());
    }
}
