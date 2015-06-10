package com.baidu.rigel.biplatform.ma.report.utils;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.baidu.rigel.biplatform.ac.minicube.CallbackLevel;
import com.baidu.rigel.biplatform.ac.model.Member;
import com.baidu.rigel.biplatform.ac.query.data.DataModel;

/**
 * MockUtils单测类
 * 
 * @author majun04
 *
 */
public class MockUtilsTest {
    private CallbackLevel callbackLevel;

    @Before
    public void init() {
        callbackLevel = new CallbackLevel();
    }

    @Test
    public void testMockMembers() {
        List<Member> memberList = MockUtils.mockMembers(callbackLevel);
        Assert.assertEquals(10, memberList.size());
    }

    @Test
    public void testMockDataModel() {
        DataModel dataModel = MockUtils.mockDataModel();
        Assert.assertNotNull(dataModel);
    }

    @Test
    public void testMockBreadCrumbs() {
        List<Map<String, String>> ls = MockUtils.mockBreadCrumbs();
        Assert.assertNotNull(ls);
    }
}
