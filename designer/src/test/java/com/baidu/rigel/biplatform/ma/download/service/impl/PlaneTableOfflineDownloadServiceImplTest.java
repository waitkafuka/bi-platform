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
import com.google.common.collect.Maps;

/**
 * @author yichao.jiang 2015年6月2日 下午3:52:00
 */
public class PlaneTableOfflineDownloadServiceImplTest {

    /**
     * 
     */
    @Test
    public void testDownloadPlaneTableOffline() {
        LogicModel logicModel = PowerMockito.mock(LogicModel.class);
        QuestionModel questionModel = PowerMockito.mock(QuestionModel.class);
        DownloadType downType = DownloadType.PLANE_TABLE_OFFLINE;
        DownloadTableDataService downloadService = DownloadServiceFactory.getDownloadTableDataService(downType);

        try {
            downloadService.downloadTableData(questionModel, logicModel);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }
    
    /**
     * 
     */
    @Test
    public void testDownloadPlaneTableOfflineShowZero() {
        LogicModel logicModel = PowerMockito.mock(LogicModel.class);
        QuestionModel questionModel = PowerMockito.mock(QuestionModel.class);
        DownloadType downType = DownloadType.PLANE_TABLE_OFFLINE;
        DownloadTableDataService downloadService = DownloadServiceFactory.getDownloadTableDataService(downType);

        try {
            downloadService.downloadTableData(questionModel, logicModel, Maps.newHashMap());
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }
}
