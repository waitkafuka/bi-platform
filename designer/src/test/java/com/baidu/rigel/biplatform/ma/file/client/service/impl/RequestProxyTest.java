package com.baidu.rigel.biplatform.ma.file.client.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.baidu.rigel.biplatform.ma.common.file.protocol.Command;
import com.baidu.rigel.biplatform.ma.common.file.protocol.Request;
import com.google.common.collect.Maps;

/**
 * RequestProxy测试
 * @author yichao.jiang
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "file:src/test/resources/applicationContext.xml")
public class RequestProxyTest {
	@Resource
	private RequestProxy requestProxy;
	/**
	 * 
	 */
	@Test
	public void test() throws Exception {
		Map<String, Object> params = Maps.newHashMap();
        Request request = new Request();
        request.setCommand(Command.LS);
        request.setParams(params);
        // 发送参数，获得文件属性
        Map<String, Object> map = requestProxy.doActionOnRemoteFileSystem(request);
        Assert.notNull(map);
	}
}
