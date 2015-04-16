package com.baidu.rigel.biplatform.ma.ds.service.impl;



import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.baidu.rigel.biplatform.ma.ds.service.DataSourceService;
import com.baidu.rigel.biplatform.ma.file.client.service.FileService;
import com.baidu.rigel.biplatform.ma.file.client.service.FileServiceException;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;

/**
 * 
 * @author jiangyichao
 * DataSourceServiceImpl测试
 */
//@RunWith(PowerMockRunner.class)
//@PrepareForTest(DataSourceServiceImpl.class)
public class DataSourceServiceImplTest {

	@InjectMocks
	private DataSourceService dataSourceService = new DataSourceServiceImpl();
	@Mock
	private FileService fileService;	
    @Before
    public void init() {
    	MockitoAnnotations.initMocks(this);    	
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
    
    public void testListAll() {
    	
    }
    
}
