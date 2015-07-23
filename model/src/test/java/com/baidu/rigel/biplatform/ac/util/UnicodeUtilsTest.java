package com.baidu.rigel.biplatform.ac.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * 汉字转unicode test
 * 
 * @author luowenlei
 *
 */
public class UnicodeUtilsTest {

    @Test
    public void testString2Unicode() {
        String chn = "'d你好d‘";
        String unicode = UnicodeUtils.string2Unicode(chn);
        Assert.assertEquals(UnicodeUtils.unicode2String(unicode), chn);
    }

    @Test
    public void testUnicode2String() {
        String chn = "'d你好d‘";
        String unicode = UnicodeUtils.string2Unicode(chn);
        Assert.assertEquals(UnicodeUtils.unicode2String(unicode), chn);
    }

    @Test
    public void testUnicode2StringNoPrefix() {
        String chn = "3434";
        Assert.assertEquals(UnicodeUtils.unicode2String(chn), chn);
    }
    
    @Test
    public void testUnicode2StringNumber() {
        String chn = "";
        String unicode = UnicodeUtils.string2Unicode(chn);
        Assert.assertEquals(UnicodeUtils.unicode2String(unicode), chn);
    }


}
