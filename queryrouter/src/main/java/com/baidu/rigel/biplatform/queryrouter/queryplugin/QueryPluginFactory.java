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
package com.baidu.rigel.biplatform.queryrouter.queryplugin;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.cache.util.ApplicationContextHelper;
import com.google.common.collect.Maps;

/**
 * 查询插件获取类
 * 
 * @author luowenlei
 *
 */
@Service
public class QueryPluginFactory {
    
    private Map<String, QueryPlugin> activePluginMap = Maps.newConcurrentMap();
    
    /**
     * 获取查询插件
     *
     * @param QuestionModel
     * @return QueryPlugin 查询插件
     */
    public QueryPlugin getPlugin(QuestionModel questionModel) {
        
        Map<String, QueryPlugin> map = ApplicationContextHelper.getContext().getBeansOfType(QueryPlugin.class);
        activePluginMap.putAll(map);

        for (QueryPlugin queryPlugin : activePluginMap.values()) {
            if (queryPlugin.isSuitable(questionModel)) {
                return queryPlugin;
            }
        }
        return null;
    }
    
    /**
     * setActivePluginMap
     *
     * @param activePluginMap
     */
    public void setActivePluginMap(Map<String, QueryPlugin> activePluginMap) {
        this.activePluginMap = activePluginMap;
    }
}
