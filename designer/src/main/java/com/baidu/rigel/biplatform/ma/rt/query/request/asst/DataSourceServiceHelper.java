/**
 * Copyright (c) 2014 Baidu, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baidu.rigel.biplatform.ma.rt.query.request.asst;

import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ma.ds.exception.DataSourceOperationException;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceService;
import com.baidu.rigel.biplatform.ma.ds.util.DataSourceDefineUtil;
import com.baidu.rigel.biplatform.ma.resource.cache.ReportModelCacheManager;

/**
 * 
 * DataSourceServiceHelper
 * @author wangyuxue
 * @version 1.0.0.1
 */
public class DataSourceServiceHelper {
    
    /**
     * DataSourceServiceHelper
     */
    private DataSourceServiceHelper() {
    }
    
    /**
     * 
     * @param reportId
     * @return
     */
    public DataSourceInfo  getDsInfoByReportId (String reportId) {
        ReportModelCacheManager reportCacheManager = BiPlatformServiceLocator.getReportCacheManager();
        String dsId = reportCacheManager.getReportModel(reportId).getDsId();
        DataSourceService service = BiPlatformServiceLocator.getDataSourceService();
        DataSourceInfo dataSoruceInfo = null;
        try {
            dataSoruceInfo = DataSourceDefineUtil.parseToDataSourceInfo(service.getDsDefine(dsId));
        } catch (DataSourceOperationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return dataSoruceInfo;
    }
    
    /**
     * 
     * @return DataSourceServiceHelper
     */
    public static DataSourceServiceHelper getInstance() {
        // 考虑是否需要单例
        DataSourceServiceHelper helper = new DataSourceServiceHelper();
        return helper;
    }
}
