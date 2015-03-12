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

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import com.baidu.rigel.biplatform.ac.util.HttpRequest;

/**
 * HttpServletRequest操作的辅助工具类
 * @author david.wang
 *
 */
final class HttpServletRequestHelper {

    private HttpServletRequestHelper() {
        
    }
    
    /**
     * 收集request请求中的所有参数，作为初始化全局参数存储
     * @param request HttpServletRequest
     * @return ConcurrentHashMap parameter's map
     */
    static ConcurrentHashMap<String, Object> collectRequestParam(HttpServletRequest request) {
        ConcurrentHashMap<String, Object> globalParams = new ConcurrentHashMap<String, Object>();
        // 将url参数添加到全局上下文中
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            globalParams.put(paramName, request.getParameter(paramName));
        }
        // 添加cookie内容
        globalParams.put(HttpRequest.COOKIE_PARAM_NAME, request.getHeader("Cookie"));
        return globalParams;
    }
}
