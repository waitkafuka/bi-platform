package com.baidu.rigel.biplatform.ma.comm.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * 测试ParamValidateUtils
 * @author jiangyichao
 *
 */
public class ParamValidateUtilsTest {

	/**
	 * 
	 */
	@Test
	public void test() {
		Assert.assertFalse(ParamValidateUtils.check(null, ""));
		Assert.assertFalse(ParamValidateUtils.check("name", null));
		Assert.assertFalse(ParamValidateUtils.check("name", new Object[]{}));
		Assert.assertFalse(ParamValidateUtils.check("name", ""));
		Assert.assertTrue(ParamValidateUtils.check("name", "value"));
	}
}
