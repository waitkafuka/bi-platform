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
package com.baidu.rigel.biplatform.parser.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.rigel.biplatform.parser.context.CompileContext;
import com.baidu.rigel.biplatform.parser.context.Condition;
import com.baidu.rigel.biplatform.parser.context.EmptyCondition;
import com.baidu.rigel.biplatform.parser.context.StringCondition;
import com.baidu.rigel.biplatform.parser.node.impl.DataNode;

/**
 *Description:
 * @author david.wang
 *
 */
public class ConditionUtilTest {
    
    @Test
    public void testMergeConditionWithNull () {
        Map<Condition, Set<String>> result = ConditionUtil.simpleMergeContexsCondition (null);
        Assert.assertNotNull (result);
        Assert.assertEquals (0, result.size ());
    }
    
    @Test
    public void testMergeConditionWithEmpty () {
        Collection<CompileContext> params = new ArrayList<> ();
        Map<Condition, Set<String>> result = ConditionUtil.simpleMergeContexsCondition (params);
        Assert.assertNotNull (result);
        Assert.assertEquals (0, result.size ());
    }
    
    @Test
    public void testMergeConditionWithSigCtx () {
        Collection<CompileContext> params = new ArrayList<> ();
        DataNode node = new DataNode (BigDecimal.ZERO);
        CompileContext context = new CompileContext (node);
        params.add (context);
        Map<Condition, Set<String>> result = ConditionUtil.simpleMergeContexsCondition (params);
        Assert.assertNotNull (result);
        Assert.assertEquals (1, result.size ());
    }
    
    @Test
    public void testMergeConditionWithMutilCtx () {
        Collection<CompileContext> params = new ArrayList<> ();
        DataNode node = new DataNode (BigDecimal.ZERO);
        CompileContext context = new CompileContext (node);
        params.add (context);
        CompileContext context1 = new CompileContext (node);
        params.add (context1);
        Map<Condition, Set<String>> result = ConditionUtil.simpleMergeContexsCondition (params);
        Assert.assertNotNull (result);
        Assert.assertEquals (1, result.size ());
    }
    
    @Test
    public void testMergeConditionWithNotEmptyCondition () {
        Collection<CompileContext> params = new ArrayList<> ();
        DataNode node = new DataNode (BigDecimal.ZERO);
        CompileContext context = new CompileContext (node);
        Map<Condition, Set<String>> conditionVariables = new HashMap<> ();
        Set<String> variable = new HashSet<> ();
        variable.add ("a");
        conditionVariables.put (EmptyCondition.getInstance (), variable);
        context.setConditionVariables (conditionVariables);
        params.add (context);
        CompileContext context1 = new CompileContext (node);
        Map<Condition, Set<String>> conditionVariables1 = new HashMap<> ();
        Set<String> variable1 = new HashSet<> ();
        variable1.add ("b");
        conditionVariables1.put (EmptyCondition.getInstance (), variable1);
        context1.setConditionVariables (conditionVariables1);
        params.add (context1);
        Map<Condition, Set<String>> result = ConditionUtil.simpleMergeContexsCondition (params);
        Assert.assertNotNull (result);
        Assert.assertEquals (1, result.size ());
        Assert.assertEquals (2, result.get (EmptyCondition.getInstance ()).size ());
    }
    
    @Test
    public void testMergeConditionWithNotEmptyCondition1 () {
        Collection<CompileContext> params = new ArrayList<> ();
        DataNode node = new DataNode (BigDecimal.ZERO);
        CompileContext context = new CompileContext (node);
        Map<Condition, Set<String>> conditionVariables = new HashMap<> ();
        Set<String> variable = new HashSet<> ();
        variable.add ("a");
        conditionVariables.put (EmptyCondition.getInstance (), variable);
        context.setConditionVariables (conditionVariables);
        params.add (context);
        CompileContext context1 = new CompileContext (node);
        Map<Condition, Set<String>> conditionVariables1 = new HashMap<> ();
        Set<String> variable1 = new HashSet<> ();
        variable1.add ("b");
        conditionVariables1.put (new StringCondition (), variable1);
        context1.setConditionVariables (conditionVariables1);
        params.add (context1);
        Map<Condition, Set<String>> result = ConditionUtil.simpleMergeContexsCondition (params);
        Assert.assertNotNull (result);
        Assert.assertEquals (2, result.size ());
        Assert.assertEquals (1, result.get (EmptyCondition.getInstance ()).size ());
    }
    
    
    @Test
    public void testMergeConditionWithEmptyCondition () {
        Collection<CompileContext> params = new ArrayList<> ();
        DataNode node = new DataNode (BigDecimal.ZERO);
        CompileContext context = new CompileContext (node);
        Map<Condition, Set<String>> conditionVariables = new HashMap<> ();
        Set<String> variable = new HashSet<> ();
        conditionVariables.put (EmptyCondition.getInstance (), variable);
        context.setConditionVariables (conditionVariables);
        params.add (context);
        CompileContext context1 = new CompileContext (node);
        Map<Condition, Set<String>> conditionVariables1 = new HashMap<> ();
        Set<String> variable1 = new HashSet<> ();
        conditionVariables1.put (EmptyCondition.getInstance (), variable1);
        context1.setConditionVariables (conditionVariables1);
        params.add (context1);
        Map<Condition, Set<String>> result = ConditionUtil.simpleMergeContexsCondition (params);
        Assert.assertNotNull (result);
        Assert.assertEquals (1, result.size ());
        Assert.assertEquals (0, result.get (EmptyCondition.getInstance ()).size ());
    }
    
    @Test
    public void testMergeConditionWithNullCondition () {
        Collection<CompileContext> params = new ArrayList<> ();
        DataNode node = new DataNode (BigDecimal.ZERO);
        CompileContext context = new CompileContext (node);
        Map<Condition, Set<String>> conditionVariables = new HashMap<> ();
        context.setConditionVariables (conditionVariables);
        params.add (context);
        CompileContext context1 = new CompileContext (node);
        Map<Condition, Set<String>> conditionVariables1 = new HashMap<> ();
        context1.setConditionVariables (conditionVariables1);
        params.add (context1);
        Map<Condition, Set<String>> result = ConditionUtil.simpleMergeContexsCondition (params);
        Assert.assertNotNull (result);
        Assert.assertEquals (0, result.size ());
    }
    
    
}
