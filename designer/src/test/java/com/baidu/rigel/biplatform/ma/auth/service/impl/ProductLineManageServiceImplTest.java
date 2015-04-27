package com.baidu.rigel.biplatform.ma.auth.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.baidu.rigel.biplatform.ma.auth.bo.ProductlineInfo;
import com.baidu.rigel.biplatform.ma.auth.service.ProductLineManageService;
import com.baidu.rigel.biplatform.ma.file.client.service.FileService;
import com.baidu.rigel.biplatform.ma.file.client.service.FileServiceException;
import com.google.gson.Gson;





/**
 * 
 * @author jiangyichao
 * 测试ProductLineManageServiceImpl
 */
public class ProductLineManageServiceImplTest {
	
	/**
	 * 产品线管理
	 */
	@InjectMocks
	private ProductLineManageService productLineManageService = new ProductLineManageServiceImpl();
	
	/**
	 * 文件服务
	 */
	@Mock
	 private FileService fileService;
	
	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
	}
	
	/**
	 * 
	 */
	@Test
	public void testQueryUserNull() throws Exception {
		Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(null);
		Assert.assertNull(productLineManageService.queryUser(null, null));		
	}
	
	/**
	 * 
	 */
	@Test
	public void testQueryUserNameNotExist() throws Exception {
		Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
				new String[] {"name1", "name2"});
		Assert.assertNull(productLineManageService.queryUser("test", null));
	}
	
	/**
	 * 
	 */
	@Test
	public void testQueryUserNameThrowException() throws Exception {
		Mockito.when(fileService.ls(Mockito.anyString())).thenThrow(
				new FileServiceException(""));
		Assert.assertNull(productLineManageService.queryUser(null, null));
	}
	
	/**
	 * 
	 */
	@Test
	public void testQueryUserNameExistAndGsonTransFail() throws Exception {
		Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
				new String[] {"name1&pwd1", "name2&pwd2" });
		ProductlineInfo productlineInfo = new ProductlineInfo();
		productlineInfo.setName("name1");
		productlineInfo.setPwd("pwd1");
		Assert.assertNull(productLineManageService.queryUser("name1", "pwd1"));
	}
	
	/**
	 * 
	 */
	@Test
	public void testQueryUser() throws Exception {
		Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
				new String[] {"name1&pwd1", "name2&pwd2" });
		ProductlineInfo productlineInfo = new ProductlineInfo();
		productlineInfo.setName("name1");
		productlineInfo.setPwd("pwd1");
		productlineInfo.setDepartment("运营产品研发部");
		productlineInfo.setDesc("RD");
		productlineInfo.setEmail("jiang@163.com");
		productlineInfo.setServiceType(0);
		Gson gson = new Gson();
		String gsonString = gson.toJson(productlineInfo);
		Mockito.when(fileService.read(Mockito.anyString())).thenReturn(
				gsonString.getBytes());
		Assert.assertNotNull( productLineManageService.queryUser("name1", "pwd1"));
	}
	
	/**
	 * 
	 */
	@Test
	public void testSaveUserWithNameExist() throws Exception {
		Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
				new String[] {"name1&pwd1"});
		
		ProductlineInfo productlineInfo = new ProductlineInfo();
		productlineInfo.setName("name1");
		productlineInfo.setPwd("pwd1");
		productlineInfo.setDepartment("运营产品研发部");
		productlineInfo.setDesc("RD");
		productlineInfo.setEmail("jiang@163.com");
		productlineInfo.setServiceType(0);
		
		Assert.assertFalse(productLineManageService.saveUser(productlineInfo));
		
	}
	
	/**
	 * 
	 */
	@Test
	public void testSaveUserThrowException() throws Exception {
		Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
				new String[] {"name1&pwd1"});
		Mockito.when(fileService.write(Mockito.anyString(), Mockito.anyObject(), 
				Mockito.anyBoolean())).thenThrow(new FileServiceException(""));
		ProductlineInfo productlineInfo = new ProductlineInfo();
		productlineInfo.setName("name2");
		productlineInfo.setPwd("pwd2");
		productlineInfo.setDepartment("运营产品研发部");
		productlineInfo.setDesc("RD");
		productlineInfo.setEmail("jiang@163.com");
		productlineInfo.setServiceType(0);
		
		Assert.assertFalse(productLineManageService.saveUser(productlineInfo));
	}
	
	/**
	 *
	 */
	@Test
	public void testSaveUser() throws Exception {
		Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
				new String[] {"name1&pwd1"});
		Mockito.when(fileService.write(Mockito.anyString(), Mockito.anyObject())).thenReturn(true);
		ProductlineInfo productlineInfo = new ProductlineInfo();
		productlineInfo.setName("name2");
		productlineInfo.setPwd("pwd2");
		productlineInfo.setDepartment("运营产品研发部");
		productlineInfo.setDesc("RD");
		productlineInfo.setEmail("jiang@163.com");
		productlineInfo.setServiceType(0);
		
		Assert.assertTrue(productLineManageService.saveUser(productlineInfo));
	}
}
