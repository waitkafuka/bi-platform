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

import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.SerializationUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.baidu.rigel.biplatform.ac.exception.MiniCubeQueryException;
import com.baidu.rigel.biplatform.ac.minicube.CallbackLevel;
import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMember;
import com.baidu.rigel.biplatform.ac.minicube.TimeDimension;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.DimensionType;
import com.baidu.rigel.biplatform.ac.model.Level;
import com.baidu.rigel.biplatform.ac.model.LevelType;
import com.baidu.rigel.biplatform.ac.model.Member;
import com.baidu.rigel.biplatform.ac.model.OlapElement;
import com.baidu.rigel.biplatform.ac.model.TimeType;
import com.baidu.rigel.biplatform.ac.query.data.DataModel;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.data.HeadField;
import com.baidu.rigel.biplatform.ac.query.model.ConfigQuestionModel;
import com.baidu.rigel.biplatform.ac.query.model.PageInfo;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.ac.query.model.SortRecord;
import com.baidu.rigel.biplatform.ac.util.DeepcopyUtils;
import com.baidu.rigel.biplatform.ac.util.HttpRequest;
import com.baidu.rigel.biplatform.ac.util.MetaNameUtil;
import com.baidu.rigel.biplatform.ac.util.TimeUtils;
import com.baidu.rigel.biplatform.api.client.service.FileService;
import com.baidu.rigel.biplatform.api.client.service.FileServiceException;
import com.baidu.rigel.biplatform.asyndownload.AyncAddDownloadTaskServiceFactory;
import com.baidu.rigel.biplatform.asyndownload.bo.AddTaskParameters;
import com.baidu.rigel.biplatform.asyndownload.bo.AddTaskStatus;
import com.baidu.rigel.biplatform.cache.util.ApplicationContextHelper;
import com.baidu.rigel.biplatform.ma.comm.util.ParamValidateUtils;
import com.baidu.rigel.biplatform.ma.download.DownloadType;
import com.baidu.rigel.biplatform.ma.download.service.DownloadServiceFactory;
import com.baidu.rigel.biplatform.ma.download.service.DownloadTableDataService;
import com.baidu.rigel.biplatform.ma.ds.exception.DataSourceConnectionException;
import com.baidu.rigel.biplatform.ma.ds.exception.DataSourceOperationException;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceConnectionService;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceConnectionServiceFactory;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceService;
import com.baidu.rigel.biplatform.ma.model.builder.Director;
import com.baidu.rigel.biplatform.ma.model.consts.Constants;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.model.service.CubeMetaBuildService;
import com.baidu.rigel.biplatform.ma.model.service.PositionType;
import com.baidu.rigel.biplatform.ma.model.service.StarModelBuildService;
import com.baidu.rigel.biplatform.ma.model.utils.GsonUtils;
import com.baidu.rigel.biplatform.ma.report.exception.CacheOperationException;
import com.baidu.rigel.biplatform.ma.report.exception.PivotTableParseException;
import com.baidu.rigel.biplatform.ma.report.exception.QueryModelBuildException;
import com.baidu.rigel.biplatform.ma.report.model.ExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.ExtendAreaContext;
import com.baidu.rigel.biplatform.ma.report.model.ExtendAreaType;
import com.baidu.rigel.biplatform.ma.report.model.FormatModel;
import com.baidu.rigel.biplatform.ma.report.model.Item;
import com.baidu.rigel.biplatform.ma.report.model.LinkParams;
import com.baidu.rigel.biplatform.ma.report.model.LiteOlapExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.baidu.rigel.biplatform.ma.report.model.MeasureTopSetting;
import com.baidu.rigel.biplatform.ma.report.model.PlaneTableCondition;
import com.baidu.rigel.biplatform.ma.report.model.PlaneTableFormat.PaginationSetting;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.report.model.ReportParam;
import com.baidu.rigel.biplatform.ma.report.query.QueryAction;
import com.baidu.rigel.biplatform.ma.report.query.QueryContext;
import com.baidu.rigel.biplatform.ma.report.query.ReportRuntimeModel;
import com.baidu.rigel.biplatform.ma.report.query.ReportRuntimeModel.DrillDownAction;
import com.baidu.rigel.biplatform.ma.report.query.ResultSet;
import com.baidu.rigel.biplatform.ma.report.query.chart.ChartShowType;
import com.baidu.rigel.biplatform.ma.report.query.chart.DIReportChart;
import com.baidu.rigel.biplatform.ma.report.query.chart.SeriesInputInfo.SeriesUnitType;
import com.baidu.rigel.biplatform.ma.report.query.pivottable.CellData;
import com.baidu.rigel.biplatform.ma.report.query.pivottable.PivotTable;
import com.baidu.rigel.biplatform.ma.report.query.pivottable.RowHeadField;
import com.baidu.rigel.biplatform.ma.report.service.AnalysisChartBuildService;
import com.baidu.rigel.biplatform.ma.report.service.ChartBuildService;
import com.baidu.rigel.biplatform.ma.report.service.QueryBuildService;
import com.baidu.rigel.biplatform.ma.report.service.ReportDesignModelService;
import com.baidu.rigel.biplatform.ma.report.service.ReportModelQueryService;
import com.baidu.rigel.biplatform.ma.report.utils.QueryUtils;
import com.baidu.rigel.biplatform.ma.report.utils.ReportDesignModelUtils;
import com.baidu.rigel.biplatform.ma.resource.cache.ReportModelCacheManager;
import com.baidu.rigel.biplatform.ma.resource.utils.DataModelUtils;
import com.baidu.rigel.biplatform.ma.resource.utils.PlaneTableUtils;
import com.baidu.rigel.biplatform.ma.resource.utils.QueryDataResourceUtils;
import com.baidu.rigel.biplatform.ma.resource.utils.ResourceUtils;
import com.baidu.rigel.biplatform.ma.resource.view.vo.DimensionMemberViewObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * CubeTable的页面交互
 * 
 * @author zhongyi
 * 
 *         2014-7-30
 */
@RestController
@RequestMapping("/silkroad/reports")
public class QueryDataResource extends BaseResource {
    
    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(QueryDataResource.class);

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
     * reportDesignModelService
     */
    @Resource(name = "reportDesignModelService")
    private ReportDesignModelService reportDesignModelService;

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

    @Resource(name = "fileService")
    private FileService fileService;
    
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
     * queryDataResourceUtils
     */
    @Resource
    private QueryDataResourceUtils queryDataResourceUtils;

    /**
     * pageSize
     */
    private static final String PAGE_SIZE = "pageSize";
    
    /**
     * currentPage
     */
    private static final String CURRENT_PAGE = "currentPage";
    
    /**
     * totalRecordCount
     */
    private static final String TOTAL_RECORD_COUNT = "totalRecordCount";
    
    /**
     * 初始化查询参数,初始化查询区域参数
     * 
     * @param reportId
     * @param request
     * @return ResponseResult
     */
    @RequestMapping(value = "/{reportId}/init_params", method = { RequestMethod.POST })
    public ResponseResult initParams(@PathVariable("reportId") String reportId, HttpServletRequest request) {
        long begin = System.currentTimeMillis();
        logger.info("[INFO]--- ---begin init params with report id {}", reportId);
        String areaIdList = request.getParameter("paramList");
        String[] areaIds = null;
        final ReportDesignModel model = getDesignModelFromRuntimeModel(reportId);
        if (!StringUtils.isEmpty (areaIdList)) {
            areaIds = areaIdList.split(",");
        } 
        if (areaIds == null || areaIds.length == 0) {
            ResponseResult rs = new ResponseResult();
            rs.setStatus(0);
            logger.info("[INFO]--- --- not needed init global params");
            return rs;
        }
        final ReportRuntimeModel runtimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        Map<String, Object> datas = Maps.newConcurrentMap();
        Map<String, String> params = Maps.newHashMap();
        runtimeModel.getContext().getParams().forEach((k, v) -> {
            params.put(k, v == null ? "" : v.toString());
        });

        for (final String areaId : areaIds) {
            ExtendArea area = model.getExtendById(areaId);
            Cube cube = null;
            if (area != null) {
                // 获取对应的cube
                cube = model.getSchema().getCubes().get(area.getCubeId());
            }
            // TODO 查询条件回填？
            if (area != null && isQueryComp(area.getType()) && !area.listAllItems().isEmpty()) {
                Item item = area.listAllItems().values().toArray(new Item[0])[0];
                Cube tmpCube = QueryUtils.transformCube(cube);
                String dimId = item.getOlapElementId();
                Dimension dim = cube.getDimensions().get(dimId);
                if (dim != null) {
                    List<Map<String, String>> values;
                    try {
                        values = Lists.newArrayList();
                        params.remove(dim.getId());
                        params.put(Constants.LEVEL_KEY, "1");
                        List<Member> members =
                                reportModelQueryService.getMembers(tmpCube, tmpCube.getDimensions().get(dim.getName()),
                                        params, securityKey).get(0);
                        members.forEach(m -> {
                            Map<String, String> tmp = Maps.newHashMap();
                            tmp.put("value", m.getUniqueName());
                            tmp.put("text", m.getCaption());
                            if (dim.getLevels().size() <= 1) {
                                tmp.put("isLeaf", "1");
                            }
                            MiniCubeMember realMember = (MiniCubeMember) m;
                            if (realMember.getParent() != null) {
                                tmp.put("parent", realMember.getParent().getUniqueName());
                            } else {
                                tmp.put("parent", "");
                            }
                            values.add(tmp);
                            List<Map<String, String>> children = getChildren(realMember, realMember.getChildren());
                            if (children != null && !children.isEmpty()) {
                                values.addAll(children);
                            }
                        });
                        // List<Map<String, String>> values =
                        // QueryUtils.getMembersWithChildrenValue(members, tmpCube, dsInfo, Maps.newHashMap());
                        Map<String, Object> datasource = Maps.newHashMap();
                        datasource.put("datasource", values);
                        fillBackParamValues (runtimeModel, dim, datasource);
                        datas.put(areaId, datasource);
                    } catch (Exception e) {
                        logger.info(e.getMessage(), e);
                    }
                }
            }
        }
        ResponseResult rs = new ResponseResult();
        rs.setStatus(0);
        rs.setData(datas);
        rs.setStatusInfo("OK");
        logger.info("[INFO]--- --- successfully init params, cost {} ms", (System.currentTimeMillis() - begin));
        return rs;
    }

    private void fillBackParamValues(final ReportRuntimeModel runtimeModel,
            Dimension dim, Map<String, Object> datasource) {
        runtimeModel.getLocalContext ().forEach ((k, v) -> {
            if (v.getParams ().containsKey (dim.getId ())) {
                Object value = v.getParams ().get (dim.getId ());
                if (value != null && value instanceof String) {
                    List<String> lists = Lists.newArrayList (((String) value).split (","));
                    datasource.put ("value", lists);
                }
            }
        });
    }

    private List<Map<String, String>> getChildren(Member parent, List<Member> children) {
        if (children == null || children.isEmpty()) {
            return null;
        }
        List<Map<String, String>> rs = Lists.newArrayList();
        MiniCubeMember tmp = null;
        for (Member m : children) {
            tmp = (MiniCubeMember) m;
            Map<String, String> map = Maps.newHashMap();
            map.put("value", tmp.getUniqueName());
            map.put("text", tmp.getCaption());
            map.put("parent", parent.getUniqueName());
            rs.add(map);
            if (!CollectionUtils.isEmpty(tmp.getChildren())) {
                rs.addAll(getChildren(tmp, tmp.getChildren()));
            }
        }
        return rs;
    }

    /**
     * @param reportId
     * @return ReportDesignModel
     */
    ReportDesignModel getDesignModelFromRuntimeModel(String reportId) {
        return reportModelCacheManager.getRuntimeModel(reportId).getModel();
    }

    /**
     * 
     * @param type 区域类型
     * @return boolean
     */
    private boolean isQueryComp(ExtendAreaType type) {
        return QueryUtils.isFilterArea(type);
    }

    /**
     * 
     * @param reportId
     * @param request
     * @return ResponseResult
     */
    @RequestMapping(value = "/{reportId}/report_id", method = { RequestMethod.GET })
    public ResponseResult getReport(@PathVariable("reportId") String reportId, HttpServletRequest request) {
        long begin = System.currentTimeMillis();
        logger.info("[INFO] --- --- begin query report model");
        ReportDesignModel model = null;
        try {
            model = this.getDesignModelFromRuntimeModel(reportId); // reportModelCacheManager.getReportModel(reportId);
        } catch (CacheOperationException e1) {
            logger.info("[INFO]--- --- can't not get report form cache", e1.getMessage());
            return ResourceUtils.getErrorResult(e1.getMessage(), ResponseResult.FAILED);
        }
        // reportModelCacheManager.loadReportModelToCache(reportId);
        ResponseResult rs = ResourceUtils.getCorrectResult("OK", model);
        logger.info("[INFO] --- --- query report model successuffly, cost {} ms", (System.currentTimeMillis() - begin));
        return rs;
    }

    /**
     * 
     * @param reportId
     * @param request
     * @param response
     * @return String
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{reportId}/report_vm", method = { RequestMethod.GET, RequestMethod.POST },
            produces = "text/html;charset=utf-8")
    public String queryVM(@PathVariable("reportId") String reportId, HttpServletRequest request,
            HttpServletResponse response) {
        long begin = System.currentTimeMillis();
        ReportDesignModel model = null;
        String reportPreview = request.getParameter("reportPreview");
        String imageId = request.getParameter ("reportImageId");
        ReportRuntimeModel runtimeModel = null;
        try {
            if (StringUtils.isEmpty (imageId) || reportId.equals (imageId)) {
                if (!StringUtils.isEmpty (reportPreview) && Boolean.valueOf (reportPreview)) {
                    model = reportModelCacheManager.getReportModel (reportId);
                    if (model != null) {
                        model = DeepcopyUtils.deepCopy (model);
                    }
                } else {
                    model = reportDesignModelService.getModelByIdOrName (reportId, true);
                }
            }
        } catch (CacheOperationException e1) {
            logger.info("[INFO]--- ---Fail in loading release report model into cache. ", e1);
//            throw new IllegalStateException();
        }
        
        if (model != null) {
            runtimeModel = new ReportRuntimeModel(reportId);
            runtimeModel.init (model, true);
        } else {
            try {
                String path = getSavedReportPath (request);
                String fileName = path + File.separator + reportId + File.separator + imageId;
                runtimeModel = 
                    (ReportRuntimeModel) SerializationUtils.deserialize (fileService.read (fileName));
                model = runtimeModel.getModel ();
            } catch (FileServiceException e) {
                logger.info("[INFO]--- ---加载保存的报表失败 ", e);
            }
        }
        if (runtimeModel == null) {
            logger.info("[INFO]--- ---init runtime model failed ");
            throw new RuntimeException("初始化报表模型失败");
        }
        // modify by jiangyichao at 2014-10-10
        // 将url参数添加到全局上下文中
        Enumeration<String> params = request.getParameterNames();
        // 请求参数
        Map<String, String> requestParams = Maps.newHashMap();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            if (request.getParameter (paramName) != null) {
                runtimeModel.getContext().put(paramName, request.getParameter(paramName));
                requestParams.put(paramName, request.getParameter(paramName));
            }
        }
        // 添加cookie内容
        runtimeModel.getContext().put(HttpRequest.COOKIE_PARAM_NAME, request.getHeader("Cookie"));

        // 获取多维数据表的报表Id
        String fromReportId = request.getParameter("fromReportId");
        // 平面表id
        String toReportId = request.getParameter("toReportId");
        // 如果是由多维跳转到明细
        if (!StringUtils.isEmpty(fromReportId) && !StringUtils.isEmpty(toReportId)) {
            // 从cache中取得多维表的运行态模型
            ReportRuntimeModel fromRuntimeModel = reportModelCacheManager.getRuntimeModel(fromReportId);
            // 如果从cache中取不到多维表的运行态模型，则抛出异常
            if (fromRuntimeModel == null) {
                logger.info("[INFO]--- ---无法获取多维表运行态模型, id :", fromReportId);
                throw new IllegalStateException("[INFO]--- ---无法获取多维表运行态模型, id :" + fromReportId);
            }
            
            // 多维表cube
            Cube multiCube = null;
            ExtendArea[] multiExtendAreas = fromRuntimeModel.getModel().getExtendAreaList();
            // 获取多维表对应的因为此处仅考虑一个cube
            for (ExtendArea extendArea : multiExtendAreas) {
                if (extendArea != null) {
                    multiCube = fromRuntimeModel.getModel().getSchema().getCubes().get(extendArea.getCubeId());                    
                }
            }
            
            // 平面表cube
            Cube planeCube = null;
            ExtendArea[] planeExtendAreas = model.getExtendAreaList();
            for (ExtendArea extendArea : planeExtendAreas) {
                if (extendArea != null && extendArea.getType() == ExtendAreaType.PLANE_TABLE) {
                    planeCube = model.getSchema().getCubes().get(extendArea.getCubeId());
                }
            }
            
            Map<String, PlaneTableCondition> planeTableConditions = 
                    model.getPlaneTableConditions();
            Map<String, Object> fromParams = fromRuntimeModel.getContext().getParams();
//            runtimeModel.getContext().getParams().putAll(
//                    PlaneTableUtils.handelTimeCondition(cube, fromParams));
            // 如果包含跳转参数
            if (fromParams != null && fromParams.containsKey("linkBridgeParams")) {
                Map<String, LinkParams> linkParams = (Map<String, LinkParams>) fromParams.get("linkBridgeParams");
                Map<String, String> planeTableCond = Maps.newHashMap();
                if (planeTableConditions == null || planeTableConditions.size() == 0) {
                    throw new RuntimeException("the plane table conditions is empty, its id is : " + toReportId);
                }
                planeTableConditions.forEach( (k, v) -> {
//                    LinkParams linkParam = linkParams.get(v.getName());
//                    if (StringUtils.isEmpty(linkParam) ||
//                            StringUtils.isEmpty(linkParam.getOriginalDimValue()) ||
//                                StringUtils.isEmpty(linkParam.getUniqueName())) {
//                        throw new RuntimeException("the need params { " + v.getName() + " } is empty, please check!");
//                    }
                    planeTableCond.put(v.getName(), v.getElementId());
                    planeTableCond.put(v.getElementId(), v.getName());
                });
                
                for (String key : linkParams.keySet()) {
                    LinkParams linkParam = linkParams.get(key);                   
                    if (StringUtils.isEmpty(linkParam.getOriginalDimValue()) ||
                            StringUtils.isEmpty(linkParam.getUniqueName())) {
                        continue;
                    }
                    String newValue = null;
                    String planeTableConditionKey = null;
                    try {
                        planeTableConditionKey = planeTableCond.get(linkParam.getParamName());
//                        for (String conditionKey : planeTableConditions.keySet()) {
//                            if (planeTableConditions.get(conditionKey).getName().
//                                    equals(linkParam.getParamName())) {
//                                planeTableConditionKey = conditionKey;
//                            }
//                        }
                        if (linkParam.getOriginalDimValue() != null && PlaneTableUtils.isTimeDim(planeCube, planeTableConditionKey)) {
                            // 如果是普通时间JSON字符串
                            if (PlaneTableUtils.isTimeJson(linkParam.getOriginalDimValue())) {
                                newValue = linkParam.getOriginalDimValue();
                            } else {
                                // 如果不是规范的时间JSON字符串，则需特殊处理
                                newValue = PlaneTableUtils.convert2TimeJson(linkParam.getOriginalDimValue(), fromParams);
                            }
                        } else {
                            if (MetaNameUtil.isUniqueName(linkParam.getUniqueName())) {
                                requestParams.put(HttpRequest.COOKIE_PARAM_NAME, request.getHeader("Cookie"));
                                newValue = 
                                        this.handleReqParams4PlaneTable(multiCube, planeTableCond, 
                                                linkParam.getUniqueName(), requestParams, securityKey);                                                                                        
                            } else {
                                newValue = linkParam.getOriginalDimValue();
                            }
                        }
                        logger.debug("the linkParam {" + linkParam.getParamName() + 
                                "}, and it's origin value is [" + linkParam.getOriginalDimValue() +
                                "], and it's new value are [" + newValue + "]");
                    } catch (Exception e) {
                        logger.error("处理平面表参数出错，请检查!");
                        throw new RuntimeException("处理平面表参数出错，请检查!");
                    }
                    if (newValue != null) {
                        runtimeModel.getContext().getParams().put(key, newValue);                        
                    }
                    if (planeTableConditionKey != null && newValue != null) {
                        runtimeModel.getContext().getParams().put(planeTableConditionKey, newValue);
                    }
                }
            } 
        } else {
            /**
             * 依据查询请求，根据报表参数定义，增量添加报表区域模型参数
             */
            Map<String, Object> tmp = QueryUtils.resetContextParam(request, model);
            runtimeModel.getContext().getParams().putAll(tmp);            
        }
        if (StringUtils.isEmpty (imageId) || reportId.equals (imageId)) {
            reportModelCacheManager.updateRunTimeModelToCache(reportId, runtimeModel);
        } else {
            reportModelCacheManager.updateRunTimeModelToCache(imageId, runtimeModel);
        }
        StringBuilder builder = buildVMString(reportId, request, response, model);
        logger.info("[INFO] query vm operation successfully, cost {} ms", (System.currentTimeMillis() - begin));
        // 如果请求中包含UID 信息，则将uid信息写入cookie中，方便后边查询请求应用
        String uid = request.getParameter (UID_KEY);
        if (uid != null) {
            Cookie cookie = new Cookie (UID_KEY, uid);
            cookie.setPath (Constants.COOKIE_PATH);
            response.addCookie (cookie);
        }
        if (request.getParameter ("newPlatform") != null) {
            return "<!DOCTYPE html><html>"
                    + "<head><meta charset=\"utf-8\"><title>报表平台-展示端</title>"
                    + "<meta name=\"description\" content=\"报表平台展示端\">"
                    + "<meta name=\"viewport\" content=\"width=device-width\">"
                    + "</head>"
                    + "<body>"
                    + "<script type=\"text/javascript\">"
                    + "var seed = document.createElement('script');"
                    + "seed.src = '/silkroad/new-biplatform/asset/seed.js?action=display&t=' + (+new Date());"
                    + "document.getElementsByTagName('head')[0].appendChild(seed);"
                    + "</script>" + "</body>" + "</html>";
        }
        return builder.toString();
    }

    /**
     * @param reportId
     * @param response
     * @param model
     * @return StringBuilder
     */
    private StringBuilder buildVMString(String reportId, HttpServletRequest request, HttpServletResponse response,
            ReportDesignModel model) {
        // TODO 临时方案，以后前端做
        String vm = model.getVmContent();
        String imageId = request.getParameter ("reportImageId");
        String js =
                "<script type='text/javascript'>" + "\r\n" + "        (function(NS) {" + "\r\n"
                        + "            NS.xui.XView.start(" + "\r\n"
                        + "                'di.product.display.ui.LayoutPage'," + "\r\n" + "                {" + "\r\n"
                        + "                    externalParam: {" + "\r\n" + "                    'reportId':'"
                        + (StringUtils.isEmpty (imageId) ? reportId : imageId)
                        + "','phase':'dev'},"
                        + "\r\n"
                        + "                    globalType: 'PRODUCT',"
                        + "\r\n"
                        + "                    diAgent: '',"
                        + "\r\n"
                        + "                    reportId: '"
                        + (StringUtils.isEmpty (imageId) ? reportId : imageId)
                        + "',"
                        + "\r\n"
                        + "                    webRoot: '/silkroad',"
                        + "\r\n"
                        + "                    phase: 'dev',"
                        + "\r\n"
                        + "                    serverTime: ' "
                        + System.currentTimeMillis ()
                        + "',"
                        + "\r\n"
                        + "                    funcAuth: null,"
                        + "\r\n"
                        + "                    extraOpt: (window.__$DI__NS$__ || {}).OPTIONS"
                        + "\r\n"
                        + "                }"
                        + "\r\n"
                        + "            );"
                        + "\r\n"
                        + "        })(window);"
                        + "\r\n"
                        + "    </script>" + "\r\n";
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>");
        builder.append("<html>");
        builder.append("<head>");
        builder.append("<title>" + model.getName() + "</title>");
        builder.append("<meta content='text/html' 'charset=UTF-8'>");
        final String theme = model.getTheme();
        builder.append("<link rel='stylesheet' href='/silkroad/asset/" + theme + "/css/-di-product-min.css'/>");
        builder.append("<script src='/silkroad/dep/jquery-1.11.1.min.js'/></script>");
        builder.append("</head>");
        builder.append("<body>");
        builder.append(vm);

        builder.append("<script src='/silkroad/asset/" + theme + "/-di-product-min.js'>");
        builder.append("</script>");
        builder.append(js);
        builder.append("</body>");
        builder.append("</html>");
        response.setCharacterEncoding("utf-8");
        return builder;
    }

    @RequestMapping(value = "/{reportId}/report_json", method = { RequestMethod.GET, RequestMethod.POST },
            produces = "text/plain;charset=utf-8")
    public String queryJson(@PathVariable("reportId") String reportId, HttpServletRequest request,
            HttpServletResponse response) {
        long begin = System.currentTimeMillis();
        ReportDesignModel model = null;
        String json = null;
        try {
            model = this.getDesignModelFromRuntimeModel(reportId);
            if (!CollectionUtils.isEmpty(model.getRegularTasks())) {
                json = this.setReportJson(model.getJsonContent(), "REGULAR");
            } else {
                json = this.setReportJson(model.getJsonContent(), "RTPL_VIRTUAL");
            }
        } catch (Exception e) {
            logger.info("[INFO]--- ---There are no such model in cache. Report Id: " + reportId, e);
            throw new IllegalStateException();
        }
        logger.info(json);
        response.setCharacterEncoding("utf-8");
        logger.info("[INFO] query json operation successfully, cost {} ms", (System.currentTimeMillis() - begin));
        return json;
    }

    /**
     * 设置报表的JSON
     * @param json
     * @param reportType
     * @return
     */
    private String setReportJson(String json, String reportType) {
        try {
            JSONObject jsonObj = new JSONObject(json);
            if (jsonObj.has("entityDefs")) {
                JSONArray jsonArrays = jsonObj.getJSONArray("entityDefs");
                for(int i = 0; i < jsonArrays.length(); i++) {
                    JSONObject value = jsonArrays.getJSONObject(i);
                    if (value.has("clzKey") && value.get("clzKey") != null &&
                            value.get("clzKey").toString().equals("DI_FORM")) {
                        value.put("reportType", reportType);
                        break;
                    }
                }
            }
            return jsonObj.toString();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return json;
    }
    /**
     * 
     * @param reportId
     * @param request
     * @return ResponseResult
     */
    @RequestMapping(value = "/{reportId}/runtime_model", method = { RequestMethod.POST })
    public ResponseResult initRunTimeModel(@PathVariable("reportId") String reportId, HttpServletRequest request) {
        long begin = System.currentTimeMillis();
        logger.info("[INFO]--- ---begin init runtime env");
        boolean edit = Boolean.valueOf(request.getParameter(Constants.IN_EDITOR));
        ReportDesignModel model = null;
        if (edit) {
            /**
             * 编辑报表
             */
            model = reportModelCacheManager.loadReportModelToCache(reportId);
            model.setPersStatus(false);
        } else {
            /**
             * 如果是新建的报表，从缓存中找
             */
            try {
                model = reportModelCacheManager.getReportModel(reportId);
                model.setPersStatus(false);
            } catch (CacheOperationException e) {
                logger.info("[INFO]There are no such model in cache. Report Id: " + reportId, e);
                return ResourceUtils.getErrorResult("缓存中不存在的报表！id: " + reportId, 1);
            }
        }
        ReportRuntimeModel runtimeModel = new ReportRuntimeModel(reportId);
        runtimeModel.init(model, true);
        for (String key : request.getParameterMap().keySet()) {
            String value = request.getParameter(key);
            if (value != null) {
                /**
                 * value 不能是null，但可以为空字符串，空字符串可能有含义
                 */
                runtimeModel.getContext().put(key, value);
            }

        }
        reportModelCacheManager.updateRunTimeModelToCache(reportId, runtimeModel);
        // reportModelCacheManager.updateReportModelToCache(reportId, model);
        ResponseResult rs = ResourceUtils.getCorrectResult("OK", "");
        logger.info("[INFO] successfully init runtime evn, cost {} ms", (System.currentTimeMillis() - begin));
        return rs;
    }

    /**
     * 更新上下文 将格式态的报表模型转化成运形态的报表模型存入缓存 或者依据用户查询逻辑更新运形态报表模型
     * 
     * @param reportId
     * @param areaId
     * @param request
     * @return
     */
    @RequestMapping(value = "/{reportId}/runtime/context", method = { RequestMethod.POST })
    public ResponseResult updateContext(@PathVariable("reportId") String reportId, HttpServletRequest request) {
        long begin = System.currentTimeMillis();
        logger.info("[INFO]------begin update global runtime context");
        Map<String, String[]> contextParams = request.getParameterMap();
        ReportRuntimeModel runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);

        ReportDesignModel model = runTimeModel.getModel();
        Map<String, String> params = Maps.newHashMap();
        if (model.getParams() != null) {
            model.getParams().forEach((k, v) -> {
                params.put(v.getElementId(), v.getName());
            });
        }

        // add by jiangyichao， 取出DesignModel中的平面表条件
        Map<String, String> condition = Maps.newHashMap();
        if (model.getPlaneTableConditions() != null) {
            model.getPlaneTableConditions().forEach((k, v) -> {
                condition.put(v.getElementId(), v.getName());
            });
        }
        for (String key : contextParams.keySet()) {
            /**
             * 更新runtimeModel的全局上下文参数
             */
            String[] value = contextParams.get(key);
            if (value != null && value.length > 0 && !StringUtils.isEmpty(value[0])) {
                String realValue = modifyFilterValue(value[0]);
                if (realValue != null) {
                    // 移除运行态模型的Context中的已有时间维度,保证有且仅有一个时间维度
                    boolean isTimeDim = 
                        realValue.contains("start") && realValue.contains("end") && realValue.contains("granularity");
                    if (isTimeDim) {
                        for (Entry<String, Object> tmpEntry : runTimeModel.getContext().getParams().entrySet()) {
                            String tmpStr = String.valueOf(tmpEntry.getValue());
                            if (tmpStr.contains("start") || tmpStr.contains("end") || tmpStr.contains("granularity")) {
                                runTimeModel.getContext().removeParam(tmpEntry.getKey());
                                Map<String, QueryContext> localContext = runTimeModel.getLocalContext();
                                localContext.forEach((k, v) -> {
                                    v.reset();
                                });
                                break;
                            }
                        }
                    }
                    runTimeModel.getContext().getParams ().put(getRealKey(model, key), realValue);
                    runTimeModel.getLocalContext ().forEach ((k, v) -> {
                        v.getParams ().put (getRealKey(model, key), realValue);
                    });
                } else {
                    runTimeModel.getContext().removeParam(getRealKey(model, key));
                }
                if (params.containsKey(key)) {
                    String paramName = params.get(key);
                    String tmp = getParamRealValue(value[0]);
                    if (StringUtils.hasText(tmp)) {
                        runTimeModel.getContext().put(paramName, tmp);
                    } else {
                        runTimeModel.getContext().removeParam(paramName);
                    }
                }

                // 将平面表查询条件放入RuntimeModel的context中
                if (condition.containsKey(key)) {
                    String conditionName = condition.get(key);
                    String tmp = getParamRealValue(value[0]);
                    if (StringUtils.hasText(tmp)) {
                        runTimeModel.getContext().put(conditionName, tmp);
                    } else {
                        runTimeModel.getContext().removeParam(conditionName);
                    }
                }

            } else {
                runTimeModel.getContext().put(getRealKey(model, key), "");
                if (params.containsKey(key)) {
                    String paramName = params.get(key);
                    runTimeModel.getContext().put(paramName, "");
                }

                if (condition.containsKey(key)) {
                    String conditionName = condition.get(key);
                    runTimeModel.getContext().put(conditionName, "");
                }
            }

        }
        resetOtherStatus (runTimeModel);
        reportModelCacheManager.updateRunTimeModelToCache(reportId, runTimeModel);
        logger.info("[INFO]current context params status {}", runTimeModel.getContext().getParams());
        logger.info("[INFO]successfully update global context, cost {} ms", (System.currentTimeMillis() - begin));
//        return initParams (reportId, request);
        ResponseResult rs = ResourceUtils.getResult("Success Getting VM of Report", "Fail Getting VM of Report", "");
        return rs;
    }

    private String getParamRealValue(String realValue) {
        // modify by yichao.jiang 接收url传递过来的时间参数，并进行转换
        if (realValue.contains("start") && realValue.contains("end")) {
            return genNewStartAndEnd(realValue);
        }
        String[] tmp = realValue.split(",");
        if (tmp.length == 1) {
            if (StringUtils.isEmpty(tmp)) {
                return realValue;
            }
            if (MetaNameUtil.isUniqueName(tmp[0])) {
                String[] metaName = MetaNameUtil.parseUnique2NameArray(tmp[0]);
                return metaName[metaName.length - 1];
            }
        }
        StringBuilder rs = new StringBuilder();
        for (int i = 0; i < tmp.length; ++i) {
            if (StringUtils.isEmpty(tmp[i]) || tmp[i].contains(":")) {
                continue;
            }
            String[] metaName = MetaNameUtil.parseUnique2NameArray(tmp[i]);
            rs.append(metaName[metaName.length - 1]);
            if (i <= tmp.length - 2) {
                rs.append(",");
            }
        }
        return rs.toString();
    }

    /**
     * 临时方案，后续需要调整
     * 
     * @param tmpValue
     * @return String
     */
    private String modifyFilterValue(String tmpValue) {
        if (tmpValue.contains("start") && tmpValue.contains("end")) {
            return genNewStartAndEnd(tmpValue);
        }
        String[] tmpValueArray = tmpValue.split(",");
        if (tmpValueArray.length == 1) {
            return tmpValue;
        }
        StringBuilder rs = new StringBuilder();
        for (int i = 0; i < tmpValueArray.length; ++i) {
            if (MetaNameUtil.isUniqueName(tmpValueArray[i])) {
                String[] metaName = MetaNameUtil.parseUnique2NameArray(tmpValueArray[i]);
                String value = metaName[metaName.length - 1];
                if (StringUtils.isEmpty(value) || value.contains(":")) {
                    continue;
                }
                rs.append(tmpValueArray[i]);
                if (i <= tmpValueArray.length - 1) {
                    rs.append(",");
                }
            }
        }
        return rs.toString();

    }

    /**
     * TODO 重新获取日期的开始和结束 ，add by jiangyichao
     */
    private String genNewStartAndEnd(String timeValue) {
        String start;
        String end;
        String result = null;
        try {
            JSONObject json = new JSONObject(String.valueOf(timeValue));
            start = json.getString("start").replace("-", "");
            end = json.getString("end").replace("-", "");
            String granularity = json.getString("granularity");
            // 保证开始时间小于结束时间
            if (start.compareTo(end) > 0) {
                String tmp = start;
                start = end;
                end = tmp;
            }
            Map<String, String> time = null;
            switch (granularity) {
            // 年
                case "Y":
                    time = TimeUtils.getTimeCondition(start, end, TimeType.TimeYear);
                    break;
                // 季度
                case "Q":
                    time = TimeUtils.getTimeCondition(start, end, TimeType.TimeQuarter);
                    break;
                // 月份
                case "M":
                    time = TimeUtils.getTimeCondition(start, end, TimeType.TimeMonth);
                    break;
                // 星期
                case "W":
                    time = TimeUtils.getTimeCondition(start, end, TimeType.TimeWeekly);
                    break;
                // 天
                case "D":
                    time = TimeUtils.getTimeCondition(start, end, TimeType.TimeDay);
                    break;
                default:
                    break;
            }
            start = time.get("start");
            end = time.get("end");
            json.put("start", start);
            json.put("end", end);
            logger.info("start time is [" + start + "],and end time is [" + end + "]");
            result = json.toString();
        } catch (Exception e) {
            logger.debug("the input time format is wrong" + timeValue, e);
        }
        return result;
    }

    /**
     * 
     * @param model {@link ReportDesignModel}
     * @param key String
     * @return String real key
     */
    private String getRealKey(ReportDesignModel model, String key) {
        if (model != null && model.getExtendById(key) != null) {
            if (model.getExtendById(key).listAllItems().isEmpty()) {
                return key;
            }
            return model.getExtendById(key).listAllItems().keySet().toArray(new String[0])[0];
        }
        return key;
    }

    private Map<String, Object> updateLocalContextAndReturn(ReportRuntimeModel runTimeModel, String areaId,
            Map<String, String[]> contextParams) {

        /**
         * 查询区域的时候，会按照当前的参数更新区域上下文
         */
        QueryContext localContext = runTimeModel.getLocalContextByAreaId(areaId);
        // localContext.reset ();

        /**
         * 查询参数，首先载入全局上下文，再覆盖局部上下文
         */
        final Map<String, Object> queryParams = Maps.newHashMap();
        /**
         * TODO 暂时用全局的覆盖本地的参数，以后考虑是否会有问题
         */
        Map<String, Object> localParams = localContext.getParams();

        if ("true".equals(localParams.get("isOverride"))) {
            queryParams.putAll(localParams);

            runTimeModel.getContext().getParams().forEach((key, value) -> {
                if (!queryParams.containsKey(key)) {
                    queryParams.put(key, value);
                }
            });

            return queryParams;
        }
        queryParams.putAll(localParams);
        if (runTimeModel.getContext() != null) {
            queryParams.putAll(runTimeModel.getContext().getParams());
        } else {
            throw new RuntimeException("没有初始化？？");
        }
        Map<String, Object> tmp = Maps.newConcurrentMap();
        queryParams.forEach((k, v) -> {
            if (v != null && !StringUtils.isEmpty(v.toString())) {
                tmp.put(k, v);
            }
        });
        // 用当前查询参数，覆盖旧参数
        for (String key : contextParams.keySet()) {
            /**
             * 更新runtimeModel的区域上下文参数
             */
            String[] value = contextParams.get(key);
            if (value != null && value.length > 0) {
                tmp.put(key, value[0]);
            }
        }
        return tmp;
    }

    /**
     * 
     * @param reportId
     * @param areaId
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}", method = { RequestMethod.POST })
    public ResponseResult queryArea(@PathVariable("reportId") String reportId, @PathVariable("areaId") String areaId,
            HttpServletRequest request) {
        long begin = System.currentTimeMillis();
        
        long curr = System.currentTimeMillis();
        logger.info("[INFO] begin query data");
        /**
         * 1. 获取缓存DesignModel对象
         */
        ReportDesignModel model;
        ReportDesignModel oriDesignModel;

        /**
         * 3. 获取运行时对象
         */
        ReportRuntimeModel runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        logger.info("[INFO]lijin queryArea cost:"+(System.currentTimeMillis()-curr)+" ms to getRuntimeModel");
        curr = System.currentTimeMillis();
        try {
            model = getRealModel(reportId, runTimeModel);
            logger.info("[INFO]lijin queryArea cost:"+(System.currentTimeMillis()-curr)+" ms to getRealModel");
            curr = System.currentTimeMillis();
            oriDesignModel = DeepcopyUtils.deepCopy(model);
        } catch (CacheOperationException e) {
            logger.info("[INFO]Report model is not in cache! ", e);
            ResponseResult rs = ResourceUtils.getErrorResult("缓存中不存在的报表，ID " + reportId, 1);
            return rs;
        }
        logger.info("[INFO]lijin queryArea cost:"+(System.currentTimeMillis()-curr)+" ms to deepCopy");
        curr = System.currentTimeMillis();

        /**
         * TODO 增加参数信息
         */
        Map<String, Object> tmp = QueryUtils.resetContextParam(request, model);
        tmp.forEach((k, v) -> {
            if (runTimeModel.getContext().getParams().containsKey("fromReportId") 
                    && runTimeModel.getContext().getParams().containsKey("toReportId")) {
                if (!runTimeModel.getLocalContextByAreaId(areaId).getParams().containsKey(k)) {
                    runTimeModel.getLocalContextByAreaId(areaId).put(k, v);
                }                 
            } else {
                runTimeModel.getLocalContextByAreaId(areaId).put(k, v);
            }
        });
        logger.info("[INFO]lijin queryArea cost:"+(System.currentTimeMillis()-curr)+" ms to set param info");
        curr = System.currentTimeMillis();
        /**
         * 2. 获取区域对象
         */
        ExtendArea targetArea = model.getExtendById(areaId);
        if (targetArea == null) {
            throw new IllegalStateException("can't get report define");
        }
        
        logger.info("[INFO]lijin queryArea cost:"+(System.currentTimeMillis()-curr)+" ms to get area obj");
        curr = System.currentTimeMillis();

        /**
         * 4. 更新区域本地的上下文
         */
        ExtendAreaContext areaContext = getAreaContext(areaId, request, targetArea, runTimeModel);
        
        logger.info("[INFO]lijin queryArea cost:"+(System.currentTimeMillis()-curr)+" ms to get area context");
        curr = System.currentTimeMillis();

        logger.info("[INFO] --- --- --- --- --- ---params with context is : " + areaContext.getParams());
        
        LogicModel logicModel = targetArea.getLogicModel ();
        if (targetArea.getType () == ExtendAreaType.LITEOLAP_TABLE
            || targetArea.getType () == ExtendAreaType.LITEOLAP_CHART) {
            logicModel = model.getExtendAreas ().get (targetArea.getReferenceAreaId ()).getLogicModel ();
        }
        if (logicModel == null) {
            ResponseResult response = new ResponseResult ();
            response.setStatus (1);
            response.setStatusInfo ("未设置坐标轴内容");
            return response;
        }
        
        logger.info("[INFO]lijin queryArea cost:"+(System.currentTimeMillis()-curr)+" ms to get logicModel");
        curr = System.currentTimeMillis();
        /**
         * 5. 生成查询动作QueryAction
         */
        QueryAction action = null;
        if (targetArea.getType() == ExtendAreaType.CHART || targetArea.getType() == ExtendAreaType.LITEOLAP_CHART) {
            String[] indNames = new String[0];
            if (StringUtils.hasText(request.getParameter("indNames"))) {
                indNames = request.getParameter("indNames").split(",");
            }
            try {
                String topSetting = request.getParameter(Constants.TOP);
                if (!StringUtils.isEmpty(topSetting)) {
                    logicModel.setTopSetting(GsonUtils.fromJson(topSetting, MeasureTopSetting.class));
                }
                action =
                        queryBuildService.generateChartQueryAction(model, areaId, areaContext.getParams(), indNames,
                                runTimeModel);
                if (action == null) {
                    return ResourceUtils.getErrorResult("该区域未包含任何维度信息", 1);
                }
                action.setChartQuery(true);
                boolean timeLine = isTimeDimOnFirstCol(model, targetArea, action);
                // TODO to be delete
                boolean isPieChart = isPieChart(getChartTypeWithExtendArea(model, targetArea));
                if (!timeLine && isPieChart) {
                    action.setNeedOthers(true);
                }
            } catch (QueryModelBuildException e) {
                String msg = "没有配置时间维度，不能使用liteOlap趋势分析图！";
                logger.warn(msg);
                DIReportChart chart = new DIReportChart();
                return ResourceUtils.getCorrectResult(msg, chart);
            }
        } else {
            action = queryBuildService.generateTableQueryAction(model, areaId, areaContext.getParams());
            if (action != null) {
                action.setChartQuery(false);
            }
        }
        
        logger.info("[INFO]lijin queryArea cost:"+(System.currentTimeMillis()-curr)+" ms to generate query action");
        curr = System.currentTimeMillis();
        /**
         * 6. 完成查询
         */
        ResultSet result;
        // 分页信息
        PageInfo pageInfo = new PageInfo();

        try {
            if (targetArea.getType() != ExtendAreaType.PLANE_TABLE
                    && (action == null || CollectionUtils.isEmpty(action.getRows()) || CollectionUtils.isEmpty(action
                            .getColumns()))) {
                return ResourceUtils.getErrorResult("单次查询至少需要包含一个横轴、一个纵轴元素", 1);
            }
            if (targetArea.getType() == ExtendAreaType.PLANE_TABLE) {
                // 获取分页设置信息
                PaginationSetting pageSetting = targetArea.getPlaneTableFormat().getPageSetting();
                if (pageSetting.getIsPagination()) {
                    // 设置分页大小
                    if (StringUtils.hasLength(request.getParameter(PAGE_SIZE))) {
                        pageInfo.setPageSize(Integer.valueOf(request.getParameter(PAGE_SIZE)));
                    }
                    // 设置当前页
                    if (StringUtils.hasLength(request.getParameter(CURRENT_PAGE))) {
                        pageInfo.setCurrentPage(Integer.valueOf(request.getParameter(CURRENT_PAGE)) - 1);
                    }
                    // 设置总的记录数
                    if (StringUtils.hasLength(request.getParameter(TOTAL_RECORD_COUNT))) {
                        pageInfo.setTotalRecordCount(Integer.valueOf(request.getParameter(TOTAL_RECORD_COUNT)));
                    } else {
                        pageInfo.setTotalRecordCount(-1);
                    }                    
                } else {
                    // 如果没有分页，则设置第一个页(对于数据库，分页从0开始)
                    pageInfo.setCurrentPage(0);
                    // 设置不分页情况下，查询的条数
                    pageInfo.setPageSize(pageSetting.getPageSize());
                    // 当该数设置为-1时，不进行count(*)查询
                    pageInfo.setTotalRecordCount(-1);
                }
                // 获取上一次查询的QueryAction
                QueryAction queryActionPrevious = runTimeModel.getPreviousQueryAction(areaId);
                // 携带之前的排序信息
                if (queryActionPrevious != null && action.getOrderDesc() != queryActionPrevious.getOrderDesc()) {
                    action.setOrderDesc(queryActionPrevious.getOrderDesc());                     
                }
                result =
                        reportModelQueryService.queryDatas(model, action, true, areaContext.getParams(), pageInfo,
                                securityKey);
            } else {
                result =
                        reportModelQueryService.queryDatas(model, action, true, true, areaContext.getParams(),
                                securityKey);
            }
        } catch (DataSourceOperationException | QueryModelBuildException e1) {
            logger.info("获取数据源失败！", e1);
            return ResourceUtils.getErrorResult("获取数据源失败！", 1);
        }
        
        logger.info("[INFO]lijin queryArea cost:"+(System.currentTimeMillis()-curr)+" ms to query");
        curr = System.currentTimeMillis();

        /**
         * 7. 对返回结果进行处理，用于表、图显示
         */
        runTimeModel.setModel(model);
        ResponseResult rs = queryDataResourceUtils.parseQueryResultToResponseResult(runTimeModel, 
                targetArea, result, areaContext,
                action);
        runTimeModel.setModel(oriDesignModel);
        
        logger.info("[INFO]lijin queryArea cost:"+(System.currentTimeMillis()-curr)+" ms to parseQueryResultToResponseResult");
        curr = System.currentTimeMillis();
        // TODO 对于平面表，需要维护分页信息
        if (targetArea.getType() == ExtendAreaType.PLANE_TABLE) {
            if (rs.getStatus() == 0) {
                Map<String, Object> data = (Map<String, Object>) rs.getData();
                if (data.containsKey("head") && data.containsKey("pageInfo") && data.containsKey("data")) {
                    PageInfo page = (PageInfo) data.get("pageInfo");
                    page.setCurrentPage(pageInfo.getCurrentPage() + 1);
                    page.setPageSize(pageInfo.getPageSize());
                    if (pageInfo.getTotalRecordCount() != -1) {
                        page.setTotalRecordCount(pageInfo.getTotalRecordCount());
                    }
                    data.put("pageInfo", page);
                    rs.setData(data);
                }
            }
        }
        areaContext.getQueryStatus().add(result);

        // 清除当前request中的请求参数，保证areaContext的参数正确
        resetAreaContext(areaContext, request);
        resetContext(runTimeModel.getLocalContextByAreaId (areaId), request);
        reportModelCacheManager.updateAreaContext(reportId, targetArea.getId(), areaContext);
        
        logger.info("[INFO]lijin queryArea cost:"+(System.currentTimeMillis()-curr)+" ms to reportModelCacheManager.updateAreaContext(reportId, targetArea.getId(), areaContext)");
        curr = System.currentTimeMillis();
        runTimeModel.updateDatas(action, result);
        reportModelCacheManager.updateRunTimeModelToCache(reportId, runTimeModel);
        logger.info("[INFO]lijin queryArea cost:"+(System.currentTimeMillis()-curr)+" ms to reportModelCacheManager.updateRunTimeModelToCache(reportId, runTimeModel)");
        
        logger.info("[INFO] successfully query data operation. cost {} ms", (System.currentTimeMillis() - begin));
        return rs;
    }

    private FormatModel getFormatModel(ReportDesignModel model, ExtendArea targetArea) {
        if (targetArea.getType() == ExtendAreaType.TABLE) {
            return targetArea.getFormatModel();
        }
        return model.getExtendById(targetArea.getReferenceAreaId()).getFormatModel();
    }

    private boolean isPieChart(Map<String, String> chartType) {
        for (String chart : chartType.values()) {
            if (ChartShowType.PIE.name().toLowerCase().equals(chart)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTimeDimOnFirstCol(ReportDesignModel model, ExtendArea targetArea, QueryAction action) {
        if (action.getRows().isEmpty()) {
            return false;
        }
        Item item = action.getRows().keySet().toArray(new Item[0])[0];
        OlapElement element =
                ReportDesignModelUtils.getDimOrIndDefineWithId(model.getSchema(), targetArea.getCubeId(),
                        item.getOlapElementId());
        boolean timeLine = element instanceof TimeDimension;
        return timeLine;
    }

    /**
     * 
     * @param queryContext
     * @param request
     */
    private void resetContext(QueryContext queryContext, HttpServletRequest request) {
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            queryContext.getParams().remove(params.nextElement());
        }
    }

    /**
     * 
     * @param areaContext
     * @param request
     */
    private void resetAreaContext(ExtendAreaContext areaContext, HttpServletRequest request) {
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            areaContext.getParams().remove(params.nextElement());
        }
    }

    /**
     * @param areaId
     * @param request
     * @param targetArea
     * @param runTimeModel
     * @return ExtendAreaContext
     */
    private ExtendAreaContext getAreaContext(String areaId, HttpServletRequest request, ExtendArea targetArea,
            ReportRuntimeModel runTimeModel) {
        Map<String, Object> queryParams = updateLocalContextAndReturn(runTimeModel, areaId, request.getParameterMap());
        runTimeModel.getLocalContextByAreaId(areaId).getParams().putAll(queryParams);
        String reportModelId = runTimeModel.getReportModelId ();
        ExtendAreaContext areaContext = reportModelCacheManager.getAreaContext(reportModelId, targetArea.getId());
        areaContext.getParams().clear();
        areaContext.getParams().putAll(queryParams);
        return areaContext;
    }

    /**
     * 获取扩展区域中定义的chartType
     * 
     * @param targetArea ExtendArea
     * @return SeriesUnitType
     */
    private Map<String, String> getChartTypeWithExtendArea(ReportDesignModel model, ExtendArea targetArea) {
        Map<String, String> chartTypes = Maps.newHashMap();
        if (targetArea.getType() == ExtendAreaType.LITEOLAP_CHART) {
            chartTypes.put("null", SeriesUnitType.LINE.name());
            return chartTypes;
            // return new String[]{SeriesUnitType.LINE.name()};
        }
        // List<String> types = Lists.newArrayList();
        targetArea
                .listAllItems()
                .values()
                .stream()
                .filter(item -> {
                    return item.getPositionType() == PositionType.Y || item.getPositionType() == PositionType.CAND_IND;
                })
                .forEach(
                        item -> {
                            OlapElement element =
                                    ReportDesignModelUtils.getDimOrIndDefineWithId(model.getSchema(),
                                            targetArea.getCubeId(), item.getOlapElementId());
                            Object chartType = item.getParams().get("chartType");
                            if (chartType == null) {
                                chartTypes.put(element.getUniqueName(), SeriesUnitType.COLUMN.name());
                            } else {
                                chartTypes.put(element.getUniqueName(), chartType.toString());
                            }
                        });
        // .forEach(str -> {
        // if (StringUtils.isEmpty(str)) {
        // types.add(SeriesUnitType.COLUMN.name());
        // } else {
        // types.add(str.toString().toUpperCase());
        // }
        // });
        return chartTypes;
    }

    /**
     * 选中行
     * 
     * @param reportId 报表id
     * @param areaId 区域id
     * @param request 请求对象
     * @return 操作结果
     */
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/selected_row/", method = { RequestMethod.POST })
    public ResponseResult selectRow(@PathVariable("reportId") String reportId, @PathVariable("areaId") String areaId,
            HttpServletRequest request) {
        long begin = System.currentTimeMillis();
        logger.info("[INFO] begin select row operation");
        String rowId = request.getParameter("rowId");
        if (!StringUtils.hasText(rowId)) {
            logger.info("[INFO]no rowid for input! ");
            return ResourceUtils.getErrorResult("no rowid for input! ", 1);
        }
        ReportRuntimeModel runTimeModel = null;
        try {
            runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        } catch (CacheOperationException e) {
            logger.info("no such runtime model found for id: " + reportId);
            return ResourceUtils.getErrorResult("no such runtime model found for id: " + reportId, 1);
        }
        runTimeModel.getSelectedRowIds().add(rowId);
        reportModelCacheManager.updateRunTimeModelToCache(reportId, runTimeModel);
        logger.info("[INFO]------successfully execute select row, cost {} ms", (System.currentTimeMillis() - begin));
        return ResourceUtils.getCorrectResult("Success adding selectedRow. ", "");
    }

    /**
     * 选中行
     * 
     * @param reportId 报表id
     * @param areaId 区域id
     * @param request 请求对象
     * @return 操作结果
     */
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/selected_row/{rowId}",
            method = { RequestMethod.DELETE })
    public ResponseResult deselectRow(@PathVariable("reportId") String reportId, @PathVariable("areaId") String areaId,
            @PathVariable("rowId") String rowId, HttpServletRequest request) {
        long begin = System.currentTimeMillis();
        if (!StringUtils.hasText(rowId)) {
            logger.info("[INFO] --- ---no rowid for input! ");
            return ResourceUtils.getErrorResult("no rowid for input! ", 1);
        }
        ReportRuntimeModel runTimeModel = null;
        try {
            runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        } catch (CacheOperationException e) {
            logger.info("[INFO]--- ---no such runtime model found for id: " + reportId);
            return ResourceUtils.getErrorResult("no such runtime model found for id: " + reportId, 1);
        }
        runTimeModel.getSelectedRowIds().remove(rowId);
        logger.info("[INFO]successfully deslect row operation, cost {} ms", (System.currentTimeMillis() - begin));
        return ResourceUtils.getCorrectResult("Success removing selectedRow. ", "");
    }

    /**
     * 
     * 下钻操作
     * 
     * @param reportId 报表id
     * @param request 请求对象
     * @return 下钻操作 操作结果
     */
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/drill", method = { RequestMethod.POST })
    public ResponseResult drillDown(@PathVariable("reportId") String reportId, @PathVariable("areaId") String areaId,
            HttpServletRequest request) {
        long begin = System.currentTimeMillis();
        logger.info("[INFO]------ begin drill down operation");
        String uniqueName = request.getParameter("uniqueName");
        boolean isRoot = uniqueName.startsWith("@") && uniqueName.endsWith("@");
        uniqueName = uniqueName.replace("@", "");
        ReportDesignModel model;
        try {
            model = this.getDesignModelFromRuntimeModel(reportId);
            // reportModelCacheManager.getReportModel(reportId);
        } catch (CacheOperationException e) {
            logger.info("[INFO]------Can not find such model in cache. Report Id: " + reportId, e);
            return ResourceUtils.getErrorResult("不存在的报表，ID " + reportId, 1);
        }
        ReportRuntimeModel runTimeModel = null;
        try {
            runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        } catch (CacheOperationException e1) {
            logger.info("[INFO]------There are no such model in cache. Report Id: " + reportId, e1);
        }
        // TODO 这里调用需要考虑是否必须，按照正常逻辑，此处runtimeModel已经初始化完毕
        if (runTimeModel == null) {
            runTimeModel = reportModelCacheManager.loadRunTimeModelToCache(reportId);
        }
        ExtendArea targetArea = model.getExtendById(areaId);
        LogicModel targetLogicModel = null;
        String logicModelAreaId = areaId;
        if (targetArea.getType() == ExtendAreaType.CHART || targetArea.getType() == ExtendAreaType.LITEOLAP_CHART) {
            return ResourceUtils.getErrorResult("can not drill down a chart. ", 1);
        } else if (targetArea.getType() == ExtendAreaType.LITEOLAP_TABLE) {
            LiteOlapExtendArea liteOlapArea = (LiteOlapExtendArea) model.getExtendById(targetArea.getReferenceAreaId());
            targetLogicModel = liteOlapArea.getLogicModel();
            logicModelAreaId = liteOlapArea.getId();
        } else {
            targetLogicModel = targetArea.getLogicModel();
        }

        if (targetLogicModel == null) {
            targetLogicModel = new LogicModel();
        }

        QueryAction action = null; // (QueryAction) runTimeModel.getContext().get(uniqueName);
        String drillTargetUniqueName = null;
        Map<String, Object> queryParams = updateLocalContextAndReturn(runTimeModel, areaId, Maps.newHashMap());
        Item row = null;
        if (uniqueName.contains(",")) {
            // isRoot = true;
            String[] uniqueNameArray = uniqueName.split(",");
            String dimName = MetaNameUtil.getDimNameFromUniqueName(uniqueNameArray[0]);
            Map<String, Item> store = runTimeModel.getUniversalItemStore().get(logicModelAreaId);
            if (CollectionUtils.isEmpty(store)) {
                String msg = "The item map of area (" + logicModelAreaId + ") is Empty!";
                logger.error(msg);
                throw new RuntimeException(msg);
            }
            row = store.get(dimName);
            queryParams.put(row.getOlapElementId(), uniqueNameArray);

            // TODO 仔细思考一下逻辑
            action = queryBuildService.generateTableQueryAction(model, areaId, queryParams);
        } else {
            /**
             * 找到下载的维度节点
             */
            String[] uniqNames =
                    com.baidu.rigel.biplatform.ac.util.DataModelUtils.parseNodeUniqueNameToNodeValueArray(uniqueName);
            if (uniqNames == null || uniqNames.length == 0) {
                String msg = String.format("Fail in drill down. UniqueName param is empty.");
                logger.error(msg);
                return ResourceUtils.getErrorResult(msg, 1);
            }
            drillTargetUniqueName = uniqNames[uniqNames.length - 1];
            logger.info("[INFO] drillTargetUniqueName : {}", drillTargetUniqueName);
            // isRoot = drillTargetUniqueName.toLowerCase().contains("all");
            Map<String, String[]> oriQueryParams = Maps.newHashMap();

            String dimName = MetaNameUtil.getDimNameFromUniqueName(drillTargetUniqueName);
            Map<String, Item> store = runTimeModel.getUniversalItemStore().get(logicModelAreaId);
            if (CollectionUtils.isEmpty(store)) {
                String msg = "The item map of area (" + logicModelAreaId + ") is Empty!";
                logger.error(msg);
                throw new RuntimeException(msg);
            }
            row = store.get(dimName);
            if (row == null) {
                throw new IllegalStateException("未找到下钻节点 -" + dimName);
            }
            String[] drillName = new String[] { drillTargetUniqueName };
            oriQueryParams.putAll(request.getParameterMap());
            /**
             * update context
             */
            queryParams = updateLocalContextAndReturn(runTimeModel, areaId, oriQueryParams);
            queryParams.put(row.getOlapElementId(), drillName);

            // TODO 仔细思考一下逻辑
//            reportModelCacheManager.getAreaContext(reportId, areaId).getParams().putAll(queryParams);
//            runTimeModel.getLocalContextByAreaId (areaId).setParams (queryParams);
            action = queryBuildService.generateTableQueryAction(model, areaId, queryParams);
            /**
             * 把下钻的值存下来 TODO 临时放在这里，需要重新考虑
             */
            if (!isRoot) {
                action.getDrillDimValues().put(row, drillTargetUniqueName);
            } else {
                action.getDrillDimValues().remove(row);
            } 
        }
        // runTimeModel.getContext().put(uniqueName, action);
        runTimeModel.setLinkedQueryAction(action);
        Cube cube = model.getSchema ().getCubes ().get (targetArea.getCubeId ());
        Map<String, Object> params = modifyReportParams(model.getParams (), queryParams, cube);
        reportModelCacheManager.getAreaContext(reportId, areaId).getParams().putAll(params);
        runTimeModel.getLocalContextByAreaId (areaId).setParams (params);
        /**
         * TODO 针对参数映射修改，将当前下钻条件设置到对应参数上
         */
        final String[] tmp = MetaNameUtil.parseUnique2NameArray(drillTargetUniqueName);
        final String elementId = row.getOlapElementId();
        if (!MetaNameUtil.isAllMemberUniqueName(drillTargetUniqueName)) {
            for (ReportParam p : model.getParams().values()) {
                if (p.getElementId().equals(elementId)) {
                    queryParams.put(p.getName(), tmp[tmp.length - 1]);
                }
            }
            ;
        }

        ResultSet result;
        try {
            result = reportModelQueryService.queryDatas(model, action, true, true, queryParams, securityKey);
        } catch (DataSourceOperationException e1) {
            logger.info("[INFO]--- ---can't get datasource！", e1);
            return ResourceUtils.getErrorResult("获取数据源失败！", 1);
        } catch (QueryModelBuildException e1) {
            logger.info("[INFO]--- ----can't not build question model！", e1);
            return ResourceUtils.getErrorResult("构建问题模型失败！", 1);
        } catch (MiniCubeQueryException e1) {
            logger.info("[INFO] --- --- can't query data ", e1);
            return ResourceUtils.getErrorResult("查询数据失败！", 1);
        }
        runTimeModel.drillDown(action, result);
        PivotTable table = null;
        Map<String, Object> resultMap = Maps.newHashMap();
        Dimension drillDim = null;
//        Cube cube = null;

        try {
//            cube = model.getSchema().getCubes().get(targetArea.getCubeId());
            drillDim = cube.getDimensions().get(elementId);
            table = queryBuildService.parseToPivotTable(cube, result.getDataModel(), targetLogicModel);
        } catch (PivotTableParseException e) {
            logger.info(e.getMessage(), e);
            return ResourceUtils.getErrorResult("Fail in parsing result. ", 1);
        }
        ExtendAreaContext areaContext = reportModelCacheManager.getAreaContext(reportId, targetArea.getId());
        if (targetArea.getType() == ExtendAreaType.TABLE || targetArea.getType() == ExtendAreaType.LITEOLAP_TABLE) {
            /**
             * TODO 考虑一下这样的逻辑是否应该放到resource中
             */
            List<Map<String, String>> mainDims = areaContext.getCurBreadCrumPath();
            DataSourceDefine define = null;
            DataSourceInfo dsInfo = null;
            try {
                define = dsService.getDsDefine(model.getDsId());
                dsInfo =
                        DataSourceConnectionServiceFactory.getDataSourceConnectionServiceInstance(
                                define.getDataSourceType().name()).parseToDataSourceInfo(define, securityKey);
            } catch (DataSourceOperationException | DataSourceConnectionException e) {
                logger.error(e.getMessage(), e);
            }

            boolean remove = false;
            if (mainDims.size() > 0 && !isRoot
                    && !mainDims.get(mainDims.size() - 1).values().toArray()[0].equals(drillTargetUniqueName)) {
                Iterator<Map<String, String>> it = mainDims.iterator();
                while (it.hasNext()) {
                    if (remove) {
                        it.next();
                        it.remove();
                        continue;
                    }
                    Map<String, String> tmpMap = it.next();
                    if (tmpMap.values().toArray()[1].equals(drillTargetUniqueName)) {
                        remove = true;
                    }
                }
            }
            if (!remove && drillTargetUniqueName != null && !drillTargetUniqueName.toLowerCase().contains("all")) {
                Map<String, String> dims3 = Maps.newHashMap();
                dims3.put("uniqName", drillTargetUniqueName);
                String showName = genShowName(drillTargetUniqueName, drillDim, cube, dsInfo, queryParams);
                if (isRoot) {
                    showName = areaContext.getCurBreadCrumPath().get(0).get("showName");
                }
                dims3.put("showName", showName);
                mainDims.add(dims3);
                // drillTargetUniqueName = MetaNameUtil.getParentUniqueName(drillTargetUniqueName);
            }
            if (isRoot) {
                Iterator<Map<String, String>> it = mainDims.iterator();
                it.next();
                while (it.hasNext()) {
                    it.next();
                    it.remove();
                }
                // Map<String, String> root = areaContext.getCurBreadCrumPath();
                // mainDims.add(root);
            }

            // List<Map<String, String>> root = areaContext.getCurBreadCrumPath();
            // mainDims.addAll(root);
            // Collections.reverse(mainDims);
            areaContext.setCurBreadCrumPath(mainDims);
            resultMap.put("mainDimNodes", mainDims);
            areaContext.getParams().put("bread_key", mainDims);
            // runTimeModel.getContext().put("bread_key", mainDims);
        }
        areaContext.getQueryStatus().add(result);
        // 更新局部区域参数，避免漏掉当前请求查询的

        // 清除展开、折叠方式下钻查询历史纪录
        resetOtherStatus (runTimeModel);

        reportModelCacheManager.updateAreaContext(reportId, targetArea.getId(), areaContext);
        reportModelCacheManager.updateRunTimeModelToCache(reportId, runTimeModel);
        DataModelUtils.decorateTable(getFormatModel(model, targetArea), table, 
                DataModelUtils.isShowZero(targetArea.getOtherSetting()));
        resultMap.put("pivottable", table);
        setTableResultProperty(reportId, table, resultMap);
        ResponseResult rs =
                ResourceUtils.getResult("Success Getting VM of Report", "Fail Getting VM of Report", resultMap);
        logger.info("[INFO]Successfully execute drill operation. cost {} ms", (System.currentTimeMillis() - begin));
        return rs;
    }

    private void resetOtherStatus(ReportRuntimeModel runTimeModel) {
        runTimeModel.getDrillDownQueryHistory().clear();
        runTimeModel.getOrderedStatus ().clear ();
        runTimeModel.setSortRecord (null);
    }

    private Map<String, Object> modifyReportParams(Map<String, ReportParam> params, 
        Map<String, Object> queryParams, Cube cube) {
        cube = QueryUtils.transformCube (cube);
        Map<String, Object> rs = Maps.newHashMap ();
        rs.putAll (queryParams);
        final Map<String, String> tmp = Maps.newHashMap ();
        params.forEach ((k, v) -> tmp.put (v.getElementId(), v.getName ())); 
        queryParams.forEach ((k, v) -> {
            if (tmp.containsKey (k) && v != null) {
                String uniqueName = null;
                if (v instanceof String[]) {
                    uniqueName = ((String[]) v)[0];
                } else if (MetaNameUtil.isUniqueName (v.toString ()) 
                    && !MetaNameUtil.isAllMemberUniqueName (v.toString ())){
                    uniqueName = v.toString ();
                }
                if (!StringUtils.isEmpty (uniqueName) && MetaNameUtil.isUniqueName (uniqueName)) {
                    String[] array = MetaNameUtil.parseUnique2NameArray (uniqueName);
                    rs.put (tmp.get (k), array[array.length - 1]);
                } else {
                    rs.put (tmp.get(k), v.toString ());
                }
            }
        });
        return rs;
    }

    private void setTableResultProperty(String reportId, PivotTable table, Map<String, Object> resultMap) {
        resultMap.put("rowCheckMin", 1);
        resultMap.put("rowCheckMax", 5);
        resultMap.put("reportTemplateId", reportId);
        if (table.getActualSize() <= 1) {
            resultMap.put("totalSize", table.getActualSize());
        } else {
            resultMap.put("totalSize", table.getActualSize() - 1);
        }
        if (table.getDataSourceRowBased().size() <= 1) {
            resultMap.put("currentSize", table.getDataSourceRowBased().size());
        } else {
            resultMap.put("currentSize", table.getDataSourceRowBased().size() - 1);
        }
    }

    /**
     * 
     * @param drillTargetUniqueName
     * @param drillDim
     * @param queryParams
     * @param dsInfo
     * @param cube
     * @return
     * 
     */
    private String genShowName(String drillTargetUniqueName, Dimension drillDim, Cube cube, DataSourceInfo dsInfo,
            Map<String, Object> params) {
        String showName =
                drillTargetUniqueName.substring(drillTargetUniqueName.lastIndexOf("[") + 1,
                        drillTargetUniqueName.length() - 1);
        if (showName.contains("All_")) {
            showName = showName.replace("All_", "全部");
            showName = showName.substring(0, showName.length() - 1);
        } else if (drillDim.getType() == DimensionType.CALLBACK) {
            String[] nameArray = MetaNameUtil.parseUnique2NameArray(drillTargetUniqueName);
            Level l = drillDim.getLevels().values().toArray(new Level[0])[nameArray.length - 2];
            Map<String, String> tmp = Maps.newHashMap();
            params.forEach((k, v) -> {
                tmp.put(k, v.toString());
            });
            logger.info("in callback dim show name generate");
            return l.getMembers(QueryUtils.transformCube(cube), dsInfo, tmp).get(0).getCaption();
        }
        return showName;
    }

    /**
     * 展开下钻操作
     * 
     * @param reportId 报表id
     * @param request 请求对象
     * @param rowIndex 当前点击行行号
     * @param colIndex 列索引
     * @return 下钻操作 操作结果
     */
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/drill/{type}", method = { RequestMethod.POST })
    public ResponseResult drillDown(@PathVariable("reportId") String reportId, @PathVariable("areaId") String areaId,
            @PathVariable("type") String type, HttpServletRequest request) throws Exception {
        long begin = System.currentTimeMillis();
        logger.info("begin drill down opeartion");
        // // 解析查询条件条件 来自于rowDefine
        String condition = request.getParameter("lineUniqueName");

        // String uniqueName = request.getParameter("uniqueName");

        ReportRuntimeModel runTimeModel = null;
        try {
            runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        } catch (CacheOperationException e1) {
            logger.info("[INFO] There are no such model in cache. Report Id: " + reportId, e1);
        }
        // TODO 这里调用需要考虑是否必须，按照正常逻辑，此处runtimeModel已经初始化完毕
        if (runTimeModel == null) {
            runTimeModel = reportModelCacheManager.loadRunTimeModelToCache(reportId);
        }
        ReportDesignModel model;
        try {
            model = DeepcopyUtils.deepCopy(runTimeModel.getModel());
        } catch (CacheOperationException e) {
            logger.info("[INFO] Can not find such model in cache. Report Id: " + reportId, e);
            return ResourceUtils.getErrorResult("不存在的报表，ID " + reportId, 1);
        }
        ExtendArea targetArea = model.getExtendById(areaId);
        /**
         * TODO 合并当前的全局上下文，需要重构下，整理上下文处理逻辑
         */
        // QueryAction previousAction = runTimeModel.getPreviousQueryAction(areaId);
        ExtendAreaContext areaContext = reportModelCacheManager.getAreaContext(reportId, targetArea.getId());

        ResultSet previousResult = areaContext.getQueryStatus().getLast();
        LogicModel targetLogicModel = null;
        String logicModelAreaId = areaId;
        LogicModel logicModel = targetArea.getLogicModel();
        if (targetArea.getType() == ExtendAreaType.CHART || targetArea.getType() == ExtendAreaType.LITEOLAP_CHART) {
            return ResourceUtils.getErrorResult("can not drill down a chart. ", 1);
        } else if (targetArea.getType() == ExtendAreaType.LITEOLAP_TABLE) {
            LiteOlapExtendArea liteOlapArea = (LiteOlapExtendArea) model.getExtendById(targetArea.getReferenceAreaId());
            targetLogicModel = liteOlapArea.getLogicModel();
            logicModelAreaId = liteOlapArea.getId();
        } else {
            targetLogicModel = logicModel;
        }

        if (targetLogicModel == null) {
            targetLogicModel = new LogicModel();
        }
        /**
         * update context
         */
        Map<String, Object> queryParams = updateLocalContextAndReturn(runTimeModel, areaId, request.getParameterMap());
        /**
         * 找到下载的维度节点
         */
        String[] uniqNames =
                com.baidu.rigel.biplatform.ac.util.DataModelUtils.parseNodeUniqueNameToNodeValueArray(condition);
        if (uniqNames == null || uniqNames.length == 0) {
            String msg = String.format("Fail in drill down. UniqueName param is empty.");
            logger.error(msg);
            return ResourceUtils.getErrorResult(msg, 1);
        }
        int targetIndex = uniqNames.length - 1;
        String drillTargetUniqueName = uniqNames[targetIndex];
        String dimName = MetaNameUtil.getDimNameFromUniqueName(drillTargetUniqueName);
        Map<String, Item> store = runTimeModel.getUniversalItemStore().get(logicModelAreaId);
        if (CollectionUtils.isEmpty(store)) {
            String msg = "The item map of area (" + logicModelAreaId + ") is Empty!";
            logger.error(msg);
            throw new RuntimeException(msg);
        }
        /**
         * 把本行前面的维度都放到过滤中，作为过滤条件
         */
        for (int i = 0; i < targetIndex; i++) {
            String rowAheadUniqueName = uniqNames[i];
            String rowAheadDimName = MetaNameUtil.getDimNameFromUniqueName(rowAheadUniqueName);
            Item rowAhead = store.get(rowAheadDimName);
            queryParams.put(rowAhead.getOlapElementId(), rowAheadUniqueName);
            // 避免出现旋转操作参数遗漏
            model.getParams().values().forEach(p -> {
                if (p.getElementId().equals(rowAhead.getOlapElementId())) {
                    String[] tmp = MetaNameUtil.parseUnique2NameArray(rowAheadUniqueName);
                    queryParams.put(p.getName(), tmp[tmp.length - 1]);
                }
            });
        }

        Item row = store.get(dimName);
        queryParams.put(row.getOlapElementId(), drillTargetUniqueName);
        model.getParams().values().forEach(p -> {
            if (p.getElementId().equals(row.getOlapElementId())) {
                String[] tmp = MetaNameUtil.parseUnique2NameArray(drillTargetUniqueName);
                queryParams.put(p.getName(), tmp[tmp.length - 1]);
            }
        });
        QueryAction action =
                queryBuildService.generateTableQueryActionForDrill(model, areaId, queryParams, targetIndex);

        ResultSet result;
        try {
            result = reportModelQueryService.queryDatas(model, action, true, false, queryParams, securityKey);
        } catch (DataSourceOperationException | QueryModelBuildException | MiniCubeQueryException e1) {
            logger.error(e1.getMessage(), e1);
            return ResourceUtils.getErrorResult("查询出错", 1);
        }
        PivotTable table = null;
        Map<String, Object> resultMap = Maps.newHashMap();
        // ResultSet previousResult = runTimeModel.getPreviousQueryResult();
//        int rowNum = this.getRowNum(previousResult, condition);
        int rowNum = 0; 
        try {
            rowNum = Integer.valueOf(request.getParameter("rowNum"));            
        } catch (Exception e) {
            rowNum = this.getRowNum(previousResult, condition);
        }
        try {
            // 查询下钻的数据
            Cube cube = model.getSchema().getCubes().get(targetArea.getCubeId());
            if (type.equals("expand")) {
                // ResultSet result = reportModelQueryService.queryDatas(model, action, true);
                logger.info("[INFO] --- --- --- ---" + result.getDataModel());
                final int curr = 
                    com.baidu.rigel.biplatform.ac.util.DataModelUtils.getLeafNodeList(result.getDataModel().getRowHeadFields()).size();
                final int newRowNum = rowNum;
                LinkedHashMap<String, DrillDownAction> newDrill = Maps.newLinkedHashMap();
                runTimeModel.getDrillDownQueryHistory().forEach((k, v) -> {
                    int rowNumIndex = Integer.valueOf(k.substring(k.lastIndexOf("_") + 1));
                    if (rowNumIndex > newRowNum) {
                        String prefix = k.substring(0, k.lastIndexOf("_") + 1);
                        int tmpRowNum = rowNumIndex + curr - 1;
                        newDrill.put(prefix + tmpRowNum, new DrillDownAction(v.action, tmpRowNum));
                    } else {
                        newDrill.put(k, v);
                    }
                });
                newDrill.put(condition + "_" + rowNum,
                        new ReportRuntimeModel.DrillDownAction(action, rowNum));
                runTimeModel.setDrillDownQueryHistory(newDrill);
                DataModel newDataModel =
                        DataModelUtils.merageDataModel(previousResult.getDataModel(), result.getDataModel(), rowNum);
                table = DataModelUtils.transDataModel2PivotTable(cube, newDataModel, 
                        false, 0, false, 
                        targetLogicModel);
                result.setDataModel(newDataModel);
                /**
                 * TODO 这里重新生成当前条件对应的action，而不是下钻使用的action，为的是记录下当前表的结果
                 * 
                 */
                QueryContext previousContext = runTimeModel.getLocalContextByAreaId(areaId);
                QueryAction recordAction =
                        queryBuildService.generateTableQueryAction(model, areaId, previousContext.getParams());
                runTimeModel.updateDatas(recordAction, result);
            } else { // 上卷或者折叠操作
                runTimeModel.getDrillDownQueryHistory().remove(condition + "_" + rowNum);
                runTimeModel.getOrderedStatus ().remove (condition + "_" + rowNum);
                List<HeadField> headField = previousResult.getDataModel().getRowHeadFields();
                List<HeadField> tmp = com.baidu.rigel.biplatform.ac.util.DataModelUtils.getLeafNodeList(headField);
                HeadField currRow = tmp.get(rowNum);
                int curr = 
                    com.baidu.rigel.biplatform.ac.util.DataModelUtils.getLeafNodeList(Lists.newArrayList(currRow)).size();
                
                Iterator<String> it = runTimeModel.getDrillDownQueryHistory().keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    int rowNumIndex = Integer.valueOf(key.substring(key.lastIndexOf("_") + 1));
                    if (rowNumIndex >= rowNum && rowNumIndex < (curr + rowNum)) {
                        it.remove();                       
                    } 
                }
                
                final int newRowNum = rowNum;
                final int newCurr = curr;
                LinkedHashMap<String, DrillDownAction> newDrill = Maps.newLinkedHashMap();
                runTimeModel.getDrillDownQueryHistory().forEach((k, v) -> {
                    int rowNumIndex = Integer.valueOf(k.substring(k.lastIndexOf("_") + 1));
                    if (rowNumIndex > newRowNum) {
                        String prefix = k.substring(0, k.lastIndexOf("_") + 1);
                        int tmpRowNum = rowNumIndex - newCurr + 1;
                        newDrill.put(prefix + tmpRowNum, new DrillDownAction(v.action, tmpRowNum));
                    } else {
                        newDrill.put(k, v);
                    }
                });
                runTimeModel.setDrillDownQueryHistory(newDrill);
                
                DataModel newModel = DataModelUtils.removeDataFromDataModel(previousResult.getDataModel(), rowNum);
                table = DataModelUtils.transDataModel2PivotTable(cube, newModel,
                        false, 0, false, 
                        targetLogicModel);
                result = new ResultSet();
                result.setDataModel(newModel);
                /**
                 * TODO 这里重新生成当前条件对应的action，而不是下钻使用的action，为的是记录下当前表的结果
                 * 
                 */
                QueryContext previousContext = runTimeModel.getLocalContextByAreaId(areaId);
                QueryAction recordAction =
                        queryBuildService.generateTableQueryAction(model, areaId, previousContext.getParams());
                runTimeModel.updateDatas(recordAction, result);
            }
            areaContext.getQueryStatus().add(result);
            reportModelCacheManager.updateAreaContext(reportId, targetArea.getId(), areaContext);
            reportModelCacheManager.updateRunTimeModelToCache(reportId, runTimeModel);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        if (targetArea.getType() == ExtendAreaType.TABLE || targetArea.getType() == ExtendAreaType.LITEOLAP_TABLE) {
            // TODO 临时解决方案，此处应将查询条件设置到QuestionModel中
            DataModelUtils.decorateTable(getFormatModel(model, targetArea), table, 
                    DataModelUtils.isShowZero(targetArea.getOtherSetting()));
            // resultMap.put("rowCheckMin", 1);
            // resultMap.put("rowCheckMax", 5);
            if (targetArea.getType() == ExtendAreaType.LITEOLAP_TABLE) {
                logicModel = model.getExtendAreas().get(targetArea.getReferenceAreaId()).getLogicModel();
            }
            logger.info("[INFO] row length = " + logicModel.getRows().length);
            if (logicModel.getRows().length >= 2) {
                Object breadCrum = areaContext.getParams().get("bread_key");
                if (breadCrum == null) {
                    List<Map<String, String>> tmp = Lists.newArrayList();
                    if (areaContext.getCurBreadCrumPath() != null && !areaContext.getCurBreadCrumPath().isEmpty()) {
                        tmp.addAll(areaContext.getCurBreadCrumPath());
                        breadCrum = tmp;
                    }
                }
                if (breadCrum != null) {
                    resultMap.put("mainDimNodes", breadCrum);
                }
            } else {
                resultMap.remove("mainDimNodes");
            }
            resultMap.put("pivottable", table);
            setTableResultProperty(reportId, table, resultMap);
        }
        ResponseResult rs =
                ResourceUtils.getResult("Success Getting VM of Report", "Fail Getting VM of Report", resultMap);
        logger.info("[INFO]Successfully execute drill down operation, cost {} ms", (System.currentTimeMillis() - begin));
        return rs;
    }

    /**
     * 
     * @param previousResult
     * @param uniqueName
     * @return
     */
    private int getRowNum(ResultSet previousResult, String uniqueName) {
        List<HeadField> headField = previousResult.getDataModel().getRowHeadFields();
        List<HeadField> tmp = com.baidu.rigel.biplatform.ac.util.DataModelUtils.getLeafNodeList(headField);
        for (int rowNum = 0; rowNum < tmp.size(); ++rowNum) {
            /**
             * TODO 需要回复
             */
            String lineUniqueName = tmp.get(rowNum).getNodeUniqueName();
            if (lineUniqueName.equals(uniqueName)) {
                return rowNum;
            }
        }
        throw new IllegalStateException("can not found rowNum and colNum");
    }

    /**
     * 上卷操作
     * 
     * @return 上卷操作 操作结果
     */
    public ResponseResult roolUp() {
        return null;
    }

    /**
     * 获取维度成员
     * 
     * @return
     */
    @RequestMapping(value = "/runtime/extend_area/{areaId}/dims/{dimId}/members", method = { RequestMethod.POST })
    public ResponseResult queryMembers(@PathVariable("areaId") String areaId, @PathVariable("dimId") String dimId,
            HttpServletRequest request) throws Exception {
        long begin = System.currentTimeMillis();
        logger.info("[INFO] begin query member operation");
        String reportId = request.getParameter("reportId");
        if (StringUtils.isEmpty(reportId)) {
            ResponseResult rs = new ResponseResult();
            rs.setStatus(1);
            rs.setStatusInfo("reportId为空，请检查输入");
            return rs;
        }
        ReportDesignModel model = null;
        try {
            model = this.getDesignModelFromRuntimeModel(reportId);
            // reportModelCacheManager.getReportModel(reportId);
        } catch (CacheOperationException e) {
            logger.error(e.getMessage(), e);
            ResponseResult rs = ResourceUtils.getErrorResult("不存在的报表，ID " + reportId, 1);
            return rs;
        }
        ReportRuntimeModel runTimeModel = null;
        try {
            runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        } catch (CacheOperationException e) {
            logger.error(e.getMessage(), e);
            ResponseResult rs = ResourceUtils.getErrorResult("不存在的报表，ID " + reportId, 1);
            return rs;
        }
        if (model == null) {
            ResponseResult rs = ResourceUtils.getErrorResult("不存在的报表，ID " + reportId, 1);
            return rs;
        }

        ExtendArea area = model.getExtendById(areaId);
        String cubeId = area.getCubeId();
        Cube cube = model.getSchema().getCubes().get(cubeId);
        Dimension dim = cube.getDimensions().get(dimId);
        /**
         * 通过全局的上下文作为members的参数
         */
        QueryContext queryContext = runTimeModel.getContext();
        Map<String, String> params = Maps.newHashMap();
        for (String key : queryContext.getParams().keySet()) {
            Object value = queryContext.get(key);
            if (value != null) {
                params.put(key, value.toString());
            }
        }
        cube = QueryUtils.getCubeWithExtendArea(model, area);
        ((MiniCube) cube).setSchema(model.getSchema());
        final Dimension newDim = QueryUtils.convertDim2Dim(dim);
        if (params.containsKey(Constants.ORG_NAME) || params.containsKey(Constants.APP_NAME)) {
            ResponseResult rs = new ResponseResult();
            rs.setStatus(0);
            rs.setStatusInfo("OK");
            return rs;
        }
        List<List<Member>> members = reportModelQueryService.getMembers(cube, newDim, params, securityKey);
        QueryContext context = runTimeModel.getLocalContextByAreaId(area.getId());
        List<DimensionMemberViewObject> datas = Lists.newArrayList();
        final AtomicInteger i = new AtomicInteger(1);
        members.forEach(tmpMembers -> {
            DimensionMemberViewObject viewObject = new DimensionMemberViewObject();
            String caption = "第" + i.getAndAdd(1) + "级";
            viewObject.setCaption(caption);
            String name = "[" + newDim.getName() + "]";
            name += ".[All_" + newDim.getName() + "s]";
            viewObject.setName(name);
            viewObject.setNeedLimit(false);
            viewObject.setSelected(i.get() == 2);
            viewObject.setChildren(genChildren(newDim, tmpMembers, context));
            datas.add(viewObject);
        });
        ResponseResult rs = new ResponseResult();
        rs.setStatus(0);
        rs.setStatusInfo("successfully");
        Map<String, List<DimensionMemberViewObject>> dimValue = Maps.newHashMap();
        dimValue.put("dimValue", datas);
        rs.setData(dimValue);
        logger.info("[INFO] query member operation successfull, cost {} ms", (System.currentTimeMillis() - begin));
        return rs;
    }

    /**
     * 按照指标指定排序方式显示数据
     * 
     * @return ResponseResult
     */
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/sort", method = { RequestMethod.POST,
            RequestMethod.GET })
    public ResponseResult sortByMeasure(@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        long begin = System.currentTimeMillis();
        logger.info("begin execuet sort by measure");
        String uniqueName = request.getParameter("uniqueName");
        String sort = request.getParameter("sortType");
        if (StringUtils.isEmpty(sort)) {
            sort = "NONE";
        }
        if (sort.equalsIgnoreCase("NONE") || sort.equalsIgnoreCase("ASC")) {
            sort = "DESC";
        } else if (sort.equalsIgnoreCase("DESC")) {
            sort = "ASC";
        }
        ReportDesignModel reportModel = this.getDesignModelFromRuntimeModel(reportId);
        // reportModelCacheManager.getReportModel(reportId);
        SortRecord.SortType sortType = SortRecord.SortType.valueOf(sort.toUpperCase());
        ExtendArea targetArea = reportModel.getExtendById(areaId);
        ExtendAreaContext context = this.reportModelCacheManager.getAreaContext(reportId, areaId);
        DataModel model = DeepcopyUtils.deepCopy(context.getQueryStatus().getLast().getDataModel());
        SortRecord type = new SortRecord(sortType, uniqueName, 500);
        com.baidu.rigel.biplatform.ac.util.DataModelUtils.sortDataModelBySort(model, type);
        ResultSet rs = new ResultSet();
        model.getColumnHeadFields().forEach(headField -> {
            if (headField.getValue().equals(uniqueName)) {
                headField.getExtInfos().put("sortType", sortType.name());
            } else {
                headField.getExtInfos().put("sortType", "NONE");
            }
        });
        rs.setDataModel(model);
        ReportRuntimeModel runtimeModel = reportModelCacheManager.getRuntimeModel (reportId);
        if (runtimeModel.getDrillDownQueryHistory() != null) {
            List<HeadField> headFields = 
                com.baidu.rigel.biplatform.ac.util.DataModelUtils.getLeafNodeList (model.getRowHeadFields ());
            for (int i = 0; i < headFields.size (); ++i) {
                String nodeUniqueName = headFields.get (i).getNodeUniqueName ();
                for (Map.Entry<String, DrillDownAction> entry : runtimeModel.getDrillDownQueryHistory ().entrySet ()) {
                    if (entry.getKey ().startsWith (nodeUniqueName)) {
                        runtimeModel.getOrderedStatus ().put (entry.getKey (), i);
                    }
                }
            }
        }
        runtimeModel.setSortRecord(type);
        reportModelCacheManager.updateRunTimeModelToCache (reportId, runtimeModel);
        context.getQueryStatus().add(rs);
        PivotTable table = null;
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            Cube cube = reportModel.getSchema().getCubes().get(targetArea.getCubeId());
            LogicModel logicModel = targetArea.getLogicModel ();
            if (targetArea.getType () == ExtendAreaType.LITEOLAP_TABLE 
                || targetArea.getType () == ExtendAreaType.LITEOLAP_CHART) {
                logicModel = reportModel.getExtendById (targetArea.getReferenceAreaId ()).getLogicModel ();
            }
            table = queryBuildService.parseToPivotTable(cube, model, logicModel);
        } catch (PivotTableParseException e) {
            logger.error(e.getMessage(), e);
            return ResourceUtils.getErrorResult("Fail in parsing result. ", 1);
        }
        ExtendArea area = reportModel.getExtendById(areaId);
        if (area.getType () == ExtendAreaType.LITEOLAP_TABLE) {
            area = reportModel.getExtendById (area.getReferenceAreaId ());
        }
        DataModelUtils.decorateTable(area.getFormatModel(), table, 
                DataModelUtils.isShowZero(targetArea.getOtherSetting()));
        if (table.getDataSourceColumnBased().size() == 0) {
            ResponseResult tmp = new ResponseResult();
            tmp.setStatus(1);
            tmp.setStatusInfo("未查到任何数据");
            return tmp;
        } else {
            resultMap.put("pivottable", table);
        }
        setTableResultProperty(reportId, table, resultMap);
        context.getQueryStatus().add(rs);
        reportModelCacheManager.updateAreaContext(reportId, areaId, context);
        logger.info("[INFO]successfully execute sort by measure. cost {} ms", (System.currentTimeMillis() - begin));
        return ResourceUtils.getResult("Success", "Fail", resultMap);
    }

    /**
     * 
     * @param tmpMembers
     * @param context
     * @return List<DimensionMemberViewObject>
     */
    private List<DimensionMemberViewObject> genChildren(Dimension dim, List<Member> tmpMembers, QueryContext context) {
        final List<DimensionMemberViewObject> rs = Lists.newArrayList();
        DimensionMemberViewObject all = new DimensionMemberViewObject();
        Map<String, Object> params = context.getParams();
        Set<String> tmpKey = Sets.newHashSet();
        params.values().forEach(strArray -> {
            if (strArray instanceof String[]) {
                String[] tmpArray = (String[]) strArray;
                for (String tmpStr : tmpArray) {
                    tmpKey.add(tmpStr);
                }
            }
        });
        all.setCaption("全部");
        all.setNeedLimit(false);
        String name = "[" + dim.getName() + "]";
        name += ".[All_" + dim.getName() + "s]";
        all.setName(name);
        all.setSelected(tmpKey.contains(name));
        rs.add(all);
        tmpMembers.forEach(m -> {
            DimensionMemberViewObject child = new DimensionMemberViewObject();
            child.setCaption(m.getCaption());
            child.setSelected(tmpKey.contains(m.getUniqueName()));
            child.setNeedLimit(false);
            child.setName(m.getUniqueName());
            rs.add(child);
        });
        return rs;
    }

    /**
     * 更新维度成员
     * 
     * @return ResponseResult
     */
    @RequestMapping(value = "/runtime/extend_area/{areaId}/dims/{dimId}/members/1", method = { RequestMethod.POST })
    public ResponseResult updateMembers(@PathVariable("areaId") String areaId, @PathVariable("dimId") String dimId,
            HttpServletRequest request) throws Exception {
        String reportId = request.getParameter("reportId");
        if (StringUtils.isEmpty(reportId)) {
            ResponseResult rs = new ResponseResult();
            rs.setStatus(1);
            rs.setStatusInfo("reportId为空，请检查输入");
            return rs;
        }
        ReportRuntimeModel model = null;
        try {
            model = reportModelCacheManager.getRuntimeModel(reportId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ResponseResult rs = ResourceUtils.getErrorResult("不存在的报表，ID " + reportId, 1);
            return rs;
        }
        ReportDesignModel designModel = this.getDesignModelFromRuntimeModel(reportId);
        // reportModelCacheManager.getReportModel(reportId);
        ExtendArea area = designModel.getExtendById(areaId);

        String[] selectedDims = request.getParameterValues("selectedNodes");
        updateLocalContext(dimId, model, selectedDims, areaId);
        if (area.getType() == ExtendAreaType.SELECTION_AREA) {
            areaId = area.getReferenceAreaId();
            LiteOlapExtendArea liteOlapArea = (LiteOlapExtendArea) designModel.getExtendById(areaId);
            String chartAreaId = liteOlapArea.getChartAreaId();
            String tableAreaId = liteOlapArea.getTableAreaId();
            updateLocalContext(dimId, model, selectedDims, chartAreaId);
            updateLocalContext(dimId, model, selectedDims, tableAreaId);
        }

        this.reportModelCacheManager.updateRunTimeModelToCache(reportId, model);
        ResponseResult rs = ResourceUtils.getCorrectResult("successfully", null);
        return rs;
    }

    /**
     * updateLocalContext
     * 
     * @param dimId
     * @param model
     * @param selectedDims
     * @param chartAreaId
     * 
     */
    private void updateLocalContext(String dimId, ReportRuntimeModel model, String[] selectedDims, String areaId) {
        QueryContext localContext = model.getLocalContextByAreaId(areaId);
        localContext.getParams().put(dimId, selectedDims);
        String reportModelId = model.getReportModelId ();
        ExtendAreaContext context = this.reportModelCacheManager.getAreaContext(reportModelId, areaId);
        context.getParams().put(dimId, selectedDims);
        reportModelCacheManager.updateAreaContext(reportModelId, areaId, context);
    }

    /**
     * 平面表下载请求，下载数据，仅6万条，同步下载
     * 
     * @param reportId
     * @param areaId
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/{reportId}/downloadOnline/{areaId}", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseResult downloadForPlaneTable(@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
   
        long begin = System.currentTimeMillis();
        ResponseResult rs = new ResponseResult();
        ReportDesignModel report = this.getDesignModelFromRuntimeModel(reportId);
        if (report == null) {
            throw new IllegalStateException("未知报表定义，请确认下载信息");
        }
        ExtendArea targetArea = report.getExtendById(areaId);

        // 从runtimeModel中取出designModel
        ReportDesignModel designModel = this.getDesignModelFromRuntimeModel(reportId);
        // 通过上一次的查询条件按获取Questionmodel
        QuestionModel questionModel = this.getQuestionModelFromQueryAction(reportId, areaId, request);
        // 下载类型
        // String downloadType = DownloadType.PLANE_TABLE_ONLINE.getName();
        // 获取下载服务
        DownloadType downloadType = DownloadType.PLANE_TABLE_ONLINE;
        downloadType.setDsType(dsService.getDsDefine(designModel.getDsId()).getDataSourceType().name());
        DownloadTableDataService downloadService = DownloadServiceFactory.getDownloadTableDataService(downloadType);
        Map<String, Object> setting = targetArea.getOtherSetting();
        // 获取下载字符串
        String csvString =
                downloadService.downloadTableData(questionModel,
                        designModel.getExtendById(areaId).getLogicModel(), setting);

//        final StringBuilder timeRange = new StringBuilder();
//        // 在上下文参数中判断是否有时间参数
//        for (Map.Entry<String, Object> entry : areaContext.getParams().entrySet()) {
//            Object v = entry.getValue();
//            if (v instanceof String && v.toString().contains("start") && v.toString().contains("end")
//                    && v.toString().contains("granularity")) {
//                try {
//                    JSONObject json = new JSONObject(v.toString());
//                    timeRange.append(json.getString("start") + "至" + json.getString("end"));
//                    break;
//                } catch (Exception e) {
//                }
//            }
//        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/vnd.ms-excel;charset=GBK");
        response.setContentType("application/x-msdownload;charset=GBK");
        // 写入文件
        final String fileName = report.getName();
//                + "_" +  timeRange.toString();
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf8") + ".csv");
        byte[] content = csvString.getBytes("GBK");
        response.setContentLength(content.length);
        OutputStream os = response.getOutputStream();
        os.write(content);
        os.flush();
        rs.setStatus(ResponseResult.SUCCESS);
        rs.setStatusInfo("successfully");

        logger.info("[INFO]download data cost : " + (System.currentTimeMillis() - begin) + " ms");
        return rs;
    }
    
    /**
     * 通过上一次查询的QueryAction，获取questionModel
     *
     * @param reportId reportId
     * @param areaId areaId
     * @param request HttpServletRequest
     * @return QuestionModel 问题模型
     */
    private QuestionModel getQuestionModelFromQueryAction(String reportId, String areaId, HttpServletRequest request) {
        ReportDesignModel report = this.getDesignModelFromRuntimeModel(reportId);
        if (report == null) {
            throw new IllegalStateException("未知报表定义，请确认下载信息");
        }
        ExtendArea targetArea = report.getExtendById(areaId);
        ReportRuntimeModel runtimeModel = reportModelCacheManager.getRuntimeModel(reportId);

        // 从runtimeModel中取出designModel
        ReportDesignModel designModel = this.getDesignModelFromRuntimeModel(reportId);

        /**
         * TODO 增加参数信息
         */
        Map<String, Object> tmp = QueryUtils.resetContextParam(request, designModel);
        tmp.forEach((k, v) -> {
            runtimeModel.getLocalContextByAreaId(areaId).put(k, v);
        });

        // 获取查询条件信息
        ExtendAreaContext areaContext = this.getAreaContext(areaId, request, targetArea, runtimeModel);
        // 设置查询不受限制
        areaContext.getParams().put(Constants.NEED_LIMITED, false);
        // 获取查询action
        // 获取上一次查询的QueryAction
        QueryAction action = runtimeModel.getPreviousQueryAction(areaId);
        if (action != null) {
            action.setChartQuery(false);
        } else {
            throw new RuntimeException("下载失败");
        }

        // 构建平面表下载分页信息
        PageInfo pageInfo = new PageInfo();
        // 默认设置为100000，这样后端不会对其实施count(*)求总的记录数
        pageInfo.setTotalRecordCount(100000);
        pageInfo.setCurrentPage(-1);
        // 默认设置为10万条记录
        pageInfo.setPageSize(-1);
        // 获取数据源信息
        DataSourceDefine dsDefine;
        QuestionModel questionModel = null;
        try {
            dsDefine = dsService.getDsDefine(designModel.getDsId());
            questionModel =
                    QueryUtils.convert2QuestionModel(dsDefine, designModel, action, areaContext.getParams(), null,
                            securityKey);
        } catch (DataSourceOperationException e) {
            logger.error(e.getCause().getMessage());
        }
        catch (QueryModelBuildException e) {
            logger.error(e.getCause().getMessage());
        }
        return questionModel;
    }

    /**
     * 平面表下载请求，默认下载全部数据，异步下载
     * 
     * @param reportId
     * @param areaId
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/{reportId}/downloadOnline/asyn/{areaId}", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseResult downloadAsynForPlaneTable(@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ResponseResult rs = new ResponseResult();
        String receiveMail = request.getParameter("receiveMail");
        String totalCount = request.getParameter("totalCount");
        if (StringUtils.isEmpty(receiveMail)) {
            rs.setStatus(ResponseResult.FAILED);
            rs.setStatusInfo("输入的邮箱不能为空");
            return rs;
        }
        if (StringUtils.isEmpty(totalCount)) {
            rs.setStatus(ResponseResult.FAILED);
            rs.setStatusInfo("下载的数据行数为0");
            return rs;
        }
        long begin = System.currentTimeMillis();
        // 从runtimeModel中取出designModel
        ReportDesignModel designModel = this.getDesignModelFromRuntimeModel(reportId);
        QuestionModel questionModel = this.getQuestionModelFromQueryAction(reportId, areaId, request);

        // setPageInfo
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageSize(5000);
        pageInfo.setCurrentPage(-1);
        pageInfo.setTotalRecordCount(Integer.valueOf(totalCount));
        questionModel.setPageInfo(pageInfo);

        // 获取cookies信息
        Cookie[] cookies = request.getCookies();
        HashMap<String, String> cookiesMap = new HashMap<String, String>(); 
        for (int i = 0; i < cookies.length; i++) {
            cookiesMap.put(cookies[i].getName(), cookies[i].getValue());
        }
        
        // 设置显示列的顺序
        LogicModel logicModel = designModel.getExtendById(areaId).getLogicModel();
        ConfigQuestionModel configQuestionModel = (ConfigQuestionModel) questionModel;
        try {
            Object obj = AyncAddDownloadTaskServiceFactory
                    .getAyncAddDownloadTaskService("defaultAyncAddDownloadTaskService");
            AddTaskParameters addTaskParameters = new AddTaskParameters();
            addTaskParameters.setQuestionModel(questionModel);
            addTaskParameters.setRecMail(receiveMail);
            addTaskParameters.setReportName(designModel.getName());
            addTaskParameters.setCookies(cookiesMap);
            addTaskParameters.setRequestUrl(request.getRequestURL().toString());
            addTaskParameters.setColumns(DataModelUtils.getKeysInOrder(configQuestionModel.getCube(), logicModel));
            AddTaskStatus result = (AddTaskStatus) obj.getClass()
                    .getMethod(
                            "addTask", AddTaskParameters.class).invoke(obj, addTaskParameters);
            if (result.getStatus() == 0) {
                rs.setStatus(ResponseResult.SUCCESS);
                rs.setStatusInfo("successfully");
            } else {
                rs.setStatus(ResponseResult.FAILED);
                rs.setStatusInfo("下载任务添加失败");
            }
            logger.info("[INFO]handle download asyn data cost : " + (System.currentTimeMillis() - begin) + " ms");
        } catch (Exception e) {
            rs.setStatus(ResponseResult.FAILED);
            rs.setStatusInfo("此下载服务目前不可用");
        }
        return rs;
    }
    
    /**
     * 下载请求
     * 
     * @return
     */
    @RequestMapping(value = "/{reportId}/download/{areaId}", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseResult download(@PathVariable("reportId") String reportId, @PathVariable("areaId") String areaId,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        long begin = System.currentTimeMillis();
        ReportDesignModel report = this.getDesignModelFromRuntimeModel(reportId);
        // reportModelCacheManager.getReportModel(reportId);
        if (report == null) {
            throw new IllegalStateException("未知报表定义，请确认下载信息");
        }
        ExtendArea targetArea = report.getExtendById(areaId);
        Cube cube = report.getSchema().getCubes().get(targetArea.getCubeId());
        final ReportRuntimeModel runtimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        ExtendAreaContext areaContext = this.getAreaContext(areaId, request, targetArea, runtimeModel);
        areaContext.getParams().put(Constants.NEED_LIMITED, false);
        QueryAction action = runtimeModel.getLinkedQueryAction();
        if (action == null) {
            action = queryBuildService.generateTableQueryAction(report, areaId, areaContext.getParams());
        }
        if (action != null) {
//            setQueryParams (areaContext, runtimeModel.getModel (), action.getColumns ());
//            setQueryParams (areaContext, runtimeModel.getModel (), action.getRows ());
//            setQueryParams (areaContext, runtimeModel.getModel (), action.getSlices ());
            action.setChartQuery(false);
        } else {
            throw new RuntimeException("下载失败");
        }
        ResultSet queryRs =
                reportModelQueryService.queryDatas(report, action, true, true, areaContext.getParams(), securityKey);
        DataModel dataModel = queryRs.getDataModel();
        SortRecord oriSortRecord = runtimeModel.getSortRecord ();
        if (oriSortRecord != null) {
            SortRecord sortRecord = 
                new SortRecord (oriSortRecord.getSortType (), oriSortRecord.getSortColumnUniquename (), 100000);
            com.baidu.rigel.biplatform.ac.util.DataModelUtils.sortDataModelBySort(dataModel, sortRecord);
        }
        final StringBuilder timeRange = new StringBuilder();
        areaContext.getParams().forEach(
                (k, v) -> {
                    if (v instanceof String && v.toString().contains("start") && v.toString().contains("end")
                            && v.toString().contains("granularity")) {
                        try {
                            JSONObject json = new JSONObject(v.toString());
                            timeRange.append(json.getString("start") + "至" + json.getString("end"));
                        } catch (Exception e) {
                        }
                    }
                });
        logger.info("[INFO]query data cost : " + (System.currentTimeMillis() - begin) + " ms");
        begin = System.currentTimeMillis();
        ReportDesignModel model = runtimeModel.getModel ();
        if (runtimeModel.getDrillDownQueryHistory() != null && !runtimeModel.getDrillDownQueryHistory().isEmpty()) {
            List<String> keys = Lists.newArrayList (runtimeModel.getDrillDownQueryHistory ().keySet ());
            Collections.sort (keys, (k1, k2) -> {
                int tmp1 = Integer.valueOf (k1.substring (k1.lastIndexOf ("_") + 1));
                if (runtimeModel.getOrderedStatus ().get (k1) != null) {
                    tmp1 = runtimeModel.getOrderedStatus ().get (k1);
                }
                int tmp2 = Integer.valueOf (k2.substring (k2.lastIndexOf ("_") + 1));
                if (runtimeModel.getOrderedStatus ().get (k2) != null) {
                    tmp2 = runtimeModel.getOrderedStatus ().get (k2);
                }
                return tmp1 - tmp2;
            });
            for (String key : keys) {
                DrillDownAction queryAction = runtimeModel.getDrillDownQueryHistory().get (key);
                setQueryParams (areaContext, model, queryAction.action.getColumns ());
                setQueryParams (areaContext, model, queryAction.action.getRows ());
                setQueryParams (areaContext, model, queryAction.action.getSlices ());
                ResultSet subData =
                        reportModelQueryService.queryDatas(report, queryAction.action, true, true,
                                areaContext.getParams(), securityKey);
                int rowNum = queryAction.rowNum;
                if (runtimeModel.getOrderedStatus ().get (key) != null) {
                    rowNum = runtimeModel.getOrderedStatus ().get (key);
                }
                dataModel = DataModelUtils.merageDataModel(dataModel, subData.getDataModel(), rowNum);
            }
        }
        // 处理下载请求中对数据的包装
//        dataModel = DataModelUtils.preProcessDataModel4Show(dataModel, targetArea.getOtherSetting());
        LogicModel logicModel = targetArea.getLogicModel ();
        if (targetArea.getType () == ExtendAreaType.LITEOLAP_TABLE) {
            logicModel = model.getExtendAreas ().get (targetArea.getReferenceAreaId ()).getLogicModel ();
        }
        String csvString = DataModelUtils.convertDataModel2CsvString(cube, dataModel, logicModel);
        logger.info("[INFO]convert data cost : " + (System.currentTimeMillis() - begin) + " ms");
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/vnd.ms-excel;charset=GBK");
        response.setContentType("application/x-msdownload;charset=GBK");
        final String fileName = report.getName() + timeRange.toString();
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf8") + ".csv");
        byte[] content = csvString.getBytes("GBK");
        response.setContentLength(content.length);
        OutputStream os = response.getOutputStream();
        os.write(content);
        os.flush();
        ResponseResult rs = new ResponseResult();
        rs.setStatus(ResponseResult.SUCCESS);
        rs.setStatusInfo("successfully");
        return rs;
    }

    /**
     * 跟进下钻历史纪录，查询增量数据
     * @param areaContext
     * @param model
     * @param items
     */
    private void setQueryParams(ExtendAreaContext areaContext, ReportDesignModel model, Map<Item, Object> items) {
        for (Map.Entry<Item , Object>  entry : items.entrySet ()) {
            if (entry.getValue () != null) {
                String tmp = null;
                if (entry.getValue () instanceof String[]) {
                    tmp = ((String[]) entry.getValue ())[0];
                } else {
                    tmp = entry.getValue ().toString ();
                }
                List<ReportParam> params = model.getParams ().values ().stream ()
                        .filter (p -> p.getElementId ().equals (entry.getKey ().getOlapElementId ()))
                        .collect (Collectors.toList ());
                for (ReportParam p : params) {
                    areaContext.getParams ().put (p.getName (), MetaNameUtil.getNameFromMetaName (tmp));
                }
            }
        }
    }

    // @RequestMapping(value = "/test", method = {RequestMethod.POST , RequestMethod.GET})
    // public ResponseResult test(HttpServletRequest request, HttpServletResponse response) throws Exception {
    // System.out.println(request.getParameter("test"));
    // System.out.println(request.getAttribute("test"));
    // return null;
    // }

    /**
     * 图形指标切换操作api TODO 目前只支持图形，后续考虑支持表格
     * 
     * @param reportId
     * @param areaId
     * @param request
     * @return
     */
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/index/{index}", method = { RequestMethod.POST })
    public ResponseResult changeChartMeasure(@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId, @PathVariable("index") int index, HttpServletRequest request) {
        long begin = System.currentTimeMillis();
        logger.info("[INFO] begin query data with new measure");
        /**
         * 1. 获取缓存DesignModel对象
         */
        ReportDesignModel model;

        /**
         * 3. 获取运行时对象
         */
        ReportRuntimeModel runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);

        try {
            model = getRealModel(reportId, runTimeModel);
        } catch (CacheOperationException e) {
            logger.info("[INFO]Report model is not in cache! ", e);
            ResponseResult rs = ResourceUtils.getErrorResult("缓存中不存在的报表，ID " + reportId, 1);
            return rs;
        }
        /**
         * 2. 获取区域对象
         */
        ExtendArea targetArea = model.getExtendById(areaId);
        if (targetArea == null) {
            throw new IllegalStateException("can't get report define");
        }

        /**
         * 4. 更新区域本地的上下文
         */
        ExtendAreaContext areaContext = getAreaContext(areaId, request, targetArea, runTimeModel);

        /**
         * 5. 生成查询动作QueryAction
         */
        QueryAction action = null;
        if (targetArea.getType() == ExtendAreaType.CHART || targetArea.getType() == ExtendAreaType.LITEOLAP_CHART) {
            String[] indNames = new String[0];
            if (StringUtils.hasText(request.getParameter("indNames"))) {
                indNames = request.getParameter("indNames").split(",");
            }
            try {
                areaContext.getParams().put(Constants.CHART_SELECTED_MEASURE, index);
                action =
                        queryBuildService.generateChartQueryAction(model, areaId, areaContext.getParams(), indNames,
                                runTimeModel);
                if (action != null) {
                    action.setChartQuery(true);
                }
                // TODO to be delete
                boolean timeLine = isTimeDimOnFirstCol(model, targetArea, action);
                boolean isPieChart = isPieChart(getChartTypeWithExtendArea(model, targetArea));
                if (!timeLine && isPieChart) {
                    action.setNeedOthers(true);
                }
            } catch (QueryModelBuildException e) {
                String msg = "没有配置时间维度，不能使用liteOlap趋势分析图！";
                logger.warn(msg);
                DIReportChart chart = new DIReportChart();
                return ResourceUtils.getCorrectResult(msg, chart);
            }
        } else {
            throw new UnsupportedOperationException("未支持的操作");
        }

        /**
         * 6. 完成查询
         */
        ResultSet result;
        try {
            if (action == null || CollectionUtils.isEmpty(action.getRows())
                    || CollectionUtils.isEmpty(action.getColumns())) {
                return ResourceUtils.getErrorResult("单次查询至少需要包含一个横轴、一个纵轴元素", 1);
            }
            areaContext.getParams().remove(Constants.CHART_SELECTED_MEASURE);
            result =
                    reportModelQueryService.queryDatas(model, action, true, true, areaContext.getParams(), securityKey);

        } catch (DataSourceOperationException e1) {
            logger.info("获取数据源失败！", e1);
            return ResourceUtils.getErrorResult("获取数据源失败！", 1);
        } catch (QueryModelBuildException e1) {
            logger.info("构建问题模型失败！", e1);
            return ResourceUtils.getErrorResult("构建问题模型失败！", 1);
        } catch (MiniCubeQueryException e1) {
            logger.info("查询数据失败！", e1);
            return ResourceUtils.getErrorResult("没有查询到相关数据", 1);
        }
        PivotTable table = null;
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            Cube cube = model.getSchema().getCubes().get(targetArea.getCubeId());
            table = queryBuildService.parseToPivotTable(cube, result.getDataModel(), targetArea.getLogicModel ());
        } catch (PivotTableParseException e) {
            logger.info(e.getMessage(), e);
            return ResourceUtils.getErrorResult("Fail in parsing result. ", 1);
        }
        if (targetArea.getType() == ExtendAreaType.TABLE || targetArea.getType() == ExtendAreaType.LITEOLAP_TABLE) {
            throw new UnsupportedOperationException("未支持的操作");
        } else if (targetArea.getType() == ExtendAreaType.CHART
                || targetArea.getType() == ExtendAreaType.LITEOLAP_CHART) {
            DIReportChart chart = null;
            Map<String, String> chartType = getChartTypeWithExtendArea(model, targetArea);
            if (action.getRows().size() == 1) {
                Item item = action.getRows().keySet().toArray(new Item[0])[0];
                OlapElement element =
                        ReportDesignModelUtils.getDimOrIndDefineWithId(model.getSchema(), targetArea.getCubeId(),
                                item.getOlapElementId());
                if (element instanceof TimeDimension) {
                    chart = chartBuildService.parseToChart(table, chartType, true);
                } else {
                    chart = chartBuildService.parseToChart(table, chartType, false);
                }
            } else {
                chart = chartBuildService.parseToChart(table, chartType, false);
            }
            QueryUtils.decorateChart(chart, targetArea, model.getSchema(), index);
            resultMap.put("reportChart", chart);
        }
        areaContext.getQueryStatus().add(result);
        // 清除当前request中的请求参数，保证areaContext的参数正确
        resetAreaContext(areaContext, request);
        resetContext(runTimeModel.getLocalContextByAreaId (areaId), request);
        reportModelCacheManager.updateAreaContext(reportId, targetArea.getId(), areaContext);
        runTimeModel.updateDatas(action, result);
        reportModelCacheManager.updateRunTimeModelToCache(reportId, runTimeModel);
        logger.info("[INFO] successfully query data operation. cost {} ms", (System.currentTimeMillis() - begin));
        ResponseResult rs = ResourceUtils.getResult("Success", "Fail", resultMap);
        return rs;
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
        //model = getDesignModelFromRuntimeModel(reportId);
        model = runTimeModel.getModel();
        // }
        return model;
    }

    /**
     * 依据
     */
    @RequestMapping(value = "/{reportId}/members/{areaId}", method = { RequestMethod.POST })
    public ResponseResult getMemberWithParent(@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId, HttpServletRequest request) {
        long begin = System.currentTimeMillis();
        logger.info("[INFO]--- ---begin init params with report id {}", reportId);
        String currentUniqueName = request.getParameter("uniqueName");
        int level = MetaNameUtil.parseUnique2NameArray(currentUniqueName).length - 1;
        final ReportDesignModel model = getDesignModelFromRuntimeModel(reportId);
        final ReportRuntimeModel runtimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        Map<String, Object> datas = Maps.newConcurrentMap();
        Map<String, String> params = Maps.newHashMap();
        runtimeModel.getContext().getParams().forEach((k, v) -> {
            params.put(k, v == null ? "" : v.toString());
        });
        ExtendArea area = model.getExtendById(areaId);
        if (area != null && isQueryComp(area.getType()) && !area.listAllItems().isEmpty()) {
            Item item = area.listAllItems().values().toArray(new Item[0])[0];
            Cube cube = model.getSchema().getCubes().get(area.getCubeId());
            Cube tmpCube = QueryUtils.transformCube(cube);
            String dimId = item.getOlapElementId();
            Dimension dim = cube.getDimensions().get(dimId);
            if (dim != null) {
                List<Map<String, String>> values;
                try {
                    values = Lists.newArrayList();
                    params.remove(dim.getId());
                    List<Member> members =
                            reportModelQueryService.getMembers(tmpCube, currentUniqueName, params, securityKey);
                    members.forEach(m -> {
                        Map<String, String> tmp = Maps.newHashMap();
                        tmp.put("value", m.getUniqueName());
                        tmp.put("text", m.getCaption());
                        tmp.put("isLeaf", Boolean.toString(level < dim.getLevels().size()));
                        values.add(tmp);
                    });
                    Map<String, Object> datasource = Maps.newHashMap();
                    datasource.put("datasource", values);
                    if (area.getType () == ExtendAreaType.CASCADE_SELECT) {
                        fillBackParamValues (runtimeModel, dim, datasource);
                    }
                    datas.put(areaId, datasource);
                } catch (Exception e) {
                    logger.info(e.getMessage(), e);
                } // end catch
            } // end if dim != null
        } // end if area != null
        ResponseResult rs = new ResponseResult();
        rs.setStatus(0);
        rs.setData(datas);
        rs.setStatusInfo("OK");
        logger.info("[INFO]--- --- successfully query member, cost {} ms", (System.currentTimeMillis() - begin));
        return rs;
    }
    
    /**
     * 处理平面表跳转时的参数问题 handleReqParams4PlaneTable
     * @param cube
     * @param uniqueName
     * @param params
     * @return
     */
    private String handleReqParams4PlaneTable(Cube cube, Map<String, String> planeTableCond, String uniqueName,
            Map<String, String> params, String securityKey) throws DataSourceOperationException {

        if (!ParamValidateUtils.check("cube", cube)) {
            return null;
        }
        if (!ParamValidateUtils.check("planeTableCond", planeTableCond)) {
            return null;
        }
        if (!ParamValidateUtils.check("uniqueName", uniqueName)) {
            return null;
        }
        String dimName = MetaNameUtil.getDimNameFromUniqueName(uniqueName);
        Cube oriCube = QueryUtils.transformCube(cube);
        Dimension dim = oriCube.getDimensions().get(dimName);
        String[] tmp = MetaNameUtil.parseUnique2NameArray(uniqueName);
        Level tmpLevel = null;
        if (dim != null && dim.getLevels() != null) {
            tmpLevel = dim.getLevels().values().toArray(new Level[0])[0];
        }

        DataSourceDefine dsDefine = null;
        DataSourceInfo dsInfo = null;
        DataSourceService dataSourceService =
                (DataSourceService) ApplicationContextHelper.getContext().getBean("dsService");
        try {
            dsDefine = dataSourceService.getDsDefine(cube.getSchema().getDatasource());
            DataSourceConnectionService<?> dsConnService = DataSourceConnectionServiceFactory.
                    getDataSourceConnectionServiceInstance(dsDefine.getDataSourceType().name());
            dsInfo = dsConnService.parseToDataSourceInfo(dsDefine, securityKey);
        } catch (DataSourceOperationException | DataSourceConnectionException e) {
            logger.error("Fail in parse datasource to datasourceInfo.", e);
            throw new DataSourceOperationException(e);
        }
        if (isCallbackLevel(tmpLevel)) {
            // 处理callback
            CallbackLevel callbackLevel = (CallbackLevel) tmpLevel;
            Map<String, String> callbackParams = callbackLevel.getCallbackParams();
            String callbackParam = null;
            // TODO是否考虑多个参数问题
            for (String key : callbackParams.keySet()) {
                if (planeTableCond.containsKey(key)) {
                    callbackParam = key;
                    break;
                }
            }
            callbackLevel.getCallbackParams().put(callbackParam, tmp[tmp.length - 1]);
            List<Member> members = callbackLevel.getMembers(oriCube, dsInfo, params);
            for (Member member : members) {
                if (member.getUniqueName().equals(uniqueName)) {
                    MiniCubeMember miniCubeMember = (MiniCubeMember) member;
                    Set<String> queryNodes = miniCubeMember.getQueryNodes();
                    return queryNodes.stream().collect(Collectors.joining(","));
                }
            }
        } else {
            // 如果有孩子结点，则要取到孩子结点数值
            if ((dim.getLevels().size() > tmp.length - 1)) {
                Level level = dim.getLevels().values().toArray(new Level[0])[tmp.length - dim.getLevels().size()];
                List<Member> members = level.getMembers(oriCube, dsInfo, params);
                
                for (Member member : members) {
                    if (member.getUniqueName().equals(uniqueName)) {
                        List<Member> childMembers = member.getChildMembers(oriCube, dsInfo, params);
                        return childMembers.stream().map(child -> 
                            child.getName()
                        ).collect(Collectors.joining(","));
                    }
                }
            } else {
                // 如果没有孩子，则直接返回
                return tmp[tmp.length - 1];
            }
        }
        return null;
    }

    /**
     * 判断某个level是否为callback isCallbackLevel
     * 
     * @param level
     * @return
     */
    private static boolean isCallbackLevel(Level level) {
        return level != null && level.getType() == LevelType.CALL_BACK;
    }

    
    /**
     * 数据查询API，获取基于报表模型的数据
     * @param reportId
     * @param request
     * @return ResponseResult
     */
    @RequestMapping(value = "/{reportId}/data", method = { RequestMethod.POST , RequestMethod.GET})
    public ResponseResult queryData(@PathVariable("reportId") String reportId, 
        HttpServletRequest request, HttpServletResponse response) throws Exception {
        long begin = System.currentTimeMillis ();
        queryVM (reportId, request, response);
        ResponseResult rs = updateContext (reportId, request);
        if (rs.getStatus () != 0) {
            return rs;
        }
        ReportRuntimeModel runtimeModel = reportModelCacheManager.getRuntimeModel (reportId);
        ReportDesignModel model = this.getRealModel (reportId, runtimeModel);
        rs = new ResponseResult ();
        if (model == null) {
            rs.setStatus (1);
            rs.setStatusInfo ("未找到相应数据模型");
            logger.info ("cannot get report define in queryData");
        }
        if (model.getExtendAreas ().size () != 1) {
            rs.setStatus (1);
            rs.setStatusInfo ("数据区域个数大于2, 不能确定数据区域");
            logger.info ("more than one data areas, return");
        } else {
            ExtendArea area = model.getExtendAreaList ()[0];
            rs = queryArea (reportId, area.getId (), request);
            if (rs.getStatus () != 0) {
                logger.info ("unknown error!");
                return rs;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) rs.getData ();
            Map<String, List<String>> datas = Maps.newHashMap ();
            if (data.containsKey ("pivottable")) {
                PivotTable pivotTable = (PivotTable) data.get ("pivottable");
                pivotTable.getDataSourceColumnBased ();
                List<List<RowHeadField>> rowHeadFields = pivotTable.getRowHeadFields ();
                List<List<RowHeadField>> colFieldBaseRow = Lists.newArrayList ();
                // 行列互转
                for (int i = 0; i < rowHeadFields.size (); ++i) {
                    for (int j = 0; j < rowHeadFields.get (i).size (); ++j) {
                        if (colFieldBaseRow.size () <= j) {
                            List<RowHeadField> tmp = Lists.newArrayList ();
                            tmp.add (rowHeadFields.get (i).get (j));
                            colFieldBaseRow.add (tmp);
                        } else {
                            colFieldBaseRow.get (j).add (rowHeadFields.get (i).get (j));
                        }
                    }
                }
                colFieldBaseRow.forEach (list -> {
                    String key = list.get (0).getUniqueName ();
                    key = MetaNameUtil.getDimNameFromUniqueName (key);
                    List<String> value = list.stream ().map (f -> f.getV ()).collect (Collectors.toList ());
                    datas.put (key, value);
                });
                for (int i = 0; i < pivotTable.getColDefine ().size (); ++i) {
                    String key = pivotTable.getColDefine ().get (i).getUniqueName ();
                    if (MetaNameUtil.isUniqueName(key)) {
                        key = MetaNameUtil.getNameFromMetaName (key);
                    }
                    List<CellData> v = pivotTable.getDataSourceColumnBased ().get (i);
                    List<String> tmpV = 
                        v.stream ().map (cellData -> cellData.getV ().toString ()).collect (Collectors.toList ());
                    datas.put (key, tmpV);
                }
            } else {
                // DoNothing 暂时不支持平面表 
            }
            rs.setData (datas);
        }
        logger.info ("successfully get data from report model, cost {} ms", (System.currentTimeMillis () - begin));
        return rs;
    }
}
