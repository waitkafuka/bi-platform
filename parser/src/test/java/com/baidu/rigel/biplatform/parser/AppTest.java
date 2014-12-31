package com.baidu.rigel.biplatform.parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.baidu.rigel.biplatform.parser.CompileExpression;
import com.baidu.rigel.biplatform.parser.context.CompileContext;
import com.baidu.rigel.biplatform.parser.context.Condition;
import com.baidu.rigel.biplatform.parser.context.EmptyCondition;
import com.baidu.rigel.biplatform.parser.exception.InvokeFunctionException;
import com.baidu.rigel.biplatform.parser.exception.RegisterFunctionException;
import com.baidu.rigel.biplatform.parser.node.impl.MtdFunNode;
import com.baidu.rigel.biplatform.parser.node.impl.RateFunNode;
import com.baidu.rigel.biplatform.parser.result.ComputeResult;
import com.baidu.rigel.biplatform.parser.result.ListComputeResult;
import com.baidu.rigel.biplatform.parser.result.SingleComputeResult;
import com.baidu.rigel.biplatform.parser.util.ParserConstant;


/**
 * Unit test for simple App.
 */
@SuppressWarnings("deprecation")
public class AppTest 
{
    
    
    @Test
    public void test1(){
        
        System.out.println(Double.MAX_VALUE);
        
        System.out.println(Double.MIN_VALUE);
        
        
        boolean mat = Pattern.matches(ParserConstant.NUMBER_PATTERN_STR, "2014");
        System.out.println(mat);
        
        System.out.println(Pattern.matches("^\\{status:true\\}$","{status:true}"));
        
        
        
        String pattern = "\\([^\\(\\)]+\\)";
        
        
        
        Pattern parenthesesPattern = Pattern.compile(pattern);
        
        String expression = "a * (b / ( c - d ) + (e-f))";
        Map<String,String> explainExpre = new HashMap<>();
        
        parseExpressionByPattern(expression, parenthesesPattern,explainExpre);
        System.out.println(explainExpre);
        
        
        String functionExpre = "1*(${csm}+${click}*3) * (1 + rate(mtd(${csm}),3) - 1)";
        
        
        explainExpre.clear();
        
        parseExpressionByPattern(functionExpre, ParserConstant.MIX_PATTERN,explainExpre);
        
        
        try {
            RegisterFunction.register("MTD", MtdFunNode.class);
            RegisterFunction.register("Rate", RateFunNode.class);
        } catch (RegisterFunctionException e1) {
            // TODO Auto-generated catch block
            
        }
        System.out.println(explainExpre);
        
        
        Map<String,String> sections = CompileExpression.compileExpressionByPattern(functionExpre, ParserConstant.MIX_PATTERN);
        System.out.println(sections);
        
        CompileContext context;
        try {
            context = CompileExpression.resolveSections(sections);
            System.out.println(context);
            System.out.println(context.getConditionVariables());
            
            Map<Condition, Map<String, ComputeResult>> varResult = new HashMap<Condition, Map<String,ComputeResult>>();
            
            Map<String,ComputeResult> result = new HashMap<String, ComputeResult>();
            result.put("${csm}", new SingleComputeResult(12));
            result.put("${click}", new SingleComputeResult(18));
            varResult.put(EmptyCondition.getInstance(), result);
            
            List<BigDecimal> datas = new ArrayList<BigDecimal>();
            datas.add(new BigDecimal("1.1"));
            datas.add(null);
            datas.add(new BigDecimal("3"));
            Map<String,ComputeResult> result1 = new HashMap<String, ComputeResult>();
            result1.put("${csm}", new ListComputeResult(datas));
            varResult.put(MtdFunNode.MTD_CONDITION, result1);
            
            context = CompileExpression.compile("rate(mtd(${csm})+1,2)");
            context.setVariablesResult(varResult);
            
            
            System.out.println("compute result: " + context.getNode().getResult(context)); ;
            
        } catch (InvokeFunctionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        System.out.println(Pattern.matches("[\\+\\-\\*/]", "a+b*c"));
        
        parseExpressionByPattern("a+b-c*c/d",ParserConstant.ARITHMETIC_PATTERN,explainExpre);
        System.out.println(explainExpre);
        
        int add = '+';
        int sub = '-';
        int mul = '*';
        int div = '/';
        
        
        System.out.println(add + "-----------" + sub + "-------------" + mul + "------------" + div);
    }
    
    
    public void parseExpressionByPattern(String expression, Pattern pattern, Map<String, String> result) {
        Matcher matcher = pattern.matcher(expression);
        int i = 1;
        while(true){
            boolean found = false;
            while(matcher.find()){
                
                String matchExp = matcher.group();
                System.out.println(i+"===" + matcher.group());
                String variablePre = "$var";
                if(!matchExp.startsWith("(")) {
                    variablePre = "$fun";
                }
                result.put(variablePre+i, matchExp);
                
                expression = expression.replace(matchExp, variablePre + i);
                i++;
                found = true;
            }
            matcher = pattern.matcher(expression);
            if(!found){
                result.put("result", expression);
                break;
            }
        }
    }
}
