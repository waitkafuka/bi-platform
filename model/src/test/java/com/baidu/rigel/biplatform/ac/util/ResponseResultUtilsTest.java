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
package com.baidu.rigel.biplatform.ac.util;

import org.junit.Assert;
import org.junit.Test;

/**
 *Description:
 * @author david.wang
 *
 */
public class ResponseResultUtilsTest {
    
    @Test
    public void testGetResult () {
        ResponseResult rs = ResponseResultUtils.getResult ("", "fail", null);
        Assert.assertEquals ("fail", rs.getStatusInfo ());
        Assert.assertEquals (ResponseResult.FAILED, rs.getStatus ());
        
        rs = ResponseResultUtils.getResult ("suce", "fail", "abc");
        Assert.assertEquals ("suce", rs.getStatusInfo ());
        Assert.assertEquals (ResponseResult.SUCCESS, rs.getStatus ());
    }
    
    @Test
    public void testGetErrorResult () {
        ResponseResult rs = ResponseResultUtils.getErrorResult ("fail", 100);
        Assert.assertEquals ("fail", rs.getStatusInfo ());
        Assert.assertEquals (100, rs.getStatus ());
    }
    
    @Test
    public void testGetCorrectResult () {
        ResponseResult rs = ResponseResultUtils.getCorrectResult ("fail", 100);
        Assert.assertEquals (ResponseResult.SUCCESS, rs.getStatus ());
        Assert.assertNull (rs.getStatusInfo ());
        ThreadLocalPlaceholder.bindProperty (ThreadLocalPlaceholder.ERROR_MSG_KEY, "test");
        rs = ResponseResultUtils.getCorrectResult ("fail", 100);
        Assert.assertEquals (ResponseResult.SUCCESS, rs.getStatus ());
        Assert.assertEquals ("test", rs.getStatusInfo ());
    }
}
