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

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeDimension;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.Level;
import com.baidu.rigel.biplatform.ac.model.LevelType;
import com.baidu.rigel.biplatform.ac.model.Measure;
import com.baidu.rigel.biplatform.ac.model.OlapElement;
import com.baidu.rigel.biplatform.ac.util.MetaNameUtil;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceService;
import com.baidu.rigel.biplatform.ma.model.builder.Director;
import com.baidu.rigel.biplatform.ma.model.service.CubeMetaBuildService;
import com.baidu.rigel.biplatform.ma.model.service.PositionType;
import com.baidu.rigel.biplatform.ma.model.service.StarModelBuildService;
import com.baidu.rigel.biplatform.ma.report.exception.CacheOperationException;
import com.baidu.rigel.biplatform.ma.report.model.ExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.ExtendAreaContext;
import com.baidu.rigel.biplatform.ma.report.model.ExtendAreaType;
import com.baidu.rigel.biplatform.ma.report.model.Item;
import com.baidu.rigel.biplatform.ma.report.model.LiteOlapExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.report.model.ReportParam;
import com.baidu.rigel.biplatform.ma.report.query.ReportRuntimeModel;
import com.baidu.rigel.biplatform.ma.report.service.AnalysisChartBuildService;
import com.baidu.rigel.biplatform.ma.report.service.ChartBuildService;
import com.baidu.rigel.biplatform.ma.report.service.QueryBuildService;
import com.baidu.rigel.biplatform.ma.report.service.ReportModelQueryService;
import com.baidu.rigel.biplatform.ma.report.utils.ReportDesignModelUtils;
import com.baidu.rigel.biplatform.ma.resource.cache.ReportModelCacheManager;
import com.baidu.rigel.biplatform.ma.resource.utils.LiteOlapViewUtils;
import com.baidu.rigel.biplatform.ma.resource.utils.ResourceUtils;
import com.baidu.rigel.biplatform.ma.resource.view.liteolap.IndCandicateForChart;
import com.baidu.rigel.biplatform.ma.resource.view.liteolap.MetaData;
import com.baidu.rigel.biplatform.ma.resource.view.liteolap.MetaStatusData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * CubeTable的页面交互
 * 
 * @author zhongyi
 * 
 *         2014-7-30
 */
@RestController
@RequestMapping("/silkroad/reports")
public class LiteOlapResource {

    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(LiteOlapResource.class);

    /**
     * reportModelCacheManager
     */
    @Resource
    private ReportModelCacheManager reportModelCacheManager;

    /**
     * cubeMetaBuildService
     */
    @Resource
    private CubeMetaBuildService cubeBuildService;

    /**
     * starModelBuildService
     */
    @Resource
    private StarModelBuildService starModelBuildService;

    /**
     * queryBuildService
     */
    @Resource
    private QueryBuildService queryBuildService;

    /**
     * analysisChartBuildService
     */
    @Resource
    private AnalysisChartBuildService analysisChartBuildService;

    /**
     * 报表数据查询服务
     */
    @Resource
    private ReportModelQueryService reportModelQueryService;

    /**
     * chartBuildService
     */
    @Resource
    private ChartBuildService chartBuildService;

    /**
     * director
     */
    @Resource
    private Director director;

    /**
     * dsService
     */
    @Resource
    private DataSourceService dsService;

    /**
     * 维度轴标识
     */
    private static final String ROW = "ROW";

    /**
     * 指标轴标识
     */
    private static final String COLUMN = "COLUMN";

    /**
     * 条件轴标识
     */
    private static final String FILTER = "FILTER";

    /**
     * 
     * @param reportId
     * @param request
     * @return ResponseResult
     */

    /**
     * @param reportId
     * @param areaId
     * @param request
     * @return
     */
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/item", method = { RequestMethod.POST })
    public ResponseResult dragAndDrop(@PathVariable("reportId") String reportId, @PathVariable("areaId") String areaId,
            HttpServletRequest request) {

        String from = request.getParameter("from");
        String to = request.getParameter("to");
        int toPosition = Integer.valueOf(request.getParameter("toPosition"));
        String targetName = request.getParameter("uniqNameList");

        if (StringUtils.isEmpty(from) && StringUtils.isEmpty(to)) {
            return ResourceUtils.getCorrectResult("OK", "");
        }
        ReportDesignModel model = null;
        try {
            model = reportModelCacheManager.getRuntimeModel(reportId).getModel();
            // reportModelCacheManager.getReportModel(reportId);
        } catch (CacheOperationException e) {
            logger.error("There are no such model in cache. Report Id: " + reportId, e);
            return ResourceUtils.getErrorResult("没有运行时的报表实例！报表ID：" + reportId, 1);
        }
        if (model == null) {
            return ResourceUtils.getErrorResult("没有运行时的报表实例！报表ID：" + reportId, 1);
        }
        ExtendArea sourceArea = model.getExtendById(areaId);
        if (sourceArea.getType() != ExtendAreaType.SELECTION_AREA) {
            logger.error("Drag Operation is Not supported for type of non-SELECTION_AREA !");
            return ResourceUtils.getErrorResult("Drag Operation is Not supported for type of non-SELECTION_AREA !", 1);
        }
        /**
         * 
         */
        ExtendArea parent = model.getExtendById(sourceArea.getReferenceAreaId());
        if (parent == null || parent.getType() != ExtendAreaType.LITEOLAP) {
            logger.error("Drag Operation is Not supported for type of non-LITEOLAP !");
            return ResourceUtils.getErrorResult("Drag Operation is Not supported for type of non-LITEOLAP !", 1);
        }
        LiteOlapExtendArea liteOlapArea = (LiteOlapExtendArea) parent;
        LogicModel logicModel = liteOlapArea.getLogicModel();
        Item targetItem = null;
//        // TODO yichao.jiang DirtySolution，后续必须进行修改  
//        // 首先校验是否为callback维度或者时间维度，这两种维度不允许进行拖拽
//        boolean isTimeOrCalback = this.isTimeOrCallbackDim(targetName, model);
//        if (isTimeOrCalback) {
//            ResponseResult rs = ResourceUtils.getCorrectResult("OK", "");
//            return rs;
//        }
        // 校验是否允许拖拽，此处是对维度只能拖动到维度和条件轴上
        // 指标只能拖动到指标轴上
        boolean allowDrag = this.preCheck4DragValidate(from, to, targetName, model);
        if (!allowDrag) {
            ResponseResult rs = ResourceUtils.getCorrectResult("OK", "");
            return rs;
        }
        
        if (StringUtils.isEmpty(from)) {
            if (liteOlapArea.getCandDims().containsKey(targetName)) {
                /**
                 * 移动候选维度
                 */
                targetItem = liteOlapArea.getCandDims().get(targetName);
            } else if (liteOlapArea.getCandInds().containsKey(targetName)) {
                /**
                 * 移动候选指标
                 */
                targetItem = liteOlapArea.getCandInds().get(targetName);
            }
        } else {
            switch (from) {
                case ROW:
                    targetItem = logicModel.removeRow(targetName);
                    break;
                case COLUMN:
                    targetItem = logicModel.removeColumn(targetName);
                    break;
                case FILTER:
                    targetItem = logicModel.removeSlice(targetName);
                    break;
                default:
                    return ResourceUtils.getErrorResult("不认识的位置！From: " + from, 1);
            }
        }
        /**
         * 
         */
        if (model == null || liteOlapArea == null || targetItem == null) {
            throw new RuntimeException("未找到指定的维度或指标信息 : model - [" + model + "] area - [" + liteOlapArea + "] item - ["
                    + targetItem + "]");
        }

        OlapElement element =
                ReportDesignModelUtils.getDimOrIndDefineWithId(model.getSchema(), liteOlapArea.getCubeId(),
                        targetItem.getOlapElementId());
        ReportRuntimeModel runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        if (!StringUtils.hasText(to)) {
            // // 如果from不为空，to为空，则表示要将条件拖走，则需要校验该条件是否为必须
            // boolean isNeed = this.checkIsNeed(targetName, model);
            // if (isNeed) {
            // // 如果必须，则将其放置到条件轴上
            // targetItem.setPositionType(PositionType.S);
            // logicModel.addSlice(targetItem, 0);
            // } else {
            /**
             * 根据item的类型加入到候选维度或者候选指标中
             */
            if (element instanceof Dimension) {
                liteOlapArea.getCandDims().put(element.getId(), targetItem);
            } else if (element instanceof Measure) {
                liteOlapArea.getCandInds().put(element.getId(), targetItem);
            }
            
            
            // }
        } else {
            // TODO 后续考虑优化
            if (element instanceof Dimension) {
                MiniCubeDimension dimension = (MiniCubeDimension) element;
                Level level = dimension.getLevels().values().toArray(new Level[0])[0];
                // 如果拖动的是岗位条件，则将其放置到第一个位置
                if (level.getType() == LevelType.CALL_BACK) {
                    toPosition = 0;
                } else {
                    // TODO 如果拖动的是其他维度，则需要保证岗位在第一个位置
                    // 如果目标轴是横轴，需要保证岗位在第一个位置
                    if (ROW.equals(to)) {
                        // 横轴上至少有一个维度，所以无需判定非空
                        Item[] items = logicModel.getRows();
                        // 如果维度轴上的第一个位置是岗位
                        if (this.isTimeOrCallbackDim(items[0].getId(), model)) {
                            toPosition = toPosition + 1;
                        }
                    }
                }
            }
            switch (to) {
                case ROW:
                    targetItem.setPositionType(PositionType.X);
                    logicModel.addRow(targetItem, toPosition);
                    break;
                case COLUMN:
                    if (element instanceof Measure) {
                        targetItem.setPositionType(PositionType.Y);
                        logicModel.addColumn(targetItem, toPosition);
                    }
                    break;
                case FILTER:
                    targetItem.setPositionType(PositionType.S);
                    logicModel.addSlice(targetItem, toPosition);
                    break;
                default:
                    return ResourceUtils.getErrorResult("不认识的位置！To: " + to, 1);
            }
        }
        // reportModelCacheManager.updateReportModelToCache(reportId, model);
        ExtendAreaContext extendContext = reportModelCacheManager.getAreaContext(reportId,
                liteOlapArea.getTableAreaId());
        // 获取对应项的elementId
        String olapElementId = targetItem.getOlapElementId();
        // 仅在维度轴上拖拽，此时需要清理图形上的面包屑和uniqueName信息，无需清理候选区域上的参数
        if ((!StringUtils.isEmpty(to) && !StringUtils.isEmpty(from))
                && ((ROW.equals(to) && to.equals(from)) || (ROW.equals(from) && FILTER.equals(to)))) {
            String liteOlapTableId = liteOlapArea.getTableAreaId();
            // 如果面包屑上对应的维度和本次移动的维度不相等，则需要从面包屑中找到对应的维度，将其条件清除
            String uniqueName = (String) extendContext.getParams().get("uniqueName");
            String dimElementId = "";
            if (!StringUtils.isEmpty(uniqueName)) {
                // 如果有@符号，先替换掉
                if (uniqueName.startsWith("@")) {
                    uniqueName = uniqueName.substring(1, uniqueName.length() - 1);
                }
                String dimName = "";
                // 先处理uniqueName为数组的情况
                if (uniqueName.contains(",")) {
                    String[] uniqueNameArray = uniqueName.split(",");
                    dimName = MetaNameUtil.getDimNameFromUniqueName(uniqueNameArray[0]);
                } else {
                    // 根据uniqueName获取维度名称
                    dimName = MetaNameUtil.getDimNameFromUniqueName(uniqueName);
                }
                // 定义一个变量，方便在java 8中使用
                final String finalDimName = dimName;
                // 从cube中读取对应的维度信息，进而获取dimId
                Cube cube = model.getSchema().getCubes().get(sourceArea.getCubeId());
                if (cube.getDimensions() != null && cube.getDimensions().size() != 0) {
                    Object[] tmp =  cube.getDimensions().values().stream().filter (dim -> dim != null).filter(dim -> {
                        return finalDimName.equals(dim.getName());
                    }).toArray();
                    if (tmp != null && tmp.length == 1) {
                        dimElementId = ((OlapElement) tmp[0]).getId();
                    }
                }
            }
            // 移除对应的参数
            if (dimElementId != null) {
                runTimeModel.getLocalContextByAreaId(liteOlapTableId).getParams().remove(dimElementId);
                extendContext.getParams().remove(dimElementId);
            }
            // 清除表上同面包屑、参数有关的信息
            runTimeModel.getLocalContextByAreaId(liteOlapTableId).getParams().remove(olapElementId);
            runTimeModel.getLocalContextByAreaId(liteOlapTableId).getParams().remove("uniqueName");
            runTimeModel.getLocalContextByAreaId(liteOlapTableId).getParams().remove("bread_key");
            
            // 清除图形上同面包屑、参数有关的信息
            String liteOlapChartId = liteOlapArea.getChartAreaId();
            runTimeModel.getLocalContextByAreaId(liteOlapChartId).getParams().remove(olapElementId);
            runTimeModel.getLocalContextByAreaId(liteOlapChartId).getParams().remove("uniqueName");
            runTimeModel.getLocalContextByAreaId(liteOlapChartId).getParams().remove("bread_key");
            // 清除参数信息
            extendContext.getParams().remove(olapElementId);
            extendContext.getParams().remove("uniqueName");
            extendContext.getParams().remove("bread_key");
        }
        // 如果from不为空，但是to为空，则需要根据情况将该项对应的条件从lite-olap选择区域清除，同时需要清除面包屑
        if (StringUtils.isEmpty(to) && !StringUtils.isEmpty(from)) {
            // step1. 清除局部extendAreaContext
            extendContext.getParams().remove(olapElementId);
            extendContext.getParams().remove("uniqueName");
            extendContext.getParams().remove("bread_key");
            // step2. 清除selection-area中的参数
            runTimeModel.getLocalContextByAreaId(liteOlapArea.getSelectionAreaId()).getParams().remove(olapElementId);
            runTimeModel.getLocalContextByAreaId(liteOlapArea.getSelectionAreaId()).getParams().remove("uniqueName");
            runTimeModel.getLocalContextByAreaId(liteOlapArea.getSelectionAreaId()).getParams().remove("bread_key");
            
            // step3. 从表区域中删除
            runTimeModel.getLocalContextByAreaId(liteOlapArea.getTableAreaId()).getParams().remove(element.getId());
            // step4. 从图区域中删除
            runTimeModel.getLocalContextByAreaId(liteOlapArea.getChartAreaId()).getParams().remove(element.getId());
            // step5. 获取该id对应的参数维度p中的参数名称
            String paramName = this.getParamDimension(element.getId(), model);
            // 如果参数名称不为空，则同样将其移除
            if (StringUtils.hasText(paramName)) {
                extendContext.getParams().remove(paramName);
                runTimeModel.getLocalContextByAreaId(areaId).getParams().remove(paramName);
                runTimeModel.getLocalContextByAreaId(liteOlapArea.getTableAreaId()).getParams().remove(paramName);
                runTimeModel.getLocalContextByAreaId(liteOlapArea.getChartAreaId()).getParams().remove(paramName);
            }
        }
        // 统一清理面包屑问题
        extendContext.setCurBreadCrumPath(null);
        // 更新context
        reportModelCacheManager.updateAreaContext(reportId, 
              liteOlapArea.getTableAreaId(), extendContext);
//        if (extendContext.getCurBreadCrumPath() != null) {
//            List<Map<String, String>> breadPath = extendContext.getCurBreadCrumPath();
//            String uniqueName = breadPath.get(breadPath.size() - 1).get("uniqName");
//            if (uniqueName.startsWith("@")) {
//                uniqueName = uniqueName.substring(1, uniqueName.length() - 1);
//            }
//            Cube cube = model.getSchema().getCubes().get(sourceArea.getCubeId());
//            Dimension dim = cube.getDimensions().get(logicModel.getRows()[0].getId());
//            if (dim != null && !dim.getName().equals(MetaNameUtil.getDimNameFromUniqueName(uniqueName))) {
//                runTimeModel.getLocalContext().clear();
//                reportModelCacheManager.updateAreaContext(reportId, 
//                        liteOlapArea.getTableAreaId(), new ExtendAreaContext());
//                
//            }
//        }
//        // 移除或移入维度轴清理面包屑
//        if ((StringUtils.isEmpty(to) && ROW.equals(from)) 
//                || (StringUtils.isEmpty(from) && ROW.equals(to))) {
////            runTimeModel.getLocalContext().clear();
//            runTimeModel.getLocalContext().remove("bread_key");
//            reportModelCacheManager.updateAreaContext(reportId, 
//                    liteOlapArea.getTableAreaId(), new ExtendAreaContext());
//        }
        runTimeModel.getDrillDownQueryHistory().clear();
        runTimeModel.setLinkedQueryAction(null);
        runTimeModel.getDatas().clear();
        // runTimeModel.setModel (model);
        runTimeModel.updateDimStores(model);
        reportModelCacheManager.updateRunTimeModelToCache(reportId, runTimeModel);
        ResponseResult rs = ResourceUtils.getCorrectResult("OK", "");
        return rs;
    }

    /**
     * 判断某个维度是否设置了参数维度
     * @param elementId 维度或者指标id
     * @param model 报表模型
     * @return 返回该维度对应的参数名称
     */
    private String getParamDimension(String elementId, ReportDesignModel model) {
        Map<String, ReportParam> reportParams = model.getParams();
        // 对于必须的参数
        if (reportParams != null && reportParams.size() != 0) {
            for (String key : reportParams.keySet()) {
                ReportParam param = reportParams.get(key);
                if (param.getElementId().equals(elementId)) {
                    return param.getName();
                }
            }
        }
        return null;
    }

    /**
     * 校验是否为时间维度或者callback维度
     * @param elemengId
     * @param model
     * @return
     */
    private boolean isTimeOrCallbackDim(String elementId, ReportDesignModel model) {
        OlapElement element = this.getOlapElementAccordingName(model, elementId);
        if (element != null && element instanceof Dimension) {
            Dimension dim = (Dimension) element;
            if (dim.isTimeDimension()) {
                return true;
            } else {
                Level level = dim.getLevels().values().toArray(new Level[0])[0];
                return level.getType() == LevelType.CALL_BACK;
            }
        }
        return false;
    }
    
    /**
     * 在拖拽前校验是否可以拖拽 校验规则：1.维度仅能拖动到维度轴或者条件轴上；2.指标仅能拖动到指标轴
     * 
     * @param to
     * @param elementId
     * @param model
     * @return true表示可以拖拽；false表示不可以
     */
    private boolean preCheck4DragValidate(String from, String to, String elementId, ReportDesignModel model) {
        // 1.检查是否存在非法拖拽情况
        OlapElement element = this.getOlapElementAccordingName(model, elementId);
        // 对于指标，不允许移动到维度轴或者条件轴
        if (element instanceof Measure && (ROW.equals(to) || FILTER.equals(to))) {
            return false;
        }
        // 对于维度，不允许移动到指标轴
        if (element instanceof Dimension && COLUMN.equals(to)) {
            return false;
        }
        return true;
    }

    /**
     * 根据targetName获取维度或者指标，在这里targetName实际为elementId
     * 
     * @param model
     * @param targetName
     * @return
     */
    private OlapElement getOlapElementAccordingName(ReportDesignModel model, String targetName) {
        if (!StringUtils.isEmpty(targetName) && model.getSchema() != null && model.getSchema().getCubes().size() != 0
                && !CollectionUtils.isEmpty(model.getSchema().getCubes().values())) {
            MiniCube cube = model.getSchema().getCubes().values().toArray(new MiniCube[0])[0];
            OlapElement element =
                    ReportDesignModelUtils.getDimOrIndDefineWithId(model.getSchema(), cube.getId(), targetName);
            return element;
        }
        return null;
    }

    /**
     * 
     * @param reportId
     * @param request
     * @return
     */
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/config", method = { RequestMethod.POST })
    public ResponseResult getConfigOfArea(@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId, HttpServletRequest request) {

        ReportDesignModel model = null;
        try {
            model = reportModelCacheManager.getRuntimeModel(reportId).getModel();
        } catch (CacheOperationException e) {
            logger.error("There are no such model in cache. Report Id: " + reportId, e);
            return ResourceUtils.getErrorResult("没有运行时的报表实例！报表ID：" + reportId, 1);
        }
        ExtendArea target = model.getExtendById(areaId);
        if (target.getType() != ExtendAreaType.SELECTION_AREA) {
            logger.debug("not support for getting config of non-SELECTION area! ");
            return ResourceUtils.getCorrectResult("OK", "");
        }
        ExtendArea parent = model.getExtendById(target.getReferenceAreaId());
        if (parent.getType() != ExtendAreaType.LITEOLAP) {
            logger.error("Get Config Operation is Not supported for type of non-LITEOLAP !");
            return ResourceUtils.getErrorResult("Drag Operation is Not supported for type of non-LITEOLAP !", 1);
        }
        LiteOlapExtendArea liteOlapArea = (LiteOlapExtendArea) parent;
        MetaData metaData = LiteOlapViewUtils.parseMetaData(liteOlapArea, model.getSchema());
        MetaStatusData metaStatusData = LiteOlapViewUtils.parseMetaStatusData(liteOlapArea, model.getSchema());
        Map<String, Object> selected = LiteOlapViewUtils.parseSelectedItemMap(liteOlapArea, model.getSchema());
        /*
         * wrap the result
         */
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("index4Selected", new String[] { "COLUMN", "ROW", "FILTER" });
        resultMap.put("metaData", metaData);
        resultMap.put("metaStatusData", metaStatusData);
        resultMap.put("selected", selected);
        return ResourceUtils.getCorrectResult("OK", resultMap);
    }

    /**
     * 获取当前图上可选的指标
     */
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/ind_for_chart", method = { RequestMethod.POST })
    public ResponseResult getIndsForChart(@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId, HttpServletRequest request) {

        ReportDesignModel model = null;
        try {
            model = reportModelCacheManager.getRuntimeModel(reportId).getModel();
            // reportModelCacheManager.getReportModel(reportId);
        } catch (CacheOperationException e) {
            logger.error("There are no such model in cache. Report Id: " + reportId, e);
            return ResourceUtils.getErrorResult("没有运行时的报表实例！报表ID：" + reportId, 1);
        }
        ExtendArea target = model.getExtendById(areaId);
        if (target.getType() != ExtendAreaType.LITEOLAP_CHART) {
            logger.debug("not support for getting config of non-SELECTION area! ");
            return ResourceUtils.getCorrectResult("OK", "");
        }
        ExtendArea parent = model.getExtendById(target.getReferenceAreaId());
        if (parent.getType() != ExtendAreaType.LITEOLAP) {
            logger.error("Get Config Operation is Not supported for type of non-LITEOLAP !");
            return ResourceUtils.getErrorResult("Drag Operation is Not supported for type of non-LITEOLAP !", 1);
        }
        LogicModel logicModel = parent.getLogicModel();
        Map<String, Object> resultMap = Maps.newHashMap();
        List<IndCandicateForChart> inds = Lists.newArrayList();
        for (Item item : logicModel.getColumns()) {
            OlapElement element =
                    ReportDesignModelUtils.getDimOrIndDefineWithId(model.getSchema(), item.getCubeId(),
                            item.getOlapElementId());
            IndCandicateForChart indForChart = LiteOlapViewUtils.parseIndForChart(element);
            inds.add(indForChart);
        }
        resultMap.put("currentInds", new String[0]);
        resultMap.put("inds", inds.toArray(new IndCandicateForChart[0]));
        return ResourceUtils.getCorrectResult("OK", resultMap);
    }

}