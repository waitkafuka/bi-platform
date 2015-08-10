package com.baidu.rigel.biplatform.ma.report.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONException;
import org.json.JSONObject;
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

import com.baidu.rigel.biplatform.ac.util.AesUtil;
import com.baidu.rigel.biplatform.ac.util.DeepcopyUtils;
import com.baidu.rigel.biplatform.api.client.service.FileService;
import com.baidu.rigel.biplatform.api.client.service.FileServiceException;
import com.baidu.rigel.biplatform.ma.PrepareMemDb4Test;
import com.baidu.rigel.biplatform.ma.PrepareModelObject4Test;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceService;
import com.baidu.rigel.biplatform.ma.model.consts.Constants;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.model.utils.GsonUtils;
import com.baidu.rigel.biplatform.ma.report.model.ExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.ExtendAreaType;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.baidu.rigel.biplatform.ma.report.model.MeasureTopSetting;
import com.baidu.rigel.biplatform.ma.report.model.PlaneTableFormat;
import com.baidu.rigel.biplatform.ma.report.model.PlaneTableFormat.PaginationSetting;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.report.service.ReportDesignModelService;
import com.baidu.rigel.biplatform.ma.report.utils.ContextManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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

    /**
     * 报表基地址
     */
    @Value("${biplatform.ma.report.location}")
    private String reportBaseDir;

    /**
     * 密钥
     */
    @Value("${biplatform.ma.ser_key}")
    protected String securityKey;

    /**
     * 预置对象
     */
    private ReportDesignModel reportDesignModel;

    /**
     * 数据源对象
     */
    private DataSourceDefine dsDefine;

    /**
     * 初始化 init
     */
    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        reportDesignModel = PrepareModelObject4Test.getReportDesignModel();
        dsDefine = PrepareModelObject4Test.getDataSourceDefine();
        String pwd = dsDefine.getDbPwd();
        String pwdEncrypt = AesUtil.getInstance().encryptAndUrlEncoding(pwd, securityKey);
        dsDefine.setDbPwd(pwdEncrypt);
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
     * testSaveOrUpdateRep
     */
    @Test
    public void testSaveOrUpdateRepExp() throws Exception {
        try {
            reportDesignModelService.saveOrUpdateModel(null);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
        String name = this.reportDesignModel.getName();
        this.reportDesignModel.setName(null);
        try {
            reportDesignModelService.saveOrUpdateModel(reportDesignModel);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        } finally {
            reportDesignModel.setName(name);
        }
    }

    /**
     * 
     * testSaveOrUpdateRep
     * 
     * @throws Exception
     */
    @Test
    public void testSaveOrUpdateRep() throws Exception {
        String name = reportDesignModel.getId();
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] { name });
        Mockito.when(fileService.read(ContextManager.getProductLine()
                + File.separator + this.reportBaseDir + File.separator
                    + "dev" + File.separator + name)).thenReturn(
                        SerializationUtils.serialize(this.reportDesignModel));
        Mockito.when(fileService.write(this.generateDevReportLocation(reportDesignModel),
                SerializationUtils.serialize(reportDesignModel))).thenReturn(true);
        ReportDesignModel actual = reportDesignModelService.saveOrUpdateModel(reportDesignModel);
        this.compareReportDesginModel(reportDesignModel, actual);
    }

    /**
     * 
     * testSaveOrUpdateRepRmFail
     * 
     * @throws Exception
     */
    @Test
    public void testSaveOrUpdateRepRmFail() throws Exception {
        String name = reportDesignModel.getId();
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] { name });
        Mockito.when(fileService.read(ContextManager.getProductLine()
                + File.separator + this.reportBaseDir + File.separator
                    + "dev" + File.separator + name)).thenReturn(
                        SerializationUtils.serialize(this.reportDesignModel));
        // 新目录删除失败
        Mockito.when(fileService.rm(this.generateDevReportLocation(reportDesignModel))).thenThrow(
                new FileServiceException("test"));
        // 尝试使用原始目录删除
        Mockito.when(fileService.rm(this.getOriReleaseReportLocation(this.reportDesignModel))).thenReturn(true);
        Mockito.when(fileService.write(this.generateDevReportLocation(reportDesignModel),
                SerializationUtils.serialize(reportDesignModel))).thenReturn(true);
        ReportDesignModel actual = reportDesignModelService.saveOrUpdateModel(reportDesignModel);
        this.compareReportDesginModel(reportDesignModel, actual);
    }

    /**
     * 
     * testSaveOrUpdateRepWriteExp
     * 
     * @throws Exception
     */
    @Test
    public void testSaveOrUpdateRepWriteExp() throws Exception {
        String name = reportDesignModel.getId();
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] { name });
        Mockito.when(fileService.read(ContextManager.getProductLine()
                + File.separator + this.reportBaseDir + File.separator
                    + "dev" + File.separator + name)).thenReturn(
                        SerializationUtils.serialize(this.reportDesignModel));
        // 新目录删除失败
        Mockito.when(fileService.rm(this.generateDevReportLocation(reportDesignModel))).thenThrow(
                new FileServiceException("test"));
        // 尝试使用原始目录删除
        Mockito.when(fileService.rm(this.getOriReleaseReportLocation(this.reportDesignModel))).thenReturn(true);
        Mockito.when(fileService.write(this.generateDevReportLocation(reportDesignModel),
                SerializationUtils.serialize(reportDesignModel))).thenThrow(new FileServiceException("test1"));
        ReportDesignModel actual = reportDesignModelService.saveOrUpdateModel(reportDesignModel);
        Assert.assertNull(actual);
    }

    /**
     * 
     * testCopyRepModelNull
     */
    @Test
    public void testCopyRepModelNull() {
        // 原名称为空
        try {
            reportDesignModelService.copyModel("", "targetName");
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
        // 目标名称为空
        try {
            reportDesignModelService.copyModel("srcName", "");
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }

        // 目标名称已存在
        try {
            String name = "a_for_test";
            Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] { name });
            Mockito.when(fileService.read(ContextManager.getProductLine()
                    + File.separator + this.reportBaseDir + File.separator
                        + "dev" + File.separator + name)).thenReturn(
                            SerializationUtils.serialize(this.reportDesignModel));
            reportDesignModelService.copyModel("a_for_test", "a_for_test");
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }

        // 原名称不存在
        try {
            String name = "test^_^" + "a_for_test";
            Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] { name });
            Mockito.when(fileService.read(ContextManager.getProductLine()
                    + File.separator + this.reportBaseDir + File.separator
                        + "dev" + File.separator + name)).thenReturn(
                            SerializationUtils.serialize(this.reportDesignModel));
            reportDesignModelService.copyModel("b_for_test", "c_for_test");
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }

    /**
     * 
     * testCopyModel
     */
    @Test
    public void testCopyModel() throws Exception {
        String name = "a_for_test^_^" + "a_for_test";
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] { name });
        Mockito.when(fileService.read(ContextManager.getProductLine()
                + File.separator + this.reportBaseDir + File.separator
                    + "dev" + File.separator + name)).thenReturn(
                        SerializationUtils.serialize(this.reportDesignModel));
        // 新目录删除失败
        Mockito.when(fileService.rm(
                this.generateDevReportLocation(reportDesignModel))).thenThrow(new FileServiceException("test"));
        // 尝试使用原始目录删除
        Mockito.when(fileService.rm(this.getOriReleaseReportLocation(this.reportDesignModel))).thenReturn(true);
        reportDesignModel.setName("c_for_test");
        Mockito.when(fileService.write(Mockito.anyString(), Mockito.anyObject())).thenReturn(true);

        ReportDesignModel actual = reportDesignModelService.copyModel("a_for_test", "c_for_test");
        Assert.assertEquals("c_for_test", actual.getName());
    }

    /**
     * 依据model对象生成持久化文件名称
     * 
     * @param model
     * @return
     */
    private String generateDevReportLocation(ReportDesignModel model) {
        if (model == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(getDevReportDir());
        builder.append(File.separator);
        builder.append(model.getId());
        builder.append(Constants.FILE_NAME_SEPERATOR);
        builder.append(model.getName().hashCode());
        builder.append(Constants.FILE_NAME_SEPERATOR);
        builder.append(model.getDsId());
        return builder.toString();
    }

    /**
     * 获取开发状态报表存储路径
     * 
     * @return
     */
    private String getDevReportDir() {
        String productLine = ContextManager.getProductLine();
        return productLine + File.separator + reportBaseDir + File.separator
                + "dev";
    }

    /**
     * 获取发布的报表的存储路径
     * 
     * @return
     */
    private String getReleaseReportLocation(ReportDesignModel model) {
        if (model == null) {
            return null;
        }
        String productLine = ContextManager.getProductLine();
        return productLine + File.separator + reportBaseDir + File.separator
                + "release" + File.separator + model.getId()
                    + Constants.FILE_NAME_SEPERATOR + model.getName().hashCode();
    }

    /**
     * 获取开发状态报表存储路径
     * 
     * @return
     */
    private String getReleaseReportDir() {
        String productLine = ContextManager.getProductLine();
        return productLine + File.separator + reportBaseDir + File.separator
                + "release";
    }

    /**
     * 
     * getOriReleaseReportLocation
     * 
     * @param model
     * @return
     */
    @Deprecated
    private String getOriReleaseReportLocation(ReportDesignModel model) {
        if (model == null) {
            return null;
        }
        String name = model.getName();
        String productLine = ContextManager.getProductLine();
        try {
            String[] listFile = fileService.ls(this.getReleaseReportDir());
            for (String file : listFile) {
                if (file.startsWith(model.getId())) {
                    return productLine + File.separator + reportBaseDir
                            + File.separator + "release" + File.separator
                            + file;
                }
            }
        } catch (FileServiceException e) {
            e.printStackTrace();
        }
        return productLine + File.separator + reportBaseDir + File.separator
                + "release" + File.separator + model.getId()
                + Constants.FILE_NAME_SEPERATOR + name;
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
                new String[] { "ID" + Constants.FILE_NAME_SEPERATOR + "reportName"
                        + Constants.FILE_NAME_SEPERATOR + "DsID", "." });
        Assert.assertTrue(reportDesignModelService.isNameExist("reportName"));
    }

    /**
     * fileService的ls方法抛出异常
     */
    @Test
    public void testReportNameWithThrowsException() throws Exception {
        Mockito.when(fileService.ls(Mockito.anyString())).thenThrow(new FileServiceException(""));
        Assert.assertFalse(reportDesignModelService.isNameExist("testName"));
    }

    /**
     * 
     * testPublishRepNull
     * 
     * @throws Exception
     */
    @Test
    public void testPublishRep() throws Exception {
        String name = "a_for_test^_^" + reportDesignModel.getId();
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] { name });
        Mockito.when(fileService.read(ContextManager.getProductLine()
                + File.separator + this.reportBaseDir + File.separator
                    + "release" + File.separator + name)).thenReturn(
                        SerializationUtils.serialize(this.reportDesignModel));
        // 新目录删除失败
        Mockito.when(fileService.rm(
                this.getReleaseReportLocation(reportDesignModel))).thenThrow(new FileServiceException("test"));
        // 尝试使用原始目录删除
        Mockito.when(fileService.rm(this.getOriReleaseReportLocation(this.reportDesignModel))).thenReturn(true);

        String devReportLocation = this.generateDevReportLocation(reportDesignModel);
        String realeaseLocation = this.getReleaseReportLocation(reportDesignModel);
        Mockito.when(fileService.copy(devReportLocation, realeaseLocation)).thenReturn(true);

        Mockito.when(dsService.getDsDefine(Mockito.anyString())).thenReturn(dsDefine);

        // 创建数据库
        PrepareMemDb4Test.createMemDb();
        ExtendArea area = new ExtendArea();
        area.setType(ExtendAreaType.TABLE);
        Map<String, ExtendArea> areas = Maps.newHashMap();
        areas.put("extendArea", area);
        reportDesignModel.setExtendAreas(areas);
        // 发布
        Assert.assertTrue(reportDesignModelService.publishReport(reportDesignModel, securityKey));
    }

    /**
     * 
     * testPublishRepNull
     * 
     * @throws Exception
     */
    @Test
    public void testPublishRepNull() throws Exception {
        String name = "a_for_test^_^" + reportDesignModel.getId();
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] { name });
        Mockito.when(fileService.read(ContextManager.getProductLine()
                + File.separator + this.reportBaseDir + File.separator
                    + "release" + File.separator + name)).thenReturn(
                        SerializationUtils.serialize(this.reportDesignModel));
        // 删除原文件
        Mockito.when(fileService.rm(
                this.getReleaseReportLocation(reportDesignModel))).thenReturn(true);
        String devReportLocation = this.generateDevReportLocation(reportDesignModel);
        String realeaseLocation = this.getReleaseReportLocation(reportDesignModel);
        // 调用fileService.copy时抛出异常
        Mockito.when(fileService.copy(devReportLocation,
                realeaseLocation)).thenThrow(new FileServiceException("test"));

        // 创建数据库
        PrepareMemDb4Test.createMemDb();
        ExtendArea area = new ExtendArea();
        area.setType(ExtendAreaType.TABLE);
        Map<String, ExtendArea> areas = Maps.newHashMap();
        areas.put("extendArea", area);
        reportDesignModel.setExtendAreas(areas);
        // 发布
        try {
            reportDesignModelService.publishReport(reportDesignModel, securityKey);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }

    /**
     * 删除目录异常 testPublishRepExp
     * 
     * @throws Exception
     */
    @Test
    public void testPublishRepExp() throws Exception {
        String name = "a_for_test^_^" + reportDesignModel.getId();
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] { name });
        Mockito.when(fileService.read(ContextManager.getProductLine()
                + File.separator + this.reportBaseDir + File.separator
                    + "release" + File.separator + name)).thenReturn(
                        SerializationUtils.serialize(this.reportDesignModel));
        // 新目录删除失败
        Mockito.when(fileService.rm(
                this.getReleaseReportLocation(reportDesignModel))).thenThrow(new FileServiceException("test"));
        // 尝试使用原始目录删除
        Mockito.when(fileService.rm(this.getOriReleaseReportLocation(this.reportDesignModel))).thenThrow(
                new FileServiceException("test"));
        // 创建数据库
        PrepareMemDb4Test.createMemDb();
        ExtendArea area = new ExtendArea();
        area.setType(ExtendAreaType.TABLE);
        Map<String, ExtendArea> areas = Maps.newHashMap();
        areas.put("extendArea", area);
        reportDesignModel.setExtendAreas(areas);
        // 发布
        try {
            reportDesignModelService.publishReport(reportDesignModel, securityKey);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }

    /**
     * 
     * testPublishCopyFail
     * 
     * @throws Exception
     */
    @Test
    public void testPublishCopyFail() throws Exception {
        String name = "a_for_test^_^" + reportDesignModel.getId();
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] { name });
        Mockito.when(fileService.read(ContextManager.getProductLine()
                + File.separator + this.reportBaseDir + File.separator
                    + "release" + File.separator + name)).thenReturn(
                        SerializationUtils.serialize(this.reportDesignModel));
        // 删除原文件
        Mockito.when(fileService.rm(
                this.getReleaseReportLocation(reportDesignModel))).thenReturn(true);
        String devReportLocation = this.generateDevReportLocation(reportDesignModel);
        String realeaseLocation = this.getReleaseReportLocation(reportDesignModel);
        // 调用fileService.copy时失败
        Mockito.when(fileService.copy(devReportLocation,
                realeaseLocation)).thenReturn(false);

        // 创建数据库
        PrepareMemDb4Test.createMemDb();
        ExtendArea area = new ExtendArea();
        area.setType(ExtendAreaType.TABLE);
        Map<String, ExtendArea> areas = Maps.newHashMap();
        areas.put("extendArea", area);
        reportDesignModel.setExtendAreas(areas);
        // 发布
        try {
            reportDesignModelService.publishReport(reportDesignModel, securityKey);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }

    /**
     * 
     * testUpdateArea
     */
    @Test
    public void testUpdateArea() {
        ExtendArea area = new ExtendArea();

        // 数据样式
        String dataFormat = "{'test': 'testJson'}";
        Map<String, String> expect = this.convertStr2Map(dataFormat);
        reportDesignModelService.updateAreaWithDataFormat(area, dataFormat);
        Map<String, String> actual = area.getFormatModel().getDataFormat();
        Assert.assertEquals(expect, actual);

        // 颜色样式
        reportDesignModelService.updateAreaColorFormat(area, dataFormat);
        actual = area.getFormatModel().getColorFormat();
        Assert.assertEquals(expect, actual);

        // 位置
        reportDesignModelService.updateAreaPositionDef(area, dataFormat);
        actual = area.getFormatModel().getPositions();
        Assert.assertEquals(expect, actual);

        // 文本对齐信息
        reportDesignModelService.updateAreaTextAlignFormat(area, dataFormat);
        actual = area.getFormatModel().getTextAlignFormat();
        Assert.assertEquals(expect, actual);

        // 其他设置信息
        reportDesignModelService.updateAreaWithOtherSetting(area, dataFormat);
        Map<String, Object> actualOther = area.getOtherSetting();
        @SuppressWarnings("unchecked")
        Map<String, Object> expectOther = GsonUtils.fromJson(dataFormat, HashMap.class);
        Assert.assertEquals(expectOther, actualOther);
        
        // ToolTips提示信息
        reportDesignModelService.updateAreaWithToolTips(area, dataFormat);
        actual = area.getFormatModel().getToolTips();
        Assert.assertEquals(expectOther, actualOther);
        
        // 分页设置信息
        String pageJson = "{'isPagiganation': true, 'pageSize': 1000, 'pageSizeOptions': [10,50,100]}";
        reportDesignModelService.updatePageSetting4PlaneTable(area, pageJson);
        PaginationSetting setting = GsonUtils.fromJson(pageJson, PaginationSetting.class);
        PaginationSetting actualSetting = area.getPlaneTableFormat().getPageSetting();
        Assert.assertEquals(setting.getIsPagination(), actualSetting.getIsPagination());
        Assert.assertEquals(setting.getPageSize(), actualSetting.getPageSize());
        Assert.assertEquals(setting.getPageSizeOptions(), actualSetting.getPageSizeOptions());
        
        // TOpSetting
        LogicModel logicModel = new LogicModel();
        area.setLogicModel(logicModel);
        area.setId("areaId");
        String topSetting = "{'topType':'NONE','measureId':'measureId','recordSize':10,'areaId':'areaId'}";
        MeasureTopSetting expectTopSetting = GsonUtils.fromJson(topSetting, MeasureTopSetting.class);        
        reportDesignModelService.updateAreaWithTopSetting(area, topSetting);
        MeasureTopSetting actualTopSetting = area.getLogicModel().getTopSetting();
        Assert.assertEquals(expectTopSetting.getAreaId(), actualTopSetting.getAreaId());
        Assert.assertEquals(expectTopSetting.getMeasureId(), actualTopSetting.getMeasureId());
        Assert.assertEquals(expectTopSetting.getRecordSize(), actualTopSetting.getRecordSize());
        Assert.assertEquals(expectTopSetting.getTopType(), actualTopSetting.getTopType());
        
        dataFormat = "test is not json";
        try {
            reportDesignModelService.updateAreaColorFormat(area, dataFormat);            
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
        
        // 分页设置信息
        String paginationJson = "{'isPagination': true, 'pageSize': 1000, 'pageSizeOptions': null}";
        PaginationSetting defaultSetting = new PlaneTableFormat().new PaginationSetting();
        reportDesignModelService.updatePageSetting4PlaneTable(area, paginationJson);
        actualSetting = area.getPlaneTableFormat().getPageSetting();
        Assert.assertEquals(defaultSetting.getIsPagination(), actualSetting.getIsPagination());
        Assert.assertEquals(defaultSetting.getPageSize(), actualSetting.getPageSize());
        Assert.assertEquals(defaultSetting.getPageSizeOptions(), actualSetting.getPageSizeOptions());
    }

    /**
     * 
     * testUpdateAreaWithNull
     */
    @Test
    public void testUpdateAreaWithNull() {
        ExtendArea area = new ExtendArea();
        String format = "";
        // 颜色样式
        reportDesignModelService.updateAreaColorFormat(area, format);
        Assert.assertNotNull(area);
        // 位置
        reportDesignModelService.updateAreaPositionDef(area, format);
        Assert.assertNotNull(area);
        // 文本对齐信息
        reportDesignModelService.updateAreaTextAlignFormat(area, format);
        Assert.assertNotNull(area);
        // 分页设置信息
        String pageJson = "";
        reportDesignModelService.updatePageSetting4PlaneTable(area, pageJson);
        Assert.assertNotNull(area);
    }
    
    /**
     * 将json串转换为map
     * 
     * @param dataFormat
     * @return Map<String, String>
     */
    private Map<String, String> convertStr2Map(String dataFormat) {
        try {
            JSONObject json = new JSONObject(dataFormat);
            Map<String, String> rs = Maps.newHashMap();
            for (String str : JSONObject.getNames(json)) {
                rs.put(str, json.getString(str));
            }
            return rs;
        } catch (JSONException e) {
            throw new IllegalArgumentException("数据格式必须为Json格式， dataFormat = "
                    + dataFormat);
        }
    }
    
    /**
     * 
     * testLsReportWithId
     */
    @Test
    public void testLsReportWithIdExp() throws Exception {
        String name = "a_for_test^_^" + reportDesignModel.getId();
        Mockito.when(fileService.ls(Mockito.anyString())).thenThrow(new FileServiceException("test"));
        Assert.assertEquals(Lists.newArrayList(), reportDesignModelService.lsReportWithDsId(name));
        
    }
    
    /**
     * 
     * testLsReprtWithIdNull
     * @throws Exception
     */
    @Test
    public void testLsReprtWithIdNull() throws Exception {
        String name = "a_for_test^_^" + reportDesignModel.getId();
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(null);
        Assert.assertEquals(Lists.newArrayList(), reportDesignModelService.lsReportWithDsId(name));
        
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] {});
        Assert.assertEquals(Lists.newArrayList(), reportDesignModelService.lsReportWithDsId(name));        
    }
    
    /**
     * 
     * testLsReport
     * @throws Exception
     */
    @Test
    public void testLsReport() throws Exception {
        String name = "a_for_test^_^" + reportDesignModel.getId() 
                + "^_^" + reportDesignModel.getDsId(); 
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] {name});
        List<String> expect = Lists.newArrayList();
        expect.add(reportDesignModel.getId());
        List<String> actual = 
                reportDesignModelService.lsReportWithDsId(reportDesignModel.getDsId());
        Assert.assertEquals(expect, actual);
    }
    
    /**
     * 
     * testIsNameExistsWithId
     * @throws Exception
     */
    @Test
    public void testIsNameExistsWithIdFalse() throws Exception {
        Assert.assertFalse(reportDesignModelService.isNameExist(null, "id"));
        // 空目录
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] {});
        Assert.assertFalse(reportDesignModelService.isNameExist("name", "id"));
        
        // ls抛出异常
        Mockito.when(fileService.ls(Mockito.anyString())).thenThrow(
                new FileServiceException("test"));
        Assert.assertFalse(reportDesignModelService.isNameExist("name", "id"));
    }
    
    /**
     * 
     * testIsNameExistsWithId
     * @throws Exception
     */
    @Test
    public void testIsNameExistsWithId() throws Exception {
        String name = "a_for_test^_^" + reportDesignModel.getId() 
                + "^_^" + reportDesignModel.getDsId(); 
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] {name});
        // 名称不存在
        boolean actual = reportDesignModelService.isNameExist("name", "id");
        Assert.assertFalse(actual);
        name = "^_^" + String.valueOf("a_for_test".hashCode()) 
                + Constants.FILE_NAME_SEPERATOR + reportDesignModel.getId();
        Mockito.when(fileService.ls(Mockito.anyString())).thenReturn(new String[] {name});
        // 名称存在，id不存在
        actual = reportDesignModelService.isNameExist("a_for_test", "id");
        Assert.assertTrue(actual);
        
    }
    
    /**
     * 
     * testUpdateReportModel
     * @throws Exception
     */
    @Test
    public void testUpdateReportModelNameExists() throws Exception {
        String name = "^_^" + String.valueOf("a_for_test".hashCode()) 
                + Constants.FILE_NAME_SEPERATOR + reportDesignModel.getId();
        Mockito.when(fileService.ls(this.getDevReportDir())).thenReturn(new String[] {name});
        Assert.assertFalse(
                reportDesignModelService.updateReportModel(reportDesignModel, true));
        
    }
    
    /**
     * 
     * testUpdateReportModelNameNotExists
     * @throws Exception
     */
    @Test
    public void testUpdateReportModelNameNotExists() throws Exception {
        String name = "a_for_test^_^" + reportDesignModel.getId(); 
        Mockito.when(fileService.ls(this.getDevReportDir())).thenReturn(new String[] {name});
        
        Mockito.when(fileService.read(ContextManager.getProductLine()
                + File.separator + this.reportBaseDir + File.separator
                    + "dev" + File.separator + name)).thenReturn(
                        SerializationUtils.serialize(this.reportDesignModel));
        Assert.assertTrue(
                reportDesignModelService.updateReportModel(reportDesignModel, true));
    }
    
    /**
     * 
     * testUpdateReportModelExp
     * @throws Exception
     */
    @Test
    public void testUpdateReportModelExp() throws Exception {
        String name = "a_for_test^_^" + reportDesignModel.getId(); 
        Mockito.when(fileService.ls(this.getDevReportDir())).thenReturn(new String[] {name});
        
        Mockito.when(fileService.read(ContextManager.getProductLine()
                + File.separator + this.reportBaseDir + File.separator
                    + "dev" + File.separator + name)).thenReturn(
                        SerializationUtils.serialize(this.reportDesignModel));
        
        Mockito.when(fileService.rm(
                this.generateDevReportLocation(reportDesignModel))).thenThrow(new FileServiceException("test"));
        
        Mockito.when(fileService.rm(
                this.generateOriDevReportLocation(reportDesignModel))).thenThrow(
                        new FileServiceException("test"));
        
        Assert.assertFalse(
                reportDesignModelService.updateReportModel(reportDesignModel, true));
    }
    
    /**
     * 升级方法
     * 
     * @param model
     * @return String
     */
    @Deprecated
    private String generateOriDevReportLocation(ReportDesignModel model) {
        if (model == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(getDevReportDir());
        builder.append(File.separator);
        String name = null;
        try {
            String[] listFile = fileService.ls(this.getDevReportDir());
            for (String file : listFile) {

                if (file.startsWith(model.getId())) {
                    name = file;
                    builder.append(name);
                    break;
                }
            }
        } catch (FileServiceException e) {
            e.printStackTrace();
        }
        if (name == null) {
            builder.append(model.getId());
            builder.append(Constants.FILE_NAME_SEPERATOR);
            builder.append(name);
            builder.append(Constants.FILE_NAME_SEPERATOR);
            builder.append(model.getDsId());
        }
        return builder.toString();
    }
}
