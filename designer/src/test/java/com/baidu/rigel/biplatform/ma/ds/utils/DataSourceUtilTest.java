package com.baidu.rigel.biplatform.ma.ds.utils;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceGroupDefine;
import com.baidu.rigel.biplatform.ma.model.utils.UuidGeneratorUtils;
import com.baidu.rigel.biplatform.ma.report.utils.ContextManager;

/**
 * 数据源工具类测试
 * @author jiangyichao
 *
 */ 
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "file:src/test/resources/applicationContext.xml")
public class DataSourceUtilTest {
	@Value("${biplatform.ma.ds.location}")
    private String dsFileBaseDir;
	
	/**
	 * 测试数据源
	 */
	@Test
	public void testGetDs() {
		String productLine = "productLine";
		ContextManager.cleanProductLine();
		ContextManager.setProductLine(productLine);
		DataSourceDefine ds = new DataSourceDefine();
		String id = UuidGeneratorUtils.generate();
		String name = "name";
		ds.setId(id);
		ds.setName(name);
		String expectStr = productLine + File.separator + this.dsFileBaseDir;
		expectStr = expectStr + File.separator + id + "_" + name;
		Assert.assertEquals(expectStr, DataSourceUtil.getDsFileName(ds));
		String fileName = id + "_" +name;
		Assert.assertEquals(expectStr, DataSourceUtil.getDsFileName(fileName));
		
		String expectDir = productLine + File.separator + this.dsFileBaseDir;
		Assert.assertEquals(expectDir, DataSourceUtil.getDsFileStoreDir());
		Assert.assertEquals(expectDir, DataSourceUtil.getDsFileStoreDir(productLine));
	}
	
	/**
	 * 测试数据源组
	 */
	@Test
	public void testDsGroup() {
		DataSourceGroupDefine dsG = new DataSourceGroupDefine();
		String productLine = "productLine";
		ContextManager.cleanProductLine();
		ContextManager.setProductLine(productLine);
		String id = UuidGeneratorUtils.generate();
		dsG.setId(id);
		String name = "name";
		dsG.setName(name);
		
		String expectStr = productLine + File.separator + this.dsFileBaseDir;
		expectStr = expectStr + File.separator + id + "_" + name;
		Assert.assertEquals(expectStr, DataSourceUtil.getDsGroupFileName(dsG));
		String fileName = id + "_" +name;
		Assert.assertEquals(expectStr, DataSourceUtil.getDsGroupFileName(fileName));
		
		String expectDir = productLine + File.separator + this.dsFileBaseDir;
		Assert.assertEquals(expectDir, DataSourceUtil.getDsGroupFileStoreDir());
		Assert.assertEquals(expectDir, DataSourceUtil.getDsGroupFileStoreDir(productLine));
	}
}
