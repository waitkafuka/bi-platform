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
/**
 * 
 */
package com.baidu.rigel.biplatform.tesseract.isservice.startup;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.tesseract.isservice.index.service.IndexMetaService;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexMeta;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexShard;
import com.baidu.rigel.biplatform.tesseract.node.meta.Node;
import com.baidu.rigel.biplatform.tesseract.node.meta.NodeState;
import com.baidu.rigel.biplatform.tesseract.node.service.IndexAndSearchServer;
import com.baidu.rigel.biplatform.tesseract.node.service.IsNodeService;
import com.baidu.rigel.biplatform.tesseract.util.isservice.LogInfoConstants;

/**
 * IndexAndSearchStartupListener
 * 
 * @author lijin
 *
 */
@Service
public class IndexAndSearchStartupListener implements ApplicationContextAware,
        ApplicationListener<ContextRefreshedEvent> {
    
    /**
     * LOGGER
     */
    private static final Logger LOGGER = LoggerFactory
        .getLogger(IndexAndSearchStartupListener.class);
    /**
     * context
     */
    private ApplicationContext context;
    
    /**
     * isNodeService
     */
    @Resource(name = "isNodeService")
    private IsNodeService isNodeService;
    
    @Resource
    private IndexMetaService idxMetaService;
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org
     * .springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_ON_LISTENER_BEGIN,
            "IndexAndSearchStartupListener.onApplicationEvent", event));
        if (event == null) {
            LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_EXCEPTION,
                "IndexAndSearchStartupListener.onApplicationEvent", event));
            throw new IllegalArgumentException();
        }
        // 程序初始化结束后
        // 启动索引查询服务
        IndexAndSearchServer isServer = this.context.getBean(IndexAndSearchServer.class);        
        
        isServer.start();
        int count = 0;
        try {
            while (!isServer.isRunning()) {
                Thread.sleep(500);
                count++;
                if (count > 10) {
                    throw new Exception();
                }
            }
        } catch (Exception e) {
            LOGGER.error(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_ERROR,
                "IndexAndSearchStartupListener.onApplicationEvent-Server startup failed "), e);
            
        }
        
        if (isServer.isRunning()) {
            // 注册信息
            if (isServer.getNode() != null) {
                isServer.getNode().setNodeState(NodeState.NODE_AVAILABLE);
                isServer.getNode().setLastStateUpdateTime(System.currentTimeMillis());
                this.isNodeService.saveOrUpdateNodeInfo(isServer.getNode());
            }
            
            // 恢复本地镜像
            Node currNode = isServer.getNode();
            if (currNode != null) {
                // 如果本地镜像存在
                File localImage = new File(currNode.getImageFilePath());
                if (localImage.exists()) {
                    // 恢复本地元数据
                    loadLocalInfo(currNode);
                }
            }
            
            // 启动状态更新&检查线程
            ClusterNodeCheckThread clusterNodeCheckThread = this.context
                .getBean(ClusterNodeCheckThread.class);
            clusterNodeCheckThread.start();
            
//            // 启动hz中的queue事件监听
//            LocalEventListenerThread localListenerThread = this.context
//                .getBean(LocalEventListenerThread.class);
//            localListenerThread.start();
        } else {
            LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_ERROR,
                "IndexAndSearchStartupListener.onApplicationEvent-Server is not running ", event));
        }
        
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_ON_LISTENER_END,
            "IndexAndSearchStartupListener.onApplicationEvent", event));
        return;
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.context.ApplicationContextAware#setApplicationContext
     * (org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        
    }
    
    private void loadLocalInfo(Node node) {
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_BEGIN, "loadLocalInfo",
            "[Node:" + node + "]"));
        Node currNode = node;
        currNode = this.isNodeService.loadLocalNodeImage(currNode);
        
        // 当前节点不是一个空节点
        if (currNode != null && currNode.getUsedIndexShardList() != null) {
            for (IndexShard idxShard : currNode.getUsedIndexShardList()) {
                
                IndexMeta idxMeta = idxShard.getIndexMeta();
                
                String cubeId = null;
                IndexMeta remoteIndexMeta = null;
                if (idxMeta != null && idxMeta.getCubeIdSet() != null
                        && idxMeta.getCubeIdSet().size() > 0) {
                    cubeId = idxMeta.getCubeIdSet().toArray(new String[0])[0];
                    remoteIndexMeta = this.idxMetaService.getIndexMetaByCubeId(cubeId,
                        IndexMeta.getDataStoreName());
                }
                // 由于现有的本地镜像都是在存完后再写的，所以remote的版本一定是最新的
                // 两种情况：
                // 1、集群中的索引元数据中存在node信息，只不过是node独立的状态被标记为不可用，只需要同步node的信息就可以，不需要fix索引元数据信息
                // 2、整个集群都宕掉了，需要重建
                if (remoteIndexMeta != null) {
                    idxMeta = remoteIndexMeta;
                    // 集群中有一部分数据
                    IndexShard remoteIdxShard = null;
                    for (IndexShard iShard : remoteIndexMeta.getIdxShardList()) {
                        if (iShard.equals(idxShard)) {
                            remoteIdxShard = iShard;
                            break;
                        }
                    }
                    remoteIdxShard = updateNodeInfoOfIndexShard(remoteIdxShard, currNode);
                    
//                    if (remoteIdxShard != null) {
//                        boolean nodeExist = false;
//                        if (!CollectionUtils.isEmpty(remoteIdxShard.getReplicaNodeList())) {
//                            for (Node reNode : remoteIdxShard.getReplicaNodeList()) {
//                                if (reNode.equals(currNode)) {
//                                    nodeExist = true;
//                                    break;
//                                    
//                                }
//                            }
//                        }
//                        if (!nodeExist
//                                && (remoteIdxShard.getNode() != null && !remoteIdxShard.getNode()
//                                        .equals(currNode))) {
//                            // node在复本中不存在，并且主NODE也不是当前节点；
//                            remoteIdxShard.getReplicaNodeList().add(currNode);
//                        } else if (!nodeExist && (remoteIdxShard.getNode() == null)) {
//                            // node在复本中不存在，并且主NODE为空
//                            remoteIdxShard.setNode(currNode);
//                        } else if (nodeExist && (remoteIdxShard.getNode() == null)) {
//                            // node在复本中存在，且主NODE为空
//                            remoteIdxShard.setNode(currNode);
//                            remoteIdxShard.getReplicaNodeList().remove(currNode);
//                            // node在复本中存在，且主NODE不为当前节点，不用处理
//                        }
//                    }
                    idxMeta = remoteIndexMeta;
                    
                } else {
                    idxShard = updateNodeInfoOfIndexShard(idxShard, currNode);
                }
                this.idxMetaService.saveOrUpdateIndexMeta(idxMeta);
            }
        }
        LOGGER.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_END, "loadLocalInfo",
            "[Node:" + node + "]"));
    }
    
    /**
     * 
     * updateNodeInfoOfIndexShard
     * 
     * @param remoteIdxShard
     *            remoteIdxShard
     * @param currNode
     *            currNode
     * @return IndexShard
     */
    private IndexShard updateNodeInfoOfIndexShard(IndexShard remoteIdxShard, Node currNode) {
        List<Node> currAvailableNodeList = isNodeService.getAvailableNodeListByClusterName(currNode
                .getClusterName());
        if (remoteIdxShard != null) {
            boolean nodeExist = false;
            if (!CollectionUtils.isEmpty(currAvailableNodeList)) {
                if (!CollectionUtils.isEmpty(remoteIdxShard.getReplicaNodeList())) {
                    remoteIdxShard.getReplicaNodeList().retainAll(currAvailableNodeList);
                }
                if (!currAvailableNodeList.contains(remoteIdxShard.getNode())) {
                    remoteIdxShard.setNode(null);
                }
            } else {
                remoteIdxShard.getReplicaNodeList().clear();
                remoteIdxShard.setNode(null);
            }
            
            // 判断是否在复本节点中
            if (!CollectionUtils.isEmpty(remoteIdxShard.getReplicaNodeList())) {
                
                for (Node reNode : remoteIdxShard.getReplicaNodeList()) {
                    if (reNode.equals(currNode)) {
                        nodeExist = true;
                        break;
                        
                    }
                }
            }
            
            if (!nodeExist
                    && (remoteIdxShard.getNode() != null
                    && remoteIdxShard.getNode().getNodeState().equals(NodeState.NODE_AVAILABLE) && !remoteIdxShard
                        .getNode().equals(currNode))) {
                // node在复本中不存在，并且主NODE也不是当前节点；
                remoteIdxShard.getReplicaNodeList().add(currNode);
            } else if (!nodeExist
                            && (remoteIdxShard.getNode() == null || !remoteIdxShard.getNode().getNodeState()
                                .equals(NodeState.NODE_AVAILABLE))) {
                // node在复本中不存在，并且主NODE为空
                remoteIdxShard.setNode(currNode);
            } else if (nodeExist && (remoteIdxShard.getNode() == null)) {
                // node在复本中存在，且主NODE为空
                remoteIdxShard.setNode(currNode);
                remoteIdxShard.getReplicaNodeList().remove(currNode);
                // node在复本中存在，且主NODE不为当前节点，不用处理
            }
        }
        return remoteIdxShard;
    }
    
}
