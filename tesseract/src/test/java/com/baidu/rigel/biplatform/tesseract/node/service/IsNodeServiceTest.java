package com.baidu.rigel.biplatform.tesseract.node.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import com.baidu.rigel.biplatform.ac.util.Md5Util;
import com.baidu.rigel.biplatform.cache.StoreManager;
import com.baidu.rigel.biplatform.tesseract.isservice.meta.IndexShard;
import com.baidu.rigel.biplatform.tesseract.node.meta.Node;
import com.baidu.rigel.biplatform.tesseract.node.meta.NodeState;
import com.baidu.rigel.biplatform.tesseract.node.service.impl.IsNodeServiceImpl;

/**
 * 
 * ISNodeService的测试类
 * 
 * @author lijin
 *
 */
public class IsNodeServiceTest {
    /**
     * 需要注入Mock的isNodeService
     */
    @InjectMocks
    private IsNodeServiceImpl isNodeService;
    /**
     * Mock 一个storeManager
     */
    @Mock
    private StoreManager storeManager;
    
    @Mock
    private Node node;
    
    /**
     * 字符串常量-STR_CLUSTER_NAME
     */
    private static final String STR_CLUSTER_NAME = "testCluster";
    
    /**
     * 
     * 使用Mockito注解初始化Mock
     * 
     * @throws Exception
     *             有可能抛出异常
     */
    @Before
    public void initMocks() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    
    /**
     * 
     * 测试saveOrUpdateNodeInfo方法，使用null参数
     */
    @Test
    public void testSaveOrUpdateNodeInfoByNull() {
        Node node = null;
        boolean result = this.isNodeService.saveOrUpdateNodeInfo(node);
        Assert.assertFalse(result);
    }
    
    /**
     * 
     * Mock一个Cache
     * 
     * @return Cache
     */
    private Cache mockCache() {
        Cache cache = new ConcurrentMapCache(Node.getDataStoreName());
        
        List<Node> nodeList = new ArrayList<Node>();
        Node node1 = new Node("10.1.1.1", 8080, STR_CLUSTER_NAME);
        node1.setNodeState(NodeState.NODE_AVAILABLE);
        Node node2 = new Node("10.2.2.1", 8080, STR_CLUSTER_NAME);
        node2.setNodeState(NodeState.NODE_AVAILABLE);
        Node node3 = new Node("10.2.2.4", 8080, STR_CLUSTER_NAME);
        node3.setNodeState(NodeState.NODE_AVAILABLE);
        nodeList.add(node1);
        nodeList.add(node2);
        nodeList.add(node3);
        
        String storeKey = Md5Util.encode(STR_CLUSTER_NAME);
        
        cache.put(storeKey, nodeList);
        
        return cache;
        
    }
    
    /**
     * 
     * 测试getNodeListByClusterName方法
     */
    @Test
    public void testGetNodeListByClusterName() {
        String clusterName = null;
        // 1、clusterName is null
        List<Node> result = null;
        try {
            result = this.isNodeService.getNodeListByClusterName(clusterName);
        } catch (IllegalArgumentException e) {
            System.out.println("Exception occured");
        }
        Assert.assertNull(result);
        // 2、clusterName is ""
        clusterName = "";
        Mockito.when(this.storeManager.getDataStore(Mockito.anyString())).thenReturn(mockCache());
        try {
            result = this.isNodeService.getNodeListByClusterName(clusterName);
        } catch (IllegalArgumentException e) {
            System.out.println("Exception occured");
        }
        Assert.assertNull(result);
        // 3、clusterName is STR_CLUSTER_NAME
        clusterName = STR_CLUSTER_NAME;
        Mockito.when(this.storeManager.getDataStore(Mockito.anyString())).thenReturn(mockCache());
        result = this.isNodeService.getNodeListByClusterName(clusterName);
        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());
        
        clusterName = "test_no_data";
        Mockito.when(this.storeManager.getDataStore(Mockito.anyString())).thenReturn(mockCache());
        result = this.isNodeService.getNodeListByClusterName(clusterName);
        Assert.assertNull(result);
    }
    
    @Test
    public void testGetNodeByCurrNode() {
        
        // node == null
        Node result = null;
        try {
            result = this.isNodeService.getNodeByCurrNode(null);
            
        } catch (IllegalArgumentException e) {
            System.out.println("Exception occured");
        }
        Assert.assertNull(result);
        
        // node.getAddress() is empty
        Node node1 = new Node("", 8080, STR_CLUSTER_NAME);
        try {
            result = this.isNodeService.getNodeByCurrNode(node1);
            
        } catch (IllegalArgumentException e) {
            System.out.println("Exception occured");
        }
        Assert.assertNull(result);
        
        // node.getPort<=0
        node1 = new Node("10.0.0.1", 0, STR_CLUSTER_NAME);
        try {
            result = this.isNodeService.getNodeByCurrNode(node1);
            
        } catch (IllegalArgumentException e) {
            System.out.println("Exception occured");
        }
        Assert.assertNull(result);
        
        // node.getClusterName() is empty
        node1 = new Node("10.0.0.1", 8080, "");
        try {
            result = this.isNodeService.getNodeByCurrNode(node1);
            
        } catch (IllegalArgumentException e) {
            System.out.println("Exception occured");
        }
        Assert.assertNull(result);
        
        node1 = new Node("10.1.1.1", 8080, "abc");
        Mockito.when(this.storeManager.getDataStore(Mockito.anyString())).thenReturn(mockCache());
        try {
            result = this.isNodeService.getNodeByCurrNode(node1);
            
        } catch (IllegalArgumentException e) {
            System.out.println("Exception occured");
        }
        Assert.assertNull(result);
        
        Mockito.when(this.storeManager.getDataStore(Mockito.anyString())).thenReturn(mockCache());
        node1 = new Node("10.1.1.1", 8080, STR_CLUSTER_NAME);
        try {
            result = this.isNodeService.getNodeByCurrNode(node1);
            
        } catch (IllegalArgumentException e) {
            System.out.println("Exception occured");
        }
        Assert.assertNotNull(result);
        Assert.assertEquals(node1, result);
        
        node1 = new Node("10.123.1.1", 8080, STR_CLUSTER_NAME);
        Mockito.when(this.storeManager.getDataStore(Mockito.anyString())).thenReturn(mockCache());
        try {
            result = this.isNodeService.getNodeByCurrNode(node1);
            
        } catch (IllegalArgumentException e) {
            System.out.println("Exception occured");
        }
        Assert.assertNull(result);
        
    }
    
    @Test
    public void testGetCurrentNode() {
        Node result = this.isNodeService.getCurrentNode();
        Assert.assertEquals(this.node, result);
    }
    
    @Test
    public void testSaveNodeImage() {
        // node == null
        boolean result = true;
        try {
            this.isNodeService.saveNodeImage(null);
        } catch (IllegalArgumentException e) {
            result = false;
        }
        Assert.assertFalse(result);
        
        // node.getClusterName() is empty
        Node node1 = new Node("10.1.1.1", 8080, "");
        result = true;
        try {
            this.isNodeService.saveNodeImage(node1);
        } catch (IllegalArgumentException e) {
            result = false;
        }
        Assert.assertFalse(result);
        
        node1 = new Node("10.1.1.1", 8080, STR_CLUSTER_NAME);
        result = true;
        try {
            this.isNodeService.saveNodeImage(node1);
        } catch (IllegalArgumentException e) {
            result = false;
        }
        Assert.assertTrue(result);
    }
    
    @Test
    public void testLoadLocalNodeImage() {
        // node ==null
        boolean result = true;
        try {
            this.isNodeService.loadLocalNodeImage(null);
        } catch (IllegalArgumentException e) {
            result = false;
        }
        Assert.assertFalse(result);
        
        // node.getClusterName() is empty
        Node node1 = new Node("10.1.1.1", 8080, "");
        result = true;
        try {
            this.isNodeService.loadLocalNodeImage(node1);
        } catch (IllegalArgumentException e) {
            result = false;
        }
        Assert.assertFalse(result);
        
        node1 = new Node("10.144.1.1", 8087, STR_CLUSTER_NAME);
        Mockito.when(this.storeManager.getDataStore(Mockito.anyString())).thenReturn(mockCache());
        Node resultNode = null;
        try {
            resultNode = this.isNodeService.loadLocalNodeImage(node1);
        } catch (IllegalArgumentException e) {
            System.out.println("Exception occured");
        }
        Assert.assertNotNull(resultNode);
        Assert.assertEquals(node1, resultNode);
        
        node1 = new Node("10.144.1.1", 8089, STR_CLUSTER_NAME);
        node1.setBlockSize(-9);
        Mockito.when(this.storeManager.getDataStore(Mockito.anyString())).thenReturn(mockCache());
        
        resultNode = null;
        try {
            this.isNodeService.saveNodeImage(node1);
            node1.setBlockSize(10);
            resultNode = this.isNodeService.loadLocalNodeImage(node1);
        } catch (IllegalArgumentException e) {
            System.out.println("Exception occured");
        }
        Assert.assertNotNull(resultNode);
        Assert.assertEquals(-9, resultNode.getBlockSize());
        
    }
    
    @Test
    public void testMarkClusterBadNode() {
        boolean result = true;
        try {
            this.isNodeService.markClusterBadNode();
        } catch (IllegalArgumentException e) {
            result = false;
        }
        Assert.assertFalse(result);
        
        result = true;
        Node node1 = new Node("10.144.1.1", 8089, "abc");
        Mockito.when(this.storeManager.getDataStore(Mockito.anyString())).thenReturn(mockCache());
        this.isNodeService.setNode(node1);
        try {
            this.isNodeService.markClusterBadNode();
        } catch (IllegalArgumentException e) {
            result = false;
        }
        Assert.assertTrue(result);
        
        node1 = new Node("10.144.1.1", 8080, STR_CLUSTER_NAME);
        this.isNodeService.setNode(node1);
        Cache cache = this.mockCache();
        List<Node> nodeList = (List<Node>) cache.get(Md5Util.encode(STR_CLUSTER_NAME)).get();
        for (Node curr : nodeList) {
            curr.setNodeState(NodeState.NODE_UNAVAILABLE);
        }
        cache.put(Md5Util.encode(STR_CLUSTER_NAME), nodeList);
        Mockito.when(this.storeManager.getDataStore(Mockito.anyString())).thenReturn(cache);
        try {
            this.isNodeService.markClusterBadNode();
        } catch (IllegalArgumentException e) {
            result = false;
        }
        Assert.assertTrue(result);
    }
    
    @Test
    public void testGetAvailableNodeListByIndexShard() {
        List<Node> result = null;
        try {
            result = this.isNodeService.getAvailableNodeListByIndexShard(null, "abc");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception occured");
        }
        Assert.assertNull(result);
        
        Node node1 = new Node("10.144.1.1", 8089, "abc");
        IndexShard idxShard = new IndexShard("testShard", node1);
        Mockito.when(this.storeManager.getDataStore(Mockito.anyString())).thenReturn(mockCache());
        try {
            result = this.isNodeService.getAvailableNodeListByIndexShard(idxShard, "abc");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception occured");
        }
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
        
        node1 = new Node("10.1.1.1", 8080, STR_CLUSTER_NAME);
        idxShard = new IndexShard("testShard", node1);
        Mockito.when(this.storeManager.getDataStore(Mockito.anyString())).thenReturn(mockCache());
        try {
            result = this.isNodeService
                .getAvailableNodeListByIndexShard(idxShard, STR_CLUSTER_NAME);
        } catch (IllegalArgumentException e) {
            System.out.println("Exception occured");
        }
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
    }
    
    @Test
    public void testGetNodeByNodeKey() {
        Node result = null;
        
        // clusterName is null
        result = this.isNodeService.getNodeByNodeKey(null, "abc", false);
        Assert.assertNull(result);
        // nodeKey is null
        result = this.isNodeService.getNodeByNodeKey("abc", "", false);
        Assert.assertNull(result);
        
        result = this.isNodeService.getNodeByNodeKey("", "", false);
        Assert.assertNull(result);
        
        Node node1 = new Node("10.1.1.1", 8080, STR_CLUSTER_NAME);
        Mockito.when(this.storeManager.getDataStore(Mockito.anyString())).thenReturn(mockCache());
        result = this.isNodeService.getNodeByNodeKey(STR_CLUSTER_NAME, node1.getNodeKey(), false);
        Assert.assertNotNull(result);
        Assert.assertEquals(node1, result);
        
        result = this.isNodeService.getNodeByNodeKey("abc", node1.getNodeKey(), false);
        Assert.assertNull(result);
        
        result = this.isNodeService.getNodeByNodeKey(STR_CLUSTER_NAME, "abc", false);
        Assert.assertNull(result);
    }
    
    @Test
    public void testAssignFreeNodeByNodeList() {
        Map<Node, Integer> result = null;
        // existNodeList is null
        boolean isException = false;
        try {
            result = this.isNodeService.assignFreeNodeByNodeList(null, 1, "");
        } catch (IllegalArgumentException e) {
            isException = true;
        }
        Assert.assertTrue(isException);
        
        Mockito.when(this.storeManager.getDataStore(Mockito.anyString())).thenReturn(mockCache());
        try {
            result = this.isNodeService.assignFreeNodeByNodeList(null, 1, STR_CLUSTER_NAME);
        } catch (IllegalArgumentException e) {
            isException = true;
        }
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
        
        Cache cache = this.mockCache();
        List<Node> nodeList = (List<Node>) cache.get(Md5Util.encode(STR_CLUSTER_NAME)).get();
        for (Node curr : nodeList) {
            curr.setCurrBlockUsed(-1);
        }
        cache.put(Md5Util.encode(STR_CLUSTER_NAME), nodeList);
        Mockito.when(this.storeManager.getDataStore(Mockito.anyString())).thenReturn(cache);
        try {
            result = this.isNodeService.assignFreeNodeByNodeList(null, 1, STR_CLUSTER_NAME);
        } catch (IllegalArgumentException e) {
            isException = true;
        }
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        
        cache = this.mockCache();
        nodeList = (List<Node>) cache.get(Md5Util.encode(STR_CLUSTER_NAME)).get();
        for (Node curr : nodeList) {
            curr.setCurrBlockUsed(-1);
        }
        nodeList.get(0).setBlockCapacity(3);
        nodeList.get(1).setNodeState(NodeState.NODE_UNAVAILABLE);
        cache.put(Md5Util.encode(STR_CLUSTER_NAME), nodeList);
        Mockito.when(this.storeManager.getDataStore(Mockito.anyString())).thenReturn(cache);
        try {
            result = this.isNodeService.assignFreeNodeByNodeList(null, 1, STR_CLUSTER_NAME);
        } catch (IllegalArgumentException e) {
            isException = true;
        }
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        
    }
    
    @Test
    public void testAssignFreeNodeForReplica() {
        Map<String, Node> result = null;
        Mockito.when(this.storeManager.getDataStore(Mockito.anyString())).thenReturn(mockCache());
        result = this.isNodeService.assignFreeNodeForReplica(1, "abc", "abc");
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
        
        result = this.isNodeService.assignFreeNodeForReplica(-1, "abc", STR_CLUSTER_NAME);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
        
        result = this.isNodeService.assignFreeNodeForReplica(1, "abc", STR_CLUSTER_NAME);
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
        
        Cache cache = this.mockCache();
        List<Node> nodeList = (List<Node>) cache.get(Md5Util.encode(STR_CLUSTER_NAME)).get();
        nodeList.get(2).setCurrBlockUsed(1);
        nodeList.get(2).setBlockCapacity(3);
        cache.put(Md5Util.encode(STR_CLUSTER_NAME), nodeList);
        Mockito.when(this.storeManager.getDataStore(Mockito.anyString())).thenReturn(cache);
        Node node1 = new Node("10.1.1.1", 8080, STR_CLUSTER_NAME);
        result = this.isNodeService.assignFreeNodeForReplica(1, node1.getNodeKey(),
            STR_CLUSTER_NAME);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
    }
    
}
