package com.baidu.rigel.biplatform.ma.report.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.baidu.rigel.biplatform.ma.file.client.service.FileService;
import com.baidu.rigel.biplatform.ma.file.client.service.FileServiceException;
import com.baidu.rigel.biplatform.ma.model.consts.Constants;
import com.baidu.rigel.biplatform.ma.report.service.ReportDesignModelService;

/**
 * 
 * @author jiangyichao
 * 测试ReportDesignModelService
 */

public class ReportDesignModelServiceImplTest {
	
    /**
     * reportDesignModelService
     */
	@InjectMocks
    private  ReportDesignModelService reportDesignModelService = new ReportDesignModelServiceImpl();
	
	@Mock
	private FileService fileService;
	
    
    @Before
    public void init() {
    	MockitoAnnotations.initMocks(this);    	
    }
    
    
    /**
	 * 测试创建报表名称为null
	 */
	@Test
	public void testReportNameWithNull() {
		Assert.assertFalse(reportDesignModelService.isNameExist(null));
	}
	
	/**
	 * 产品线下研发状态报表列表为空
	 */
	@Test 
	public void testReportNameWithEmptyDevDir() throws Exception {
		Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(null);
		Assert.assertFalse(reportDesignModelService.isNameExist("testName"));
	}
	
	/**
	 * 产品线下已经存在名称
	 */
	@Test
	public void testReportNameWithExists() throws Exception {
		Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(
				new String[] { "ID" + Constants.FILE_NAME_SEPERATOR + "reportName".hashCode() 
						+ Constants.FILE_NAME_SEPERATOR + "DsID", "." });
		Assert.assertFalse(reportDesignModelService.isNameExist("reportName"));
	}
	
	/**
	 * fileService的ls方法抛出异常
	 */
	@Test
	public void testReportNameWithThrowsException() throws Exception {
		Mockito.when(fileService.ls(Mockito.anyString())).thenThrow(new FileServiceException(""));
		Assert.assertFalse(reportDesignModelService.isNameExist("testName"));
	}
}
