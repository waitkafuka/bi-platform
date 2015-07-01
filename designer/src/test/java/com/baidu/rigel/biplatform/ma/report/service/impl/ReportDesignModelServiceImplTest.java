package com.baidu.rigel.biplatform.ma.report.service.impl;

import java.io.File;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.SerializationUtils;

import com.baidu.rigel.biplatform.ac.util.DeepcopyUtils;
import com.baidu.rigel.biplatform.ma.PrepareModelObject4Test;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceService;
import com.baidu.rigel.biplatform.ma.file.client.service.FileService;
import com.baidu.rigel.biplatform.ma.file.client.service.FileServiceException;
import com.baidu.rigel.biplatform.ma.model.consts.Constants;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.report.service.ReportDesignModelService;
import com.baidu.rigel.biplatform.ma.report.utils.ContextManager;

/**
 * 
 * @author jiangyichao 测试ReportDesignModelService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/test/resources/applicationContext.xml")
public class ReportDesignModelServiceImplTest {

    /**
     * reportDesignModelService
     */
    @InjectMocks
    @Resource
    private ReportDesignModelService reportDesignModelService;

    /**
     * 文件服务
     */
    @Mock
    private FileService fileService;

    /**
     * 数据源服务
     */
    @Mock
    private DataSourceService dsService;

    @Value("${biplatform.ma.report.location}")
    private String reportBaseDir;

    /**
     * 预置对象
     */
    private ReportDesignModel reportDesignModel;

    /**
     * 初始化 init
     */
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        reportDesignModel = PrepareModelObject4Test.getReportDesignModel();
    }

    /**
     * 
     * testGetAllRepNullFile
     */
    @Test
    public void testGetAllRepNullFile() throws Exception {
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] {});
        Assert.assertArrayEquals(new ReportDesignModel[0],
                reportDesignModelService.queryAllModels(true));
    }

    /**
     * 
     * testGetAllRepExp
     * 
     * @throws Exception
     */
    @Test
    public void testGetAllRepExp() throws Exception {
        Mockito.when(fileService.ls(Mockito.anyString())).thenThrow(new FileServiceException("ls"));
        Assert.assertArrayEquals(new ReportDesignModel[0],
                reportDesignModelService.queryAllModels(true));
    }

    /**
     * 
     * testGetAllRep
     */
    @Test
    public void testGetAllRep() throws Exception {
        // 测试已发布
        this.test4QueryAllModel(true, "release");
        // 测试未发布
        this.test4QueryAllModel(false, "dev");

    }

    /**
     * 
     * test4QueryAllModel
     */
    private void test4QueryAllModel(boolean release, String dir) throws Exception {
        String name1 = "a_for_test";
        String name2 = "b_for_test";
        ReportDesignModel modelCopy = DeepcopyUtils.deepCopy(this.reportDesignModel);
        modelCopy.setName(name2);
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] { name1, name2 });
        Mockito.when(fileService.read(ContextManager.getProductLine() 
                + File.separator + this.reportBaseDir + File.separator 
                    + dir + File.separator + name1)).thenReturn(
                            SerializationUtils.serialize(this.reportDesignModel));
        Mockito.when(fileService.read(ContextManager.getProductLine() 
                + File.separator + this.reportBaseDir + File.separator 
                    + dir + File.separator + name2)).thenReturn(
                            SerializationUtils.serialize(modelCopy));
        ReportDesignModel[] actual = null;
        // 查询已发布所有的模型
        actual = reportDesignModelService.queryAllModels(release);
        Assert.assertNotNull(actual);
        Assert.assertEquals(2, actual.length);

        ReportDesignModel actual1 = actual[0];
        Assert.assertEquals(name1, actual1.getName());
        ReportDesignModel actual2 = actual[1];
        Assert.assertEquals(name2, actual2.getName());
    }

    /**
     * 
     * testGetModelByIdOrName
     */
    @Test
    public void testGetModelByIdOrName() throws Exception {
        String name = "test" + "^_^" + "a_for_test";
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] { name });
        Mockito.when(fileService.read(Mockito.anyString())).thenReturn(
                SerializationUtils.serialize(this.reportDesignModel));
        // 走发布分枝
        ReportDesignModel actual = reportDesignModelService.getModelByIdOrName("a_for_test", true);
        this.compareReportDesginModel(this.reportDesignModel, actual);
        // 走未发布分枝
        actual = reportDesignModelService.getModelByIdOrName("a_for_test", false);
        this.compareReportDesginModel(reportDesignModel, actual);
    }

    /**
     * 
     * testGetModelByIdNull
     * 
     * @throws Exception
     */
    @Test
    public void testGetModelByIdNull() throws Exception {
        // 文件列表长度为0
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[0]);
        Assert.assertNull(reportDesignModelService.getModelByIdOrName("a_for_test", true));
        // 文件列表为空
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(null);
        Assert.assertNull(reportDesignModelService.getModelByIdOrName("a_for_test", true));
        // 文件名错误
        String name = "test.test";
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] { name });
        Assert.assertNull(reportDesignModelService.getModelByIdOrName("a_for_test", true));
        // 获取文件列表出错
        Mockito.when(fileService.ls(Mockito.anyString())).thenThrow(
                new FileServiceException("test"));
        Assert.assertNull(reportDesignModelService.getModelByIdOrName("a_for_test", true));
    }

    /**
     * 比较两个模型是否相等 compareReportDesginModel
     * 
     * @param expect
     * @param actual
     */
    private void compareReportDesginModel(ReportDesignModel expect, ReportDesignModel actual) {
        Assert.assertEquals(expect.getName(), actual.getName());
        Assert.assertEquals(expect.getId(), actual.getId());
    }

    /**
     * 
     * testDelReport
     */
    @Test
    public void testDelReportFail() throws Exception {
        // 假定在未发布报表中获得的是null
        Mockito.when(this.reportDesignModelService
                .getModelByIdOrName(Mockito.anyString(), false)).thenReturn(null);
        Assert.assertFalse(reportDesignModelService.deleteModel("id", true));

        String name = "test" + "^_^" + "a_for_test";
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] { name });
        Mockito.when(fileService.read(Mockito.anyString())).thenReturn(
                SerializationUtils.serialize(this.reportDesignModel));
        Assert.assertFalse(reportDesignModelService.deleteModel("a_for_test", true));
    }

    /**
     * 
     * testDelRepRmFail
     */
    @Test
    public void testDelRepRmFail() throws Exception {
        String name = "a_for_test";
        this.prepare4DelRep(name, false);
        Assert.assertFalse(reportDesignModelService.deleteModel(name, true));
    }

    /**
     * 
     * prepare4DelRep
     * 
     * @param rmResult
     * @throws Exception
     */
    private void prepare4DelRep(String name, boolean rmResult) throws Exception {
        // 假定报表未发布
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] { name });
        Mockito.when(fileService.read(ContextManager.getProductLine() 
                + File.separator + this.reportBaseDir + File.separator 
                    + "dev" + File.separator + name)).thenReturn(
                            SerializationUtils.serialize(this.reportDesignModel));
        Mockito.when(fileService.read(ContextManager.getProductLine() 
                + File.separator + this.reportBaseDir + File.separator 
                    + "release" + File.separator + name)).thenReturn(null);
        Mockito.when(fileService.rm(Mockito.anyString())).thenReturn(rmResult);
    }

    /**
     * 
     * testDelRepSuccess
     * 
     * @throws Exception
     */
    @Test
    public void testDelRepSuccess() throws Exception {
        // 假定报表未发布
        String name = "a_for_test";
        this.prepare4DelRep(name, true);
        Assert.assertTrue(reportDesignModelService.deleteModel(name, true));
    }

    /**
     * 
     * testDelRepException
     */
    @Test
    public void testDelRepException() throws Exception {
        // 假定报表未发布
        String name = "a_for_test";
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] { name });
        Mockito.when(fileService.read(ContextManager.getProductLine() 
                + File.separator + this.reportBaseDir + File.separator 
                    + "dev" + File.separator + name)).thenReturn(
                            SerializationUtils.serialize(this.reportDesignModel));
        Mockito.when(fileService.read(ContextManager.getProductLine() 
                + File.separator + this.reportBaseDir + File.separator 
                + "release" + File.separator + name)).thenReturn(null);
        Mockito.when(fileService.rm(Mockito.anyString())).thenThrow(new FileServiceException("test"));
        try {
            reportDesignModelService.deleteModel(name, true);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }

    /**
     * 
     * testDelRep
     * 
     * @throws Exception
     */
    @Test
    public void testDelRep() throws Exception {

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
