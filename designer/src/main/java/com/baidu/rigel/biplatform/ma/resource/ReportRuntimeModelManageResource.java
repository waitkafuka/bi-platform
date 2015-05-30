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
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.Level;
import com.baidu.rigel.biplatform.ac.model.Measure;
import com.baidu.rigel.biplatform.ac.model.Schema;
import com.baidu.rigel.biplatform.ac.query.model.PageInfo;
import com.baidu.rigel.biplatform.ac.query.model.SQLCondition.SQLConditionType;
import com.baidu.rigel.biplatform.ma.ds.exception.DataSourceOperationException;
import com.baidu.rigel.biplatform.ma.model.service.PositionType;
import com.baidu.rigel.biplatform.ma.model.utils.GsonUtils;
import com.baidu.rigel.biplatform.ma.report.exception.CacheOperationException;
import com.baidu.rigel.biplatform.ma.report.exception.QueryModelBuildException;
import com.baidu.rigel.biplatform.ma.report.model.ExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.ExtendAreaContext;
import com.baidu.rigel.biplatform.ma.report.model.Item;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.baidu.rigel.biplatform.ma.report.model.PlaneTableCondition;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.report.query.QueryAction;
import com.baidu.rigel.biplatform.ma.report.query.QueryAction.MeasureOrderDesc;
import com.baidu.rigel.biplatform.ma.report.query.ReportRuntimeModel;
import com.baidu.rigel.biplatform.ma.report.query.ResultSet;
import com.baidu.rigel.biplatform.ma.report.service.ReportModelQueryService;
import com.baidu.rigel.biplatform.ma.resource.cache.ReportModelCacheManager;
import com.baidu.rigel.biplatform.ma.resource.utils.PlaneTableUtils;
import com.baidu.rigel.biplatform.ma.resource.utils.QueryDataResourceUtils;
import com.baidu.rigel.biplatform.ma.resource.utils.ResourceUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * 报表运行时模型管理服务接口： 此接口针提供对运行时报表模型的修改、动态查询条件修改、当前运行模型对应的报表逻辑模型定义检索服务等
 * 
 * @author david.wang
 *
 */
@RestController
@RequestMapping("/silkroad/reports/")
public class ReportRuntimeModelManageResource extends BaseResource{

    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(ReportRuntimeModelManageResource.class);

    /**
     * cache manager
     */
    @Resource(name = "reportModelCacheManager")
    private ReportModelCacheManager reportModelCacheManager;

    /**
     * 报表数据查询服务
     */
    @Resource
    private ReportModelQueryService reportModelQueryService;
    
    /**
     * queryDataResourceUtils
     */
    @Resource
    private QueryDataResourceUtils queryDataResourceUtils;
        
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/dimAndInds", method = RequestMethod.POST)
    public ResponseResult getAllDimAndMeasuers(@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId, HttpServletRequest request) {
        ResponseResult result = new ResponseResult();
        ReportRuntimeModel runTimeModel = null;
        try {
            runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        } catch (CacheOperationException e1) {
            logger.info("[INFO] There are no such model in cache. Report Id: " + reportId, e1);
            result.setStatus(1);
            result.setStatusInfo("未能获取正确的报表定义");
            return result;
        }

        if (runTimeModel == null) {
            logger.info("[INFO] There are no such model in cache. Report Id: " + reportId);
            result.setStatus(1);
            result.setStatusInfo("未能获取正确的报表定义");
            return result;
        }

        // 获取扩展区域
        ExtendArea extendArea = runTimeModel.getModel().getExtendById(areaId);
        // 逻辑模型
        LogicModel logicModel = extendArea.getLogicModel();
        Item[] items = logicModel.getColumns();
        String cubeId = runTimeModel.getModel().getExtendById(areaId).getCubeId();
        Cube cube = runTimeModel.getModel().getSchema().getCubes().get(cubeId);
        List<Map<String, Object>> cols = Lists.newArrayList();

        // 设置维度，如果在LogicModel中则设置为选中，否则不选中；指标处理类似
        cube.getDimensions().forEach((k, dim) -> {
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
        cube.getMeasures().forEach((k, m) -> {
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
        result.setStatus(0);
        result.setData(cols);
        return result;
    }

    /**
     * 重置区域逻辑模型
     * 
     * @param reportId
     * @param areaId
     * @param request
     * @return
     */
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/reset", method = RequestMethod.POST)
    public ResponseResult resetArea(@PathVariable("reportId") String reportId, @PathVariable("areaId") String areaId,
            HttpServletRequest request) {
        ResponseResult result = new ResponseResult();
        ReportRuntimeModel runTimeModel = null;
        try {
            runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        } catch (CacheOperationException e1) {
            logger.info("[INFO] There are no such model in cache. Report Id: " + reportId, e1);
            result.setStatus(1);
            result.setStatusInfo("未能获取正确的报表定义");
            return result;
        }

        if (runTimeModel == null) {
            logger.info("[INFO] There are no such model in cache. Report Id: " + reportId);
            result.setStatus(1);
            result.setStatusInfo("未能获取正确的报表定义");
            return result;
        }
        ReportDesignModel reportModel = runTimeModel.getModel();
        LogicModel model = reportModel.getExtendById(areaId).getLogicModel();
        String[] ids = request.getParameter("selectedFields").split(",");
        Item[] items = new Item[ids.length];
        for (int i = 0; i < ids.length; ++i) {
            items[i] = new Item();
            items[i].setAreaId(areaId);
            items[i].setId(ids[i]);
            items[i].setOlapElementId(ids[i]);
            items[i].setReportId(reportId);
            items[i].setPositionType(PositionType.Y);
        }
        model.resetColumns(items);
        model.resetSlices(new Item[0]);
        result.setStatus(0);
        result.setStatusInfo("success");
        runTimeModel.getContext().reset();
        runTimeModel.getLocalContextByAreaId(areaId).reset();
        runTimeModel.getQueryActions().clear();
        runTimeModel.setModel(reportModel);
        reportModelCacheManager.updateRunTimeModelToCache(reportId, runTimeModel);
        return result;
    }

    /**
     * 增加或修改运行时平面表条件 add by jiangyichao at 2015-05-25, 平面表条件设置或修改
     * 
     * @return
     */
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/submitSetInfo", method = { RequestMethod.POST })
    public ResponseResult addOrModifyRuntimePlaneTableCondition(@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId, HttpServletRequest request) {
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
            Map<String, String> conditionMap = GsonUtils.fromJson(conditions, new TypeToken<Map<String, String>>() {
            }.getType());
            // 获取条件
            String id = conditionMap.get("id");
            String name = conditionMap.get("field");
            String defaultValue = conditionMap.get("defaultValue");
            String condition = conditionMap.get("condition");
            if (PlaneTableUtils.checkSQLCondition(condition, defaultValue)) {
                PlaneTableCondition planeTableCondition = new PlaneTableCondition();
                planeTableCondition.setElementId(id);
                planeTableCondition.setName(name);
                planeTableCondition.setSQLCondition(SQLConditionType.valueOf(condition));
                planeTableCondition.setDefaultValue(defaultValue);
                // 获取原有报表的平面表条件信息
                Map<String, PlaneTableCondition> oldConditions = model.getPlaneTableConditions();
                // 替换原有条件
                oldConditions.put(id, planeTableCondition);
                model.setPlaneTableConditions(oldConditions);

                // 更新报表模型
                reportModelCacheManager.updateRunTimeModelToCache(reportId, runTimeModel);
//                reportModelCacheManager.updateReportModelToCache(reportId, model);
                logger.info("successfully add planeTable condition in runtime phase");
                result.setStatus(0);
                result.setData(model);
                result.setStatusInfo("successfully add planeTable condition in runtime phase ");
                return result;
            } else {
                result.setStatus(1);
                result.setStatusInfo("参数设置不正确，请注意检查");
                return result;
            }
        } else {
            result.setStatus(1);
            result.setStatusInfo("没有传入参数条件，请检查");
            return result;
        }

    }

    /**
     * 删除平面表条件信息 add by jiangyichao at 2015-05-25，删除平面表条件信息
     * 
     * @param reportId
     * @param request
     * @return
     */
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/item/{elementId}/removeSetInfo",
            method = { RequestMethod.POST })
    public ResponseResult removeRuntimePlaneTableConditions(@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId, @PathVariable("elementId") String elementId,
            HttpServletRequest request) {
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
        // 删除对应条件
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
     * 平面表排序
     * 
     * @param reportId
     * @param areaId
     * @param elementId
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/item/{elementId}/sort",
            method = { RequestMethod.POST })
    public ResponseResult sortPlaneTableColumns(@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId, @PathVariable("elementId") String elementId,
            HttpServletRequest request) {
        long begin = System.currentTimeMillis();
        logger.info("begin execuet sort for planeTable");
//        String orderBy = request.getParameter("orderbyParamKey");
        String sort = request.getParameter("sortType");
        // 获取排序方式
        ResponseResult result = new ResponseResult();
        if (StringUtils.isEmpty(sort)) {
            sort = "NONE";
        }
        if (sort.equalsIgnoreCase("NONE") || sort.equalsIgnoreCase("ASC")) {
            sort = "DESC";
        } else if (sort.equalsIgnoreCase("DESC")) {
            sort = "ASC";
        }

        // 获取运行态模型
        ReportRuntimeModel runTimeModel = null;
        try {
            runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        } catch (CacheOperationException e1) {
            logger.info("[INFO] There are no such model in cache. Report Id: " + reportId, e1);
            result.setStatus(1);
            result.setStatusInfo("未能获取正确的报表定义");
            return result;
        }

        if (runTimeModel == null) {
            logger.info("[INFO] There are no such model in cache. Report Id: " + reportId);
            result.setStatus(1);
            result.setStatusInfo("未能获取正确的报表定义");
            return result;
        }

        ReportDesignModel model;
        try {
            // 根据运行态取得设计模型
            model = getRealModel(reportId, runTimeModel);
        } catch (CacheOperationException e) {
            logger.info("[INFO]Report model is not in cache! ", e);
            result = ResourceUtils.getErrorResult("缓存中不存在的报表，ID " + reportId, 1);
            return result;
        }
        
        // 区域上下文
        ExtendAreaContext areaContext = reportModelCacheManager.getAreaContext(areaId);
        areaContext.getParams().clear();
        areaContext.getParams().putAll(runTimeModel.getContext().getParams());
        
        // 扩展区域
        ExtendArea area = model.getExtendById(areaId);
        Schema schema = model.getSchema();
        Map<String, ? extends Cube> cubes = schema.getCubes();
        Cube cube = cubes.get(area.getCubeId());
        // 获取上一次查询的QueryAction
        QueryAction queryAction = runTimeModel.getPreviousQueryAction(areaId);
        // 获取排序条件
        MeasureOrderDesc orderDesc = this.getNewOrderDesc(cube, elementId, sort);
        if (orderDesc != null) {
            // 重新设置QueryAction的排序方式
            queryAction.setMeasureOrderDesc(orderDesc);           
        }       
        // 构建分页信息
        PageInfo pageInfo = this.getPageInfo(request);
        // 结果集
        ResultSet resultSet = null;
        // 重新查询数据
        try {
            resultSet = reportModelQueryService.queryDatas(model, queryAction, true, 
                    areaContext.getParams(), pageInfo, securityKey);
        } catch (DataSourceOperationException e1) {
            logger.info("获取数据源失败！", e1);
            return ResourceUtils.getErrorResult("获取数据源失败！", 1);
        } catch (QueryModelBuildException e1) {
            logger.info("构建问题模型失败！", e1);
            return ResourceUtils.getErrorResult("构建问题模型失败！", 1);
        } catch (Exception e1) {
            logger.info("查询数据失败！", e1);
            return ResourceUtils.getErrorResult("没有查询到相关数据", 1);
        } catch (Throwable t) {
            return ResourceUtils.getErrorResult("没有查询到相关数据", 1);
        }
        
        // 对返回结果进行处理，用于表、图显示
        ResponseResult rs = queryDataResourceUtils.parseQueryResultToResponseResult(runTimeModel, area, 
                resultSet, areaContext, queryAction);
        // 维护平面表分页信息
        if (rs.getStatus() == 0) {
            Map<String, Object> data = (Map<String, Object>) rs.getData();
            if (data.containsKey("head") && data.containsKey("pageInfo") && data.containsKey("data")) {
                PageInfo page = (PageInfo) data.get("pageInfo");
                page.setCurrentPage(pageInfo.getCurrentPage() + 1);
                page.setPageSize(pageInfo.getPageSize());
                data.put("pageInfo", page);
                rs.setData(data);
            }
        }
        // 更新本次操作结果
        runTimeModel.updateDatas(queryAction, resultSet);
        reportModelCacheManager.updateRunTimeModelToCache(reportId, runTimeModel);
        reportModelCacheManager.updateReportModelToCache(reportId, model);
        logger.info("[INFO]successfully sort by " + orderDesc.getName() + 
                " as " + orderDesc.getOrderType() + " for planeTable ");
        logger.info("[INFO]sort planeTable cost : " + (System.currentTimeMillis() - begin) + " ms" );
        result.setStatus(0);
        result.setData(model);
        result.setStatusInfo("successfully remove planeTable condition in runtime phase ");
        return result;
    }

    /**
     * 产生新的排序信息
     * @param cube
     * @param elementId
     * @param sort
     * @return
     */
    private MeasureOrderDesc getNewOrderDesc(Cube cube, String elementId, String sort) {
        // 获取指标
        Map<String, Measure> measures = cube.getMeasures();
        // 获取维度
        Map<String, Dimension> dimensions = cube.getDimensions();
        // 如果待排序列为指标
        if (measures.containsKey(elementId)) {
            Measure measure = measures.get(elementId);
            // 指定排序的名称、排序方式，最后一个暂不解析
            return new MeasureOrderDesc(measure.getName(), sort, 500);
        } else if (dimensions.containsKey(elementId)) {
            // 如果待排序列为维度
            Dimension dimension = dimensions.get(elementId);
            Level l = dimension.getLevels ().values ().toArray (new Level[0])[0];
            // 指定排序的名称、排序方式，最后一个暂不解析
            return new MeasureOrderDesc(l.getName(), sort, 500);
        }
        return null;
    }
    
    /**
     * 获取平面表分页信息
     * @param request
     * @return
     */
    private PageInfo getPageInfo(HttpServletRequest request) {
        PageInfo pageInfo = new PageInfo();
        // 设置分页大小
        if (StringUtils.hasLength(request.getParameter("pageSize"))) {
            pageInfo.setPageSize(Integer.valueOf(request.getParameter("pageSize")));
        }
        // 设置当前页
        if (StringUtils.hasLength(request.getParameter("currentPage"))) {
            pageInfo.setCurrentPage(Integer.valueOf(request.getParameter("currentPage")) -1 );
        }
        // 设置总的记录数
        if (StringUtils.hasLength(request.getParameter("totalRecordCount"))) {
            pageInfo.setTotalRecordCount(Integer.valueOf(request.getParameter("totalRecordCount")));
        } else {
            pageInfo.setTotalRecordCount(-1);
        }
        return pageInfo;
    }
    /**
     * 
     * @param reportId
     * @param runTimeModel
     * @return ReportDesignModel
     */
    private ReportDesignModel getRealModel(String reportId, ReportRuntimeModel runTimeModel) {
        ReportDesignModel model;
        // Object isEditor = runTimeModel.getContext().get(Constants.IN_EDITOR);
        // Object preview = runTimeModel.getContext().get("reportPreview");
        // if ((isEditor != null && isEditor.toString().equals("true"))
        // || (preview != null && preview.toString().equals("true"))) {
        // model = DeepcopyUtils.deepCopy (reportModelCacheManager.getReportModel(reportId));
        // } else {
        model = getDesignModelFromRuntimeModel(reportId);
        // }
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
