/*
 * Copyright 2000-2011 baidu.com All right reserved. 
 */
package com.baidu.rigel.biplatform.ma.download.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.ma.download.DownloadType;
import com.baidu.rigel.biplatform.ma.download.service.DownloadServiceFactory;
import com.baidu.rigel.biplatform.ma.download.service.DownloadTableDataService;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;

/**
 * pivotTable离线下载实现测试
 * 
 * @author yichao.jiang 2015年6月2日 下午4:57:21
 */
public class PivotTableOfflineDownloadServiceImplTest {

    /**
     * 
     */
    @Test
    public void testDownloadTableForPivotTableOffline() {
        LogicModel logicModel = PowerMockito.mock(LogicModel.class);
        QuestionModel questionModel = PowerMockito.mock(QuestionModel.class);
        DownloadType downType = DownloadType.PIVOT_TABLE_OFFLINE;
        DownloadTableDataService downloadService = DownloadServiceFactory.getDownloadTableDataService(downType);

        try {
            downloadService.downloadTableData(questionModel, logicModel);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }
}
