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

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.baidu.rigel.biplatform.ma.ds.service.DataSourceService;
import com.baidu.rigel.biplatform.ma.resource.cache.ReportModelCacheManager;


/**
 *  
 * BiPlatformServiceLocator
 * @author wangyuxue
 * @version 1.0.0.1
 */
public class BiPlatformServiceLocator {
    
    private static final ApplicationContext CONTEXT = new ClassPathXmlApplicationContext("applicationContext.xml");

    /**
     * BiPlatformServiceLocator
     */
    private BiPlatformServiceLocator() {
    }
    
    /**
     * 依据service name 查询 service
     * @param serviceName 服务名称
     * @return 服务实例
     */
    @SuppressWarnings("unchecked")
    private static <T> T  lookupService(String serviceName) {
        return (T) CONTEXT.getBean(serviceName);
    }
    
    /**
     * 
     * @return ReportModelCacheManager
     * 
     */
    public static ReportModelCacheManager getReportCacheManager() {
        return lookupService("reportModelCacheManager");
    }
    
    /**
     * 
     * @return DataSourceService
     */
    public static DataSourceService getDataSourceService() {
        return lookupService("dsService");
    }
}
