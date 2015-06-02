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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.rigel.biplatform.ac.util.DesCoderUtil;

/**
 * Api server to server 的认证
 * 
 * @author luowenlei
 *
 */
public class ApiDesAuthService {
    
    /**
     * logger
     */
    private static Logger logger = LoggerFactory.getLogger(QueryRouterApplication4Enterprise.class);
    
    /**
     * {@inheritDoc}
     */
    public boolean auth(HttpServletRequest request, HttpServletResponse response, String key)
            throws Exception {
        // 参数验证
        try {
            String value = request.getParameterMap().get("question")[0].toString();
            value = DesCoderUtil.decrypt(value, key);
            request.setAttribute("question", value);
        } catch (Exception e) {
            logger.error("查询参数传递错误:{}", e.getCause().getMessage());
            return false;
        }
        return true;
    }
    
}
