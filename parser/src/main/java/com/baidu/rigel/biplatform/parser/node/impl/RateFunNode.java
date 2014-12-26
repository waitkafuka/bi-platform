
package com.baidu.rigel.biplatform.parser.node.impl;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import com.baidu.rigel.biplatform.parser.context.Condition;
import com.baidu.rigel.biplatform.parser.exception.NodeCompileException;
import com.baidu.rigel.biplatform.parser.node.FunctionNode;
import com.baidu.rigel.biplatform.parser.node.Node;
import com.baidu.rigel.biplatform.parser.result.SingleComputeResult;
import com.baidu.rigel.biplatform.parser.util.ParserConstant;

public class RateFunNode extends FunctionNode {

    /** 
     * serialVersionUID
     */
    private static final long serialVersionUID = 6966191881881585349L;
    
    /** 
     * 构造函数
     */
    public RateFunNode(Node numeratorNode, Node denominatorNode) {
        super(numeratorNode, denominatorNode);
        
    }
    
    public RateFunNode() {
        super(2);
    }

    @Override
    public String getName() {
        return "Rate";
    }

    @Override
    protected BigDecimal compute(BigDecimal arg1, BigDecimal arg2) {
        return arg1.divide(arg2, ParserConstant.COMPUTE_SCALE, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE);
    }


    @Override
    public Map<Condition, Set<String>> mergeCondition(Node node) {
        return node.collectVariableCondition();
        
    }

    @Override
    public void check() {
        super.check();
        Node node = getArgs().get(1);
        if(node.getNodeType().equals(NodeType.Numeric)) {
            SingleComputeResult result = (SingleComputeResult) node.getResult(null);
            if (BigDecimal.ZERO.equals(result.getData())) {
                throw new NodeCompileException(this, "rate function denominator can not be zero.");
            }
        }
    }
    


}

