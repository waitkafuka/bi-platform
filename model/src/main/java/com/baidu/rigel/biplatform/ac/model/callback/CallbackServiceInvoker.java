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
import org.springframework.util.StringUtils;

import com.baidu.rigel.biplatform.ac.util.HttpRequest;
import com.google.common.collect.Maps;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

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
        if (timeOutMillSecond <= 0) {
            timeOutMillSecond = 1000;
        }
        params.put(HttpRequest.SOCKET_TIME_OUT, String.valueOf(timeOutMillSecond));
        String responseStr = HttpRequest.sendPost(url, params);
        CallbackResponse response = convertStrToResponse(responseStr, type);
        LOG.info("[INFO] --- --- resposne : {}", response);
        LOG.info("[INFO] -------------------------------------------------------------------------\r\n" );
        long end = System.currentTimeMillis() - begin;
        LOG.info("[INFO] --- --- invoke callback service cost : " + end + "ms,"
                + " cost on data transfer : " + (end - response.getCost()) + "ms,"
                + " callback execute cost : " + response.getCost() + "ms") ;
        return response;
    }

    /**
     * 将callback请求结果封装为CallbackResponse，如因404等错误信息需cache异常另处理
     * @param responseStr
     * @param type
     * @return CallbackResponse
     */
    private static CallbackResponse convertStrToResponse(String responseStr, CallbackType type) {
        LOG.info("[INFO] --- --- message received from callback server  is {}", responseStr);
        CallbackResponse rs = new CallbackResponse();
        long begin = System.currentTimeMillis();
        if (StringUtils.isEmpty(responseStr)) {
            throw new RuntimeException("请求响应未满足协议规范");
        }
        JsonObject json = new JsonParser().parse(responseStr).getAsJsonObject();
        int status = json.get("status").getAsInt();
        String message = json.get("message").getAsString();
        String provider = json.get("provider").getAsString();
        String cost = json.get("cost").getAsString();
        String version = json.get("version").getAsString();
        LOG.info("[INFO] ------------------------------callback response desc -----------------------------------");
        LOG.info("[INFO] --- --- status : {}", status);
        LOG.info("[INFO] --- --- message : {}", message);
        LOG.info("[INFO] --- --- provider : {}", provider);
        LOG.info("[INFO] --- --- cost : {}", cost);
        LOG.info("[INFO] --- --- callback version : {}", version);
        LOG.info("[INFO] -----------------------------end print response desc -----------------------------------");
        LOG.info("[INFO] --- --- package result to CallbackResponse cost {} ms",
                (System.currentTimeMillis() - begin));
        rs.setCost(Integer.valueOf(StringUtils.isEmpty(cost) ? "0" : cost));
        rs.setStatus(ResponseStatus.valueOf(String.valueOf(status)));
        rs.setProvider(provider);
        rs.setVersion(version);
        rs.setMessage(getNlsMessage(status));
        if (ResponseStatus.SUCCESS.getValue() == status) {
            // 处理结果
        }
        return rs;
    }

    /**
     * 获取提示信息
     * @param status
     * @return String
     */
    private static String getNlsMessage(int status) {
        ResponseStatus statusType = ResponseStatus.valueOf(String.valueOf(status));
        // 以后考虑国际化，此处为临时方案
        switch (statusType) {
            case SUCCESS:
                return "成功受理请求";
            
        }
        return "未知错误，请联系系统管理人员";
    }
}
