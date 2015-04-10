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

import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.rigel.biplatform.parser.context.CompileContext;
import com.baidu.rigel.biplatform.parser.context.Condition;
import com.baidu.rigel.biplatform.parser.exception.IllegalCompileContextException;
import com.baidu.rigel.biplatform.parser.node.FunctionNode;
import com.baidu.rigel.biplatform.parser.node.Node;
import com.baidu.rigel.biplatform.parser.result.ComputeResult;

/**
 *Description:
 * @author david.wang
 *
 */
public class RegisterFunctionTest {
    
    @Test
    public void testRegisterWithNullFunName ()  throws Exception {
        try {
            RegisterFunction.register ("", "");
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testRegisterWithNullClass ()  throws Exception {
        try {
            RegisterFunction.register ("test", "");
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testRegisterWithInValidateClass ()  throws Exception {
        try {
            RegisterFunction.register ("test", "java.lang.String");
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testRegisterWithUnexistClass ()  throws Exception {
        try {
            RegisterFunction.register ("test", "java.lang.StringString");
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testRegisterWithInvalidateClass1 ()  throws Exception {
        try {
            RegisterFunction.register ("test", String.class);
            Assert.fail ();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testRegisterWithExistName ()  throws Exception {
        try {
            RegisterFunction.register ("test", SubFunctionNode.class.getName ());
            boolean rs = RegisterFunction.register ("test", "java.lang.StringString");
            Assert.assertFalse (rs);
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testRegisterWithExistClass ()  throws Exception {
        try {
            RegisterFunction.register ("test", SubFunctionNode.class.getName ());
            boolean rs = RegisterFunction.register ("test", String.class);
            Assert.assertFalse (rs);
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testRegister ()  throws Exception {
        try {
            boolean rs = RegisterFunction.register ("test", SubFunctionNode.class.getName ());
            Assert.assertTrue (rs);
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testRegisterWithClass ()  throws Exception {
        try {
            boolean rs = RegisterFunction.register ("test", SubFunctionNode.class);
            Assert.assertTrue (rs);
        } catch (Exception e) {
        }
    }
    
    private static class SubFunctionNode extends FunctionNode {

        /**
         * 
         */
        private static final long serialVersionUID = -7429155671800569579L;

        @Override
        public String getName() {
            return null;
        }

        @Override
        public Map<Condition, Set<String>> mergeCondition(Node node) {
            return null;
        }

        @Override
        public ComputeResult getResult(CompileContext context)
                throws IllegalCompileContextException {
            return null;
        }

        @Override
        public int getArgsLength() {
            return 0;
        }
        
    }
}
