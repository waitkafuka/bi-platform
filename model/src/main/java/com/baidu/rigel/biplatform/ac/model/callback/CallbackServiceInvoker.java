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
package com.baidu.rigel.biplatform.ac.model.callback;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.rigel.biplatform.ac.util.HttpRequest;

/**
 * 
 * Description:
 * @author david.wang
 *
 */
public final class CallbackServiceInvoker {
    
    /**
     * LOG
     */
    private static final Logger LOG = LoggerFactory.getLogger(CallbackServiceInvoker.class);
    
    /**
     * 构造函数
     */
    private CallbackServiceInvoker() {
    }
    
    /**
     * 
     * @param url callback请求url
     * @param params callback请求参数
     * @param type callback请求类型
     * @return CallbackResponse callback响应
     */
    public static CallbackResponse invokeCallback(String url, Map<String, String> params, CallbackType type) {
        return invokeCallback(url, params, type, Integer.MAX_VALUE);
    }
    
    /**
     * 
     * @param url callback请求url
     * @param params callback请求参数
     * @param type callback请求类型
     * @param timeOutMillSecond 超时时间
     * @return CallbackResponse callback响应
     */
    public static CallbackResponse invokeCallback(String url, Map<String, String> params,
            CallbackType type, int timeOutMillSecond) {
        long begin = System.currentTimeMillis();
        LOG.info("[INFO] --- --- begin invoke callback service ... ...");
        LOG.info("[INFO] --- --- params : {}", params);
        LOG.info("[INFO] --- --- request url : {}", url);
        LOG.info("[INFO] --- --- timeout time : {} ms", timeOutMillSecond);
        LOG.info("[INFO] --- --- callback type : {}", type.name());
        LOG.info("[INFO] --- --- end invoke callback service. result is : \r\n");
        LOG.info("[INFO] -------------------------------------------------------------------------\r\n" );
        String responseStr = HttpRequest.sendPost(url, params);
        CallbackResponse response = convertStrToResponse(responseStr, type);
        LOG.info(response.toString());
        LOG.info("[INFO] -------------------------------------------------------------------------\r\n" );
        long end = System.currentTimeMillis() - begin;
        LOG.info("[INFO] --- --- invoke callback service cost : " + end + "ms,"
                + " cost on data transfer : " + (end - response.getCost()) + "ms,"
                + " callback execute cost : " + response.getCost() + "ms") ;
        return response;
    }

    /**
     * 
     * @param responseStr
     * @param type
     * @return CallbackResponse
     */
    private static CallbackResponse convertStrToResponse(String responseStr, CallbackType type) {
        return null;
    }
}
