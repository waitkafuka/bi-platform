package com.baidu.rigel.biplatform.ma.report.utils;

import java.util.LinkedHashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * LinkedHashMapUtils单测
 * 
 * @author majun04
 *
 */
public class LinkedHashMapUtilsTest {
    private LinkedHashMap<String, String> oldMap;

    @Before
    public void init() {
        oldMap = new LinkedHashMap<String, String>();
    }

    @Test
    public void testBuildLinkedMapWithNewEntry() {
        LinkedHashMap<String, String> newLinkedMap =
                LinkedHashMapUtils.buildLinkedMapWithNewEntry(oldMap, "testValue", "testKey", 2);
        Assert.assertEquals(newLinkedMap.size(), 1);
    }

    @Test
    public void testBuildLinkedMapWithNewEntry2() {
        oldMap.put("testKey1", "testValue1");
        LinkedHashMap<String, String> newLinkedMap =
                LinkedHashMapUtils.buildLinkedMapWithNewEntry(oldMap, "testValue", "testKey", 0);
        Assert.assertEquals(newLinkedMap.size(), 2);
    }
}
