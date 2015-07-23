package com.baidu.rigel.biplatform.queryrouter.queryplugin.plugins.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Description: where
 * @author 罗文磊
 *
 */
public class Where extends SqlSegment {
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4324971251933970062L;

    /**
     * name
     */
    private String name;
    
    /**
     * values
     */
    private List<Object> values;

    /**
     * getName
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * setName
     * 
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * getValues
     * 
     * @return the values
     */
    public List<Object> getValues() {
        return values;
    }

    /**
     * setValues
     * 
     * @param values the values to set
     */
    public void setValues(List<Object> values) {
        this.values = values;
    }
    
    /**
     * Where
     */
    public Where() {
        this.values = new ArrayList<Object>();
    }
}