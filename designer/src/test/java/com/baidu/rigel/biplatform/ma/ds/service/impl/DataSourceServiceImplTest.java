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
import org.springframework.util.SerializationUtils;

import com.baidu.rigel.biplatform.ac.util.AesUtil;
import com.baidu.rigel.biplatform.ma.ds.exception.DataSourceOperationException;
import com.baidu.rigel.biplatform.ma.file.client.service.FileService;
import com.baidu.rigel.biplatform.ma.file.client.service.FileServiceException;
import com.baidu.rigel.biplatform.ma.model.consts.DatasourceType;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
/**
 * 
 * @author jiangyichao
 * DataSourceServiceImpl测试
 */
//@RunWith(PowerMockRunner.class)
//@PrepareForTest(DataSourceServiceImpl.class)
public class DataSourceServiceImplTest {
	   /**
     * dataSourceService
     */
	@InjectMocks
    private DataSourceServiceImpl dataSourceService = new DataSourceServiceImpl();
    
    /**
     * FileService
     */
    @Mock
    private FileService fileService;
    
    /**
     * 
     */
    @Before
    public void before() {
    	MockitoAnnotations.initMocks(this);
       // fileService = Mockito.mock(FileService.class);
       // dataSourceService.setFileService(fileService);
    }
    
    /**
     * 测试返回数组length为0
     */
    @Test
    public void testIsNameExistWithEmptyDir() throws Exception {
        Mockito.doReturn(new String[] {}).when(fileService).ls(Mockito.anyString());
        try {
            Assert.assertFalse(dataSourceService.isNameExist("test"));
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
			dataSourceService.isNameExist("test");
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
            Assert.assertFalse(dataSourceService.isNameExist("test"));
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
            Assert.assertFalse(dataSourceService.isNameExist("test"));
        } catch (Exception e) {
            Assert.assertNotNull(e);
            Assert.fail();
        }
    }
    
    /**
     * 
     */
    @Test
    public void testIsNameExist() throws Exception {
        Mockito.doReturn(new String[] { "test", "abcdefg" }).when(fileService).ls(Mockito.anyString());
        try {
            Assert.assertTrue(dataSourceService.isNameExist("test"));
        } catch (Exception e) {
            Assert.assertNotNull(e);
            Assert.fail();
        }
    }
    
    /**
     * 
     */
    @Test
    public void testGetDefineWithEmptyDir() throws Exception {
		Mockito.doReturn(new String[] {}).when(fileService).ls(Mockito.anyString());		
		Assert.assertNull(dataSourceService.getDsDefine("test"));
	}
    
    /**
     * 
     */
    @Test
    public void testGetDefineWithNullDir() throws Exception {
        Mockito.doReturn(null).when(fileService).ls(Mockito.anyString());
        Assert.assertNull(dataSourceService.getDsDefine("test"));
    }
    
    /**
     * 
     */
    @Test
    public void testGetDefineThrowException() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenThrow(
    			new FileServiceException("FileServiceException"));
    	dataSourceService.getDsDefine("test");
    }
    
    /**
     * 
     */
    @Test
    public void testGetDefineFileNotExist() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_test", "abc_abc"});
    	Assert.assertNull(dataSourceService.getDsDefine("test_test"));
    }
    
    /**
     * 
     */
    @Test
    public void testGetDefineWithNameExist() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_test", "abc_abc"});
    	Mockito.when(fileService.read(Mockito.anyString())).thenReturn(
    			SerializationUtils.serialize(new DataSourceDefine()));
    	Assert.assertNotNull(dataSourceService.getDsDefine("test"));
    }
    
    
    /**
     * 
     */
    @Test
    public void testGetDefineWithEmptyDirUnderProductLine() throws Exception {
		Mockito.doReturn(new String[] {}).when(fileService).ls(Mockito.anyString());		
		Assert.assertNull(dataSourceService.getDsDefine("productLine", "test"));
	}
    
    /**
     * 
     */
    @Test
    public void testGetDefineWithNullDirUnderProductLine() throws Exception {
        Mockito.doReturn(null).when(fileService).ls(Mockito.anyString());
        Assert.assertNull(dataSourceService.getDsDefine("productLine", "test"));
    }
    
    /**
     * 
     */
    @Test
    public void testGetDefineThrowExceptionUnderProductLine() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenThrow(
    			new FileServiceException("FileServiceException"));
    	dataSourceService.getDsDefine("productLine", "test");
    }
    
    /**
     * 
     */
    @Test
    public void testGetDefineFileNotExistUnderProductLine() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_test", "abc_abc"});
    	Assert.assertNull(dataSourceService.getDsDefine("productLine", "test_test"));
    }
    
    /**
     * 
     */
    @Test
    public void testGetDefineWithNameExistUnderProductLine() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_test", "abc_abc"});
    	Mockito.when(fileService.read(Mockito.anyString())).thenReturn(
    			SerializationUtils.serialize(new DataSourceDefine()));
    	Assert.assertNotNull(dataSourceService.getDsDefine("productLine", "test"));
    }
    
    /**
     * 
     */
    @Test
    public void testListAllWithNullDir() throws Exception {
        Mockito.doReturn(null).when(fileService).ls("null/null");
        Assert.assertEquals(0, dataSourceService.listAll().length);
    }
    
    /**
     * 
     */
    @Test
    public void testListAllWithEmptyDir() throws Exception {
        Mockito.doReturn(new String[] {}).when(fileService).ls("null/null");
        Assert.assertEquals(0, dataSourceService.listAll().length);
    }
    
    /**
     * listAll()，获取所有数据源定义
     */
    @SuppressWarnings("deprecation")
	@Test
    public void testListAllWithNullDataSourceDefine() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(null);
    	Assert.assertEquals(new DataSourceDefine[0], dataSourceService.listAll());
    }
    
    /**
     * 测试listAll
     */
    @SuppressWarnings("deprecation")
	@Test
    public void testListAllWithFileReadException() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] {"testListAll"});
    	Mockito.when(fileService.read(Mockito.anyString())).thenThrow(new FileServiceException(""));
    	Assert.assertEquals(new DataSourceDefine[0], dataSourceService.listAll());
    }
    
    /**
     * 测试listAll抛出异常
     */
    @Test
    public void testListAllThrowException() throws Exception {
    	Mockito.when(fileService.ls(Mockito.anyString())).thenThrow(
    			new FileServiceException(""));
    	try {
    		dataSourceService.listAll();
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
    			SerializationUtils.serialize(new DataSourceDefine()));
    	Assert.assertNotNull(dataSourceService.listAll());
    	
    }
    /**
     * 
     */
    @Test
    public void testIsValidateConnWithNull() {
//        Assert.assertFalse(dataSourceService.isValidateConn(null, null));
    }
    
    /**
     * 测试数据库连接
     */
    @Test
    public void testIsValidateConn() throws Exception {
    	// 数据库连接基本信息，并构建内存数据库H2
    	String username = "test";
    	String password = "test";
    	String dbInstance = "testDB";
    	String name = "testDB";
    	String securityKey = "0000000000000000";
    	String passwordEncrypt = AesUtil.getInstance().encryptAndUrlEncoding(password, securityKey);
    	String url = "jdbc:h2:mem:" + dbInstance + ";";
    	try {
    		Class.forName("org.h2.Driver");
    		Connection conn = DriverManager.getConnection(url,username,password); 
    		if (conn != null) {
    			System.out.println("get H2 datasource by username:" + username);    			
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	// 测试空数据源定义对象
    	try {
    		dataSourceService.isValidateConn(null, securityKey);    		
    	} catch(Exception e) {
    		Assert.assertNotNull(e);
    	}
    	// 创建数据源定义对象
    	DataSourceDefine dataSourceDefine = new DataSourceDefine();
    	DatasourceType.H2.setPrefix("jdbc:h2:mem://");
    	dataSourceDefine.setType(DatasourceType.H2);
    	dataSourceDefine.setDbUser(username);
    	dataSourceDefine.setDbPwd(passwordEncrypt);
    	dataSourceDefine.setDbInstance(dbInstance);
    	dataSourceDefine.setName(name);
    	dataSourceDefine.setHostAndPort("127.0.0.1:3306");
    	// dataSourceDefine.setEncoding("utf8");
    	// 测试合法数据定义
    	Assert.assertTrue(dataSourceService.isValidateConn(dataSourceDefine, securityKey));
    	
    	// 以未加密密码访问
    	dataSourceDefine.setDbPwd(password);
    	try {
    		dataSourceService.isValidateConn(dataSourceDefine, securityKey);    		
    	} catch (Exception e) {
    		Assert.assertNotNull(e);
    	}
    	// 设置正确的数据库前缀
    	DatasourceType.H2.setPrefix("jdbc:h2:tcp://");    	
    }
    
    /**
     * 
     */
    @Test
    public void testRemoveDsWithEmptyDir() throws Exception {
    	// 空数据源定义
    	Mockito.when(dataSourceService.getDsDefine(Mockito.anyString())).thenReturn(
    			null);
    	try {
    		dataSourceService.removeDataSource("test");
    	} catch (Exception e) {
    		Assert.assertNotNull(e);
    	}
    	// 数据源删除成功
    	DataSourceDefine ds = new DataSourceDefine();
    	ds.setId("test");
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_test", "abc_abc"});
    	Mockito.when(fileService.read(Mockito.anyString())).thenReturn(
    			SerializationUtils.serialize(ds));
    	Mockito.when(fileService.rm(Mockito.anyString())).thenReturn(true);
    	Assert.assertTrue(dataSourceService.removeDataSource("test"));
    	
    	// 数据源删除失败
    	ds.setId("test");
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_test", "abc_abc"});
    	Mockito.when(fileService.read(Mockito.anyString())).thenReturn(
    			SerializationUtils.serialize(ds));
    	Mockito.when(fileService.rm(Mockito.anyString())).thenReturn(false);
    	Assert.assertFalse(dataSourceService.removeDataSource("test"));
    	
    	// 删除数据源抛出异常
    	ds.setId("test");
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_test", "abc_abc"});
    	Mockito.when(fileService.read(Mockito.anyString())).thenReturn(
    			SerializationUtils.serialize(ds));
    	Mockito.when(fileService.rm(Mockito.anyString())).thenThrow(
    			new FileServiceException(""));
    	try {
    		Assert.assertTrue(dataSourceService.removeDataSource("test"));    		
    	} catch (Exception e) {
    		Assert.assertNotNull(e);
    	}
    	
    	// 测试read抛出异常
    	ds.setId("test");
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_test", "abc_abc"});
    	Mockito.when(fileService.read(Mockito.anyString())).thenThrow(
    			new FileServiceException(""));
    	try {
    		Assert.assertTrue(dataSourceService.removeDataSource("test"));    		
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
    		dataSourceService.saveOrUpdateDataSource(null, null);
    	} catch (DataSourceOperationException e) {
    		Assert.assertNotNull(e);
    	}
    	// 测试数据源对象不含有产品线
    	try {
    		dataSourceService.saveOrUpdateDataSource(new DataSourceDefine(), null);
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
    			System.out.println("get H2 datasource by username:" + username);    			
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	// 创建数据源定义对象
    	DataSourceDefine dataSourceDefine = new DataSourceDefine();
    	DatasourceType.H2.setPrefix("jdbc:h2:mem://");
    	dataSourceDefine.setType(DatasourceType.H2);
    	dataSourceDefine.setDbUser(username);
    	dataSourceDefine.setId(dbID);
    	dataSourceDefine.setDbPwd(passwordEncrypt);
    	dataSourceDefine.setDbInstance(dbInstance);
    	dataSourceDefine.setName(name);
    	dataSourceDefine.setHostAndPort("127.0.0.1:3306");
    	dataSourceDefine.setProductLine("productLine");
    	// 测试数据源名称已经存在
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test", "abc"});
    	try {
    		dataSourceService.saveOrUpdateDataSource(dataSourceDefine, securityKey);    		
    	} catch (DataSourceOperationException e) {
    		Assert.assertNotNull(e);
    	}
    	//修改数据库地址，导致连接抛出异常，数据源无法连接
    	dataSourceDefine.setHostAndPort("127.0.0.10:3306");
    	try {
    		dataSourceService.saveOrUpdateDataSource(dataSourceDefine, securityKey);    		
    	} catch (DataSourceOperationException e) {
    		Assert.assertNotNull(e);
    	}
    	
    	// 测试数据源有效，并执行保存操作
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"abc_abc"});
    	dataSourceDefine.setHostAndPort("127.0.0.1:3306");
    	Assert.assertEquals(dataSourceDefine, dataSourceService.saveOrUpdateDataSource(dataSourceDefine, securityKey)); 
    	
    	// 测试数据源有效，并执行更新操作
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_abc"});
    	dataSourceDefine.setId("test");
    	Mockito.when(fileService.read(Mockito.anyString())).thenReturn(
    			SerializationUtils.serialize(dataSourceDefine));
    	dataSourceDefine.setName("newName");
    	Assert.assertEquals(dataSourceDefine, dataSourceService.saveOrUpdateDataSource(dataSourceDefine, securityKey)); 
    	
    	// 修改数据源，新名称存在
    	Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
    			new String[] {"test_abc", "newName_newName"});
    	dataSourceDefine.setId("test");
    	Mockito.when(fileService.read(Mockito.anyString())).thenReturn(
    			SerializationUtils.serialize(dataSourceDefine));
    	dataSourceDefine.setName("abc");
    	try {
    		dataSourceService.saveOrUpdateDataSource(dataSourceDefine, securityKey);     		
    	} catch(Exception e) {
    		Assert.assertNotNull(e);
    	}
    	
    	// 设置正确的数据库前缀
    	DatasourceType.H2.setPrefix("jdbc:h2:tcp://"); 
    }
}
