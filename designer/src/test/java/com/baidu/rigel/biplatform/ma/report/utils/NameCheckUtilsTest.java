package com.baidu.rigel.biplatform.ma.report.utils;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author jiangjiangyichao
 * NameCheckUtils工具类测试
 */
public class NameCheckUtilsTest {
	
	/**
	 * 测试name为NULL
	 */
	@Test
	public void testWithNullName() {
		Assert.assertTrue(NameCheckUtils.isInvalidName(null));
	}
	
	/**
	 * 测试name为空白字符串
	 */
	@Test
	public void testWithWhiteSpaceName() {
		Assert.assertTrue(NameCheckUtils.isInvalidName("    "));
	}
	
	/**
	 * 测试name长度大于250
	 */
	@Test
	public void testWithNameLongerThan250() {
		char[] names = new char[250];
		Arrays.fill(names, 'c');
		Assert.assertTrue(NameCheckUtils.isInvalidName(Arrays.toString(names)));
	}
	
	/**
	 * 测试使用默认名称匹配规则，名称不合法
	 */
	@Test
	public void testWithDefaultRuleAndIllegalName() {
		Assert.assertTrue(NameCheckUtils.checkNameWithIllegalRule("@%", null));
	}
	
	/**
	 * 测试使用默认名称匹配规则，名称合法
	 */
	@Test
	public void testWithDefaultRuleAndSuccess() {
		Assert.assertFalse(NameCheckUtils.checkNameWithIllegalRule("testName", null));
	}
	
	/**
	 * 测试使用自定义匹配规则，名称有效
	 */
	@Test
	public void testWithCustomRuleAndFail() {
		/**
		 * 名称要求不能为数字，testName不为数字，所以为有效名称
		 */
		Assert.assertFalse(NameCheckUtils.checkNameWithIllegalRule("testName", "[0-9]+?"));
	}
	
	/**
	 * 测试使用自定义匹配规则，名称无效
	 */
	@Test
	public void testWithCustomRuleAndSuccess() {
		/**
		 * 名称要求不能为数字
		 */
		Assert.assertTrue(NameCheckUtils.checkNameWithIllegalRule("12345", "[0-9]+?"));
	}
}
