package com.baidu.rigel.biplatform.ma.datasource.utils;

import java.io.File;

import com.baidu.rigel.biplatform.ma.comm.util.ConfigUtil;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.report.utils.ContextManager;

/**
 * 数据源工具类
 * @author jiangyichao
 *
 */
public final class DataSourceUtil {
	
    /**
     * 获取数据源文件的文件名（含路径）
     * @param ds 数据源定义
     * @return 返回数据源定义文件文件名（绝对路径）
     */
    public static String getDsFileName(DataSourceDefine ds) {
        String basePath = getDsFileStoreDir();
        return basePath + File.separator + ds.getId() + "_" + ds.getName();
    }
    
    /**
     * 获取数据源的存储路径
     * @return 数据源存储路径
     */
    public static String getDsFileStoreDir() {
        String productLine = ContextManager.getProductLine();
        String basePath = productLine + File.separator + ConfigUtil.getDsBaseDir();
        return basePath;
    }
}
