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

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ma.model.service.PositionType;
import com.baidu.rigel.biplatform.ma.model.utils.GsonUtils;
import com.baidu.rigel.biplatform.ma.report.exception.CacheOperationException;
import com.baidu.rigel.biplatform.ma.report.model.ExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.Item;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.baidu.rigel.biplatform.ma.report.model.PlaneTableCondition;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.report.query.ReportRuntimeModel;
import com.baidu.rigel.biplatform.ma.resource.cache.ReportModelCacheManager;
import com.baidu.rigel.biplatform.ma.resource.utils.ResourceUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

/**
 * 
 *报表运行时模型管理服务接口：
 *  此接口针提供对运行时报表模型的修改、动态查询条件修改、当前运行模型对应的报表逻辑模型定义检索服务等
 * @author david.wang
 *
 */
@RestController
@RequestMapping("/silkroad/reports/")
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
    
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/dimAndInds", method = RequestMethod.POST)
    public ResponseResult getAllDimAndMeasuers (@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId, HttpServletRequest request) {
        ResponseResult result = new ResponseResult ();
        ReportRuntimeModel runTimeModel = null;
        try {
            runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        } catch (CacheOperationException e1) {
            logger.info("[INFO] There are no such model in cache. Report Id: " + reportId, e1);
            result.setStatus(1);
            result.setStatusInfo ("未能获取正确的报表定义");
            return result;
        }
        
        if (runTimeModel == null) {
            logger.info("[INFO] There are no such model in cache. Report Id: " + reportId);
            result.setStatus(1);
            result.setStatusInfo ("未能获取正确的报表定义");
            return result;
        }
        
        // 获取扩展区域
        ExtendArea extendArea = runTimeModel.getModel().getExtendById(areaId);
        // 逻辑模型
        LogicModel logicModel = extendArea.getLogicModel();
        Item[] items = logicModel.getColumns();
        String cubeId = runTimeModel.getModel ().getExtendById (areaId).getCubeId ();
        Cube cube = runTimeModel.getModel ().getSchema ().getCubes ().get (cubeId);
        List<Map<String, Object>> cols = Lists.newArrayList();
        
        // 设置维度，如果在LogicModel中则设置为选中，否则不选中；指标处理类似
        cube.getDimensions ().forEach ((k, dim) -> {
            Map<String, Object> map = Maps.newHashMap();
            boolean isInLogicModel = false;
            map.put("id", dim.getId());
            map.put("name", dim.getCaption());
            for (Item item : items) {
                if (item.getOlapElementId().equals(dim.getId())) {
                    map.put("selected", true);
                    isInLogicModel = true;
                    break;
                }
            }
            if (!isInLogicModel) {
                map.put("selected", false);
            }
            cols.add(map);
        });
        cube.getMeasures ().forEach ((k, m) -> {
            Map<String, Object> map = Maps.newHashMap();
            boolean isInLogicModel = false;
            map.put("id", m.getId());
            map.put("name", m.getCaption());
            for (Item item : items) {
                if (item.getOlapElementId().equals(m.getId())) {
                    map.put("selected", true);
                    isInLogicModel = true;
                    break;
                }
            }
            if (!isInLogicModel) {
                map.put("selected", false);
            }
            cols.add(map);
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
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/reset", method = RequestMethod.POST)
    public ResponseResult resetArea (@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId, HttpServletRequest request) {
        ResponseResult result = new ResponseResult ();
        ReportRuntimeModel runTimeModel = null;
        try {
            runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        } catch (CacheOperationException e1) {
            logger.info("[INFO] There are no such model in cache. Report Id: " + reportId, e1);
            result.setStatus(1);
            result.setStatusInfo ("未能获取正确的报表定义");
            return result;
        }
        
        if (runTimeModel == null) {
            logger.info("[INFO] There are no such model in cache. Report Id: " + reportId);
            result.setStatus(1);
            result.setStatusInfo ("未能获取正确的报表定义");
            return result;
        }
        ReportDesignModel reportModel = runTimeModel.getModel ();
        LogicModel model = reportModel.getExtendById (areaId).getLogicModel ();
        String[] ids = request.getParameter ("selectedFields").split (",");
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
        model.resetSlices(new Item[0]);
        result.setStatus (0);
        result.setStatusInfo ("success");
        runTimeModel.getContext ().reset ();
        runTimeModel.getLocalContextByAreaId (areaId).reset ();
        runTimeModel.getQueryActions ().clear ();
        runTimeModel.setModel (reportModel);
        reportModelCacheManager.updateRunTimeModelToCache (reportId, runTimeModel);
        return result;
    }
    
    
    /**
     * 增加或修改运行时平面表条件 add by jiangyichao at 2015-05-25, 平面表条件设置或修改
     * 
     * @return
     */
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/item/{elementId}/submitSetInfo", method = { RequestMethod.POST })
    public ResponseResult addOrModifyRuntimePlaneTableCondition(@PathVariable("reportId") String reportId, @PathVariable("areaId") String areaId,
            @PathVariable("elementId") String elementId, HttpServletRequest request) {
        logger.info("[INFO] begin query data with new measure");
        ResponseResult result = new ResponseResult();
        if (StringUtils.isEmpty(reportId)) {
            logger.debug("report id is empty");
            result.setStatus(1);
            result.setStatusInfo("report id is empty");
            return result;
        }
        ReportDesignModel model;
        // 获取运行时报表模型
        ReportRuntimeModel runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        try {
            // 根据运行态取得设计模型
            model = getRealModel(reportId, runTimeModel);
        } catch (CacheOperationException e) {
            logger.info("[INFO]Report model is not in cache! ", e);
            result = ResourceUtils.getErrorResult("缓存中不存在的报表，ID " + reportId, 1);
            return result;
        }

        // 获取平面表条件
        String conditions = request.getParameter("conditions");
        // TODO 是否修改
        if (!StringUtils.isEmpty(conditions)) {
            Map<String, String> conditionMap = GsonUtils.fromJson(conditions, new TypeToken<Map<String, String>>(){}.getType()); 
            String id = conditionMap.get("id");
            String name = conditionMap.get("field");
            String defaultValue = conditionMap.get("defaultValue");
            String condition = conditionMap.get("condition");
//            // 检查平面表条件值是否合理
//            for (PlaneTableCondition tmpCondition : conditions.values()) {
//                if (!PlaneTableUtils.checkSQLCondition(tmpCondition.getSQLCondition(), tmpCondition.getDefaultValue())) {
//                    result.setStatus(1);
//                    result.setStatusInfo("条件参数设置不合理，请检查！");
//                    return result;
//                }
//            }

//            // 获取原有报表的平面表条件信息
//            Map<String, PlaneTableCondition> oldConditions = model.getPlaneTableConditions();
//            // 替换原有条件
//            oldConditions.put(elementId, conditions.get(elementId));
//            model.setPlaneTableConditions(conditions);
        }

        reportModelCacheManager.updateRunTimeModelToCache(reportId, runTimeModel);
        reportModelCacheManager.updateReportModelToCache(reportId, model);
        logger.info("successfully add planeTable condition in runtime phase");
        result.setStatus(0);
        result.setData(model);
        result.setStatusInfo("successfully add planeTable condition in runtime phase ");
        return result;
    }

    /**
     * 删除平面表条件信息 add by jiangyichao at 2015-05-25，删除平面表条件信息
     * 
     * @param reportId
     * @param request
     * @return
     */
    @RequestMapping(value = "/{id}/{elementId}/runtime/planeTableConditions", method = { RequestMethod.GET })
    public ResponseResult removeRuntimePlaneTableConditions(@PathVariable("id") String reportId,
            @PathVariable("elementId") String elementId, HttpServletRequest request) {
        ResponseResult result = new ResponseResult();
        if (StringUtils.isEmpty(reportId)) {
            logger.debug("report id is empty");
            result.setStatus(1);
            result.setStatusInfo("report id is empty");
            return result;
        }

        ReportDesignModel model;
        // 获取运行时报表模型
        ReportRuntimeModel runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        try {
            // 根据运行态取得设计模型
            model = getRealModel(reportId, runTimeModel);
        } catch (CacheOperationException e) {
            logger.info("[INFO]Report model is not in cache! ", e);
            result = ResourceUtils.getErrorResult("缓存中不存在的报表，ID " + reportId, 1);
            return result;
        }

        // 获取该element对应的平面表条件信息
        Map<String, PlaneTableCondition> oldConditionsMap = model.getPlaneTableConditions();
        if (oldConditionsMap.containsKey(elementId)) {
            oldConditionsMap.remove(elementId);
        }
        model.setPlaneTableConditions(oldConditionsMap);
        reportModelCacheManager.updateRunTimeModelToCache(reportId, runTimeModel);
        reportModelCacheManager.updateReportModelToCache(reportId, model);
        logger.info("successfully remove planeTable condition in runtime phase");
        result.setStatus(0);
        result.setData(model);
        result.setStatusInfo("successfully remove planeTable condition in runtime phase ");
        return result;
    }
    
    
    /**
     * 
     * @param reportId
     * @param runTimeModel
     * @return ReportDesignModel
     */
    private ReportDesignModel getRealModel(String reportId, ReportRuntimeModel runTimeModel) {
        ReportDesignModel model;
//        Object isEditor = runTimeModel.getContext().get(Constants.IN_EDITOR);
//        Object preview = runTimeModel.getContext().get("reportPreview");
//        if ((isEditor != null && isEditor.toString().equals("true")) 
//                || (preview != null && preview.toString().equals("true"))) {
//            model = DeepcopyUtils.deepCopy (reportModelCacheManager.getReportModel(reportId));
//        } else {
        model = getDesignModelFromRuntimeModel(reportId);
//        }
        return model;
    }
    
    /**
     * @param reportId
     * @return ReportDesignModel
     */
    ReportDesignModel getDesignModelFromRuntimeModel(String reportId) {
        return reportModelCacheManager.getRuntimeModel(reportId).getModel();
    }
}
