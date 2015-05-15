package com.baidu.rigel.biplatform.ma.model.utils;



import java.util.Hashtable;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * 测试RegExUtils
 * @author yichao.jiang
 *
 */
public class RegExUtilsTest {

	/**
	 * 
	 */
	@Test
	public void testNullTableNameList() {
		try {
			RegExUtils.regExTableName(null, null);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
	}
	
	/**
	 * 
	 */
	@Test
	public void testNullTableName() {
		try {
			List<String> tableName = Lists.newArrayList();
			tableName.add(null);
			RegExUtils.regExTableName(tableName, null);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
	}
	
	/**
	 * 
	 */
	@Test
	public void testEmptyTableName() {
		try {
			List<String> tableName = Lists.newArrayList();
			tableName.add("");
			RegExUtils.regExTableName(tableName, null);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
	}
	
	/**
	 * 
	 */
	@Test
	public void testInvalidRegexRule() {
		try {
			List<String> tableName = Lists.newArrayList();
			List<String> regexRules = Lists.newArrayList();
			tableName.add("tableName");
			regexRules.add(null);
			regexRules.add("");
			regexRules.add("[]");
			Hashtable<String, String[]> expected = new Hashtable<String, String[]>();
			expected.put("other", new String[] {"tableName"});
			Hashtable<String, String[]> actual = RegExUtils.regExTableName(tableName, regexRules);
			Assert.assertEquals(expected.keySet(), actual.keySet());
			String[] expectedArray = new String[] {"tableName"};
			String[] actualArray = actual.get("other");
			Assert.assertArrayEquals(expectedArray, actualArray);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
	}
	
	/**
	 * 
	 */
	@Test
	public void testWithValidRegexRule() {
		List<String> tableName = Lists.newArrayList();
		List<String> regexRules = Lists.newArrayList();
		tableName.add("tablenamea");
		tableName.add("tablenameb");
		tableName.add("23table");
		// 匹配小写字母
		regexRules.add("^[a-z]+$");
		Hashtable<String, String[]> expected = new Hashtable<String, String[]>();
		expected.put("other", new String[] {"23table"});
		expected.put("^[a-z]+$", new String[] {"tablenamea", "tablenameb"});
		Hashtable<String, String[]> actual = RegExUtils.regExTableName(tableName, regexRules);
		Assert.assertEquals(expected.keySet(), actual.keySet());
		String[] expectedArrayOther = new String[] {"23table"};
		String[] actualArrayOther = actual.get("other");
		Assert.assertArrayEquals(expectedArrayOther, actualArrayOther);
			
		String[] expectedArrayRegex = new String[] {"tablenamea", "tablenameb" };
		String[] actualArrayRegex = actual.get("^[a-z]+$");
		Assert.assertArrayEquals(expectedArrayRegex, actualArrayRegex);
	}
}
