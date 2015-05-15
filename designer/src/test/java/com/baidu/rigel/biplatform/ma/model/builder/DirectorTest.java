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
package com.baidu.rigel.biplatform.ma.model.builder;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.rigel.biplatform.ac.minicube.MiniCubeSchema;
import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Measure;
import com.baidu.rigel.biplatform.ac.model.Schema;
import com.baidu.rigel.biplatform.ac.model.TimeType;
import com.baidu.rigel.biplatform.ma.model.builder.impl.DirectorImpl;
import com.baidu.rigel.biplatform.ma.model.meta.CallbackDimTableMetaDefine;
import com.baidu.rigel.biplatform.ma.model.meta.ColumnMetaDefine;
import com.baidu.rigel.biplatform.ma.model.meta.DimTableMetaDefine;
import com.baidu.rigel.biplatform.ma.model.meta.FactTableMetaDefine;
import com.baidu.rigel.biplatform.ma.model.meta.ReferenceDefine;
import com.baidu.rigel.biplatform.ma.model.meta.StandardDimTableMetaDefine;
import com.baidu.rigel.biplatform.ma.model.meta.StarModel;
import com.baidu.rigel.biplatform.ma.model.meta.TimeDimTableMetaDefine;
import com.baidu.rigel.biplatform.ma.model.meta.TimeDimType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * test class
 *
 * @author david.wang
 * @version 1.0.0.1
 */
public class DirectorTest {

	/**
     * 
     */
	Director director = new DirectorImpl();

	/**
     * 
     */
	@Test
	public void testGetSchemaWithNull() {
		Assert.assertNull(director.getSchema(null));
	}

	/**
     * 
     */
	@Test
	public void testGetSchemaWithEmptyArray() {
		Assert.assertNull(director.getSchema(new StarModel[0]));
	}

	/**
     * 
     */
	@Test
	public void testGetSchemaWithNullDsId() {
		StarModel starModel = new StarModel();
		StarModel[] starModels = new StarModel[] { starModel };
		try {
			director.getSchema(starModels);
			Assert.fail();
		} catch (Throwable e) {
			Assert.assertNotNull(e);
		}
	}

	/**
     * 
     */
	@Test
	public void testGetSchemaWithDsId() {
		StarModel starModel = new StarModel();
		starModel.setDsId("test");
		StarModel[] starModels = new StarModel[] { starModel };
		Schema schema = director.getSchema(starModels);
		Assert.assertNotNull(schema);
		Assert.assertEquals(0, schema.getCubes().size());
	}

	/**
     * 
     */
	@Test
	public void testGetSchemaWithFactableWithNullColumn() {
		StarModel starModel = new StarModel();
		starModel.setDsId("test");
		FactTableMetaDefine factTable = new FactTableMetaDefine();
		factTable.setName("fact");
		starModel.setFactTable(factTable);
		StarModel[] starModels = new StarModel[] { starModel };
		Schema schema = director.getSchema(starModels);
		Assert.assertNotNull(schema);
		Assert.assertEquals(1, schema.getCubes().size());
		Cube cube = schema.getCubes().values().toArray(new Cube[0])[0];
		Assert.assertEquals(0, cube.getMeasures().size());
		Assert.assertEquals(0, cube.getDimensions().size());
	}

	/**
     * 
     */
	@Test
	public void testGetSchemaWithFactableWithColumn() {
		StarModel starModel = new StarModel();
		starModel.setDsId("test");
		FactTableMetaDefine factTable = new FactTableMetaDefine();
		factTable.setName("fact");
		ColumnMetaDefine column = new ColumnMetaDefine();
		column.setName("abc");
		factTable.addColumn(column);
		starModel.setFactTable(factTable);
		StarModel[] starModels = new StarModel[] { starModel };
		Schema schema = director.getSchema(starModels);
		Assert.assertNotNull(schema);
		Assert.assertEquals(1, schema.getCubes().size());
		Cube cube = schema.getCubes().values().toArray(new Cube[0])[0];
		Assert.assertEquals(1, cube.getMeasures().size());
		Assert.assertEquals(0, cube.getDimensions().size());

		Measure m = cube.getMeasures().values().toArray(new Measure[0])[0];
		Assert.assertEquals("abc", m.getName());
	}

	/**
     * 
     */
	@Test
	public void testGetSchemaWithNullDimTable() {
		StarModel starModel = new StarModel();
		starModel.setDsId("test");
		FactTableMetaDefine factTable = new FactTableMetaDefine();
		factTable.setName("fact");
		ColumnMetaDefine abc = new ColumnMetaDefine();
		abc.setName("abc");
		factTable.addColumn(abc);

		ColumnMetaDefine def = new ColumnMetaDefine();
		def.setName("def");
		factTable.addColumn(def);
		starModel.setFactTable(factTable);

		List<DimTableMetaDefine> dimTables = Lists.newArrayList();
		starModel.setDimTables(dimTables);
		StarModel[] starModels = new StarModel[] { starModel };
		Schema schema = director.getSchema(starModels);
		Assert.assertNotNull(schema);
		Assert.assertEquals(1, schema.getCubes().size());
		Cube cube = schema.getCubes().values().toArray(new Cube[0])[0];
		Assert.assertEquals(2, cube.getMeasures().size());
		Assert.assertEquals(0, cube.getDimensions().size());
	}

	/**
     * 
     */
	@Test
	public void testGetSchemaWithEmptyDimTable() {
		StarModel starModel = new StarModel();
		starModel.setDsId("test");
		FactTableMetaDefine factTable = new FactTableMetaDefine();
		factTable.setName("fact");
		ColumnMetaDefine abc = new ColumnMetaDefine();
		abc.setName("abc");
		factTable.addColumn(abc);

		ColumnMetaDefine def = new ColumnMetaDefine();
		def.setName("def");
		factTable.addColumn(def);
		starModel.setFactTable(factTable);

		List<DimTableMetaDefine> dimTables = Lists.newArrayList();
		DimTableMetaDefine dimTable = new StandardDimTableMetaDefine();
		dimTables.add(dimTable);
		starModel.setDimTables(dimTables);
		StarModel[] starModels = new StarModel[] { starModel };
		try {
			director.getSchema(starModels);
			Assert.fail();
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}

	}

	/**
     * 
     */
	@Test
	public void testGetSchemaWithDimTableEmptyRef() {
		StarModel starModel = new StarModel();
		starModel.setDsId("test");
		FactTableMetaDefine factTable = new FactTableMetaDefine();
		factTable.setName("fact");
		ColumnMetaDefine abc = new ColumnMetaDefine();
		abc.setName("abc");
		factTable.addColumn(abc);

		ColumnMetaDefine def = new ColumnMetaDefine();
		def.setName("def");
		factTable.addColumn(def);
		starModel.setFactTable(factTable);

		List<DimTableMetaDefine> dimTables = Lists.newArrayList();
		DimTableMetaDefine dimTable = new StandardDimTableMetaDefine();
		dimTable.setName("dim");
		dimTables.add(dimTable);
		starModel.setDimTables(dimTables);
		StarModel[] starModels = new StarModel[] { starModel };
		try {
			director.getSchema(starModels);
			Assert.fail();
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
	}

	/**
     * 
     */
	@Test
	public void testGetSchemaWithDimTableNotCorrectRef() {
		StarModel starModel = new StarModel();
		starModel.setDsId("test");
		FactTableMetaDefine factTable = new FactTableMetaDefine();
		factTable.setName("fact");
		ColumnMetaDefine abc = new ColumnMetaDefine();
		abc.setName("abc");
		factTable.addColumn(abc);

		ColumnMetaDefine def = new ColumnMetaDefine();
		def.setName("def");
		factTable.addColumn(def);
		starModel.setFactTable(factTable);

		List<DimTableMetaDefine> dimTables = Lists.newArrayList();
		DimTableMetaDefine dimTable = new StandardDimTableMetaDefine();
		dimTable.setName("dim");
		ReferenceDefine reference = new ReferenceDefine();
		reference.setMajorColumn(null);
		reference.setMajorTable(null);
		reference.setSalveColumn(null);
		dimTable.setReference(reference);
		dimTables.add(dimTable);
		starModel.setDimTables(dimTables);
		StarModel[] starModels = new StarModel[] { starModel };
		try {
			director.getSchema(starModels);
			// Assert.fail();
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
	}

	/**
     * 
     */
	@Test
	public void testGetSchemaWithDimTableCorrectRefNoneDimColumn() {
		StarModel starModel = new StarModel();
		starModel.setDsId("fact");
		FactTableMetaDefine factTable = new FactTableMetaDefine();
		factTable.setName("test");
		ColumnMetaDefine abc = new ColumnMetaDefine();
		abc.setName("abc");
		factTable.addColumn(abc);

		ColumnMetaDefine def = new ColumnMetaDefine();
		def.setName("def");
		factTable.addColumn(def);
		starModel.setFactTable(factTable);

		List<DimTableMetaDefine> dimTables = Lists.newArrayList();
		DimTableMetaDefine dimTable = new StandardDimTableMetaDefine();
		dimTable.setName("dim");
		ReferenceDefine reference = new ReferenceDefine();
		reference.setMajorColumn("abc");
		reference.setMajorTable("fact");
		reference.setSalveColumn("aaa");
		dimTable.setReference(reference);
		dimTables.add(dimTable);
		starModel.setDimTables(dimTables);
		StarModel[] starModels = new StarModel[] { starModel };
		Schema schema = director.getSchema(starModels);
		Assert.assertNotNull(schema);
		Assert.assertEquals(1, schema.getCubes().size());
		Cube cube = schema.getCubes().values().toArray(new Cube[0])[0];
		Assert.assertEquals(1, cube.getMeasures().size());
		Assert.assertEquals(0, cube.getDimensions().size());
	}

	/**
     * 
     */
	@Test
	public void testGetSchemaWithDimTableCorrectRefDimColumn() {
		StarModel starModel = new StarModel();
		starModel.setDsId("fact");
		FactTableMetaDefine factTable = new FactTableMetaDefine();
		factTable.setName("test");
		ColumnMetaDefine abc = new ColumnMetaDefine();
		abc.setName("fact_abc");
		factTable.addColumn(abc);

		ColumnMetaDefine def = new ColumnMetaDefine();
		def.setName("fact_def");
		factTable.addColumn(def);
		starModel.setFactTable(factTable);

		List<DimTableMetaDefine> dimTables = buildBaseDimTables();
		starModel.setDimTables(dimTables);
		StarModel[] starModels = new StarModel[] { starModel };
		Schema schema = director.getSchema(starModels);
		Assert.assertNotNull(schema);
		Assert.assertEquals(1, schema.getCubes().size());
		Cube cube = schema.getCubes().values().toArray(new Cube[0])[0];
		Assert.assertEquals(1, cube.getMeasures().size());
		Assert.assertEquals(1, cube.getDimensions().size());
	}

	/**
     * 
     */
	@Test
	public void testGetSchemaWithTimeDimension() {
		StarModel starModel = new StarModel();
		starModel.setDsId("fact");
		FactTableMetaDefine factTable = new FactTableMetaDefine();
		factTable.setName("test");
		ColumnMetaDefine abc = new ColumnMetaDefine();
		abc.setName("fact_abc");
		factTable.addColumn(abc);

		ColumnMetaDefine def = new ColumnMetaDefine();
		def.setName("fact_def");
		factTable.addColumn(def);

		ColumnMetaDefine timeCol = new ColumnMetaDefine();
		timeCol.setName("fact_time_id");
		factTable.addColumn(timeCol);
		starModel.setFactTable(factTable);

		List<DimTableMetaDefine> dimTables = buildDimTablesWithTimeDim();

		starModel.setDimTables(dimTables);

		StarModel[] starModels = new StarModel[] { starModel };
		Schema schema = director.getSchema(starModels);
		Assert.assertNotNull(schema);
		Assert.assertEquals(1, schema.getCubes().size());
		Cube cube = schema.getCubes().values().toArray(new Cube[0])[0];
		Assert.assertEquals(1, cube.getMeasures().size());
		Assert.assertEquals(1, cube.getDimensions().size());
	}

	/**
     * 
     */
	@Test
	public void testGetSchemaWithCallbackDim() {
		Schema schema = genSchema();
		Assert.assertNotNull(schema);
		Assert.assertEquals(1, schema.getCubes().size());
		Cube cube = schema.getCubes().values().toArray(new Cube[0])[0];
		Assert.assertEquals(1, cube.getMeasures().size());
		Assert.assertEquals(2, cube.getDimensions().size());
	}

	/**
     * 
     */
	@Test
	public void testGetStarModel() {
		/**
		 * 以空schema获取starModel
		 */
		StarModel[] expectStarModel = new StarModel[0];
		Assert.assertArrayEquals(expectStarModel,
				this.director.getStarModel(null));

		/**
		 * schema的cube为空
		 */
		MiniCubeSchema miniCubeSchema = new MiniCubeSchema();
		miniCubeSchema.setCubes(Maps.newHashMap());
		Assert.assertArrayEquals(expectStarModel,
				this.director.getStarModel(miniCubeSchema));

		/**
		 * schema的cube不为空
		 */
		Schema schema = this.genSchema();
		StarModel[] starModels = this.director.getStarModel(schema);
		Assert.assertNotNull(starModels);
		Assert.assertEquals(1, starModels.length);
		StarModel starModel = starModels[0];
		// 有两个维度，时间维度和callback维度
		Assert.assertEquals(2, starModel.getDimTables().size());
		Assert.assertEquals("fact", starModel.getFactTable().getName());
	}

	/**
	 * 根据星型模型修改Schema
	 */
	@Test
	public void testModifySchemaWithNewModel() {
		Schema schema = this.genSchema();
		StarModel starModel = genStarModel();
		
		StarModel[] starModels = new StarModel[] { starModel };
		// 修改Cube对应的id，保证新的starModel同原来的starModel对应的Cube是同一个
		Map<String, ? extends Cube> cubes = schema.getCubes();
		for (Cube cube : cubes.values()) {
			starModel.setCubeId(cube.getId());
		}
		Schema newSchema = this.director.modifySchemaWithNewModel(schema,
				starModels);
		Assert.assertNotNull(newSchema);
		Assert.assertEquals(schema.getId(), newSchema.getId());
		Assert.assertEquals(schema.getName(), newSchema.getName());
		Assert.assertEquals(schema.getCubes().size(), newSchema.getCubes()
				.size());
	}

	/**
	 * 
	 */
	@Test
	public void testModifySchemaWithNewModelDifferentCube() {
		Schema schema = this.genSchema();
		StarModel starModel = genStarModel();
		StarModel[] starModels = new StarModel[] { starModel };
		Schema newSchema = this.director.modifySchemaWithNewModel(schema,
				starModels);
		Assert.assertNotNull(newSchema);
		Assert.assertEquals(schema.getId(), newSchema.getId());
		Assert.assertEquals(schema.getName(), newSchema.getName());
		Assert.assertEquals(schema.getCubes().size(), newSchema.getCubes()
				.size());
	}

	/**
	 * 
	 */
	@Test
	public void testModifySchemaWithNewModelNullCondition() {
		StarModel starModel = this.genStarModel();
		StarModel[] starModels = new StarModel[] { starModel };
		try {
			this.director.modifySchemaWithNewModel(null, starModels);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
		Schema schema = this.genSchema();
		Assert.assertEquals(schema,
				this.director.modifySchemaWithNewModel(schema, null));
		MiniCubeSchema miniCubeSchema = (MiniCubeSchema) schema;
		miniCubeSchema.setCubes(null);
		try {
			this.director.modifySchemaWithNewModel(schema, starModels);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
	}

	
	/**
	 * 产生schema对象
	 * 
	 * @return
	 */
	private Schema genSchema() {
		StarModel starModel = genStarModel();

		StarModel[] starModels = new StarModel[] { starModel };
		Schema schema = director.getSchema(starModels);
		return schema;
	}

	/**
	 * 构建星型模型
	 * 
	 * @return
	 */
	private StarModel genStarModel() {
		StarModel starModel = new StarModel();
		starModel.setDsId("test");
		// 建立事实表
		FactTableMetaDefine factTable = new FactTableMetaDefine();
		factTable.setName("fact");
		// 建立事实表的第一列
		ColumnMetaDefine abc = new ColumnMetaDefine();
		abc.setName("fact_abc");
		factTable.addColumn(abc);
		// 建立事实表的第二列
		ColumnMetaDefine def = new ColumnMetaDefine();
		def.setName("fact_def");
		factTable.addColumn(def);
		// 建立callback回调列
		ColumnMetaDefine calback = new ColumnMetaDefine();
		calback.setName("fact_callback");
		factTable.addColumn(calback);
		// 建立时间列
		ColumnMetaDefine timeCol = new ColumnMetaDefine();
		timeCol.setName("fact_time_id");
		factTable.addColumn(timeCol);

//		// 建立自定义维度列
//		ColumnMetaDefine userCol = new ColumnMetaDefine();
//		userCol.setName("fact_user");
//		factTable.addColumn(userCol);
		
		// 设置starModel的事实表
		starModel.setFactTable(factTable);

		// 建立维度表
		List<DimTableMetaDefine> dimTables = buildDimTablesWithTimeDim();
		// 添加回调维度
		dimTables.add(this.buildCallbackDim());
//		// 添加自定义维度
//		dimTables.add(buildUserDefineDimTableMetaDefine());
		
		// 设置星型模型的维度表
		starModel.setDimTables(dimTables);
		return starModel;
	}

	/**
	 * 构建回调维度
	 * 
	 * @return
	 */
	private CallbackDimTableMetaDefine buildCallbackDim() {
		// 建立callback回调维度
		CallbackDimTableMetaDefine callBackDim = new CallbackDimTableMetaDefine();
		callBackDim.setName("call_back");
		callBackDim.setUrl("http://host:port/callbackService");
		ColumnMetaDefine callBackCol = new ColumnMetaDefine();
		callBackCol.setName("fact_callback");
		callBackCol.setCaption("fact_callback");
		callBackDim.addColumn(callBackCol);
		// 构建回调维度对应的引用列
		ReferenceDefine callbackRef = new ReferenceDefine();
		callbackRef.setMajorColumn("fact_callback");
		callbackRef.setMajorTable("fact");
		callbackRef.setSalveColumn("gen_call_back_id");
		callBackDim.setReference(callbackRef);
		return callBackDim;
	}

	/**
	 * 构建时间维度表
	 * 
	 * @return
	 */
	private List<DimTableMetaDefine> buildDimTablesWithTimeDim() {
		List<DimTableMetaDefine> dimTables = buildBaseDimTables();
		// 时间维度表
		TimeDimTableMetaDefine timeDimTable = new TimeDimTableMetaDefine(
				TimeDimType.STANDARD_TIME);
		ColumnMetaDefine timeId = new ColumnMetaDefine();
		timeId.setName(TimeType.TimeYear.toString());
		timeId.setCaption(TimeType.TimeYear.toString());
		timeDimTable.addColumn(timeId);
		// 构建时间维度对应的事实表引用列
		ReferenceDefine timeReference = new ReferenceDefine();
		timeReference.setMajorColumn("fact_time_id");
		timeReference.setMajorTable("fact");
		timeReference.setSalveColumn("time_id");
		timeDimTable.setReference(timeReference);
		dimTables.add(timeDimTable);
		return dimTables;
	}

	/**
	 * 构建基本维度表
	 * 
	 * @return
	 */
	private List<DimTableMetaDefine> buildBaseDimTables() {
		List<DimTableMetaDefine> dimTables = Lists.newArrayList();
		DimTableMetaDefine dimTable = new StandardDimTableMetaDefine();
		dimTable.setName("dim");
		ColumnMetaDefine dimColumn = new ColumnMetaDefine();
		dimColumn.setName("dim_abc");
		dimColumn.setCaption("dim_abc");
		dimTable.addColumn(dimColumn);
		ColumnMetaDefine anotherDimCol = new ColumnMetaDefine();
		anotherDimCol.setName("dim_def");
		anotherDimCol.setCaption("dim_def");
		dimTable.addColumn(anotherDimCol);
		ReferenceDefine reference = new ReferenceDefine();
		reference.setMajorColumn("fact_abc");
		reference.setMajorTable("fact");
		reference.setSalveColumn("dim_abc");
		dimTable.setReference(reference);
		dimTables.add(dimTable);
		return dimTables;
	}

//	/**
//	 * 构建用户自定义维度
//	 * 
//	 * @return
//	 */
//	private UserDefineDimTableMetaDefine buildUserDefineDimTableMetaDefine() {
//		UserDefineDimTableMetaDefine ud = new UserDefineDimTableMetaDefine();
//		ColumnMetaDefine dimColumn = new ColumnMetaDefine();
//		dimColumn.setName("dim_user");
//		dimColumn.setCaption("dim_user");
//		ud.setSourceType(DimSourceType.SQL);
//		ud.addColumn(dimColumn);
//		ReferenceDefine reference = new ReferenceDefine();
//		reference.setMajorColumn("fact_user");
//		reference.setMajorTable("fact");
//		reference.setSalveColumn("dim_user");
//		ud.setReference(reference);
//		ud.setParams(Maps.newHashMap());
//		
//		return ud;
//	}
}
