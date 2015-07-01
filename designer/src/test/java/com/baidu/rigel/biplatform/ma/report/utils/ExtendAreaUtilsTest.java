package com.baidu.rigel.biplatform.ma.report.utils;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.rigel.biplatform.ma.report.model.ExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.ExtendAreaType;

/**
 * ExtendAreaUtils单测
 * 
 * @author majun04
 *
 */
public class ExtendAreaUtilsTest {
    @Test
    public void testGenereateExtendAreas() {
        List<ExtendArea> resultList =
                ExtendAreaUtils.genereateExtendAreas(ExtendAreaType.LITEOLAP.name(), "testReferenceAreaId");
        Assert.assertEquals(4, resultList.size());
    }

}
