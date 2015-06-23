package com.baidu.rigel.biplatform.ma.report.service;

import java.util.List;
import java.util.Map;

import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ma.report.model.ExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.LinkInfo;
import com.baidu.rigel.biplatform.ma.report.model.LinkParams;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;

/**
 * 多维报表跳转操作相关service
 * 
 * @author majun04
 *
 */
public interface OlapLinkService {
    /**
     * 得到所有本产品线下所有包含平面报表组件的ReportDesignModel列表
     * 
     * @return 符合条件的ReportDesignModel列表
     */
    public List<ReportDesignModel> getDesignModelListContainsPlaneTable();

    /**
     * 从平面报表设计模型中，得到所有平面报表设计用参数
     * 
     * @param planeTableDesignModel 平面报表设计模型
     * @return 得到平面报表参数列表
     */
    public List<String> getPlaneTableConditionList(ReportDesignModel planeTableDesignModel);

    /**
     * 根据多维报表模型和table对应的区域，得到所有table中要有的维度对象
     * 
     * @param olapTableDesignModel 多维报表设计模型
     * @param olapTableArea 多维报表area区域对象
     * @return 维度对象列表
     */
    public List<Dimension> getOlapDims(ReportDesignModel olapTableDesignModel, ExtendArea olapTableArea);

    /**
     * 根据传入的uniqueName，解析并构造跳转所需的条件对象
     * 
     * @param uniqueName uniqueName
     * @return 构造完毕的条件对象
     */
    public Map<String, Map<String, String>> buildConditionMapFromRequestParams(String uniqueName);

    /**
     * 根据跳转对象和前端传入的条件对象，构建LinkBridgeParams对象
     * 
     * @param linkInfo 跳转对象
     * @param conditionMap 条件map对象
     * @return 返回构造完成的LinkBridgeParams对象
     */
    public Map<String, LinkParams> buildLinkBridgeParams(LinkInfo linkInfo,
            Map<String, Map<String, String>> conditionMap);
}
