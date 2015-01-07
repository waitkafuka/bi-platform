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
package com.baidu.rigel.biplatform.ma.rt.resource;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.Member;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceService;
import com.baidu.rigel.biplatform.ma.ds.util.DataSourceDefineUtil;
import com.baidu.rigel.biplatform.ma.model.builder.Director;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.model.service.CubeBuildService;
import com.baidu.rigel.biplatform.ma.model.service.StarModelBuildService;
import com.baidu.rigel.biplatform.ma.report.exception.CacheOperationException;
import com.baidu.rigel.biplatform.ma.report.model.ExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.Item;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.report.service.AnalysisChartBuildService;
import com.baidu.rigel.biplatform.ma.report.service.ChartBuildService;
import com.baidu.rigel.biplatform.ma.report.service.QueryBuildService;
import com.baidu.rigel.biplatform.ma.report.service.ReportDesignModelService;
import com.baidu.rigel.biplatform.ma.report.service.ReportModelQueryService;
import com.baidu.rigel.biplatform.ma.report.utils.QueryUtils;
import com.baidu.rigel.biplatform.ma.resource.BaseResource;
import com.baidu.rigel.biplatform.ma.resource.QueryDataResource;
import com.baidu.rigel.biplatform.ma.resource.ResponseResult;
import com.baidu.rigel.biplatform.ma.resource.cache.ReportModelCacheManager;
import com.baidu.rigel.biplatform.ma.resource.utils.ResourceUtils;
import com.baidu.rigel.biplatform.ma.rt.Context;
import com.baidu.rigel.biplatform.ma.rt.ExtendAreaContext;
import com.baidu.rigel.biplatform.ma.rt.query.model.QueryRequest;
import com.baidu.rigel.biplatform.ma.rt.query.model.QueryResult;
import com.baidu.rigel.biplatform.ma.rt.query.model.QueryStrategy;
import com.baidu.rigel.biplatform.ma.rt.query.service.QueryException;
import com.baidu.rigel.biplatform.ma.rt.query.service.ReportQueryService;
import com.baidu.rigel.biplatform.ma.rt.request.build.QueryRequestBuilder;
import com.baidu.rigel.biplatform.ma.rt.utils.RuntimeEvnUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * 报表查询服务入口，接收客户端的http请求，将查询数据以json格式返回给客户端
 * 
 * @author wangyuxue
 *
 */
@RestController
@RequestMapping("/silkroad/reports")
public class ReportQueryResource extends BaseResource {
//    /**
//     * logger
//     */
//    private static final Logger LOG = LoggerFactory.getLogger(QueryDataResource.class);
//
//    /**
//     * reportModelCacheManager
//     */
//    @Resource
//    private ReportModelCacheManager reportModelCacheManager;
//
//    /**
//     * cubeBuildService
//     */
//    @Resource
//    private CubeBuildService cubeBuildService;
//
//    /**
//     * starModelBuildService
//     */
//    @Resource
//    private StarModelBuildService starModelBuildService;
//
//    /**
//     * reportDesignModelService
//     */
//    @Resource(name = "reportDesignModelService")
//    private ReportDesignModelService reportDesignModelService;
//
//    /**
//     * queryBuildService
//     */
//    @Resource
//    private QueryBuildService queryBuildService;
//
//    /**
//     * analysisChartBuildService
//     */
//    @Resource
//    private AnalysisChartBuildService analysisChartBuildService;
//
//    /**
//     * 报表数据查询服务
//     */
//    @Resource
//    private ReportModelQueryService reportModelQueryService;
//
//    /**
//     * chartBuildService
//     */
//    @Resource
//    private ChartBuildService chartBuildService;
//
//    /**
//     * director
//     */
//    @Resource
//    private Director director;
//
//    /**
//     * dsService
//     */
//    @Resource
//    private DataSourceService dsService;
//    
//    /**
//     * reportQuerySerivce
//     */
//    @Resource
//    private ReportQueryService reportQueryService;
//    
//    /**
//     * TODO 临时方案，后续需要调整，删除此处
//     * @param model
//     * @return StringBuilder
//     */
//    private StringBuilder genVm(ReportDesignModel model) {
//        String reportId = model.getId();
//        String vm = model.getVmContent();
//        String js = "<script type='text/javascript'>" + "\r\n" + "        (function(NS) {" + "\r\n"
//                + "            NS.xui.XView.start(" + "\r\n"
//                + "                'di.product.display.ui.LayoutPage'," + "\r\n"
//                + "                {" + "\r\n" + "                    externalParam: {" + "\r\n"
//                + "                    'reportId':'"
//                + reportId
//                + "','phase':'dev','token':'tieba'},"
//                + "\r\n"
//                + "                    globalType: 'PRODUCT',"
//                + "\r\n"
//                + "                    diAgent: '',"
//                + "\r\n"
//                + "                    reportId: '"
//                + reportId
//                + "',"
//                + "\r\n"
//                + "                    webRoot: '/silkroad',"
//                + "\r\n"
//                + "                    phase: 'dev',"
//                + "\r\n"
//                + "                    serverTime: ' " + new Date().getTime() + "',"
//                + "\r\n"
//                + "                    funcAuth: null,"
//                + "\r\n"
//                + "                    extraOpt: (window.__$DI__NS$__ || {}).OPTIONS"
//                + "\r\n"
//                + "                }"
//                + "\r\n"
//                + "            );"
//                + "\r\n"
//                + "        })(window);"
//                + "\r\n" + "    </script>" + "\r\n";
//        StringBuilder builder = new StringBuilder();
//        builder.append("<!DOCTYPE html>");
//        builder.append("<html>");
//        builder.append("<head>");
//        builder.append("<meta content='text/html' 'charset=UTF-8'>");
//        builder.append("<link rel='stylesheet' href='/silkroad/asset/css/-di-product-min.css'/>");
//        builder.append("</head>");
//        builder.append("<body>");
//        builder.append(vm);
//        
//        builder.append("<script src='/silkroad/asset/-di-product-min.js'>");
//        builder.append("</script>");
//        builder.append(js);
//        builder.append("</body>");
//        builder.append("</html>");
//        return builder;
//    }
//    
//    /**
//     * 运行态报表查询服务请求入口：用户查询报表模型定义中的vm信息，用于展现报表时客户端布局
//     * @param reportId 报表id
//     * @param request HttpServletRequest
//     * @param response HttpServletResponse
//     * @return String vm字符串
//     * 
//     */
//    @RequestMapping(value = "/{reportId}/report_vm", method = { RequestMethod.GET },
//            produces = "text/html;charset=utf-8")
//    public String queryVM(@PathVariable("reportId") String reportId, HttpServletRequest request,
//            HttpServletResponse response) {
//    		long begin = System.currentTimeMillis();
//    		LOG.info("[INFO]--- --- begin invoke query report_vm request... ...");
//        ReportDesignModel model = null;
//        try {
//            model = reportModelCacheManager.loadReleaseReportModelToCache(reportId);
//        } catch (CacheOperationException e1) {
//            LOG.error("Fail in loading release report model into cache. ", e1);
//            throw new IllegalStateException("未找到要查询的报表，请确认是否发布报表");
//        }
//        if (model == null) {
//        		throw new IllegalStateException("未找到要查询的报表，请确认是否发布报表");
//        }
//        ConcurrentHashMap<String, Object> globalParams = HttpServletRequestHelper.collectRequestParam(request);
//        // ServletContext
//        ServletContext servletContext = request.getSession().getServletContext();
//        // Spring的ApplicationContext
//        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
//        // 将全局上下文放入cache中
//        Context context = RuntimeEvnUtil.initRuntimeEvn(model, applicationContext, globalParams);
//        reportModelCacheManager.updateContext(reportId, context);
//        StringBuilder builder = this.genVm(model);
//        LOG.info("[INFO] --- --- Successfully invoke query report vm request, cost : {} ms", 
//        		(System.currentTimeMillis() - begin));
//        return builder.toString();
//    }
//
//	
//
//    @RequestMapping(value = "/{reportId}/report_json", method = { RequestMethod.GET },
//            produces = "text/plain;charset=utf-8")
//    public String queryJson(@PathVariable("reportId") String reportId, HttpServletRequest request,
//            HttpServletResponse response) {
//        ReportDesignModel model = null;
//        try {
//            model = reportModelCacheManager.getReportModel(reportId);
//        } catch (CacheOperationException e) {
//            LOG.debug("There are no such model in cache. Report Id: " + reportId, e);
//        }
//        if (model == null) {
//            throw new IllegalStateException("");
//        }
//        String json = model.getJsonContent();
//        response.setCharacterEncoding("utf-8");
//        return json;
//    }
//
//    /**
//     * 
//     * @param reportId
//     * @param request
//     * @return ResponseResult
//     */
//    @RequestMapping(value = "/{reportId}/runtime_model", method = { RequestMethod.POST })
//    public ResponseResult initRunTimeModel(@PathVariable("reportId") String reportId, HttpServletRequest request) {
////        // ServletContext
////        ServletContext servletContext = request.getSession().getServletContext();
////        // Spring的ApplicationContext
////        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
//        boolean edit = Boolean.valueOf(request.getParameter("isEdit"));
//        ReportDesignModel model = null;
//        if (edit) {
//            /**
//             * 编辑报表
//             */
//            model = reportModelCacheManager.loadReportModelToCache(reportId);
//        } else {
//            /**
//             * 如果是新建的报表，从缓存中找
//             */
//            try {
//                model = reportModelCacheManager.getReportModel(reportId);
//            } catch (CacheOperationException e) {
//                LOG.debug("There are no such model in cache. Report Id: " + reportId, e);
//                return ResourceUtils.getErrorResult("缓存中不存在的报表！id: " + reportId, 1);
//            }
//        }
////
////        // 初始化上下文
////        ContextEntity contextEntity = RuntimeEvnUtil.initRuntimeEvn(model, applicationContext);
////        Context context = contextEntity.getContext();
////        // 全局参数
////        ConcurrentHashMap<String, Object> globalParams = context.getGlobalParams();
////        // 添加request请求参数
////        for (String key : request.getParameterMap().keySet()) {
////            String value = request.getParameter(key);
////            if (value != null) {
////                /**
////                 * value 不能是null，但可以为空字符串，空字符串可能有含义
////                 */
////                globalParams.put(key, value);
////            }
////        }
////        context.setGlobalParams(globalParams);
////        
//        reportModelCacheManager.updateReportModelToCache(reportId, model);
////        reportModelCacheManager.updateContext(reportId, context);
////        // 获取区域上下文列表，并发如cache中
////        List<ExtendAreaContext> extendAreaContextLists = contextEntity.getExtendAreaContextLists();
////        for (ExtendAreaContext extendAreaContext : extendAreaContextLists) {
////            reportModelCacheManager.updateAreaContext(extendAreaContext.getAreaId(), extendAreaContext);
////        }
//        ResponseResult rs = ResourceUtils.getCorrectResult("OK", "");
//        return rs;
//    }
//
//    /**
//     * 更新上下文 将格式态的报表模型转化成运形态的报表模型存入缓存 或者依据用户查询逻辑更新运形态报表模型
//     * 
//     * @param reportId
//     * @param areaId
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/{reportId}/runtime/context", method = { RequestMethod.POST })
//    public ResponseResult updateContext(@PathVariable("reportId") String reportId, HttpServletRequest request) {
//    		long begin = System.currentTimeMillis();
//		LOG.info("[INFO]--- --- begin invoke update global context request... ...");
//        Map<String, String[]> contextParams = request.getParameterMap();
//        Context context = reportModelCacheManager.getContext(reportId);
//        
//        // 原全局上下文参数
//        ConcurrentHashMap<String, Object> oldParams = context.getGlobalParams();
//        LOG.info("[INFO]--- --- old params = {}", oldParams);
//        ConcurrentHashMap<String, Object> newParams = new ConcurrentHashMap<String, Object>();
//        if (oldParams != null) {
//	        	// TODO 更新时间参数
//	        	for (String key : oldParams.keySet()) {
//	        		String value = oldParams.get(key).toString();
//	        		if (!(value.contains("start") && value.contains("end"))) {
//	        			newParams.put(key, value);
//	        		}
//	        	}
//        }
//        // 更新请求参数
//        for (String key : contextParams.keySet()) {
//            String[] value = contextParams.get(key);
//            if (value != null && value.length > 0) {
//                newParams.put(key, value[0]);
//            } else if (newParams.contains(key)){
//            		newParams.remove(key);
//            } else { // 设置为null，避免由于脏数据导致查询条件异常
//            		newParams.put(key, null);
//            }
//        }  
//        LOG.info("[INFO]--- --- new params = {}", newParams);
//        context.setGlobalParams(newParams);
//        // 更新上下文请求
//        reportModelCacheManager.updateContext(reportId, context);
//        ResponseResult rs = ResourceUtils.getResult("Success Update Global Context", 
//        		"Failed Update Global Context", "");
//        LOG.info("[INFO]--- --- update global context successfully , cost {} ms",
//        		(System.currentTimeMillis() - begin));
//        return rs;
//    }
//
//    /**
//     * 
//     * @param reportId
//     * @param areaId
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/{reportId}/runtime/extend_area/{areaId}", method = { RequestMethod.POST })
//    public ResponseResult queryArea(@PathVariable("reportId") String reportId, @PathVariable("areaId") String areaId,
//            HttpServletRequest request) {
//        
//        /**
//         * 2. 获取全局上下文
//         */
//        Context context = reportModelCacheManager.getContext(reportId);
//        ConcurrentHashMap<String, Object> globalParams = context.getGlobalParams();
//             
//        /**
//         * 3. 获取区域的上下文信息
//         */
//        
//        ExtendAreaContext extendAreaContext = reportModelCacheManager.getAreaContext(areaId);
//        // 没有初始化过扩展区域上下文，此处需要初始化
//        if (extendAreaContext.getDefaultDsInfo() == null) {
//	        	initExtendAreaContext(reportId, areaId, extendAreaContext);
//        }
//        /**
//         * 4. 合并全局上下文信息与request请求信息,作为总的请求信息queryParams
//         */
//        Map<String, String[]> contextParams = request.getParameterMap();
//        Map<String, Object> queryParams = Maps.newHashMap();
//        // 合并全局参数
//        queryParams.putAll(globalParams); 
//        
//        for (String key : contextParams.keySet()) {
//            String[] value = contextParams.get(key);
//            if (value != null && value.length > 0) {
//                queryParams.put(key, value[0]);
//            }
//        }
//        
//        /**
//         * 5. 构建QueryRequest
//         */
//        // TODO 先测试单表查询，设置查询策略为单表查询
//        QueryStrategy queryStrategy = QueryStrategy.TABLE_QUERY;
//        QueryRequest queryRequest = QueryRequestBuilder.buildQueryRequest(extendAreaContext, queryStrategy, queryParams, null);
//        
//        /**
//         * 6. 查询并返回结果集
//         */
//        QueryResult queryResult = new QueryResult();
//        try {
//            queryResult = reportQueryService.query(queryRequest);
//        } catch (QueryException e) {
//            LOG.error("can't get query result", e);
//        }
//        /**
//         * 7. 将结果返回
//         */
//        ResponseResult rs = ResourceUtils.getResult("Success", "Fail", queryResult.getMapDatas());
//        return rs;       
//    }
//
//	/**
//	 * @param reportId
//	 * @param areaId
//	 * @param extendAreaContext
//	 */
//	private void initExtendAreaContext(String reportId, String areaId,
//			ExtendAreaContext extendAreaContext) {
//		try {
//			ReportDesignModel model = reportModelCacheManager.getReportModel(reportId);
//			DataSourceDefine dsDefine = dsService.getDsDefine(model.getDsId());
//			RuntimeEvnUtil.initExtendAreaContext(extendAreaContext, model, dsDefine, areaId);
//			extendAreaContext.setDefaultDsInfo(DataSourceDefineUtil.parseToDataSourceInfo(dsDefine, securityKey));
//			reportModelCacheManager.updateAreaContext(areaId, extendAreaContext);
//		} catch (Exception e) {
//			LOG.error("Report model is not in cache! ", e);
//			throw new RuntimeException(e);
//		}
//	}
//	
//	/**
//     * 初始化查询参数,初始化查询区域参数
//     * @param reportId
//     * @param request
//     * @return ResponseResult
//     */
//    @RequestMapping(value = "/{reportId}/init_params", method = { RequestMethod.POST })
//    public ResponseResult initParams(@PathVariable("reportId") String reportId,
//            HttpServletRequest request) {
//    		long begin = System.currentTimeMillis();
//    		LOG.info("[INFO]--- ---begin init params with report id {}", reportId);
//    		String[] areaIds = request.getParameter("paramList").split(",");
//    		if (areaIds == null || areaIds.length == 0) {
//    			ResponseResult rs = new ResponseResult();
//    			rs.setStatus(0);
//    			LOG.info("[INFO]--- --- not needed init global params");
//    	        return rs;
//    		}
//    		final ReportDesignModel model = reportModelCacheManager.getReportModel(reportId);
//    		Map<String, Map<String, List<Map<String, String>>>> datas = Maps.newConcurrentMap();
//    		for (final String areaId : areaIds) {
//			ExtendArea area = model.getExtendById(areaId);
//			if (area != null && !QueryUtils.isFilterArea(area.getType()) && !area.listAllItems().isEmpty()) {
//				Item item = area.listAllItems().values().toArray(new Item[0])[0];
//				Cube cube = model.getSchema().getCubes().get(area.getCubeId());
//				Cube tmpCube = QueryUtils.transformCube(cube);
//				String dimId = item.getOlapElementId();
//				Dimension dim = cube.getDimensions().get(dimId);
//				if (dim != null) {
//					List<Map<String, String>> values;
//					try {
//						List<Member> members = reportModelQueryService.getMembers(tmpCube, 
//										tmpCube.getDimensions().get(dim.getName()),
//										Maps.newHashMap(), securityKey).get(0);
//						values = Lists.newArrayList();
//						members.forEach(m -> {
//							Map<String, String> tmp = Maps.newHashMap();
//							tmp.put("value", m.getUniqueName());
//							tmp.put("text", m.getCaption());
//							values.add(tmp);
//						});
//						Map<String, List<Map<String, String>>> datasource = Maps.newHashMap();
//						datasource.put("datasource", values);
//						datas.put(areaId, datasource);
//					} catch (Exception e) {
//						LOG.info("[INFO] exception happened : " + e.getMessage());
//					}
//				}
//			}
//    		}
//        ResponseResult rs = new ResponseResult();
//        rs.setStatus(0);
//        rs.setData(datas);
//        rs.setStatusInfo("OK");
//        LOG.info("[INFO]--- --- successfully init params, cost {} ms", (System.currentTimeMillis() - begin));
//        return rs;
//    }
}
