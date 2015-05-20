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

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.baidu.rigel.biplatform.ac.query.MiniCubeConnection;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceConnectionService;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceConnectionServiceFactory;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceService;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.model.utils.GsonUtils;
//import com.baidu.rigel.biplatform.ma.report.service.ReportNoticeByJmsService;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * 同步更新数据rest接口，用于提供同步数据更新支持
 * @author david.wang
 *
 */
@RestController
@RequestMapping("/silkroad/reports/dataupdate")
public class UpdateDataResource extends BaseResource {
    
    /**
     * LOG
     */
    private static final Logger LOG = LoggerFactory.getLogger(UpdateDataResource.class);
    
    /**
     * dsService
     */
    @Resource
    private DataSourceService dsService;
    
    
//    @Resource
//    private ReportNoticeByJmsService reportNoticeByJmsService;

    /**
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return ResponseResult
     */
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseResult updateData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("[INFO] --- --- begin update index meta with new request");
        long begin = System.currentTimeMillis();
        String dsName = request.getParameter("dsName");
        String factTables = request.getParameter("factTables");
        if (StringUtils.isEmpty(dsName) || StringUtils.isEmpty(factTables)) {
            ResponseResult rs = new ResponseResult();
            rs.setStatus(1);
            rs.setStatusInfo("请求中需要包含dsName, factTables信息。"
                    + "其中dsName为数据源名称，factTables为更新的事实表列表，多张表以’,‘分割");
            return rs;
        }
        String[] factTableArray = factTables.split(",");
        ResponseResult rs = new ResponseResult();
        DataSourceDefine ds = dsService.getDsDefine(dsName);
        DataSourceConnectionService<?> dsConnService = DataSourceConnectionServiceFactory.
        		getDataSourceConnectionServiceInstance(ds.getDataSourceType().name ());
        DataSourceInfo dsInfo = dsConnService.parseToDataSourceInfo(ds, securityKey);
        Map<String, Map<String, String>> conds = Maps.newHashMap();
        for (String factTable : factTableArray) {
            String str = request.getParameter(factTable);
            LOG.info("[INFO] --- --- conditions for {} is : {}", factTable, str);
            if (isValidate(str)) {
                conds.put(factTable, GsonUtils.fromJson(str, new TypeToken<Map<String, String>>() {}.getType()));
            }
        }
        String condsStr = null;
        if (conds.size() > 0) {
            condsStr = GsonUtils.toJson(conds);
        }
        LOG.info("[INFO] --- --- conds : {}", conds);
        LOG.info("[INFO] --- --- request params list ----------------------------------");
        LOG.info("[INFO] --- --- dsName = {}", dsName);
        LOG.info("[INFO] --- --- factTables = {}", factTables);
        LOG.info("[INFO] --- --- conds = {}", condsStr);
        LOG.info("[INFO] --- --- --- ---- ---- end pring param list --- --- --- --- ---- ");
        boolean result = MiniCubeConnection.ConnectionUtil.refresh(dsInfo, factTableArray, condsStr);
//        reportNoticeByJmsService.refreshIndex(dsInfo, factTableArray, condsStr);
        if (result) {
            rs.setStatus(0);
            rs.setStatusInfo("successfully");
        } else {
            rs.setStatus(1);
            rs.setStatusInfo("failed");
        }
        LOG.info("[INFO] -- --- update index meta result : {}", result);
        LOG.info("[INFO] --- --- end update index meta, cost {} ms", (System.currentTimeMillis() - begin));
        return rs;
    }

    private boolean isValidate(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        try {
            JSONObject json = new JSONObject(str);
            if (StringUtils.isEmpty(json.getString("begin")) || StringUtils.isEmpty(json.getString("end"))) {
                throw new IllegalStateException("request param status incorrected");
            }
            Long begin = Long.valueOf(json.getString("begin"));
            if (begin <= 0) {
                throw new IllegalStateException("begin value need bigger than zero");
            }
            Long end = Long.valueOf(json.getString("end"));
            if (end < begin) {
                throw new IllegalStateException("end value must larger than begin");
            }
        } catch (Exception e) {
            LOG.info(e.getMessage(), e);
            throw new IllegalStateException("request param must be json style");
        }
        return true;
    }
}
