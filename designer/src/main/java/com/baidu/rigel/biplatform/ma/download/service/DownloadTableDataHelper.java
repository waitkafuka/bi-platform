package com.baidu.rigel.biplatform.ma.download.service;

import java.util.Map;

import com.baidu.rigel.biplatform.ma.download.DownloadType;
import com.baidu.rigel.biplatform.ma.download.service.impl.PivotTableOfflineDownloadServiceImpl;
import com.baidu.rigel.biplatform.ma.download.service.impl.PivotTableOnlineDownloadServiceImpl;
import com.baidu.rigel.biplatform.ma.download.service.impl.PlaneTableOfflineDownloadServiceImpl;
import com.baidu.rigel.biplatform.ma.download.service.impl.PlaneTableOnlineDownloadServiceImpl;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceType;
import com.google.common.collect.Maps;

/**
 * 
 * 表格数据下载辅助类
 * 
 * @author jiangjiangyichao 2015年5月25日 下午4:47:08
 */
public class DownloadTableDataHelper {
    /**
     * 报表数据下载仓库类
     */
    private static final Map<String, DownloadTableDataService> SERVICE_REPOSITORY = Maps.newHashMap();

    static {
        /**
         * 提供默认表格数据下载支持(在线支持、离线支持)
         */
        try {
            String mysqlDataDownload = DownloadType.PLANE_TABLE_ONLINE.getName() + "_" + DataSourceType.MYSQL;
            SERVICE_REPOSITORY.put(mysqlDataDownload ,
                    new PlaneTableOnlineDownloadServiceImpl());
            String oracleDataDownload = DownloadType.PLANE_TABLE_ONLINE.getName() + "_" + DataSourceType.ORACLE;
            SERVICE_REPOSITORY.put(oracleDataDownload ,
                    new PlaneTableOnlineDownloadServiceImpl());
            String h2DataDownload = DownloadType.PLANE_TABLE_ONLINE.getName() + "_" + DataSourceType.H2;
            SERVICE_REPOSITORY.put(h2DataDownload ,
                    new PlaneTableOnlineDownloadServiceImpl());
            SERVICE_REPOSITORY.put(DownloadType.PLANE_TABLE_OFFLINE.getName(), new PlaneTableOfflineDownloadServiceImpl());
            SERVICE_REPOSITORY.put(DownloadType.PIVOT_TABLE_ONLINE.getName(), new PivotTableOnlineDownloadServiceImpl());
            SERVICE_REPOSITORY.put(DownloadType.PIVOT_TABLE_OFFLINE.getName(), new PivotTableOfflineDownloadServiceImpl());
        } catch (Exception e) {
        }
    }

    /**
     * 注册下载服务方法
     * 
     * @param dsType
     * @param downloadClazz
     */
    public static void registryDownloadTableDataService(String downType, @SuppressWarnings("rawtypes") Class downloadClazz) {
        try {
            SERVICE_REPOSITORY.put(downType, (DownloadTableDataService) downloadClazz.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * @param downType 下载类型
     * @return DownloadTableDataService 表格下载接口
     */
    public static DownloadTableDataService getDownloadTableDataService(String downType) {
        return SERVICE_REPOSITORY.get(downType);
    }

    /**
     * 依据配置注册下载类型
     */
    public static void registryDsMetaServices(String type, @SuppressWarnings("rawtypes") Class clazz) {
        try {
            SERVICE_REPOSITORY.put(type, (DownloadTableDataService) clazz.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
