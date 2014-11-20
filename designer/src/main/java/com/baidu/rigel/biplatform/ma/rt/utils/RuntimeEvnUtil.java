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
package com.baidu.rigel.biplatform.ma.rt.utils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.baidu.rigel.biplatform.ma.ds.exception.DataSourceOperationException;
import com.baidu.rigel.biplatform.ma.ds.service.DataSourceService;
import com.baidu.rigel.biplatform.ma.ds.util.DataSourceDefineUtil;
import com.baidu.rigel.biplatform.ma.model.ds.DataSourceDefine;
import com.baidu.rigel.biplatform.ma.report.model.ExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.Item;
import com.baidu.rigel.biplatform.ma.report.model.LiteOlapExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.rt.Context;
import com.baidu.rigel.biplatform.ma.rt.ExtendAreaContext;
import com.google.common.collect.Maps;

/**
 * 工具类：用于提供运行时环境初始化、运行时上下文操作等
 *
 * @author wangyuxue
 * @version 1.0.0.1
 */
public final class RuntimeEvnUtil {
    /**
     * 日期
     */
    private static Logger logger = Logger.getLogger(RuntimeEvnUtil.class);
    /**
     * 数据源服务
     */
    @Resource(name = "dsService")
    static DataSourceService dsService;
	/**
	 * 构造函数
	 */
	private RuntimeEvnUtil () {
	}
	
	/**
	 * 根据报表id初始化报表对应运行时啥下文
	 * @param designModel 报表模型
	 * @return Context 运行时上下文
	 */
	public static final Context initContext(ReportDesignModel designModel) {
	    //TODO Spring的ApplicationContext
	    ApplicationContext applicationContext = null; 
	    // 上下文信息
	    Context context = new Context(applicationContext);
	    // 局部上下文参数
	    ConcurrentHashMap<String, ExtendAreaContext> localCtxMap = context.getLocalCtxMap();
	    // 获取扩展区域列表
	    ExtendArea[] extendAreas = designModel.getExtendAreaList();
	    if (extendAreas != null) {
	        // 遍历扩展区，获取每个扩展区的上下文信息
	        for (ExtendArea extendArea : extendAreas) {
	            ExtendAreaContext localContext = getLocalContextOfExtendArea(extendArea, designModel);
	            localCtxMap.put(extendArea.getId(), localContext);
	        }
	    }
	    // 更新局部上下文信息
	    context.setLocalCtxMap(localCtxMap);
		return context;
	}
	
	/**
	 * 获取扩展区域上下文
	 * getLocalContextOfExtendArea
	 * @param extendArea
	 * @return context 局部上下文
	 */
	private static ExtendAreaContext getLocalContextOfExtendArea (ExtendArea extendArea, ReportDesignModel designModel) {
	    ExtendAreaContext context = new ExtendAreaContext();
	    context.setAreaId(extendArea.getId());
	    context.setAreaType(extendArea.getType());
	    // 对数据源信息进行处理
	    DataSourceDefine dsDefine = new DataSourceDefine();
        try {
            dsDefine = dsService.getDsDefine(designModel.getDsId());
        } catch (DataSourceOperationException e) {
            logger.error("fail to get datasource define ", e);
        }
        // 设置默认数据源信息
	    context.setDefaultDsInfo(DataSourceDefineUtil.parseToDataSourceInfo(dsDefine));
	    // 设置数据格式模型
	    context.setFormatModel(extendArea.getFormatModel());
	    
	    // 获取区域逻辑模型
	    LogicModel logicModel = extendArea.getLogicModel();
	    // 利用逻辑模型获取行轴，列轴，以及切片轴信息
	    Item[] columns = logicModel.getColumns();
	    Item[] rows = logicModel.getRows();
	    Item[] slices = logicModel.getSlices();
	    Map<Item, Object> x = Maps.newLinkedHashMap();
	    for (Item item : rows) {
	        x.put(item, null);
	    }
	    Map<Item, Object> y = Maps.newLinkedHashMap();
        for (Item item : columns) {
            y.put(item, null);
        }
	    Map<Item, Object> s = Maps.newLinkedHashMap();
        for (Item item : slices) {
            s.put(item, null);
        } 
        // 将行轴，列轴，切片轴信息添加到上下文中
        context.setX(x);
        context.setY(y);
        context.setS(s);
        
	    switch (extendArea.getType()) {
	        case LITEOLAP:
	            LiteOlapExtendArea liteOlapExtendArea = (LiteOlapExtendArea) extendArea;
	            // 获取候选维度，候选指标
	            Map<String, Item> canDims = liteOlapExtendArea.getCandDims();
	            Map<String, Item> canInds = liteOlapExtendArea.getCandInds();
	            context.setCanDim((Set<Item>) canDims.values());
	            context.setCanInd((Set<Item>) canInds.values());
	        default:
	    }
	    return context;
	}
}
