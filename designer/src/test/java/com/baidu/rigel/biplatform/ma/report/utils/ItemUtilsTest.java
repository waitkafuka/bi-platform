package com.baidu.rigel.biplatform.ma.report.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.baidu.rigel.biplatform.ac.minicube.MiniCube;
import com.baidu.rigel.biplatform.ac.minicube.MiniCubeSchema;
import com.baidu.rigel.biplatform.ac.minicube.TimeDimension;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.model.OlapElement;
import com.baidu.rigel.biplatform.ma.report.model.Item;

/**
 * ItemUtils单测类
 * 
 * @author majun04
 *
 */
public class ItemUtilsTest {

    private MiniCubeSchema schema;

    private Item item;

    private String cubeId = "testCubeId";

    @Before
    public void init() {
        schema = new MiniCubeSchema("testSchemaName");
        item = new Item();
        item.setOlapElementId("testOlapElementId");
        Map<String, MiniCube> cubes = new HashMap<String, MiniCube>();
        MiniCube miniCube = new MiniCube();
        Map<String, Dimension> dimensions = new HashMap<String, Dimension>();
        TimeDimension timeDimension = new TimeDimension("testOlapElementId");
        dimensions.put("testOlapElementId", timeDimension);
        miniCube.setDimensions(dimensions);
        cubes.put("testCubeId", miniCube);
        schema.setCubes(cubes);
    }

    @Test
    public void testIsTimeDimWithNullCubeName() {
        schema = new MiniCubeSchema();
        boolean flag = ItemUtils.isTimeDim(item, schema, cubeId);
        Assert.assertFalse(flag);
    }

    @Test
    public void testIsTimeDim() {

        boolean flag = ItemUtils.isTimeDim(item, schema, cubeId);
        Assert.assertTrue(flag);
    }

    @Test
    public void testGetOlapElementByItem() {
        OlapElement olapElement = ItemUtils.getOlapElementByItem(item, schema, cubeId);
        Assert.assertNotNull(olapElement);
    }
}
