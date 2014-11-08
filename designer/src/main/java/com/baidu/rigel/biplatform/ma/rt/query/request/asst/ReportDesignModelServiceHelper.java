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

import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.resource.cache.ReportModelCacheManager;

/**
 * 
 * 报表模型查询服务门面接口，负责提供运行时环境报表模型定义查询操作
 * @author wangyuxue
 * @version 1.0.0.1
 */
public class ReportDesignModelServiceHelper {
    
    /**
     * 
     * ReportDesignModelServiceHelper
     */
    private ReportDesignModelServiceHelper() {
    }
    
    /**
     *  TODO 考虑是否单例
     * @return ReportDesignModelServiceHelper
     */
    public static ReportDesignModelServiceHelper getInstance() {
        return new ReportDesignModelServiceHelper();
    }
    
    /**
     * @param reportId 报表id
     * @return ReportDesignModel
     */
    public ReportDesignModel getReportDesignModel(String reportId) {
        ReportModelCacheManager reportCacheManager = BiPlatformServiceLocator.getReportCacheManager();
        ReportDesignModel reportModel = reportCacheManager.getReportModel(reportId);
        return reportModel;
    }
}
