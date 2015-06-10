package com.baidu.rigel.biplatform.ma.report.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class ContextManagerTest {

    @Test
    public void testSetSessionId() {
        ContextManager.setSessionId("testSessionId");
        Assert.assertEquals("testSessionId", ContextManager.getSessionId());
    }

    @Test
    public void testCleanSessionId() {
        ContextManager.setSessionId("testSessionId");
        ContextManager.cleanSessionId();
        Assert.assertEquals(null, ContextManager.getSessionId());
    }

    @Test
    public void testGetParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("testKey", "testValue");
        ContextManager.setParams(params);
        Map<String, String> resultMap = ContextManager.getParams();
        Assert.assertEquals(resultMap.size(), 1);
    }

    @Test
    public void testCleanParams() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("testKey", "testValue");
        ContextManager.setParams(params);
        ContextManager.cleanParams();
        Map<String, String> resultMap = ContextManager.getParams();
        Assert.assertEquals(resultMap.size(), 0);
    }

    @Test
    public void testSetProductLine() {
        ContextManager.setProductLine("testProductLine");
        Assert.assertEquals(ContextManager.getProductLine(), "testProductLine");
    }

    @Test
    public void testCleanProductLine() {
        ContextManager.setProductLine("testProductLine");
        ContextManager.cleanProductLine();
        Assert.assertNull(ContextManager.getProductLine());
    }

}
