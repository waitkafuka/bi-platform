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
package com.baidu.rigel.biplatform.ma.resource.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebFault;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.baidu.rigel.biplatform.ac.util.AesUtil;
import com.baidu.rigel.biplatform.ma.model.consts.Constants;
import com.baidu.rigel.biplatform.ma.report.utils.ContextManager;
import com.google.common.collect.Lists;

/**
 * 
 * 过滤器
 * @author peizhongyi01
 *
 *         2014-8-13
 */
public class UniversalContextSettingFilter implements Filter {
    private static final Logger LOG = Logger.getLogger(UniversalContextSettingFilter.class);
    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        
    }
    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request; 
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        // modify by jiangyichao at 2014-09-12
        String sessionId = null;
        String productLine = "tieba";
        if (httpRequest.getCookies() != null && httpRequest.getCookies().length > 0) {
            List<Cookie> cookies = Lists.newArrayList();
            Collections.addAll(cookies, httpRequest.getCookies());
            Object[] tmp = cookies.stream().filter(cookie -> {
               return Constants.BIPLATFORM_PRODUCTLINE.equals(cookie.getName()); 
            }).map(encryptProductLine).toArray();
            if (tmp != null && tmp.length > 0) {
                productLine = tmp[0].toString();
            }
            tmp = cookies.stream().filter(cookie -> {
                return Constants.SESSION_ID.equals(cookie.getName());
            }).map(genSessionId).toArray();
            
            if (tmp != null && tmp.length > 0) {
                sessionId = tmp[0].toString();
            }
        }

        
        try { 
            ContextManager.setSessionId(sessionId);
            ContextManager.setProductLine(productLine);
            chain.doFilter(request, response);
        } finally {
            /**
             * 清空线程里面的东西
             */
            ContextManager.cleanSessionId();
            ContextManager.cleanProductLine();
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig chain) throws ServletException {
    }
    
    // 对产品线cookie的处理
    private Function<Cookie, String> encryptProductLine = cookie -> {
        String innerProductLine = cookie.getValue();
        if (StringUtils.hasText(innerProductLine)) {
            // 调用解密算法，对productLine进行解密
            try {
                innerProductLine = AesUtil.getInstance().decrypt(innerProductLine);
            } catch (Exception e) {
                LOG.error(innerProductLine);
                LOG.error(e.getMessage(),e);
                //e.printStackTrace();
            }
        } // 产品线value不为空
        return innerProductLine;
    };
    
    // 对sessionId的cookie的处理
    private Function<Cookie, String> genSessionId = cookie -> {
        return cookie.getValue();
    };  
}