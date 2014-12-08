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
package com.baidu.rigel.biplatform.tesseract.netty.message.isservice;

import java.io.Serializable;

import com.baidu.rigel.biplatform.tesseract.netty.message.AbstractMessage;
import com.baidu.rigel.biplatform.tesseract.netty.message.MessageHeader;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryRequest;

/**
 * SearchRequestMessage
 * 
 * @author lijin
 *
 */
public class SearchRequestMessage extends AbstractMessage {
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1566435422800554951L;
    /**
     * queryRequest
     */
    private QueryRequest queryRequest;
    /**
     * idxPath
     */
    private String idxPath;
    
    /**
     * 
     * Constructor by
     * 
     * @param messageHeader
     *            messageHeader
     */
    public SearchRequestMessage(MessageHeader messageHeader) {
        super(messageHeader);
        
    }
    
    /**
     * 
     * Constructor by
     * 
     * @param messageHeader
     *            messageHeader
     * @param data
     *            data
     */
    public SearchRequestMessage(MessageHeader messageHeader, QueryRequest data) {
        super(messageHeader);
        this.queryRequest = data;
        this.messageHeader.setMd5sum(this.getMessageBodyMd5sum());
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.rigel.biplatform.tesseract.netty.message.AbstractMessage#
     * getMessageBody()
     */
    @Override
    public Serializable getMessageBody() {
        return this.queryRequest;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.rigel.biplatform.tesseract.netty.message.AbstractMessage#
     * getMessageBodyMd5sum()
     */
    @Override
    public String getMessageBodyMd5sum() {
        String result = null;
        if (this.queryRequest != null) {
            result = Integer.toString(this.queryRequest.hashCode());
        }
        return result;
        
    }
    
    /**
     * getter method for property idxPath
     * 
     * @return the idxPath
     */
    public String getIdxPath() {
        return idxPath;
    }
    
    /**
     * setter method for property idxPath
     * 
     * @param idxPath
     *            the idxPath to set
     */
    public void setIdxPath(String idxPath) {
        this.idxPath = idxPath;
    }
    
}
