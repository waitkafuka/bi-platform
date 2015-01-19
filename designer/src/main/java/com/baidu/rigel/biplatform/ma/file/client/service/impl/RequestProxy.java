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
package com.baidu.rigel.biplatform.ma.file.client.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.ma.common.file.protocol.Request;
import com.baidu.rigel.biplatform.ma.common.file.protocol.Response;
import com.baidu.rigel.biplatform.ma.common.file.protocol.ResponseStatus;
import com.baidu.rigel.biplatform.ma.file.client.service.FileServiceException;
import com.google.common.collect.Maps;

/**
 * 文件流操作实现类，主要提供读取内容，向服务器写内容
 * 
 * @author jiangyichao
 *
 */
@Service
public class RequestProxy {
    /**
     * 日志对象
     */
    private static final Logger LOG = Logger.getLogger(RequestProxy.class);
    
    /**
     * 服务器地址、端口缓存
     */
    private static final LinkedHashMap<String, Integer> SERVERS = Maps.newLinkedHashMap();
    
    /**
     * 文件服务器客户端
     */
    private final FileServerClient client = FileServerClient.newInstance();

    /**
     * 文件服务器ip地址
     */
    @Value("${biplatform.ma.fileserver.inetaddress}")
    private String host;
    
    /**
     * 文件服务器端口号
     */
//    @Value("${biplatform.ma.fileserver.port}")
//    private int port;

    /**
     * 文件服务器操作请求
     * 
     * @param request
     * @return 从服务器读取到操作结果
     * 
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> doActionOnRemoteFileSystem(Request request) throws FileServiceException {
        Response response = null;
        try {
            response = doRequest(request);
        } catch (Throwable e) {
            LOG.error(e.getMessage(), e);
        }
        //需要重新定义response的datas属性
        if (response.getStatus() == ResponseStatus.SUCCESS) { //兼容原有逻辑，后续需要调整
            return (Map<String, Object>) response.getDatas();
        }
        LOG.debug("do action on remote file sys: " + request.getCommand());
        Map<String, Object> rs = new HashMap<String, Object>();
        rs.put("result", response.getStatus().name());
        rs.put("msg", response.getMessage());
        return rs;
    }

    /**
     * 
     * @param request
     * @return Response
     */
    private Response doRequest(Request request) {
        if (SERVERS.isEmpty()) {
            initServers();
        }
        Set<Map.Entry<String, Integer>> entry = SERVERS.entrySet();
        Response response = null;
        for (Map.Entry<String, Integer> tmp : entry) {
            response = client.doRequest(tmp.getKey(), tmp.getValue(), request);
            if (response.getStatus() != ResponseStatus.FAIL) {
                break;
            }
        }
        return response;
    }

    private void initServers() {
        String[] servers = host.split(",");
        for (String server : servers) {
            String[] tmp = server.split(":");
            SERVERS.put(tmp[0], Integer.valueOf(tmp[1]));
        }
    }
}
