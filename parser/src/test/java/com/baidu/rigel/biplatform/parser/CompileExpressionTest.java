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
package com.baidu.rigel.biplatform.parser;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.rigel.biplatform.parser.context.CompileContext;
import com.baidu.rigel.biplatform.parser.context.Condition;
import com.baidu.rigel.biplatform.parser.context.EmptyCondition;
import com.baidu.rigel.biplatform.parser.exception.IllegalCompileContextException;
import com.baidu.rigel.biplatform.parser.exception.RegisterFunctionException;
import com.baidu.rigel.biplatform.parser.node.FunctionNode;
import com.baidu.rigel.biplatform.parser.node.Node;
import com.baidu.rigel.biplatform.parser.node.Node.NodeType;
import com.baidu.rigel.biplatform.parser.result.ComputeResult;
import com.baidu.rigel.biplatform.parser.result.SingleComputeResult;

/**
 *Description:
 * @author david.wang
 *
 */
public class CompileExpressionTest {
    
    @Test
    public void testCompleWithNullExpression () {
        try {
            CompileExpression.compile (null);
            Assert.fail ();
        } catch (Exception e) {
            
        }
    }
    
    @Test
    public void testCompleWithEmptyExpression () {
        try {
            CompileExpression.compile ("");
            Assert.fail ();
        } catch (Exception e) {
            
        }
    }
    
    @Test
    public void testCompleWithSpace () {
        try {
            CompileExpression.compile (" ");
            Assert.fail ();
        } catch (Exception e) {
            
        }
    }
    
    @Test
    public void testCompleWithMutipleSpace () {
        try {
            CompileExpression.compile ("    ");
            Assert.fail ();
        } catch (Exception e) {
            
        }
    }
    
    @Test
    public void testCompleWithParenthesis () {
        try {
            CompileExpression.compile ("()");
            Assert.fail ();
        } catch (Exception e) {
            
        }
    }
    
    @Test
    public void testCompleWithSingleNumber () {
        try {
            CompileContext context = CompileExpression.compile ("1");
            Assert.assertNotNull (context);
            Assert.assertEquals (context.getExpression (), "1");
            Assert.assertEquals (NodeType.Numeric, context.getNode ().getNodeType ());
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    @Test
    public void testCompleWithNumberAndAdd () {
        try {
            CompileExpression.compile ("1 + ");
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testCompleWithNumberAndDiv () {
        try {
            CompileExpression.compile ("1 / ");
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testCompleWithNumberAndSub () {
        try {
            CompileExpression.compile ("1 - ");
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testCompleWithNumberAndMutilple () {
        try {
            CompileExpression.compile ("1 * ");
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testCompleWithAddExpression () {
        try {
            CompileContext context = CompileExpression.compile ("1 + 2");
            Assert.assertEquals ("1 + 2", context.getExpression ());
            Assert.assertEquals (NodeType.Calculate, context.getNode ().getNodeType ());
            Assert.assertEquals ("3", context.getNode ().getResult (context).toString ());
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    @Test
    public void testCompleWithDevExpression () {
        try {
            CompileContext context = CompileExpression.compile ("4 / 2");
            Assert.assertEquals ("4 / 2", context.getExpression ());
            Assert.assertEquals (NodeType.Calculate, context.getNode ().getNodeType ());
            Assert.assertEquals ("2.00000000", context.getNode ().getResult (context).toString ());
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    @Test
    public void testCompleWithMutilExpression () {
        try {
            CompileContext context = CompileExpression.compile ("2 * 22");
            Assert.assertEquals ("2 * 22", context.getExpression ());
            Assert.assertEquals (NodeType.Calculate, context.getNode ().getNodeType ());
            Assert.assertEquals ("44", context.getNode ().getResult (context).toString ());
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    @Test
    public void testCompleWithSubExpression () {
        try {
            CompileContext context = CompileExpression.compile ("2 - 1");
            Assert.assertEquals ("2 - 1", context.getExpression ());
            Assert.assertEquals (NodeType.Calculate, context.getNode ().getNodeType ());
            Assert.assertEquals ("1", context.getNode ().getResult (context).toString ());
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    @Test
    public void testCompleWithCompExpression () {
        try {
            CompileContext context = CompileExpression.compile ("(3 - 1) * (3 + 2)");
            Assert.assertEquals ("(3 - 1) * (3 + 2)", context.getExpression ());
            Assert.assertEquals (NodeType.Calculate, context.getNode ().getNodeType ());
            Assert.assertEquals ("10", context.getNode ().getResult (context).toString ());
            
            context = CompileExpression.compile ("(3 - 1) * 3 + 2");
            Assert.assertEquals ("(3 - 1) * 3 + 2", context.getExpression ());
            Assert.assertEquals (NodeType.Calculate, context.getNode ().getNodeType ());
            Assert.assertEquals ("8", context.getNode ().getResult (context).toString ());
            
            context = CompileExpression.compile ("-3 - 1");
            Assert.assertEquals ("-3 - 1", context.getExpression ());
            Assert.assertEquals (NodeType.Calculate, context.getNode ().getNodeType ());
            Assert.assertEquals ("-4", context.getNode ().getResult (context).toString ());
            
            context = CompileExpression.compile ("-3 - (-1)");
            Assert.assertEquals ("-3 - (-1)", context.getExpression ());
            Assert.assertEquals (NodeType.Calculate, context.getNode ().getNodeType ());
            Assert.assertEquals ("-2", context.getNode ().getResult (context).toString ());
            
            context = CompileExpression.compile ("3 - 1 * 3 + 2");
            Assert.assertEquals ("3 - 1 * 3 + 2", context.getExpression ());
            Assert.assertEquals (NodeType.Calculate, context.getNode ().getNodeType ());
            Assert.assertEquals ("2", context.getNode ().getResult (context).toString ());
            
            context = CompileExpression.compile ("3 - 1 * 3 * 2");
            Assert.assertEquals ("3 - 1 * 3 * 2", context.getExpression ());
            Assert.assertEquals (NodeType.Calculate, context.getNode ().getNodeType ());
            Assert.assertEquals ("-3", context.getNode ().getResult (context).toString ());
            
            context = CompileExpression.compile ("3 - 1 * 3 * 2 + 8 * 1 / 2 - 3");
            Assert.assertEquals ("3 - 1 * 3 * 2 + 8 * 1 / 2 - 3", context.getExpression ());
            Assert.assertEquals (NodeType.Calculate, context.getNode ().getNodeType ());
            Assert.assertEquals ("-2.00000000", context.getNode ().getResult (context).toString ());
            
            context = CompileExpression.compile ("3 - 1 * 3 * 2 + 8");
            Assert.assertEquals ("3 - 1 * 3 * 2 + 8", context.getExpression ());
            Assert.assertEquals (NodeType.Calculate, context.getNode ().getNodeType ());
            Assert.assertEquals ("5", context.getNode ().getResult (context).toString ());
            
            context = CompileExpression.compile ("0 - 3 - 1 * 3 * 2 + 8");
            Assert.assertEquals ("0 - 3 - 1 * 3 * 2 + 8", context.getExpression ());
            Assert.assertEquals (NodeType.Calculate, context.getNode ().getNodeType ());
            Assert.assertEquals ("-1", context.getNode ().getResult (context).toString ());
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    @Test
    public void testCompileWithNotExistVariable () {
        try {
            CompileContext context = CompileExpression.compile ("(a - 1) * (3 + 2)");
            Assert.assertEquals ("(a - 1) * (3 + 2)", context.getExpression ());
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testCompileWithVariable () {
        try {
            CompileContext context = CompileExpression.compile ("(${a} - 1) * (3 + 2)");
            Map<Condition, Set<String>> conditionVariables = new HashMap<> ();
            Set<String> variable = new HashSet<String> ();
            variable.add ("${a}");
            conditionVariables.put (EmptyCondition.getInstance (), variable);
            context.setConditionVariables (conditionVariables);
            Map<Condition, Map<String, ComputeResult>> variablesResult = new HashMap<> ();
            Map<String, ComputeResult> result = new HashMap<> ();
            result.put ("${a}", new SingleComputeResult (1));
            variablesResult.put (EmptyCondition.getInstance (), result);
            context.setVariablesResult (variablesResult);
            Assert.assertEquals ("(${a} - 1) * (3 + 2)", context.getExpression ());
            Assert.assertEquals ("0", context.getNode ().getResult (context).toString ());
        } catch (Exception e) {
            Assert.fail ();
        }
    }
    
    @Test
    public void testCompileWithBaseUdf () {
        try {
            RegisterFunction.register ("udf", UserDefFunction.class);
        } catch (RegisterFunctionException e) {
            Assert.fail ();
        }
        CompileContext context = CompileExpression.compile ("udf(-1)");
        Assert.assertEquals ("udf(-1)", context.getExpression ());
        Assert.assertEquals ("1", context.getNode ().getResult (context).toString ());
        
        context = CompileExpression.compile ("udf(+1)");
        Assert.assertEquals ("udf(+1)", context.getExpression ());
        Assert.assertEquals ("-1", context.getNode ().getResult (context).toString ());
        
    }
    
    /**
     * 
     * Description: base udf for test
     * @author david.wang
     *
     */
    public static class UserDefFunction extends FunctionNode {

        /**
         * 
         */
        private static final long serialVersionUID = -2557343941040637669L;

        public UserDefFunction() {
        }
        
        @Override
        public String getName() {
            return "udf";
        }

        @Override
        public Map<Condition, Set<String>> mergeCondition(Node node) {
            return node.collectVariableCondition ();
        }

        @Override
        protected BigDecimal compute(BigDecimal arg1, BigDecimal arg2) {
            return BigDecimal.ZERO.subtract (arg1);
        }
        
        @Override
        public ComputeResult getResult(CompileContext context) throws IllegalCompileContextException {
            Node args = getArgs ().get (0);
            SingleComputeResult rs = (SingleComputeResult) args.getResult (context);
            return new SingleComputeResult (BigDecimal.ZERO.subtract (rs.getData ()));
        }
        
        @Override
        public int getArgsLength() {
            return 1;
        }
        
    }
    
    
}
