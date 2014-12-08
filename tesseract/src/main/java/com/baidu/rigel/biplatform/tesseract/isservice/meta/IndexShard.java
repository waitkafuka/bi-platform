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
package com.baidu.rigel.biplatform.tesseract.isservice.meta;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.baidu.rigel.biplatform.tesseract.node.meta.Node;
import com.baidu.rigel.biplatform.tesseract.util.IndexFileSystemConstants;

/**
 * IndexShard
 * 
 * @author lijin
 *
 */
public class IndexShard implements Serializable {
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7727279768348029673L;
    /**
     * 默认复本数
     */
    private static final int DEFAULT_SHARD_REPLICA_NUM = 1;
    
    /**
     * 分区名称：productLine_facttable_shard_shardId
     */
    private String shardName;
    /**
     * shardId
     */
    private Long shardId;
    /**
     * node 主节点
     */
    private Node node;
    
    /**
     * replicaNodeList 复本所在节点
     */
    private List<Node> replicaNodeList;
    /**
     * idxShardStrategy
     */
    private IndexShardStrategy idxShardStrategy;
    /**
     * filePath 用于创建/更新索引的路径
     */
    private String filePath;
    /**
     * idxFilePath 用于提供索引服务的路径
     */
    private String idxFilePath;
    /**
     * 当前分片的版本
     */
    private long idxVersion;
    /**
     * 当前分片是否写满
     */
    private boolean isFull;
    
    /**
     * 当前分片实际占用的物理空间大小，单位bytes
     */
    private long diskSize;
    
    /**
     * 索引状态
     */
    private IndexState idxState = IndexState.INDEX_UNAVAILABLE;
    
    /**
     * 当前索引分片所属的IndexMeta 注意：这个idxMeta只用于保存节点镜像用
     */
    private IndexMeta idxMeta;
    
    /**
     * 构造函数
     * 
     * @param shardName
     *            shardName
     * @param node
     *            node
     */
    public IndexShard(String shardName, Node node) {
        super();
        this.shardName = shardName;
        this.node = node;
    }
    
    /**
     * getter method for property shardName
     * 
     * @return the shardName
     */
    public String getShardName() {
        return shardName;
    }
    
    /**
     * setter method for property shardName
     * 
     * @param shardName
     *            the shardName to set
     */
    public void setShardName(String shardName) {
        this.shardName = shardName;
    }
    
    /**
     * getter method for property shardId
     * 
     * @return the shardId
     */
    public Long getShardId() {
        return shardId;
    }
    
    /**
     * setter method for property shardId
     * 
     * @param shardId
     *            the shardId to set
     */
    public void setShardId(Long shardId) {
        this.shardId = shardId;
    }
    
    /**
     * getter method for property node
     * 
     * @return the node
     */
    public Node getNode() {
        return node;
    }
    
    /**
     * setter method for property node
     * 
     * @param node
     *            the node to set
     */
    public void setNode(Node node) {
        this.node = node;
    }
    
    /**
     * getter method for property idxShardStrategy
     * 
     * @return the idxShardStrategy
     */
    public IndexShardStrategy getIdxShardStrategy() {
        return idxShardStrategy;
    }
    
    /**
     * setter method for property idxShardStrategy
     * 
     * @param idxShardStrategy
     *            the idxShardStrategy to set
     */
    public void setIdxShardStrategy(IndexShardStrategy idxShardStrategy) {
        this.idxShardStrategy = idxShardStrategy;
    }
    
    /**
     * getter method for property filePath
     * 
     * @return the filePath
     */
    public String getFilePath(Node node) {
        return this.filePath;
    }
    
    public String getFilePath() {
        return this.filePath;
    }
    
    public String getAbsoluteFilePath(Node node) {
        return this.concatIndexBaseDir(this.filePath, node);
    }
    
    public String getAbsoluteFilePath() {
        return this.concatIndexBaseDir(this.filePath, node);
    }
    
    /**
     * setter method for property filePath
     * 
     * @param filePath
     *            the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public void setFilePathWithAbsoluteFilePath(String absoluteFilePath, Node node) {
        
        this.filePath = this.trimIndexBaseDir(absoluteFilePath, node);
    }
    
    /**
     * getter method for property serialversionuid
     * 
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    
    /**
     * getter method for property defaultShardReplicaNum
     * 
     * @return the defaultShardReplicaNum
     */
    public static int getDefaultShardReplicaNum() {
        return DEFAULT_SHARD_REPLICA_NUM;
    }
    
    /**
     * getter method for property idxFilePath
     * 
     * @return the idxFilePath
     */
    public String getIdxFilePath(Node node) {
        
        return this.idxFilePath;
    }
    
    public String getIdxFilePath() {
        
        return this.idxFilePath;
    }
    
    public String getAbsoluteIdxFilePath(Node node) {
        
        return this.concatIndexBaseDir(this.idxFilePath, node);
    }
    
    public String getAbsoluteIdxFilePath() {
        
        return this.concatIndexBaseDir(this.idxFilePath, node);
    }
    
    /**
     * setter method for property idxFilePath
     * 
     * @param idxFilePath
     *            the idxFilePath to set
     */
    public void setIdxFilePath(String idxFilePath) {
        this.idxFilePath = idxFilePath;
    }
    
    public void setIdxFilePathWithAbsoluteIdxFilePath(String absoluteIdxFilePath, Node node) {
        this.idxFilePath = this.trimIndexBaseDir(absoluteIdxFilePath, node);
    }
    
    /**
     * getter method for property idxVersion
     * 
     * @return the idxVersion
     */
    public long getIdxVersion() {
        return idxVersion;
    }
    
    /**
     * setter method for property idxVersion
     * 
     * @param idxVersion
     *            the idxVersion to set
     */
    public void setIdxVersion(long idxVersion) {
        this.idxVersion = idxVersion;
    }
    
    /**
     * getter method for property isFull
     * 
     * @return the isFull
     */
    public boolean isFull() {
        if ((isFull == false) && this.diskSize >= IndexFileSystemConstants.DEFAULT_INDEX_SHARD_SIZE) {
            isFull = true;
        }
        return isFull;
    }
    
    /**
     * setter method for property isFull
     * 
     * @param isFull
     *            the isFull to set
     */
    public void setFull(boolean isFull) {
        this.isFull = isFull;
    }
    
    /**
     * getter method for property diskSize
     * 
     * @return the diskSize
     */
    public long getDiskSize() {
        return diskSize;
    }
    
    /**
     * setter method for property diskSize
     * 
     * @param diskSize
     *            the diskSize to set
     */
    public void setDiskSize(long diskSize) {
        this.diskSize = diskSize;
    }
    
    /**
     * getter method for property replicaNodeList
     * 
     * @return the replicaNodeList
     */
    public List<Node> getReplicaNodeList() {
        if (this.replicaNodeList == null) {
            this.replicaNodeList = new ArrayList<Node>();
        }
        return replicaNodeList;
    }
    
    /**
     * setter method for property replicaNodeList
     * 
     * @param replicaNodeList
     *            the replicaNodeList to set
     */
    public void setReplicaNodeList(List<Node> replicaNodeList) {
        this.replicaNodeList = replicaNodeList;
    }
    
    /**
     * getter method for property idxState
     * 
     * @return the idxState
     */
    public IndexState getIdxState() {
        return idxState;
    }
    
    /**
     * setter method for property idxState
     * 
     * @param idxState
     *            the idxState to set
     */
    public void setIdxState(IndexState idxState) {
        this.idxState = idxState;
    }
    
    /**
     * getter method for property idxMeta
     * 
     * @return the idxMeta
     */
    public IndexMeta getIndexMeta() {
        
        return idxMeta;
    }
    
    /**
     * setter method for property idxMeta
     * 
     * @param idxMeta
     *            the idxMeta to set
     */
    public void setIdxMeta(IndexMeta idxMeta) {
        this.idxMeta = idxMeta;
    }
    
    /**
     * 
     * concatIndexBaseDir
     * 
     * @param filePath
     *            相对路径
     * @param node
     *            节点
     * @return String
     */
    private String concatIndexBaseDir(String filePath, Node node) {
        StringBuilder sb = new StringBuilder();
        if (node != null && !StringUtils.isEmpty(node.getIndexBaseDir())) {
            sb.append(node.getIndexBaseDir());
            sb.append(File.separator);
        }
        sb.append(filePath);
        return sb.toString();
    }
    
    private String trimIndexBaseDir(String absolutePath, Node node) {
        String result = null;
        if (!StringUtils.isEmpty(absolutePath) && node != null
                && !StringUtils.isEmpty(node.getIndexBaseDir())) {
            result = absolutePath.substring((node.getIndexBaseDir() + File.separator).length());
        }
        return result;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
        result = prime * result + ((idxFilePath == null) ? 0 : idxFilePath.hashCode());
        result = prime * result + ((shardId == null) ? 0 : shardId.hashCode());
        result = prime * result + ((shardName == null) ? 0 : shardName.hashCode());
        return result;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        IndexShard other = (IndexShard) obj;
        if (filePath == null) {
            if (other.filePath != null) {
                return false;
            }
        } else if (!filePath.equals(other.filePath)) {
            return false;
        }
        if (idxFilePath == null) {
            if (other.idxFilePath != null) {
                return false;
            }
        } else if (!idxFilePath.equals(other.idxFilePath)) {
            return false;
        }
        if (shardId == null) {
            if (other.shardId != null) {
                return false;
            }
        } else if (!shardId.equals(other.shardId)) {
            return false;
        }
        if (shardName == null) {
            if (other.shardName != null) {
                return false;
            }
        } else if (!shardName.equals(other.shardName)) {
            return false;
        }
        return true;
    }
    
}
