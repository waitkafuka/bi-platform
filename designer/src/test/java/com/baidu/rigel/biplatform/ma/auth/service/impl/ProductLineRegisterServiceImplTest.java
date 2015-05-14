package com.baidu.rigel.biplatform.ma.auth.service.impl;



import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.rigel.biplatform.ac.util.AesUtil;
import com.baidu.rigel.biplatform.ma.auth.bo.ProductlineInfo;
import com.baidu.rigel.biplatform.ma.auth.service.ProductLineManageService;
import com.baidu.rigel.biplatform.ma.auth.service.ProductLineRegisterService;
import com.baidu.rigel.biplatform.ma.file.client.service.FileService;
import com.baidu.rigel.biplatform.ma.file.client.service.FileServiceException;

/**
 * 
 * @author jiangyichao
 * 产品线注册类测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "file:src/test/resources/applicationContext.xml")
public class ProductLineRegisterServiceImplTest {
	
	@InjectMocks
	@Resource
	private ProductLineRegisterService productLineRegisterService;
	@Mock
	private FileService fileService;
	@Mock
	private ProductLineManageService userManageService;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testSendRegisterMsgToAdministrator() throws Exception {
		ProductlineInfo productlineInfo = new ProductlineInfo();
		productlineInfo.setName("test");
		productlineInfo.setPwd(AesUtil.getInstance().
				encryptAndUrlEncoding("test", "0000000000000000"));
		productlineInfo.setEmail("test20150401@sina.com");
		productlineInfo.setServiceType(1);
		productlineInfo.setDepartment("test");
		
		String hostAddress = "127.0.0.1";
		String magicStr = String.valueOf(System.nanoTime());
		// 测试线上服务
		Assert.assertEquals(0, productLineRegisterService
				.sendRegisterMsgToAdministrator(productlineInfo, hostAddress,
						magicStr));
		// 测试线下服务
		productlineInfo.setServiceType(0);
		Assert.assertEquals(0, productLineRegisterService
				.sendRegisterMsgToAdministrator(productlineInfo, hostAddress,
						magicStr));
		
		// 测试解密异常
		productlineInfo.setPwd("test");
		Assert.assertEquals(-1, productLineRegisterService
				.sendRegisterMsgToAdministrator(productlineInfo, hostAddress,
						magicStr));
	}
	
	@Test
	public void testSendOpenServiceMsgToUser() throws Exception {
		ProductlineInfo productlineInfo = new ProductlineInfo();
		productlineInfo.setName("test");
		productlineInfo.setPwd(AesUtil.getInstance().
				encryptAndUrlEncoding("test", "0000000000000000"));
		productlineInfo.setEmail("test20150401@sina.com");
		productlineInfo.setServiceType(1);
		productlineInfo.setDepartment("test");
		Assert.assertEquals(0, productLineRegisterService.sendOpenServiceMsgToUser(productlineInfo, 1));
		
		Assert.assertEquals(0, productLineRegisterService.sendOpenServiceMsgToUser(productlineInfo, 0));
		
		productlineInfo.setPwd("test");
		Assert.assertEquals(-1, productLineRegisterService.sendOpenServiceMsgToUser(productlineInfo, 1));
		
	}
	
	@Test 
	public void testOpenServiceFailed() throws Exception {
		ProductlineInfo productlineInfo = new ProductlineInfo();
		productlineInfo.setName("test");
		productlineInfo.setPwd(AesUtil.getInstance().
				encryptAndUrlEncoding("test", "0000000000000000"));
		productlineInfo.setEmail("test20150401@sina.com");
		productlineInfo.setServiceType(1);
		productlineInfo.setDepartment("test");
		Mockito.when(fileService.mkdir(Mockito.anyString())).thenThrow(new FileServiceException(""));
		Assert.assertEquals(-1, productLineRegisterService.openOnlineService(productlineInfo));
		productlineInfo.setServiceType(0);
		Assert.assertEquals(-1, productLineRegisterService.openOfflineService(productlineInfo));
	}
	
	@Test 
	public void testOpenServiceSuccess() throws Exception {
		ProductlineInfo productlineInfo = new ProductlineInfo();
		productlineInfo.setName("test");
		productlineInfo.setPwd(AesUtil.getInstance().
				encryptAndUrlEncoding("test", "0000000000000000"));
		productlineInfo.setEmail("test20150401@sina.com");
		productlineInfo.setServiceType(1);
		productlineInfo.setDepartment("test");
		
		Mockito.when(fileService.mkdir(Mockito.anyString())).thenReturn(true);
		Mockito.when(userManageService.saveUser(Mockito.anyObject())).thenReturn(true);
		Assert.assertEquals(0, productLineRegisterService.openOnlineService(productlineInfo));
		productlineInfo.setServiceType(0);
		Assert.assertEquals(0, productLineRegisterService.openOfflineService(productlineInfo));
	}
}
