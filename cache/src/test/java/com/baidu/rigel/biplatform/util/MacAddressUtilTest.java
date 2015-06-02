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
package com.baidu.rigel.biplatform.util;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.baidu.rigel.biplatform.cache.util.MacAddressUtil;

/**
 *Description:
 * @author david.wang
 *
 */
@RunWith(PowerMockRunner.class)
public class MacAddressUtilTest {
    
    @Test
    @PowerMockIgnore
    public void testMachineNetworkFlagWithNull () {
        try {
            String rs = MacAddressUtil.getMachineNetworkFlag (null);
            Assert.assertNotNull (rs);
        } catch (SocketException | UnknownHostException e) {
            Assert.fail ();
        }
        
    }
    
    @Test
    @PrepareForTest(MacAddressUtil.class)
    public void testMachineNetworkFlagWithNullMac () {
        try {
            PowerMockito.mockStatic (MacAddressUtil.class);
            PowerMockito.when (MacAddressUtil.getMacAddress (InetAddress.getLocalHost())).thenReturn (null);
            PowerMockito.when (MacAddressUtil.getMachineNetworkFlag (null)).thenCallRealMethod ();
        } catch (SocketException | UnknownHostException e) {
            Assert.fail();
        }
    }
    
}
