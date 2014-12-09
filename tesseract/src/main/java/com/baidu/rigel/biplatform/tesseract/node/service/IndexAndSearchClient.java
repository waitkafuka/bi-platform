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
package com.baidu.rigel.biplatform.tesseract.node.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.baidu.rigel.biplatform.tesseract.isservice.exception.IndexAndSearchException;
import com.baidu.rigel.biplatform.tesseract.isservice.exception.IndexAndSearchExceptionType;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexShard;
import com.baidu.rigel.biplatform.tesseract.isservice.netty.service.FileClientHandler;
import com.baidu.rigel.biplatform.tesseract.isservice.netty.service.IndexClientHandler;
import com.baidu.rigel.biplatform.tesseract.isservice.netty.service.SearchClientHandler;
import com.baidu.rigel.biplatform.tesseract.netty.AbstractChannelInboundHandler;
import com.baidu.rigel.biplatform.tesseract.netty.exception.HandlerRegistException;
import com.baidu.rigel.biplatform.tesseract.netty.message.AbstractMessage;
import com.baidu.rigel.biplatform.tesseract.netty.message.MessageHeader;
import com.baidu.rigel.biplatform.tesseract.netty.message.NettyAction;
import com.baidu.rigel.biplatform.tesseract.netty.message.isservice.IndexMessage;
import com.baidu.rigel.biplatform.tesseract.netty.message.isservice.SearchRequestMessage;
import com.baidu.rigel.biplatform.tesseract.netty.message.isservice.SearchResultMessage;
import com.baidu.rigel.biplatform.tesseract.netty.message.isservice.SendFileMessage;
import com.baidu.rigel.biplatform.tesseract.netty.message.isservice.ServerExceptionMessage;
import com.baidu.rigel.biplatform.tesseract.netty.message.isservice.ServerFeedbackMessage;
import com.baidu.rigel.biplatform.tesseract.node.meta.Node;
import com.baidu.rigel.biplatform.tesseract.qsservice.query.vo.QueryRequest;
import com.baidu.rigel.biplatform.tesseract.resultset.TesseractResultSet;
import com.baidu.rigel.biplatform.tesseract.util.FileUtils;
import com.baidu.rigel.biplatform.tesseract.util.IndexFileSystemConstants;
import com.baidu.rigel.biplatform.tesseract.util.TesseractConstant;
import com.baidu.rigel.biplatform.tesseract.util.TesseractExceptionUtils;
import com.baidu.rigel.biplatform.tesseract.util.isservice.LogInfoConstants;

/**
 * IndexAndSearchClient 用spring管理，单例
 * 
 * @author lijin
 *
 */

public class IndexAndSearchClient {
    /**
     * logger
     */
    private Logger logger = LoggerFactory.getLogger(IndexAndSearchClient.class);
    
    /**
     * isNodeService
     */
    @Resource(name = "isNodeService")
    private IsNodeService isNodeService;
    
    /**
     * LOCAL_HOST_ADDRESS
     */
    private static final String LOCAL_HOST_ADDRESS = "127.0.0.1";
    
    // private ConcurrentHashMap<NodeAddress, Channel> channelMaps;
    //private ConcurrentHashMap<String, ChannelHandler> actionHandlerMaps;
    private Bootstrap b;
    private EventLoopGroup group;
    
    private static IndexAndSearchClient INSTANCE;
    
    public IndexAndSearchClient() {
        // channelMaps = new ConcurrentHashMap<NodeAddress, Channel>();
        //actionHandlerMaps = new ConcurrentHashMap<String, ChannelHandler>();
        b = new Bootstrap();
        group = new NioEventLoopGroup();
        b.group(group);
        b.channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true);
        b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("encode", new ObjectEncoder());
                pipeline.addLast("decode",
                    new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
//                pipeline.addLast("frameencoder",new LengthFieldPrepender(4,false));
//                pipeline.addLast("framedecoder",new LengthFieldBasedFrameDecoder(1024*1024*1024, 0, 4,0,4));
            }
        });
        
        logger.info("IndexAndSearchClient init finished");
    }
    
    public static synchronized IndexAndSearchClient getNodeClient() {
        if (INSTANCE == null) {
            INSTANCE = new IndexAndSearchClient();
        }
        return INSTANCE;
    }
    
    public class NodeAddress {
        private String ip;
        private int port;
        
        /**
         * @param ip
         * @param port
         */
        public NodeAddress(String ip, int port) {
            super();
            this.ip = ip;
            this.port = port;
        }
        
        /**
         * getter method for property ip
         * 
         * @return the ip
         */
        public String getIp() {
            return ip;
        }
        
        /**
         * setter method for property ip
         * 
         * @param ip
         *            the ip to set
         */
        public void setIp(String ip) {
            this.ip = ip;
        }
        
        /**
         * getter method for property port
         * 
         * @return the port
         */
        public int getPort() {
            return port;
        }
        
        /**
         * setter method for property port
         * 
         * @param port
         *            the port to set
         */
        public void setPort(int port) {
            this.port = port;
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
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((ip == null) ? 0 : ip.hashCode());
            result = prime * result + port;
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
            if (!(obj instanceof NodeAddress)) {
                return false;
            }
            NodeAddress other = (NodeAddress) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (ip == null) {
                if (other.ip != null) {
                    return false;
                }
            } else if (!ip.equals(other.ip)) {
                return false;
            }
            if (port != other.port) {
                return false;
            }
            return true;
        }
        
        private IndexAndSearchClient getOuterType() {
            return IndexAndSearchClient.this;
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "NodeAddress [ip=" + ip + ", port=" + port + "]";
        }
        
    }
    
//    public ChannelHandler getActionHandler(String actionMove) {
//        logger.info("getActionHandler:[actionMove=" + actionMove + "]");
//        if (!StringUtils.isEmpty(actionMove) && this.actionHandlerMaps.containsKey(actionMove)) {
//            return this.actionHandlerMaps.get(actionMove);
//        }
//        logger.info("getActionHandler:[actionMove=" + actionMove + "] has no handler");
//        return null;
//    }
    
//    private boolean registerActionHandler(NettyAction nettyAction,
//        AbstractChannelInboundHandler handler) throws InstantiationException,
//        IllegalAccessException {
//        logger.info("registerActionHandler:[NettyAction=" + nettyAction + "][handler=" + handler
//            + "] start");
//        if (nettyAction == null || handler == null) {
//            return false;
//        }
//        this.actionHandlerMaps.put(nettyAction.getActionName(), handler);
//        logger.info("registerActionHandler:[NettyAction=" + nettyAction + "][handler=" + handler
//            + "] success");
//        return true;
//    }
    
    public Channel getChannelByAddressAndPort(String ipAddress, int port)
        throws IndexAndSearchException {
        logger.info("getChannelByAddressAndPort:[address=" + ipAddress + "][port=" + port
            + "] start");
        if (StringUtils.isEmpty(ipAddress) || port <= 0) {
            throw new IndexAndSearchException(TesseractExceptionUtils.getExceptionMessage(
                IndexAndSearchException.INDEXEXCEPTION_MESSAGE,
                IndexAndSearchExceptionType.ILLEGALARGUMENT_EXCEPTION),
                IndexAndSearchExceptionType.ILLEGALARGUMENT_EXCEPTION);
        }
        Channel channel = null;
        // NodeAddress nodeAddr = new NodeAddress(address, port);
        String address = ipAddress;
        try {
            InetAddress currAddress = InetAddress.getLocalHost();
            
            if (ipAddress.equals(currAddress.getHostAddress())) {
                address = LOCAL_HOST_ADDRESS;
            }
            if (b != null) {
                channel = b.connect(address, port).sync().channel();
                // this.channelMaps.put(nodeAddr, channel);
                logger.info("getChannelByAddressAndPort:connect server success [address=" + address
                    + "][port=" + port + "]");
            }
            
        } catch (Exception e) {
            logger.error("getChannelByAddressAndPort:connect server [address=" + address
                + "][port=" + port + "] exception.", e);
            throw new IndexAndSearchException(TesseractExceptionUtils.getExceptionMessage(
                IndexAndSearchException.INDEXEXCEPTION_MESSAGE,
                IndexAndSearchExceptionType.INDEX_EXCEPTION), e,
                IndexAndSearchExceptionType.INDEX_EXCEPTION);
        }
        
        logger.info("getChannelByAddressAndPort:[address=" + address + "][port=" + port
            + "] connect sucess");
        return channel;
        
    }
    
    public IndexMessage index(TesseractResultSet data, boolean isInit, boolean isUpdate, IndexShard idxShard,
        String idName, boolean lastPiece) throws IndexAndSearchException {
        logger.info("index:[data=" + data + "][isUpdate=" + isUpdate + "][idxShard=" + idxShard
            + "][idName:" + idName + "] start");
        if (data == null || idxShard == null || StringUtils.isEmpty(idxShard.getFilePath())
                || StringUtils.isEmpty(idxShard.getIdxFilePath())) {
            throw new IndexAndSearchException(TesseractExceptionUtils.getExceptionMessage(
                IndexAndSearchException.INDEXEXCEPTION_MESSAGE,
                IndexAndSearchExceptionType.ILLEGALARGUMENT_EXCEPTION),
                IndexAndSearchExceptionType.ILLEGALARGUMENT_EXCEPTION);
        }
        
        NettyAction action = null;
        if (isUpdate) {
            action = NettyAction.NETTY_ACTION_UPDATE;
        } else if (isInit) {
            action = NettyAction.NETTY_ACTION_INITINDEX;
        } else {
            action = NettyAction.NETTY_ACTION_INDEX;
        }
        MessageHeader messageHeader = new MessageHeader(action, data.toString());
        IndexMessage message = new IndexMessage(messageHeader, data);
        message.setIdxPath(idxShard.getAbsoluteFilePath());
        message.setIdxServicePath(idxShard.getAbsoluteIdxFilePath());
        message.setBlockSize(IndexFileSystemConstants.DEFAULT_INDEX_SHARD_SIZE);
        message.setIdName(idName);
        message.setLastPiece(lastPiece);
//        List<String> measureInfoList = new ArrayList<String>();
//        measureInfoList.addAll(idxShard.getIndexMeta().getMeasureInfoMap().keySet());
//        message.setMeasureInfo(measureInfoList);
        logger.info("ready to send index message:" + message.toString());
        AbstractMessage ret = null;
        IndexMessage result = null;
        IndexClientHandler handler = new IndexClientHandler();
        try {
            ret = this.executeAction(action, message, handler, idxShard.getNode());
            if (ret instanceof IndexMessage) {
                result = (IndexMessage) ret;
            } else {
                throw new IndexAndSearchException(TesseractExceptionUtils.getExceptionMessage(
                    IndexAndSearchException.INDEXEXCEPTION_MESSAGE,
                    IndexAndSearchExceptionType.INDEX_EXCEPTION),
                    ((ServerExceptionMessage) ret).getCause(),
                        IndexAndSearchExceptionType.INDEX_EXCEPTION);
            }
        } catch (Exception e) {
            throw new IndexAndSearchException(TesseractExceptionUtils.getExceptionMessage(
                IndexAndSearchException.INDEXEXCEPTION_MESSAGE,
                IndexAndSearchExceptionType.INDEX_EXCEPTION), e.getCause(),
                IndexAndSearchExceptionType.INDEX_EXCEPTION);
        }
        logger.info("index:[data=" + data + "][isUpdate=" + isUpdate + "][idxShard=" + idxShard
            + "] finished index ");
        return result;
    }
    
    
    
    public ServerFeedbackMessage copyIndexDataToRemoteNode(String filePath, String targetFilePath, boolean replace,
        Node node) throws IndexAndSearchException {
        logger.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_BEGIN,
            "copyIndexDataToRemoteNode", "[filePath:" + filePath + "][replace:" + replace
                + "][Node:" + node + "]"));
        
        if (StringUtils.isEmpty(filePath) || node == null) {
            logger.info(String.format(LogInfoConstants.INFO_PATTERN_FUNCTION_EXCEPTION,
                "copyIndexDataToRemoteNode", "[filePath:" + filePath + "][replace:" + replace
                    + "][nodeList:" + node + "]"));
            throw new IllegalArgumentException();
        }
        // 压缩
        String compressedFilePath = filePath + ".tar.gz";
        File compressedFile = new File(compressedFilePath);
        compressedFile.deleteOnExit();
        
        try {
            compressedFilePath = FileUtils.doCompressFile(filePath);
        } catch (IOException e2) {
            throw new IndexAndSearchException(TesseractExceptionUtils.getExceptionMessage(
                IndexAndSearchException.INDEXEXCEPTION_MESSAGE,
                IndexAndSearchExceptionType.INDEX_EXCEPTION), e2,
                    IndexAndSearchExceptionType.INDEX_EXCEPTION);
        }
        
        // 读文件
        File fin = new File(compressedFilePath);
        FileChannel fcin = null;
        try {
            fcin = new RandomAccessFile(fin, "r").getChannel();
        } catch (FileNotFoundException e1) {
            throw new IndexAndSearchException(TesseractExceptionUtils.getExceptionMessage(
                IndexAndSearchException.INDEXEXCEPTION_MESSAGE,
                IndexAndSearchExceptionType.INDEX_EXCEPTION), e1,
                    IndexAndSearchExceptionType.INDEX_EXCEPTION);
        }
        ByteBuffer rBuffer = ByteBuffer.allocate(TesseractConstant.FILE_BLOCK_SIZE);
        ServerFeedbackMessage backMessage = null;
        boolean isFirst = true;
        try {
            while (true) {
                rBuffer.clear();
                int r = fcin.read(rBuffer);
                
                if (r == -1) {
                    break;
                }
                boolean isLast = false;
                if (rBuffer.position() < rBuffer.capacity()) {
                    isLast = true;
                }
                rBuffer.flip();
                
                NettyAction action = NettyAction.NETTY_ACTION_COPYFILE;
                MessageHeader mh = new MessageHeader(action);
                SendFileMessage sfm = new SendFileMessage(mh, rBuffer.array(), targetFilePath
                        + File.separator + fin.getName());
                if (isFirst) {
                    sfm.setFirst(isFirst);
                } else {
                    sfm.setFirst(false);
                }
                
                sfm.setLast(isLast);

                FileClientHandler handler = new FileClientHandler();
                AbstractMessage bMessage = this.executeAction(action, sfm, handler, node);
                
                if (bMessage instanceof ServerFeedbackMessage) {
                    backMessage = (ServerFeedbackMessage) bMessage;
                } else {
                    throw new IndexAndSearchException(TesseractExceptionUtils.getExceptionMessage(
                        IndexAndSearchException.INDEXEXCEPTION_MESSAGE,
                        IndexAndSearchExceptionType.INDEX_EXCEPTION),
                        ((ServerExceptionMessage) bMessage).getCause(),
                            IndexAndSearchExceptionType.INDEX_EXCEPTION);
                }
                
                if (backMessage == null
                        || backMessage.getResult().equals(TesseractConstant.FEED_BACK_MSG_RESULT_FAIL)) {
                    throw new IndexAndSearchException(TesseractExceptionUtils.getExceptionMessage(
                        IndexAndSearchException.INDEXEXCEPTION_MESSAGE,
                        IndexAndSearchExceptionType.INDEX_EXCEPTION),
                        IndexAndSearchExceptionType.INDEX_EXCEPTION);
                }
                
            }
        } catch (Exception e) {
            throw new IndexAndSearchException(TesseractExceptionUtils.getExceptionMessage(
                IndexAndSearchException.INDEXEXCEPTION_MESSAGE,
                IndexAndSearchExceptionType.INDEX_EXCEPTION), e.getCause(),
                IndexAndSearchExceptionType.INDEX_EXCEPTION);
        } finally {
            try {
                fcin.close();
            } catch (IOException e) {
                throw new IndexAndSearchException(TesseractExceptionUtils.getExceptionMessage(
                    IndexAndSearchException.INDEXEXCEPTION_MESSAGE,
                    IndexAndSearchExceptionType.INDEX_EXCEPTION), e.getCause(),
                    IndexAndSearchExceptionType.INDEX_EXCEPTION);
            }
        }
        return backMessage;
    }
    
    public SearchResultMessage search(QueryRequest query, IndexShard idxShard, Node searchNode)
        throws IndexAndSearchException {
        NettyAction action = NettyAction.NETTY_ACTION_SEARCH;
        
        MessageHeader messageHeader = new MessageHeader(action);
        
        SearchRequestMessage message = new SearchRequestMessage(messageHeader, query);
        message.setIdxPath(idxShard.getAbsoluteIdxFilePath(searchNode));
        
        AbstractMessage ret = null;
        
        SearchClientHandler handler = new SearchClientHandler();
        SearchResultMessage result = null;
        
        try {
            ret = this.executeAction(action, message, handler, searchNode);
            if (ret instanceof SearchResultMessage) {
                result = (SearchResultMessage) ret;
            } else {
                throw new IndexAndSearchException(TesseractExceptionUtils.getExceptionMessage(
                    IndexAndSearchException.QUERYEXCEPTION_MESSAGE,
                    IndexAndSearchExceptionType.SEARCH_EXCEPTION),
                    ((ServerExceptionMessage) ret).getCause(),
                        IndexAndSearchExceptionType.SEARCH_EXCEPTION);
            }
        } catch (Exception e) {
            throw new IndexAndSearchException(TesseractExceptionUtils.getExceptionMessage(
                IndexAndSearchException.QUERYEXCEPTION_MESSAGE,
                IndexAndSearchExceptionType.SEARCH_EXCEPTION), e.getCause(),
                IndexAndSearchExceptionType.SEARCH_EXCEPTION);
        }
        
        return result;
    }
    
    public <T extends AbstractMessage, R extends AbstractMessage, S extends AbstractChannelInboundHandler> T executeAction(
        NettyAction action, R message, S handler, Node node) throws Exception {
        logger.info("executeAction:[NettyAction=" + action + "][Message=" + message + "][Handler="
            + handler + "]");
        T returnMessage = null;
        if (action == null || handler == null) {
            logger.info("executeAction:[NettyAction=" + action + "][Message=" + message
                + "][Handler=" + handler + "]-Exception:IllegalArgumentException");
            throw new IllegalArgumentException();
        }
//        if (!registerActionHandler(action, handler)) {
//            logger.info("executeAction-Exception:HandlerRegistException");
//            throw new HandlerRegistException();
//        }
        Channel channel = null;
        channel = this.getChannelByAddressAndPort(node.getAddress(), node.getPort());
        channel.pipeline().addLast(handler);
        
        channel.writeAndFlush(message);
        channel.closeFuture().sync();
        returnMessage = handler.getMessage();
        
        handler.setMessage(null);
        
        logger.info("executeAction:[NettyAction=" + action + "][Message=" + message + "][Handler="
            + handler + "] success");
        
        return returnMessage;
        
    }
    
    public void shutDown() {
        
//        if (this.actionHandlerMaps != null && !this.actionHandlerMaps.isEmpty()) {
//            this.actionHandlerMaps.clear();
//        }
        this.b.group().shutdownGracefully();
    }
    
//    public ConcurrentHashMap<String, ChannelHandler> getActionHandlerMaps() {
//        if (this.actionHandlerMaps == null) {
//            this.actionHandlerMaps = new ConcurrentHashMap<String, ChannelHandler>();
//        }
//        return actionHandlerMaps;
//    }
//    
}
