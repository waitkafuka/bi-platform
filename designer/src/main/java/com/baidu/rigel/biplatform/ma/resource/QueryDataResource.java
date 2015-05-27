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

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeMember;
import com.baidu.rigel.biplatform.ac.minicube.TimeDimension;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.DimensionType;
import com.baidu.rigel.biplatform.ac.model.Level;
import com.baidu.rigel.biplatform.ac.model.Member;
import com.baidu.rigel.biplatform.ac.model.OlapElement;
import com.baidu.rigel.biplatform.ac.model.TimeType;
import com.baidu.rigel.biplatform.ac.query.data.DataModel;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.data.HeadField;
import com.baidu.rigel.biplatform.ac.query.model.PageInfo;
import com.baidu.rigel.biplatform.ac.query.model.SortRecord;
import com.baidu.rigel.biplatform.ac.util.DeepcopyUtils;
import com.baidu.rigel.biplatform.ac.util.HttpRequest;
import com.baidu.rigel.biplatform.ac.util.MetaNameUtil;
import com.baidu.rigel.biplatform.ac.util.TimeUtils;
import com.baidu.rigel.biplatform.ma.ds.exception.DataSourceConnectionException;
import com.baidu.rigel.biplatform.ma.ds.exception.DataSourceOperationException;
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
import com.baidu.rigel.biplatform.ma.report.model.LiteOlapExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.baidu.rigel.biplatform.ma.report.model.MeasureTopSetting;
import com.baidu.rigel.biplatform.ma.report.model.PlaneTableCondition;
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
import com.baidu.rigel.biplatform.ma.report.query.pivottable.PivotTable;
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
import com.google.gson.reflect.TypeToken;



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
     * 初始化查询参数,初始化查询区域参数
     * @param reportId
     * @param request
     * @return ResponseResult
     */
    @RequestMapping(value = "/{reportId}/init_params", method = { RequestMethod.POST })
    public ResponseResult initParams(@PathVariable("reportId") String reportId,
            HttpServletRequest request) {
        long begin = System.currentTimeMillis();
        logger.info("[INFO]--- ---begin init params with report id {}", reportId);
        String[] areaIds = request.getParameter("paramList").split(",");
        if (areaIds == null || areaIds.length == 0) {
            ResponseResult rs = new ResponseResult();
            rs.setStatus(0);
            logger.info("[INFO]--- --- not needed init global params");
            return rs;
        }
        final ReportDesignModel model = getDesignModelFromRuntimeModel(reportId);
        final ReportRuntimeModel runtimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        Map<String, Map<String, List<Map<String, String>>>> datas = Maps.newConcurrentMap();
        Map<String, String> params = Maps.newHashMap();
        runtimeModel.getContext().getParams().forEach((k, v) -> {
            params.put(k,v == null ? "" : v.toString());
        }); 
//        DataSourceInfo dsInfo = null;
//        try {
//            dsInfo = DataSourceDefineUtil.parseToDataSourceInfo(dsService.getDsDefine(model.getDsId()), 
//                    securityKey);
//        } catch (DataSourceOperationException e1) {
//            logger.error(e1.getMessage(), e1);
//        }
        for (final String areaId : areaIds) {
            ExtendArea area = model.getExtendById(areaId);
            if (area != null && isQueryComp(area.getType())
                    && !area.listAllItems().isEmpty()) {
                Item item = area.listAllItems().values().toArray(new Item[0])[0];
                Cube cube = model.getSchema().getCubes().get(area.getCubeId());
                Cube tmpCube = QueryUtils.transformCube(cube);
                String dimId = item.getOlapElementId();
                Dimension dim = cube.getDimensions().get(dimId);
                if (dim != null) {
                    List<Map<String, String>> values;
                    try {
                        values = Lists.newArrayList();
                        params.remove (dim.getId ());
                        params.put (Constants.LEVEL_KEY, "1");
                        List<Member> members = reportModelQueryService
                                .getMembers(tmpCube, 
                                tmpCube.getDimensions().get(dim.getName()), params, securityKey).get(0);
                        members.forEach(m -> {
                            Map<String, String> tmp = Maps.newHashMap();
                            tmp.put("value", m.getUniqueName());
                            tmp.put("text", m.getCaption());
                            if (dim.getLevels ().size () <= 1 ) {
                                tmp.put ("isLeaf", "1");
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
//                        List<Map<String, String>> values = 
//                                QueryUtils.getMembersWithChildrenValue(members, tmpCube, dsInfo, Maps.newHashMap());
                        Map<String, List<Map<String, String>>> datasource = Maps.newHashMap();
                        datasource.put("datasource", values);
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
    public ResponseResult getReport(@PathVariable("reportId") String reportId,
            HttpServletRequest request) {
        long begin = System.currentTimeMillis();
        logger.info("[INFO] --- --- begin query report model");
        ReportDesignModel model = null;
        try {
            model = this.getDesignModelFromRuntimeModel(reportId); // reportModelCacheManager.getReportModel(reportId);
        } catch (CacheOperationException e1) {
            logger.info("[INFO]--- --- can't not get report form cache", e1.getMessage());
            return ResourceUtils.getErrorResult(e1.getMessage(), ResponseResult.FAILED);
        }
//        reportModelCacheManager.loadReportModelToCache(reportId);
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
    @RequestMapping(value = "/{reportId}/report_vm", method = { RequestMethod.GET },
            produces = "text/html;charset=utf-8")
    public String queryVM(@PathVariable("reportId") String reportId, HttpServletRequest request,
            HttpServletResponse response) {
        long begin = System.currentTimeMillis();
        ReportDesignModel model = null;
        String reportPreview = request.getParameter("reportPreview");
        ReportRuntimeModel runtimeModel = null;
        try {
            if (!StringUtils.isEmpty(reportPreview) && Boolean.valueOf(reportPreview)) {
                model = DeepcopyUtils.deepCopy(reportModelCacheManager.getReportModel(reportId));
                model.setPersStatus(false);
                    // 这里需要重新生成session id 并且放到cookie中
                    // 这里需要将此处逻辑抽象到工具类中
//                    String sessionId = UuidGeneratorUtils.generate();
//                    ContextManager.cleanSessionId();
//                    ContextManager.setSessionId(sessionId);
//                    Cookie sessionIdCookie = new Cookie(Constants.SESSION_ID,sessionId);
//                    sessionIdCookie.setPath(Constants.COOKIE_PATH);
//                    response.addCookie(sessionIdCookie);
            } else {
                model = reportDesignModelService.getModelByIdOrName(reportId, true);
                model.setPersStatus(true);
//                    runtimeModel = reportModelCacheManager.loadRunTimeModelToCache(reportId);
            }
            runtimeModel = new ReportRuntimeModel(reportId);
//            if (model == null) {
//                throw new RuntimeException("未加载到必须的报表模型");
//            }
            runtimeModel.init(model, true);
        } catch (CacheOperationException e1) {
            logger.info("[INFO]--- ---Fail in loading release report model into cache. ", e1);
            throw new IllegalStateException();
        }
        // modify by jiangyichao at 2014-10-10 
        // 将url参数添加到全局上下文中
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            runtimeModel.getContext().put(paramName, request.getParameter(paramName));
        }
        // 添加cookie内容
        runtimeModel.getContext().put(HttpRequest.COOKIE_PARAM_NAME, request.getHeader("Cookie"));
        
//        if (model == null) {
//                logger.info("[INFO]--- --- can't get model form cache, please check it!");
//            return "";
//        }
//        reportModelCacheManager.updateReportModelToCache(reportId, model);
        /**
         * 依据查询请求，根据报表参数定义，增量添加报表区域模型参数
         */
        Map<String, Object> tmp = 
                QueryUtils.resetContextParam(request, model);
        runtimeModel.getContext().getParams().putAll(tmp);
        reportModelCacheManager.updateRunTimeModelToCache(reportId, runtimeModel);
        StringBuilder builder = buildVMString(reportId, request, response, model);
        logger.info("[INFO] query vm operation successfully, cost {} ms", (System.currentTimeMillis() - begin));
        return builder.toString();
    }

    /**
     * @param reportId
     * @param response
     * @param model
     * @return StringBuilder
     */
    private StringBuilder buildVMString(String reportId, HttpServletRequest request,
            HttpServletResponse response, ReportDesignModel model) {
        // TODO 临时方案，以后前端做
        String vm = model.getVmContent();
        String js = "<script type='text/javascript'>" + "\r\n" + "        (function(NS) {" + "\r\n"
                + "            NS.xui.XView.start(" + "\r\n"
                + "                'di.product.display.ui.LayoutPage'," + "\r\n"
                + "                {" + "\r\n" + "                    externalParam: {" + "\r\n"
                + "                    'reportId':'"
                + reportId
                + "','phase':'dev'},"
                + "\r\n"
                + "                    globalType: 'PRODUCT',"
                + "\r\n"
                + "                    diAgent: '',"
                + "\r\n"
                + "                    reportId: '"
                + reportId
                + "',"
                + "\r\n"
                + "                    webRoot: '/silkroad',"
                + "\r\n"
                + "                    phase: 'dev',"
                + "\r\n"
                + "                    serverTime: ' " + new Date().getTime() + "',"
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
                + "\r\n" + "    </script>" + "\r\n";
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>");
        builder.append("<html>");
        builder.append("<head>");
        builder.append("<meta content='text/html' 'charset=UTF-8'>");
        final String theme = model.getTheme();
        builder.append("<link rel='stylesheet' href='/silkroad/asset/" + theme+ "/css/-di-product-min.css'/>");
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
    
    @RequestMapping(value = "/{reportId}/report_json", method = { RequestMethod.GET },
            produces = "text/plain;charset=utf-8")
    public String queryJson(@PathVariable("reportId") String reportId, HttpServletRequest request,
            HttpServletResponse response) {
        long begin = System.currentTimeMillis();
        ReportDesignModel model = null;
        try {
            model = this.getDesignModelFromRuntimeModel(reportId);
        } catch (CacheOperationException e) {
            logger.info("[INFO]--- ---There are no such model in cache. Report Id: " + reportId, e);
            throw new IllegalStateException();
        }
        if (model == null) {
            logger.info("[INFO]--- --- can't get model form cache, please check it!");
            return "";
        }
        String json = model.getJsonContent();
        logger.info(json);
        response.setCharacterEncoding("utf-8");
        logger.info("[INFO] query json operation successfully, cost {} ms", (System.currentTimeMillis() - begin));
        return json;
    }
    
    /**
     * 
     * @param reportId
     * @param request
     * @return ResponseResult
     */
    @RequestMapping(value = "/{reportId}/runtime_model", method = { RequestMethod.POST })
    public ResponseResult initRunTimeModel(@PathVariable("reportId") String reportId,
            HttpServletRequest request) {
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
//        reportModelCacheManager.updateReportModelToCache(reportId, model);
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
    public ResponseResult updateContext(@PathVariable("reportId") String reportId,
            HttpServletRequest request) {
        long begin = System.currentTimeMillis();
        logger.info("[INFO]------begin update global runtime context");
        Map<String, String[]> contextParams = request.getParameterMap();
        ReportRuntimeModel runTimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        // modify by jiangyichao at 2014-11-06 对时间条件进行特殊处理
        if (contextParams.get(Constants.IN_EDITOR) != null 
                || runTimeModel.getContext().getParams().containsKey(Constants.IN_EDITOR)) {
//            Map<String, Object> newParams = Maps.newHashMap();
//            Map<String, Object> oldParams = runTimeModel.getContext().getParams(); 
//            for (String key : oldParams.keySet()) {
//                String value = oldParams.get(key).toString();
//                if (!(value.contains("start") && value.contains("end"))) {
//                    newParams.put(key, value);
//                }
//            }
//            newParams.put(Constants.IN_EDITOR, true);
//            runTimeModel.getContext().reset();
//            runTimeModel.getLocalContext().forEach((key, value) -> {
//                value.reset();
//                value.setParams(newParams);
//            });
        }
        
        
        ReportDesignModel model = runTimeModel.getModel(); 
        //reportModelCacheManager.getReportModel(reportId);
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
                	if (realValue.contains("start") && realValue.contains("end") && realValue.contains("granularity")) {             		
                		for (Entry<String, Object> tmpEntry : runTimeModel.getContext().getParams().entrySet()) {
                			String tmpStr = String.valueOf(tmpEntry.getValue());
                			if (tmpStr.contains("start") || tmpStr.contains("end") || tmpStr.contains("granularity")) {
                				runTimeModel.getContext().removeParam(tmpEntry.getKey());
                				Map<String, QueryContext> localContext = runTimeModel.getLocalContext ();
                				localContext.forEach ((k, v) -> {
                				    v.reset ();
                				}); 
                				break;
                			}
                		}
                	}
                    runTimeModel.getContext().put(getRealKey(model, key), realValue);
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
            /**
             * 修正报表配置的参数的值
             * 
             */
            
        }
//        for (Map.Entry<String, Object> entry : runtimeParams.entrySet ()) {
//            runTimeModel.getContext ().put (entry.getKey (), entry.getValue ());
//        }
//        Map<String, Object> runtimeParams = QueryUtils.resetContextParam (request, model);
        reportModelCacheManager.updateRunTimeModelToCache(reportId, runTimeModel);
        ResponseResult rs = ResourceUtils.getResult("Success Getting VM of Report",
                "Fail Getting VM of Report", "");
        logger.info("[INFO]current context params status {}", runTimeModel.getContext().getParams());
        logger.info("[INFO]successfully update global context, cost {} ms", (System.currentTimeMillis() - begin));
        return rs;
    }
    
    private String getParamRealValue(String realValue) {
    	// modify by yichao.jiang  接收url传递过来的时间参数，并进行转换
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
     * TODO
     * 重新获取日期的开始和结束 ，add by jiangyichao
     */
    private String genNewStartAndEnd(String timeValue) {
    	String start;
    	String end;
    	String result = null;
//    	String yearStart;
//    	String yearEnd;
//    	String monthStart;
//    	String monthEnd;
//    	String dayStart;
//    	String dayEnd;
//    	Calendar cal;
//    	String [] startDayOfQuarter = {"0101" , "0401" , "0701", "1001"};
//    	String [] endDayOfQuarter = {"0331" , "0630" , "0930", "1231"};
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
//	            	start = start + "0101";
//	            	end = end + "1231";
	            	break;
	            // 季度
	            case "Q":
	            	time = TimeUtils.getTimeCondition(start, end, TimeType.TimeQuarter);
//	            	String [] tmpStart = start.split("Q");
//	            	yearStart = tmpStart[0];
//	            	String quarterStart = tmpStart[1];
//	            	String [] tmpEnd = end.split("Q");
//	            	yearEnd = tmpEnd[0];
//	            	String quarterEnd = tmpEnd[1];
//	            	
//	            	start = yearStart + startDayOfQuarter[Integer.valueOf(quarterStart)-1];
//	            	end = yearEnd + endDayOfQuarter[Integer.valueOf(quarterEnd)-1];
	            	break;
	            // 月份
	            case "M":
	            	time = TimeUtils.getTimeCondition(start, end, TimeType.TimeMonth);
//	            	yearEnd = end.substring(0, 4);
//	            	monthEnd = end.substring(4);
//	            	cal = Calendar.getInstance();
//	            	cal.set(Calendar.YEAR, Integer.valueOf(yearEnd));
//	            	cal.set(Calendar.MONTH, Integer.valueOf(monthEnd) - 1);
//	            	dayEnd = String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
//	            	start = start + "01";
//	            	end = yearEnd + monthEnd + dayEnd;
	            	break;
	            // 星期
	            case "W":
	            	time = TimeUtils.getTimeCondition(start, end, TimeType.TimeWeekly);
//	            	cal = Calendar.getInstance();
//	            	yearStart = start.substring(0, 4);
//	            	monthStart = start.substring(4, 6);
//	            	dayStart = start.substring(6);
//	            	cal.set(Calendar.YEAR, Integer.valueOf(yearStart));
//	            	cal.set(Calendar.MONTH, Integer.valueOf(monthStart) -1 );
//	            	cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dayStart));
//	            	cal.add(Calendar.DAY_OF_MONTH, 6);
//	            	int month = cal.get(Calendar.MONTH) + 1;
//	            	if (month < 10 ) {
//	            		end = "" + cal.get(Calendar.YEAR) + "0" + month + cal.get(Calendar.DAY_OF_MONTH);
//	            	} else {
//	            		end = "" + cal.get(Calendar.YEAR) + month + cal.get(Calendar.DAY_OF_MONTH);
//	            	}
	            	break;
	            // 天
	            case "D":
	            	time = TimeUtils.getTimeCondition(start, end, TimeType.TimeDay);
	            	break;
	            default:
	            	break;
            }
//            cal = Calendar.getInstance();
//            int yearNow = cal.get(Calendar.YEAR);
//            int monthNow = cal.get(Calendar.MONDAY) + 1 ;
//            int dayNow = cal.get(Calendar.DAY_OF_MONTH);
//            String dateNow = "" + yearNow;
//            if (monthNow >= 10 ) {
//            	dateNow = dateNow + "0" + monthNow + dayNow;
//            } else {
//            	dateNow = dateNow + monthNow + dayNow;
//            }
//            if (end.compareTo(dateNow) > 0) {
//            	end = dateNow;
//            }
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

    private Map<String, Object> updateLocalContextAndReturn(ReportRuntimeModel runTimeModel,
            String areaId, Map<String, String[]> contextParams) {
        
        /**
         * 查询区域的时候，会按照当前的参数更新区域上下文
         */
        QueryContext localContext = runTimeModel.getLocalContextByAreaId(areaId);
//        localContext.reset ();
        
        
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
        /**
         * 仅保留一个时间条件
         */
//        Iterator<String> it = localParams.keySet().iterator();
//        while (it.hasNext()) {
//                String key = it.next();
//                String value = localParams.get(key).toString();
//                if (value.contains("start") && value.contains("end")) {
//                    it.remove();
//                }
//        }
//        for (String key : localParams.keySet()) {
//                String value = localParams.get(key).toString();
//                if (value.contains("start") && value.contains("end")) {
//                    localParams.remove(key);
//                }
//        }
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
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}", method = {RequestMethod.POST})
    public ResponseResult queryArea(@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId, HttpServletRequest request) {
        long begin = System.currentTimeMillis();
        logger.info("[INFO] begin query data");
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
        
        logger.info ("[INFO] --- --- --- --- --- ---params with context is : " + areaContext.getParams ());
        
        /**
         * 5. 生成查询动作QueryAction
         */
        QueryAction action = null;
        if (targetArea.getType() == ExtendAreaType.CHART 
                || targetArea.getType() == ExtendAreaType.LITEOLAP_CHART) {
            String[] indNames = new String[0];
            if (StringUtils.hasText(request.getParameter("indNames"))) {
                indNames = request.getParameter("indNames").split(",");
            }
            try {
                String topSetting = request.getParameter(Constants.TOP);
                if (!StringUtils.isEmpty(topSetting)) {
                    model.getExtendById(areaId).getLogicModel().
                        setTopSetting(GsonUtils.fromJson(topSetting, MeasureTopSetting.class));
                }
                action = queryBuildService.generateChartQueryAction(model, areaId, 
                            areaContext.getParams(), indNames, runTimeModel);
                if (action == null) {
                    return ResourceUtils.getErrorResult ("该区域未包含任何维度信息", 1);
                }
                action.setChartQuery(true);
                boolean timeLine = isTimeDimOnFirstCol(model, targetArea, action);
                //TODO to be delete
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
        /**
         * 6. 完成查询
         */
        ResultSet result;
        try {
            if (action == null || CollectionUtils.isEmpty(action.getRows())
                    || CollectionUtils.isEmpty(action.getColumns())) {
                return ResourceUtils.getErrorResult("单次查询至少需要包含一个横轴、一个纵轴元素", 1);
            }
            if (targetArea.getType() == ExtendAreaType.PLANE_TABLE) {
            	// TODO 构建分页信息
            	PageInfo pageInfo = new PageInfo();
                result = reportModelQueryService.queryDatas(model, action, true, areaContext.getParams(), pageInfo, securityKey);
            } else {
             result = reportModelQueryService.queryDatas(model, action,true, true, areaContext.getParams(), securityKey);
            }
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
        
        /**
         * 7. 对返回结果进行处理，用于表、图显示
         */
        ResponseResult rs = queryDataResourceUtils.parseQueryResultToResponseResult(
    			runTimeModel, targetArea, result, areaContext, action);
        areaContext.getQueryStatus().add(result);
        
        // 清除当前request中的请求参数，保证areaContext的参数正确
        resetAreaContext(areaContext, request);
        resetContext(runTimeModel.getLocalContext().get(areaId), request);
        reportModelCacheManager.updateAreaContext(targetArea.getId(), areaContext);
        runTimeModel.updateDatas(action, result);
        reportModelCacheManager.updateRunTimeModelToCache(reportId, runTimeModel);
        logger.info("[INFO] successfully query data operation. cost {} ms", (System.currentTimeMillis() - begin));
        return rs;
    }

    private FormatModel getFormatModel(ReportDesignModel model, ExtendArea targetArea) {
        if (targetArea.getType () == ExtendAreaType.TABLE) {
            return targetArea.getFormatModel();
        }
        return model.getExtendById (targetArea.getReferenceAreaId ()).getFormatModel ();
    }

    private boolean isPieChart(Map<String, String> chartType) {
        for (String chart : chartType.values()) {
            if (ChartShowType.PIE.name().toLowerCase().equals(chart)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTimeDimOnFirstCol(ReportDesignModel model,
            ExtendArea targetArea, QueryAction action) {
        if (action.getRows ().isEmpty ()) {
            return false;
        }
        Item item = action.getRows().keySet().toArray(new Item[0])[0];
        OlapElement element = ReportDesignModelUtils.getDimOrIndDefineWithId(model.getSchema(),
                targetArea.getCubeId(), item.getOlapElementId());
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
    private ExtendAreaContext getAreaContext(String areaId,
            HttpServletRequest request, ExtendArea targetArea,
            ReportRuntimeModel runTimeModel) {
        Map<String, Object> queryParams = updateLocalContextAndReturn(runTimeModel, areaId, request.getParameterMap());
        runTimeModel.getLocalContextByAreaId(areaId).getParams().putAll(queryParams);
        ExtendAreaContext areaContext = reportModelCacheManager.getAreaContext(targetArea.getId());
        areaContext.getParams().clear();
        areaContext.getParams().putAll(queryParams);
        return areaContext;
    }

    /**
     * 获取扩展区域中定义的chartType
     * @param targetArea ExtendArea
     * @return SeriesUnitType
     */
    private Map<String, String> getChartTypeWithExtendArea(ReportDesignModel model, ExtendArea targetArea) {
        Map<String, String> chartTypes = Maps.newHashMap();
        if (targetArea.getType() == ExtendAreaType.LITEOLAP_CHART) {
            chartTypes.put("null", SeriesUnitType.LINE.name());
            return chartTypes;
//                return new String[]{SeriesUnitType.LINE.name()};
        }
//            List<String> types = Lists.newArrayList();
        targetArea.listAllItems().values().stream().filter(item -> {
            return item.getPositionType() == PositionType.Y 
                    || item.getPositionType() == PositionType.CAND_IND;
        }).forEach(item -> {
            OlapElement element = ReportDesignModelUtils.getDimOrIndDefineWithId(model.getSchema(),
                    targetArea.getCubeId(), item.getOlapElementId());
            Object chartType = item.getParams().get("chartType");
            if (chartType == null) {
                chartTypes.put(element.getUniqueName(), SeriesUnitType.COLUMN.name());
            } else {
                chartTypes.put(element.getUniqueName(), chartType.toString());
            }
        });
//            .forEach(str -> {
//                if (StringUtils.isEmpty(str)) {
//                    types.add(SeriesUnitType.COLUMN.name());
//                } else {
//                    types.add(str.toString().toUpperCase());
//                }
//            });
        return chartTypes;
    }



    
    /**
     * 选中行
     * 
     * @param reportId
     *            报表id
     * @param areaId
     *            区域id
     * @param request
     *            请求对象
     * @return 操作结果
     */
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/selected_row/", method = { RequestMethod.POST })
    public ResponseResult selectRow(@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId, HttpServletRequest request) {
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
     * @param reportId
     *            报表id
     * @param areaId
     *            区域id
     * @param request
     *            请求对象
     * @return 操作结果
     */
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/selected_row/{rowId}", method = {RequestMethod.DELETE})
    public ResponseResult deselectRow(@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId, @PathVariable("rowId") String rowId, 
            HttpServletRequest request) {
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
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/drill", method = {RequestMethod.POST})
    public ResponseResult drillDown(@PathVariable("reportId") String reportId, 
            @PathVariable("areaId") String areaId, HttpServletRequest request) {
        long begin = System.currentTimeMillis();
        logger.info("[INFO]------ begin drill down operation");
        String uniqueName = request.getParameter("uniqueName");
        boolean isRoot = uniqueName.startsWith ("@") && uniqueName.endsWith ("@");
        uniqueName = uniqueName.replace ("@", "");
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
        
        QueryAction action = null; //(QueryAction) runTimeModel.getContext().get(uniqueName);
        String drillTargetUniqueName = null;
        Map<String, Object> queryParams = updateLocalContextAndReturn(runTimeModel, areaId, Maps.newHashMap ());
        Item row = null;
        if (uniqueName.contains (",")) {
//            isRoot = true;
            String[] uniqueNameArray = uniqueName.split (",");
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
            reportModelCacheManager.getAreaContext(areaId).getParams().putAll(queryParams);
            action = queryBuildService.generateTableQueryAction(model, areaId, queryParams);
        } else {
            /**
             * 找到下载的维度节点
             */
            String[] uniqNames = com.baidu.rigel.biplatform.ac.util.
                    DataModelUtils.parseNodeUniqueNameToNodeValueArray(uniqueName);
            if (uniqNames == null || uniqNames.length == 0) {
                String msg = String.format("Fail in drill down. UniqueName param is empty.");
                logger.error(msg);
                return ResourceUtils.getErrorResult(msg, 1);
            }
            drillTargetUniqueName = uniqNames[uniqNames.length - 1];
            logger.info("[INFO] drillTargetUniqueName : {}", drillTargetUniqueName);
//            isRoot = drillTargetUniqueName.toLowerCase().contains("all");
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
            String[] drillName = new String[]{drillTargetUniqueName};
            oriQueryParams.putAll(request.getParameterMap());
            /**
             * update context
             */
            queryParams = updateLocalContextAndReturn(runTimeModel, areaId, oriQueryParams);
            queryParams.put(row.getOlapElementId(), drillName);
            
            // TODO 仔细思考一下逻辑
            reportModelCacheManager.getAreaContext(areaId).getParams().putAll(queryParams);
            action = queryBuildService.generateTableQueryAction(model, areaId, queryParams);
            /**
             * 把下钻的值存下来
             * TODO 临时放在这里，需要重新考虑
             */
            if (!isRoot) {
                action.getDrillDimValues().put(row, drillTargetUniqueName);
            } else {
                action.getDrillDimValues().remove (row);
            }
        }
//        runTimeModel.getContext().put(uniqueName, action);
        runTimeModel.setLinkedQueryAction (action);
        
        /** 
         * TODO 针对参数映射修改，将当前下钻条件设置到对应参数上
         */
        final String[] tmp = MetaNameUtil.parseUnique2NameArray (drillTargetUniqueName);
        final String elementId = row.getOlapElementId ();
        if (!MetaNameUtil.isAllMemberUniqueName (drillTargetUniqueName)) {
            for (ReportParam p : model.getParams ().values ()) {
                if (p.getElementId ().equals (elementId)) {
                    queryParams.put (p.getName (), tmp[tmp.length - 1]);
                }
            };
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
        Cube cube = null;
        
        try {
            cube = model.getSchema().getCubes().get(targetArea.getCubeId());
            drillDim = cube.getDimensions ().get (elementId);
            table = queryBuildService.parseToPivotTable(cube, result.getDataModel());
        } catch (PivotTableParseException e) {
            logger.info(e.getMessage(), e);
            return ResourceUtils.getErrorResult("Fail in parsing result. ", 1);
        }
        ExtendAreaContext areaContext = reportModelCacheManager.getAreaContext(targetArea.getId());
        if (targetArea.getType() == ExtendAreaType.TABLE 
                || targetArea.getType() == ExtendAreaType.LITEOLAP_TABLE) {
            /**
             * TODO 考虑一下这样的逻辑是否应该放到resource中
             */
            List<Map<String, String>> mainDims = areaContext.getCurBreadCrumPath ();
            DataSourceDefine define = null;
            DataSourceInfo dsInfo = null;
             try {
                define = dsService.getDsDefine (model.getDsId ());
                dsInfo = DataSourceConnectionServiceFactory
                        .getDataSourceConnectionServiceInstance (define.getDataSourceType ().name ())
                        .parseToDataSourceInfo (define, securityKey);
            } catch (DataSourceOperationException | DataSourceConnectionException e) {
                logger.error (e.getMessage (), e);
            }
            
             boolean  remove = false;
            if (mainDims.size () > 0  && !isRoot
                    && !mainDims.get (mainDims.size () -1).values ().toArray ()[0].equals (drillTargetUniqueName)) {
                Iterator<Map<String, String>> it = mainDims.iterator ();
                while (it.hasNext ()) {
                    if (remove) {
                        it.next ();
                        it.remove ();
                        continue;
                    }
                    Map<String, String> tmpMap = it.next ();
                    if (tmpMap.values ().toArray ()[1].equals (drillTargetUniqueName)) {
                        remove = true;
                    }
                }
            } 
            if (!remove && drillTargetUniqueName != null && !drillTargetUniqueName.toLowerCase().contains("all")) {
                Map<String, String> dims3 = Maps.newHashMap();
                dims3.put("uniqName", drillTargetUniqueName);
                String showName = genShowName(drillTargetUniqueName, drillDim, cube, dsInfo, queryParams);
                if (isRoot) {
                    showName = areaContext.getCurBreadCrumPath().get (0).get("showName");
                }
                dims3.put("showName", showName);
                mainDims.add(dims3);
//                drillTargetUniqueName = MetaNameUtil.getParentUniqueName(drillTargetUniqueName);
            } 
            if (isRoot) {
                Iterator<Map<String, String>> it = mainDims.iterator ();
                it.next ();
                while (it.hasNext ()) {
                    it.next ();
                    it.remove ();
                }
//                Map<String, String> root = areaContext.getCurBreadCrumPath();
//                mainDims.add(root);
            }
            
//            List<Map<String, String>> root = areaContext.getCurBreadCrumPath();
//            mainDims.addAll(root);
//            Collections.reverse(mainDims);
            areaContext.setCurBreadCrumPath (mainDims);
            resultMap.put("mainDimNodes", mainDims);
            areaContext.getParams ().put ("bread_key", mainDims);
//            runTimeModel.getContext().put("bread_key", mainDims);
        } 
        areaContext.getQueryStatus().add(result);
        // 更新局部区域参数，避免漏掉当前请求查询的
        
        // 清除展开、折叠方式下钻查询历史纪录
        runTimeModel.getDrillDownQueryHistory ().clear ();
        
        reportModelCacheManager.updateAreaContext(targetArea.getId(), areaContext);
        reportModelCacheManager.updateRunTimeModelToCache(reportId, runTimeModel);
        DataModelUtils.decorateTable(getFormatModel (model, targetArea), table);
        resultMap.put("pivottable", table);
        setTableResultProperty (reportId, table, resultMap);
        ResponseResult rs = ResourceUtils.getResult("Success Getting VM of Report",
                "Fail Getting VM of Report", resultMap);
        logger.info("[INFO]Successfully execute drill operation. cost {} ms", (System.currentTimeMillis() - begin));
        return rs;
    }

    private void setTableResultProperty(String reportId, PivotTable table, Map<String, Object> resultMap) {
        resultMap.put("rowCheckMin", 1);
        resultMap.put("rowCheckMax", 5);
        resultMap.put("reportTemplateId", reportId);
        if (table.getActualSize () <= 1) {
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
    private String genShowName(String drillTargetUniqueName, 
        Dimension drillDim, Cube cube, DataSourceInfo dsInfo, Map<String, Object> params) {
        String showName = drillTargetUniqueName.substring(drillTargetUniqueName.lastIndexOf("[") + 1, 
                drillTargetUniqueName.length() - 1);
        if (showName.contains("All_")) {
            showName = showName.replace("All_", "全部");
            showName = showName.substring(0, showName.length() - 1);
        } else if (drillDim.getType () == DimensionType.CALLBACK) {
            String[] nameArray = MetaNameUtil.parseUnique2NameArray (drillTargetUniqueName);
            Level l = drillDim.getLevels ().values ().toArray (new Level[0])[nameArray.length - 2];
            Map<String, String> tmp = Maps.newHashMap ();
            params.forEach ((k, v) -> {
                tmp.put (k, v.toString ());
            });
            logger.info ("in callback dim show name generate");
            return l.getMembers (QueryUtils.transformCube (cube), dsInfo, tmp).get(0).getCaption();
        }
        return showName;
    }
    
    /**
     * 展开下钻操作
     * 
     * @param reportId
     *            报表id
     * @param request
     *            请求对象
     * @param rowIndex 当前点击行行号
     * @param colIndex 列索引
     * @return 下钻操作 操作结果
     */
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/drill/{type}", method = {RequestMethod.POST})
    public ResponseResult drillDown(@PathVariable("reportId") String reportId,
            @PathVariable("areaId") String areaId,  @PathVariable("type") String type,
            HttpServletRequest request) throws Exception {
        long begin = System.currentTimeMillis();
        logger.info("begin drill down opeartion");
//        // 解析查询条件条件 来自于rowDefine
        String condition = request.getParameter("lineUniqueName");
        
//        String uniqueName = request.getParameter("uniqueName");
        
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
            model = DeepcopyUtils.deepCopy (runTimeModel.getModel ());
        } catch (CacheOperationException e) {
            logger.info("[INFO] Can not find such model in cache. Report Id: " + reportId, e);
            return ResourceUtils.getErrorResult("不存在的报表，ID " + reportId, 1);
        }
        ExtendArea targetArea = model.getExtendById(areaId);
        /**
         * TODO 合并当前的全局上下文，需要重构下，整理上下文处理逻辑
         */
//        QueryAction previousAction = runTimeModel.getPreviousQueryAction(areaId);
        ExtendAreaContext areaContext = reportModelCacheManager.getAreaContext(targetArea.getId());
        
        ResultSet previousResult = areaContext.getQueryStatus().getLast();
        LogicModel targetLogicModel = null;
        String logicModelAreaId = areaId;
        LogicModel logicModel = targetArea.getLogicModel ();
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
        String[] uniqNames = com.baidu.rigel.biplatform.ac.util.
                DataModelUtils.parseNodeUniqueNameToNodeValueArray(condition);
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
            model.getParams ().values ().forEach (p -> {
                if (p.getElementId ().equals (rowAhead.getOlapElementId())) {
                    String[] tmp = MetaNameUtil.parseUnique2NameArray (rowAheadUniqueName);
                    queryParams.put (p.getName (), tmp[tmp.length - 1]);
                }
            });
        }
        
        Item row = store.get(dimName);
        queryParams.put(row.getOlapElementId(), drillTargetUniqueName);
        model.getParams ().values ().forEach (p -> {
            if (p.getElementId ().equals (row.getOlapElementId())) {
                String[] tmp = MetaNameUtil.parseUnique2NameArray (drillTargetUniqueName);
                queryParams.put (p.getName (), tmp[tmp.length - 1]);
            }
        });
        QueryAction action = queryBuildService.generateTableQueryActionForDrill(model,
                areaId, queryParams, targetIndex);
        
        ResultSet result;
        try {
            result = reportModelQueryService.queryDatas(model, action, true, false, queryParams, securityKey);
        } catch (DataSourceOperationException | QueryModelBuildException | MiniCubeQueryException e1) {
            logger.error(e1.getMessage(), e1);
            return ResourceUtils.getErrorResult("查询出错", 1);
        } 
        PivotTable table = null;
        Map<String, Object> resultMap = Maps.newHashMap();
//        ResultSet previousResult = runTimeModel.getPreviousQueryResult();
        int rowNum = this.getRowNum(previousResult, condition);
        try {
            // 查询下钻的数据
            Cube cube = model.getSchema().getCubes().get(targetArea.getCubeId());
            if (type.equals("expand")) {
//                ResultSet result = reportModelQueryService.queryDatas(model, action, true);
                logger.info ("[INFO] --- --- --- ---" + result.getDataModel ());
                runTimeModel.getDrillDownQueryHistory ().put (condition, 
                        new ReportRuntimeModel.DrillDownAction(action, rowNum));
                DataModel newDataModel = DataModelUtils.merageDataModel(previousResult.getDataModel(), 
                        result.getDataModel(), rowNum);
                table = DataModelUtils.transDataModel2PivotTable(cube, newDataModel, false, 0, false);
                result.setDataModel(newDataModel);
                /**
                 * TODO 这里重新生成当前条件对应的action，而不是下钻使用的action，为的是记录下当前表的结果
                 * 
                 */
                QueryContext previousContext = runTimeModel.getLocalContextByAreaId(areaId);
                QueryAction recordAction = queryBuildService.generateTableQueryAction(model,
                        areaId, previousContext.getParams());
                runTimeModel.updateDatas(recordAction, result);
            } else { //上卷或者折叠操作
                runTimeModel.getDrillDownQueryHistory ().remove (condition);
                DataModel newModel = DataModelUtils.removeDataFromDataModel(previousResult.getDataModel(), rowNum);
                table = DataModelUtils.transDataModel2PivotTable(cube, newModel, false, 0, false);
                result = new ResultSet();
                result.setDataModel(newModel);
                /**
                 * TODO 这里重新生成当前条件对应的action，而不是下钻使用的action，为的是记录下当前表的结果
                 * 
                 */
                QueryContext previousContext = runTimeModel.getLocalContextByAreaId(areaId);
                QueryAction recordAction = queryBuildService.generateTableQueryAction(model,
                        areaId, previousContext.getParams());
                runTimeModel.updateDatas(recordAction, result);
            }  
            areaContext.getQueryStatus().add(result);
            reportModelCacheManager.updateAreaContext(targetArea.getId(), areaContext);
            reportModelCacheManager.updateRunTimeModelToCache(reportId, runTimeModel);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        
        if (targetArea.getType() == ExtendAreaType.TABLE || targetArea.getType() == ExtendAreaType.LITEOLAP_TABLE) {
                // TODO 临时解决方案，此处应将查询条件设置到QuestionModel中
            DataModelUtils.decorateTable(getFormatModel (model, targetArea), table);
//            resultMap.put("rowCheckMin", 1);
//            resultMap.put("rowCheckMax", 5);
            if (targetArea.getType () == ExtendAreaType.LITEOLAP_TABLE) {
                logicModel = model.getExtendAreas ().get (targetArea.getReferenceAreaId ()).getLogicModel ();
            }
            logger.info ("[INFO] row length = " + logicModel.getRows ().length);
            if (logicModel.getRows ().length >= 2) {
                Object breadCrum = areaContext.getParams ().get("bread_key");
                if (breadCrum == null) {
                    List<Map<String, String>> tmp = Lists.newArrayList();
                    if (areaContext.getCurBreadCrumPath() != null  && !areaContext.getCurBreadCrumPath().isEmpty()) {
                        tmp.addAll(areaContext.getCurBreadCrumPath());
                        breadCrum = tmp;
                    }
                }
                if (breadCrum != null) {
                    resultMap.put("mainDimNodes", breadCrum);
                }
            } else {
                resultMap.remove ("mainDimNodes");
            }
            resultMap.put("pivottable", table);
            setTableResultProperty (reportId, table, resultMap);
        } 
        ResponseResult rs = ResourceUtils.getResult("Success Getting VM of Report",
                "Fail Getting VM of Report", resultMap);
        logger.info("[INFO]Successfully execute drill down operation, cost {} ms",
                (System.currentTimeMillis() - begin));
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
     * @return
     */
    @RequestMapping(value = "/runtime/extend_area/{areaId}/dims/{dimId}/members",
            method = {RequestMethod.POST})
    public ResponseResult queryMembers(@PathVariable("areaId") String areaId, 
        @PathVariable("dimId") String dimId, HttpServletRequest request) throws Exception {
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
        members.forEach(tmpMembers ->{
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
     * @return ResponseResult
     */
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/sort", method = {RequestMethod.POST , RequestMethod.GET})
    public ResponseResult sortByMeasure(@PathVariable("reportId")String reportId,
            @PathVariable("areaId")String areaId,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
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
        ExtendAreaContext context = this.reportModelCacheManager.getAreaContext(areaId);
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
        context.getQueryStatus().add(rs);
        PivotTable table = null;
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            Cube cube = reportModel.getSchema().getCubes().get(targetArea.getCubeId());
            table = queryBuildService.parseToPivotTable(cube, model);
        } catch (PivotTableParseException e) {
            logger.error(e.getMessage(), e);
            return ResourceUtils.getErrorResult("Fail in parsing result. ", 1);
        }
        DataModelUtils.decorateTable(reportModel.getExtendById(areaId).getFormatModel(), table);
        if (table.getDataSourceColumnBased().size() == 0) {
            ResponseResult tmp = new ResponseResult();
            tmp.setStatus(1);
            tmp.setStatusInfo("未查到任何数据");
            return tmp;
        } else {
            resultMap.put("pivottable", table);
        }
        setTableResultProperty (reportId, table, resultMap);
        context.getQueryStatus().add(rs);
        reportModelCacheManager.updateAreaContext(areaId, context);
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
        tmpMembers.forEach(m ->{
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
     * @return ResponseResult
     */
    @RequestMapping(value = "/runtime/extend_area/{areaId}/dims/{dimId}/members/1",
            method = { RequestMethod.POST })
    public ResponseResult updateMembers(@PathVariable("areaId") String areaId, 
            @PathVariable("dimId") String dimId, HttpServletRequest request) throws Exception {
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
            LiteOlapExtendArea liteOlapArea =  (LiteOlapExtendArea) designModel.getExtendById(areaId);
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
     * @param dimId
     * @param model
     * @param selectedDims
     * @param chartAreaId
     * 
     */
    private void updateLocalContext(String dimId, ReportRuntimeModel model, String[] selectedDims,
            String areaId) {
        QueryContext localContext = model.getLocalContextByAreaId(areaId);
        localContext.getParams().put(dimId, selectedDims);
        ExtendAreaContext context = this.reportModelCacheManager.getAreaContext(areaId);
        context.getParams().put(dimId, selectedDims);
        reportModelCacheManager.updateAreaContext(areaId, context);
    }
    
    /**
     * 离线下载请求
     * @param reportId
     * @param areaId
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ResponseResult downloadOffline(@PathVariable("reportId") String reportId, @PathVariable("areaId") String areaId, 
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        long begin = System.currentTimeMillis();
        ResponseResult rs = new ResponseResult();
        logger.info("[INFO]convert data cost : " + (System.currentTimeMillis() - begin) + " ms" );
        return rs;
    }
    
    /**
     * 下载请求
     * @return
     */
    @RequestMapping(value = "/{reportId}/download/{areaId}", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseResult download(@PathVariable("reportId") String reportId, @PathVariable("areaId")String areaId,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        long begin = System.currentTimeMillis();
        ReportDesignModel report  = this.getDesignModelFromRuntimeModel(reportId);
                // reportModelCacheManager.getReportModel(reportId);
        if (report == null) {
            throw new IllegalStateException("未知报表定义，请确认下载信息");
        }
        ExtendArea targetArea = report.getExtendById(areaId);
        Cube cube = report.getSchema().getCubes().get(targetArea.getCubeId());
        ReportRuntimeModel model = reportModelCacheManager.getRuntimeModel(reportId);
        
        ExtendAreaContext areaContext = this.getAreaContext(areaId, request, targetArea, model);
        areaContext.getParams().put(Constants.NEED_LIMITED, false);
        QueryAction action = model.getLinkedQueryAction ();
        if (action == null) {
            action = queryBuildService.generateTableQueryAction(report, areaId, areaContext.getParams());
        }
        if (action != null) {
            action.setChartQuery(false);
        } else {
            throw new RuntimeException("下载失败"); 
        }
        ResultSet queryRs = reportModelQueryService.queryDatas(report, action, true, true,
            areaContext.getParams(), securityKey);
        DataModel dataModel = queryRs.getDataModel();
        final StringBuilder timeRange = new StringBuilder();
        areaContext.getParams().forEach((k, v) -> {
            if (v instanceof String && v.toString().contains("start") 
                && v.toString().contains("end") && v.toString().contains("granularity")) {
                try {
                    JSONObject json = new JSONObject(v.toString());
                    timeRange.append(json.getString("start") + "至"  + json.getString("end"));
                } catch (Exception e) {
                }
            }
        }); 
        logger.info("[INFO]query data cost : " + (System.currentTimeMillis() - begin) + " ms");
        begin = System.currentTimeMillis();
        if (model.getDrillDownQueryHistory () != null && !model.getDrillDownQueryHistory ().isEmpty ()) {
            for (DrillDownAction queryAction : model.getDrillDownQueryHistory ().values ()) {
                ResultSet subData = reportModelQueryService.queryDatas(report, queryAction.action, true, true,
                        areaContext.getParams(), securityKey);
                dataModel = DataModelUtils.merageDataModel (dataModel, subData.getDataModel (), queryAction.rowNum);
            }
        }
        String csvString = DataModelUtils.convertDataModel2CsvString(cube, dataModel);
        logger.info("[INFO]convert data cost : " + (System.currentTimeMillis() - begin) + " ms" );
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/vnd.ms-excel;charset=GBK");
        response.setContentType("application/x-msdownload;charset=GBK");
        final String fileName = report.getName() + timeRange.toString();
        response.setHeader("Content-Disposition", "attachment;filename=" 
                + URLEncoder.encode(fileName, "utf8") + ".csv"); 
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
    
//    @RequestMapping(value = "/test", method = {RequestMethod.POST , RequestMethod.GET})
//    public ResponseResult test(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        System.out.println(request.getParameter("test"));
//        System.out.println(request.getAttribute("test"));
//        return null;
//    }
    
    /**
     * 图形指标切换操作api
     * TODO 目前只支持图形，后续考虑支持表格
     * @param reportId
     * @param areaId
     * @param request
     * @return
     */
    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}/index/{index}", method = {RequestMethod.POST})
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
        if (targetArea.getType() == ExtendAreaType.CHART 
                || targetArea.getType() == ExtendAreaType.LITEOLAP_CHART) {
            String[] indNames = new String[0];
            if (StringUtils.hasText(request.getParameter("indNames"))) {
                indNames = request.getParameter("indNames").split(",");
            }
            try {
                areaContext.getParams().put(Constants.CHART_SELECTED_MEASURE, index);
                action = queryBuildService.generateChartQueryAction(model, areaId, 
                            areaContext.getParams(), indNames, runTimeModel);
                if (action != null) {
                    action.setChartQuery(true);
                }
                //TODO to be delete
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
            result = reportModelQueryService.queryDatas(model, action,
                    true, true, areaContext.getParams(), securityKey);
            
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
            table = queryBuildService.parseToPivotTable(cube, result.getDataModel());
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
                OlapElement element = ReportDesignModelUtils.getDimOrIndDefineWithId(model.getSchema(),
                        targetArea.getCubeId(), item.getOlapElementId());
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
        resetContext(runTimeModel.getLocalContext().get(areaId), request);
        reportModelCacheManager.updateAreaContext(targetArea.getId(), areaContext);
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
     * 依据
     */
    @RequestMapping(value = "/{reportId}/members/{areaId}", method = { RequestMethod.POST })
    public ResponseResult getMemberWithParent(@PathVariable("reportId") String reportId,
            @PathVariable("areaId")String areaId, HttpServletRequest request) {
        long begin = System.currentTimeMillis();
        logger.info("[INFO]--- ---begin init params with report id {}", reportId);
        String currentUniqueName = request.getParameter("uniqueName");
        int level = MetaNameUtil.parseUnique2NameArray (currentUniqueName).length - 1;
        final ReportDesignModel model = getDesignModelFromRuntimeModel(reportId);
        final ReportRuntimeModel runtimeModel = reportModelCacheManager.getRuntimeModel(reportId);
        Map<String, Map<String, List<Map<String, String>>>> datas = Maps.newConcurrentMap();
        Map<String, String> params = Maps.newHashMap();
        runtimeModel.getContext().getParams().forEach((k, v) -> {
            params.put(k,v == null ? "" : v.toString());
        }); 
        ExtendArea area = model.getExtendById(areaId);
        if (area != null && isQueryComp(area.getType())
                && !area.listAllItems().isEmpty()) {
            Item item = area.listAllItems().values().toArray(new Item[0])[0];
            Cube cube = model.getSchema().getCubes().get(area.getCubeId());
            Cube tmpCube = QueryUtils.transformCube(cube);
            String dimId = item.getOlapElementId();
            Dimension dim = cube.getDimensions().get(dimId);
            if (dim != null) {
                List<Map<String, String>> values;
                try {
                    values = Lists.newArrayList();
                    params.remove (dim.getId ());
                    List<Member> members = reportModelQueryService
                            .getMembers(tmpCube, currentUniqueName, params, securityKey);
                    members.forEach(m -> {
                        Map<String, String> tmp = Maps.newHashMap();
                        tmp.put("value", m.getUniqueName());
                        tmp.put("text", m.getCaption());
                        tmp.put ("isLeaf", Boolean.toString (level < dim.getLevels ().size ()));
                        values.add(tmp);
                    });
                    Map<String, List<Map<String, String>>> datasource = Maps.newHashMap();
                    datasource.put("datasource", values);
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
     * 增加或修改运行时平面表条件
     * add by jiangyichao at 2015-05-25, 平面表条件设置或修改
     * @return
     */
    @RequestMapping(value = "/{id}/{elementId}/runtime/planeTableConditions", method = {RequestMethod.POST})
    public ResponseResult addOrModifyRuntimePlaneTableCondition(@PathVariable("id") String reportId, 
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
        String conditionStr = request.getParameter("conditions");
        // TODO 是否修改
        if (!StringUtils.isEmpty(conditionStr)) {
            Map<String, PlaneTableCondition> conditions = GsonUtils.fromJson(request.getParameter("conditions"),
                    new TypeToken<Map<String, PlaneTableCondition>>(){}.getType());
            // 检查平面表条件值是否合理
            for (PlaneTableCondition tmpCondition : conditions.values()) {
                if (!PlaneTableUtils.checkSQLCondition(tmpCondition.getSQLCondition(), tmpCondition.getDefaultValue())) {
                    result.setStatus(1);
                    result.setStatusInfo("条件参数设置不合理，请检查！");
                    return result;
                }
            }
            
            // 获取原有报表的平面表条件信息
            Map<String, PlaneTableCondition> oldConditions = model.getPlaneTableConditions();
            // 替换原有条件
            oldConditions.put(elementId, conditions.get(elementId));
            model.setPlaneTableConditions(conditions);
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
     * 删除平面表条件信息
     * add by jiangyichao at 2015-05-25，删除平面表条件信息
     * @param reportId
     * @param request
     * @return
     */
    @RequestMapping(value = "/{id}/{elementId}/runtime/planeTableConditions", method = {RequestMethod.GET})
    public ResponseResult removeRuntimePlaneTableConditions(@PathVariable("id") String reportId, 
            @PathVariable("elementId") String elementId, HttpServletRequest request ) {
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
}
