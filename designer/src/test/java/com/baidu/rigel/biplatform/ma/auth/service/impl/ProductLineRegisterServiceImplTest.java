package com.baidu.rigel.biplatform.ma.auth.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.baidu.rigel.biplatform.ac.util.AesUtil;
import com.baidu.rigel.biplatform.ma.auth.bo.ProductlineInfo;
import com.baidu.rigel.biplatform.ma.auth.service.ProductLineManageService;
import com.baidu.rigel.biplatform.ma.auth.service.ProductLineRegisterService;
import com.baidu.rigel.biplatform.ma.file.client.service.FileService;

/**
 * 
 * @author jiangyichao
 * 产品线注册类测试
 */
public class ProductLineRegisterServiceImplTest {

	@InjectMocks
	private ProductLineRegisterService productLineRegisterService = new ProductLineRegisterServiceImpl();
	@Mock
	private FileService fileService;
	@Mock
	ProductLineManageService userManageService;
	/**
	 * 产品线信息
	 */
	private ProductlineInfo productlineInfo;
	@Before
	public void before() throws Exception {
		MockitoAnnotations.initMocks(this);
		productlineInfo = new ProductlineInfo();
		productlineInfo.setName("test");
		productlineInfo.setPwd(AesUtil.getInstance().
				encryptAndUrlEncoding("test", "0000000000000000"));
		productlineInfo.setEmail("test@mail.com");
		productlineInfo.setServiceType(1);
		productlineInfo.setDepartment("test");
	}
	
	@Test
	public void testSendRegisterMsgToAdministrator() {
		// String hostAddress = "127.0.0.1";
		// String magicStr = String.valueOf(System.nanoTime());

		
	}
	
	@Test
	public void testSendOpenServiceMsgToUser() {
		
	}
	
	@Test 
	public void testOpenOnlineService() {
		
	}
	
	@Test
	public void testOpenOfflineService() {
		
	}
}
