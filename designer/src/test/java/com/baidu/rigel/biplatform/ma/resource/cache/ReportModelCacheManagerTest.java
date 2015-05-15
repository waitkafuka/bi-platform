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
package com.baidu.rigel.biplatform.ma.resource.cache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.SerializationUtils;

import com.baidu.rigel.biplatform.cache.StoreManager;
import com.baidu.rigel.biplatform.ma.report.exception.CacheOperationException;
import com.baidu.rigel.biplatform.ma.report.model.ExtendAreaContext;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.report.query.ReportRuntimeModel;
import com.baidu.rigel.biplatform.ma.report.service.ReportDesignModelService;

/**
 * test class
 *
 * @author david.wang
 * @version 1.0.0.1
 */
@RunWith(MockitoJUnitRunner.class)
public class ReportModelCacheManagerTest {

	/**
	 * ReportModelCacheManager
	 */
	@InjectMocks
	ReportModelCacheManager cacheManager = new ReportModelCacheManager();

	@Mock
	CacheManagerForResource cacheManagerForResource;

	@Mock
	ReportDesignModelService reportDesignModelService;

	@Mock
	StoreManager storeManager;

	@Mock
	ReportDesignModel reportDesignModel;

	@Mock
	ReportRuntimeModel reportRuntimeModel;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		cacheManager.setCacheManagerForResource(cacheManagerForResource);
	}

	/**
     * 
     */
	@Test
	public void testGetReportModelWithUnexistKey() {
		Mockito.when(cacheManagerForResource.getFromCache("report_null_test"))
				.thenReturn(reportDesignModel);
		try {
			this.cacheManager.getReportModel("test");
			Assert.fail();
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
	}

	/**
     * 
     */
	@Test
	public void testGetReportModel() {
		byte[] modelBytes = SerializationUtils.serialize(reportDesignModel);
		Mockito.when(
				cacheManagerForResource.getFromCache("report_null_test_null"))
				.thenReturn(modelBytes);
		Assert.assertNotNull(this.cacheManager.getReportModel("test"));
	}

	@Test
	public void testLoadReportModelToCacheWithException() {
		byte[] modelBytes = SerializationUtils.serialize(reportDesignModel);
		ReportDesignModel serializeDesignModel = (ReportDesignModel) SerializationUtils
				.deserialize(modelBytes);

		Mockito.when(
				reportDesignModelService.getModelByIdOrName("test_report",
						false)).thenReturn(serializeDesignModel);
		Mockito.doThrow(new CacheOperationException(""))
				.when(cacheManagerForResource)
				.setToCache(Mockito.anyString(), Mockito.anyString());
		try {
			cacheManager.loadReportModelToCache("test_report");
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
		try {
			cacheManager.loadReleaseReportModelToCache("test_report");
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
	}

	@Test
	public void testLoadReportModelToCache() {
		byte[] modelBytes = SerializationUtils.serialize(reportDesignModel);
		ReportDesignModel serializeDesignModel = (ReportDesignModel) SerializationUtils
				.deserialize(modelBytes);
		Mockito.when(
				reportDesignModelService.getModelByIdOrName("test_report",
						false)).thenReturn(serializeDesignModel);
		Mockito.when(
				reportDesignModelService
						.getModelByIdOrName("test_report", true)).thenReturn(
				serializeDesignModel);
		// Mockito.doThrow(new CacheOperationException(""))
		// .when(cacheManagerForResource)
		// .setToCache(Mockito.anyString(), Mockito.anyString());
		ReportDesignModel reportModel = cacheManager
				.loadReportModelToCache("test_report");
		ReportDesignModel releaseReportModel = cacheManager
				.loadReleaseReportModelToCache("test_report");
		Assert.assertNotNull(reportModel);
		Assert.assertNotNull(releaseReportModel);
	}

	@Test
	public void testDeleteReportModel() {
		cacheManager.deleteReportModel("test_report");
	}

	@Test
	public void testGetRuntimeModel() {
		byte[] modelBytes = SerializationUtils.serialize(reportRuntimeModel);
		Mockito.doReturn(modelBytes).when(cacheManagerForResource)
				.getFromCache("runtime_null_test_report_null");
		ReportRuntimeModel reportRuntimeModelResult = cacheManager
				.getRuntimeModel("test_report");
		Assert.assertNotNull(reportRuntimeModelResult);
	}

	@Test
	public void testLoadRunTimeModelToCache() {
		byte[] modelBytes = SerializationUtils
				.serialize(new ReportDesignModel());
		// Mockito.doReturn(modelBytes).when(cacheManagerForResource)
		// .getFromCache("runtime_null_test_report_null");
		Mockito.when(cacheManagerForResource.getFromCache(Mockito.anyString()))
				.thenReturn(modelBytes);
		ReportRuntimeModel reportRuntimeModelResult = cacheManager
				.loadRunTimeModelToCache("test_report");
		Assert.assertNotNull(reportRuntimeModelResult);
	}

	@Test
	public void testupdateAreaContext() {
		cacheManager.updateAreaContext("areaId", new ExtendAreaContext());
	}

	@Test
	public void testgetAreaContext() {
		ExtendAreaContext mockContext = new ExtendAreaContext();
		Mockito.when(cacheManagerForResource.getFromCache(Mockito.anyString()))
				.thenReturn(mockContext);
		ExtendAreaContext resultContext = cacheManager.getAreaContext("areaId");
		Assert.assertNotNull(resultContext);
	}

	@Test
	public void testgetAreaContextWithNewContext() {
		//ExtendAreaContext mockContext = new ExtendAreaContext();
		Mockito.when(cacheManagerForResource.getFromCache(Mockito.anyString()))
				.thenReturn(null);
		ExtendAreaContext resultContext = cacheManager.getAreaContext("areaId");
		Assert.assertNotNull(resultContext);
	}
}
