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
package com.baidu.rigel.biplatform.ma.resource;

import java.util.LinkedHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ma.model.service.PositionType;
import com.baidu.rigel.biplatform.ma.report.exception.CacheOperationException;
import com.baidu.rigel.biplatform.ma.report.model.Item;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.report.query.ReportRuntimeModel;
import com.baidu.rigel.biplatform.ma.resource.cache.ReportModelCacheManager;

/**
 * 
 *报表运行时模型管理服务接口：
 *  此接口针提供对运行时报表模型的修改、动态查询条件修改、当前运行模型对应的报表逻辑模型定义检索服务等
 * @author david.wang
 *
 */
@RestController
@RequestMapping("/silkroad/reports/runtime")
public class ReportRuntimeModelManageResource {
    
    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(ReportRuntimeModelManageResource.class);
    
    /**
     * cache manager
     */
    @Resource(name = "reportModelCacheManager")
    private ReportModelCacheManager reportModelCacheManager;
    
    @RequestMapping(value = "/{reportId}/area/{areaId}", method = RequestMethod.GET)
    public ResponseResult getAllDimAndMeasuers (@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId, HttpServletRequest request) {
        ResponseResult result = new ResponseResult ();
        result.setStatus (1);
        ReportRuntimeModel runTimeModel = null;
        try {
            runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        } catch (CacheOperationException e1) {
            logger.info("[INFO] There are no such model in cache. Report Id: " + reportId, e1);
            result.setStatusInfo ("未能获取正确的报表定义");
            return result;
        }
        
        if (runTimeModel == null) {
            logger.info("[INFO] There are no such model in cache. Report Id: " + reportId);
            result.setStatusInfo ("未能获取正确的报表定义");
            return result;
        }
        String cubeId = runTimeModel.getModel ().getExtendById (areaId).getCubeId ();
        Cube cube = runTimeModel.getModel ().getSchema ().getCubes ().get (cubeId);
        LinkedHashMap<String, String> cols = new LinkedHashMap<String, String> ();
        cube.getDimensions ().forEach ((k, dim) -> {
            cols.put (dim.getCaption (), dim.getId());
        });
        cube.getMeasures ().forEach ((k, m) -> {
            cols.put (m.getCaption (), m.getId());
        });
        result.setStatus (0);
        result.setData (cols);
        return result;
    }
    
    /**
     * 重置区域逻辑模型
     * @param reportId
     * @param areaId
     * @param request
     * @return
     */
    @RequestMapping(value = "/{reportId}/area/{areaId}", method = RequestMethod.GET)
    public ResponseResult resetArea (@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId, HttpServletRequest request) {
        ResponseResult result = new ResponseResult ();
        result.setStatus (1);
        ReportRuntimeModel runTimeModel = null;
        try {
            runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        } catch (CacheOperationException e1) {
            logger.info("[INFO] There are no such model in cache. Report Id: " + reportId, e1);
            result.setStatusInfo ("未能获取正确的报表定义");
            return result;
        }
        
        if (runTimeModel == null) {
            logger.info("[INFO] There are no such model in cache. Report Id: " + reportId);
            result.setStatusInfo ("未能获取正确的报表定义");
            return result;
        }
        ReportDesignModel reportModel = runTimeModel.getModel ();
        LogicModel model = reportModel.getExtendById (areaId).getLogicModel ();
        String[] ids = request.getParameter ("ids").split (",");
        Item[] items = new Item[ids.length];
        for (int i = 0; i < ids.length; ++i) {
            items[i] = new Item();
            items[i].setAreaId (areaId);
            items[i].setId (ids[i]);
            items[i].setOlapElementId (ids[i]);
            items[i].setReportId (reportId);
            items[i].setPositionType (PositionType.Y);
        }
        model.resetColumns (items);
        result.setStatus (0);
        result.setStatusInfo ("success");
        runTimeModel.getContext ().reset ();
        runTimeModel.getLocalContextByAreaId (areaId).reset ();
        runTimeModel.getQueryActions ().clear ();
        runTimeModel.setModel (reportModel);
        reportModelCacheManager.updateRunTimeModelToCache (reportId, runTimeModel);
        return result;
    }
}
