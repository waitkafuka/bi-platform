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
/**
 * 
 */
package com.baidu.rigel.biplatform.ac.query.model;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * 指标条件
 * 
 * @author xiaoming.chen
 *
 */
public class MeasureCondition implements MetaCondition {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5059328459757316603L;

    /**
     * metaName 元数据的name
     */
    private String metaName;

    /**
     * measureConditions 指标条件
     */
    private List<SQLCondition> measureConditions;
    
    private MetaType metaType;

    /**
     * construct with metaUniqueName
     * 
     * @param metaName meta unique name
     */
    public MeasureCondition(String metaName) {
        this.metaName = metaName;
        this.metaType = MetaType.Measure;
    }
    
    public MeasureCondition() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.rigel.biplatform.ac.query.model.MetaCondition#getMetaUniqueName ()
     */
    @Override
    public String getMetaName() {
        return metaName;
    }

    /**
     * getter method for property measureConditions
     * 
     * @return the measureConditions
     */
    public List<SQLCondition> getMeasureConditions() {
    	if (this.measureConditions == null) {
    		this.measureConditions = Lists.newArrayList();
    	}
        return measureConditions;
    }

    /**
     * setter method for property measureConditions
     * 
     * @param measureConditions the measureConditions to set
     */
    public void setMeasureConditions(List<SQLCondition> measureConditions) {
        this.measureConditions = measureConditions;
    }

    @Override
    public MetaType getMetaType() {
        return metaType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MeasureCondition [metaUniqueName=" + metaName + ", measureConditions=" + measureConditions + "]";
    }

 
    /**
     * 指标查询条件
     * @author yichao.jiang
     *
     */
    public static enum SQLCondition implements Serializable {
    	// 等于
    	EQ("=") {
    		public String parseToExpression () {
    			if (!check(this.getConditionValues())) {
    				return null;
    			}
    			StringBuilder stringBuilder = new StringBuilder();
    			stringBuilder.append(this.getMetaName());
    			stringBuilder.append(this.getValue());
    			stringBuilder.append(this.getConditionValues().get(0));
    			return stringBuilder.toString();
    		}
    	}, 
    	// 不等于
    	NOT_EQ("<>") {
    		public String parseToExpression () {
    			if (!check(this.getConditionValues())) {
    				return null;
    			}
    			StringBuilder stringBuilder = new StringBuilder();
    			stringBuilder.append(this.getMetaName());
    			stringBuilder.append(this.getValue());
    			stringBuilder.append(this.getConditionValues().get(0));
    			return stringBuilder.toString();
    		}
    	},
    	
    	// 小于
    	LT("<") {
    		public String parseToExpression () {
    			if (!check(this.getConditionValues())) {
    				return null;
    			}
    			StringBuilder stringBuilder = new StringBuilder();
    			stringBuilder.append(this.getMetaName());
    			stringBuilder.append(this.getValue());
    			stringBuilder.append(this.getConditionValues().get(0));
    			return stringBuilder.toString();
    		}
    	}, 
    	
    	// 大于
    	GT(">") {
    		public String parseToExpression () {
    			if (!check(this.getConditionValues())) {
    				return null;
    			}
    			StringBuilder stringBuilder = new StringBuilder();
    			stringBuilder.append(this.getMetaName());
    			stringBuilder.append(this.getValue());
    			stringBuilder.append(this.getConditionValues().get(0));
    			return stringBuilder.toString();
    		}
    	}, 
    	
    	// between and
    	BETWEEN_AND("between and") {
    		public String parseToExpression () {
    			List<String> conditionValues = this.getConditionValues();
    			if (!check(conditionValues)) {
    				return null;
    			}
    			String leftValue = conditionValues.get(0);
    			String rightValue = conditionValues.get(1);
    			StringBuilder stringBuilder = new StringBuilder();
    			stringBuilder.append(this.getMetaName());
    			stringBuilder.append("between ");
    			stringBuilder.append(leftValue);
    			stringBuilder.append(" and ");
    			stringBuilder.append(rightValue);
    			return stringBuilder.toString();
    		}
    		
    		/**
    		 * 重写条件数值检查方法 
    		 */
    		boolean check(List<String> conditionValues) {
    			if (conditionValues == null || conditionValues.size() == 0 || conditionValues.size() != 2) {
    				return false;
    			}
    			return true;
    		}
    	}, 
    	
    	// in
    	IN("in") {
    		public String parseToExpression () {
    			List<String> conditionValues = this.getConditionValues();
    			if (!check(conditionValues)) {
    				return null;
    			}
    			StringBuilder stringBuilder = new StringBuilder();
    			stringBuilder.append(this.getMetaName());
    			stringBuilder.append("in (");
    			for (String conditionValue : conditionValues) {
    				stringBuilder.append(conditionValue);
    				stringBuilder.append(",");
    			}
    			// 替换最后一个","
    			stringBuilder.replace(stringBuilder.lastIndexOf(","), stringBuilder.length(), "");
    			stringBuilder.append(")");
    			return stringBuilder.toString();
    		}
    		
    		/**
    		 * 重写条件数值检查方法 
    		 */
    		boolean check(List<String> conditionValues) {
    			if (conditionValues == null || conditionValues.size() == 0) {
    				return false;
    			}
    			return true;
    		}
    	}, 
    	
    	// 小于等于
    	LT_EQ("<=") {
    		public String parseToExpression () {
    			if (!check(this.getConditionValues())) {
    				return null;
    			}
    			StringBuilder stringBuilder = new StringBuilder();
    			stringBuilder.append(this.getMetaName());
    			stringBuilder.append(this.getValue());
    			stringBuilder.append(this.getConditionValues().get(0));
    			return stringBuilder.toString();
    		}
    	}, 
    	
    	// 大于等于
    	GT_EQ(">=") {
    		public String parseToExpression () {
    			if (!check(this.getConditionValues())) {
    				return null;
    			}
    			StringBuilder stringBuilder = new StringBuilder();
    			stringBuilder.append(this.getMetaName());
    			stringBuilder.append(this.getValue());
    			stringBuilder.append(this.getConditionValues().get(0));
    			return stringBuilder.toString();
    		}
    	};
    	
    	/**
    	 * 条件对应的数值
    	 */
    	private List<String> conditionValues;  
    	
    	/**
    	 * 指标对应的名称
    	 */
    	private String metaName;
    	
    	/**
    	 *对应的sql条件，=,<=,等 
    	 */
    	private String value;
    	
    	/**
    	 * 构造函数
    	 * @param value
    	 */
    	private SQLCondition(String value) {
    		this.setValue(value);
    	}
    	
    	/**
    	 * 设置values值
    	 * @param values
    	 */
    	public void setConditionValues(List<String> conditionValues) {
    		this.conditionValues = conditionValues;
    	}
    	
    	/**
    	 * 获取values值
    	 * @return
    	 */
    	public List<String> getConditionValues() {
    		return this.conditionValues;
    	}
    	
    	
    	/**
		 * @return the metaName
		 */
		public String getMetaName() {
			return metaName;
		}

		/**
		 * @param metaName the metaName to set
		 */
		public void setMetaName(String metaName) {
			this.metaName = metaName;
		}

		
		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @param value the value to set
		 */
		public void setValue(String value) {
			this.value = value;
		}

		/**
    	 * 将SQL条件转为具体的表达式
    	 * @return 表达式
    	 */
    	public abstract String parseToExpression();
    	
    	/**
    	 * 检查条件对应的参数值是否合法，默认检查参数值为1个，其他需要检查多个的情况，需要对该方法进行重写
    	 * @return
    	 */
    	boolean check(List<String> conditionValues) {
    		if (conditionValues==null || conditionValues.size()==0) {
    			return false;
    		}
    		if (conditionValues.size() > 1) {
    			return false;    			
    		}
    		return true;
    	}
    }
    
}
