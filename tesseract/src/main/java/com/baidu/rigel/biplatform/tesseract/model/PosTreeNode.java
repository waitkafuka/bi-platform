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
package com.baidu.rigel.biplatform.tesseract.model;

import java.util.List;

/**
 * 类PosTreeNode.java的实现描述：岗位CALL_BACK接口返回对象元素 Callback接口请求参数:
 * http://icrm-off.baidu.com:8080/crm-portal/reportauth/dataAuth2
 * .action?appId=57&parentPosIds=32288&levelToRoot=3&needCsPosition=1 parentPosIds 请求的岗位ID列表，多个ID用逗号分隔 levelToRoot
 * 请求的岗位树和请求的岗位ID的层级，0表示只需要显示请求的岗位ID的信息，如1 表示当前ID和当前ID有 直接管辖关系的一层孩子节点的信息 needCsPosition 1表示需要显示当前节点的一线岗位ID，0则不需要，不传默认为0
 * appId 产品线的appid
 * 
 * @author chenxiaoming01 2013-12-15 下午8:49:10
 */
public class PosTreeNode implements TreeModel {

    /**
     * default generate serialVersionUID
     */
    private static final long serialVersionUID = 8835259876881009914L;

    /**
     * 岗位ID
     */
    private String posId;

    /**
     * 岗位名字
     */
    private String name;

    /**
     * 是否有孩子节点
     */
    private boolean hasChildren;

    /**
     * 当前节点管辖的一线节点ID列表
     */
    private List<String> csPosIds;

    /**
     * 当前岗位的子岗位
     */
    private List<PosTreeNode> children;

    /**
     * default generate get posId
     * 
     * @return the posId
     */
    public String getPosId() {
        return posId;
    }

    /**
     * default generate posId param set method
     * 
     * @param posId the posId to set
     */
    public void setPosId(String posId) {
        this.posId = posId;
    }

    /**
     * default generate get name
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * default generate name param set method
     * 
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * default generate get hasChildren
     * 
     * @return the hasChildren
     */
    public boolean isHasChildren() {
        return hasChildren;
    }

    /**
     * default generate hasChildren param set method
     * 
     * @param hasChildren the hasChildren to set
     */
    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    /**
     * default generate get children
     * 
     * @return the children
     */
    public List<PosTreeNode> getChildren() {
        return children;
    }

    /**
     * default generate children param set method
     * 
     * @param children the children to set
     */
    public void setChildren(List<PosTreeNode> children) {
        this.children = children;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PosTreeNode [posId=" + posId + ", name=" + name + ", hasChildren=" + hasChildren + ", leafPosids="
                + csPosIds + ", children size=" + (children == null ? 0 : children.size()) + "]";
    }

    /**
     * default generate get csPosIds
     * 
     * @return the csPosIds
     */
    public List<String> getCsPosIds() {
        return csPosIds;
    }

    /**
     * default generate csPosIds param set method
     * 
     * @param csPosIds the csPosIds to set
     */
    public void setCsPosIds(List<String> csPosIds) {
        this.csPosIds = csPosIds;
    }

}
