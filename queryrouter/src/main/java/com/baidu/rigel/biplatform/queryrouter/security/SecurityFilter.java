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
package com.baidu.rigel.biplatform.queryrouter.security;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.rigel.biplatform.ac.util.DesCoderUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 安全过滤器
 *
 * @author 罗文磊
 * @version 1.0.0.1
 */
public class SecurityFilter implements Filter {
    
    /**
     * 日志纪录器
     */
    private Logger logger = LoggerFactory.getLogger(SecurityFilter.class);
    
    /**
     * 存储配置策略的内置map
     */
    public static final Map<String, JSONObject> REPOSITORY = Maps.newConcurrentMap();
    
    public SecurityFilter() {
    }
    
    /**
     * init filter
     * 
     * @param filterConfig
     *            FilterConfig
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    /**
     * do filter
     * 
     * @param request
     *            ServletRequest
     * @param response
     *            ServletResponse
     * @param chain
     *            FilterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = null;
        httpRequest = (HttpServletRequest) request;
        
        // 不需要鉴权的请求
        String requestURI = httpRequest.getRequestURI();
        if (isExcludeUrl(requestURI)) {
            logger.info("[INFO]  ==================== request uri info : " + requestURI);
            chain.doFilter(request, response);
            return;
        }
        
        if (doAuth(httpRequest, (HttpServletResponse) response)) {
            chain.doFilter(httpRequest, response);
        }
    }
    
    /**
     * 
     * @param requestURI
     *            custom URL
     * @return boolean if the provide requestURI in exclude list return true
     *         else false
     */
    private boolean isExcludeUrl(String requestURI) {
        Set<String> excludeUrl = Sets.newHashSet();
        // excludeUrl.add("/silkroad/reports/dataupdate");
        // excludeUrl.add ("/silkroad/reports/mobile");
        for (String url : excludeUrl) {
            if (requestURI.indexOf(url) == 0) {
                // 如果匹配成功返回ture
                return true;
            }
        }
        return false;
    }
    
    /**
     *
     * 接入认证
     * 
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @param productLine
     *            产品线
     * @param magicNum
     *            cookie key
     *
     */
    private boolean doAuth(HttpServletRequest request, HttpServletResponse response) {
        // 进行认证
        try {
            if (request.getParameter("token") == null) {
                return false;
            }
            String productLine = request.getParameter("token").toString();
            ApiDesAuthService service = new ApiDesAuthService();
            final String productLineStr = DesCoderUtil.decrypt(productLine);
            JSONObject desKey = REPOSITORY.get(productLineStr);
            // 认证请求成功
            return service.auth(request, response, desKey.getString("key"));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new IllegalStateException("未能提供任何认证策略，拒绝提供服务");
        }
    }
    
    /**
     *
     * destroy
     *
     */
    @Override
    public void destroy() {
    }
    
}
