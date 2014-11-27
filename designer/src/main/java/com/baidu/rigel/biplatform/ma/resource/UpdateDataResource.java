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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.baidu.rigel.biplatform.ac.query.MiniCubeConnection;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceService;
import com.baidu.rigel.biplatform.ma.ds.util.DataSourceDefineUtil;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;

/**
 * 
 * 同步更新数据rest接口，用于提供同步数据更新支持
 * @author david.wang
 *
 */
@RestController
@RequestMapping("/silkroad/reports/dataupdate")
public class UpdateDataResource {

	/**
     * dsService
     */
    @Resource
    private DataSourceService dsService;

	/**
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @return ResponseResult
	 */
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseResult updateData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String dsName = request.getParameter("dsName");
		String factTables = request.getParameter("factTables");
		String productLine = request.getParameter("rbk");
		if (StringUtils.isEmpty(productLine) || StringUtils.isEmpty(dsName) || StringUtils.isEmpty("factTables")) {
			ResponseResult rs = new ResponseResult();
			rs.setStatus(1);
			rs.setStatusInfo("请求中需要包含dsName, rbk和factTables信息。"
					+ "其中dsName为数据源名称，factTables为更新的事实表列表，多张表以’,‘分割");
			return rs;
		}
		String[] factTableArray = factTables.split(",");
		ResponseResult rs = new ResponseResult();
		DataSourceDefine ds = dsService.getDsDefine(productLine, dsName);
		DataSourceInfo dsInfo = DataSourceDefineUtil.parseToDataSourceInfo(ds);
		MiniCubeConnection.refresh(dsInfo, factTableArray);
		rs.setStatus(0);
		rs.setStatusInfo("successfully");
		return rs;
	}
}
