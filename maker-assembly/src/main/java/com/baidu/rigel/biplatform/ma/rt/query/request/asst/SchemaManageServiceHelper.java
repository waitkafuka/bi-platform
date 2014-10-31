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
/**
 * 
 */
package com.baidu.rigel.biplatform.ma.rt.query.request.asst;

import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ma.report.exception.QueryModelBuildException;
import com.baidu.rigel.biplatform.ma.report.model.ExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.report.utils.QueryUtils;

/**
 *
 * @author wangyuxue
 * @version 1.0.0.1
 */
public class SchemaManageServiceHelper {
    
    /**
     * 
     * SchemaManageServiceHelper
     */
    private SchemaManageServiceHelper() {
    }
    
    /**
     * 依据区域模型构建cube
     * @param reportId
     * @param areaId
     * @return Cube
     */
    public Cube getCube(String reportId, String areaId) {
        ReportDesignModel reportModel = getReportModel(reportId);
        ExtendArea area = queryArea(reportModel, areaId);
        try {
            return QueryUtils.getCubeWithExtendArea(reportModel, area);
        } catch (QueryModelBuildException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * 
     * @return SchemaManageServiceHelper
     */
    public static SchemaManageServiceHelper getInstance() {
        SchemaManageServiceHelper service = new SchemaManageServiceHelper();
        return service;
    }

    /**
     * 依据报表id和区域id查询对应区域的cube定义模型
     * @param reportId 报表id
     * @param areaId 区域id
     * @return LogicModel
     */
    public LogicModel queryLogicModel(String reportId, String areaId) {
        ExtendArea area = queryArea(getReportModel(reportId), areaId);
        return area.getLogicModel();
    }

    /**
     * @param reportId
     * @param areaId
     * @return
     */
    private ExtendArea queryArea(ReportDesignModel reportModel, String areaId) {
        ExtendArea area = reportModel.getExtendById(areaId);
        return area;
    }

    /**
     * 依据报表id与区域id获得cube的定义模型
     * @param reportId 报表id
     * @param areaId 区域id
     * @return Cube
     */
    public Cube getCubeDefine(String reportId, String areaId) {
        ReportDesignModel reportModel = getReportModel(reportId);
        ExtendArea area = queryArea(reportModel, areaId);
        String cubeId = area.getCubeId();
        return reportModel.getSchema().getCubes().get(cubeId);
    }

    /**
     * @param reportId
     * @return
     */
    private ReportDesignModel getReportModel(String reportId) {
        return ReportDesignModelServiceHelper.getInstance().getReportDesignModel(reportId);
    }
    
}
