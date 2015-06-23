package com.baidu.rigel.biplatform.ma.report.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baidu.rigel.biplatform.ac.model.Cube;
import com.baidu.rigel.biplatform.ac.model.Dimension;
import com.baidu.rigel.biplatform.ac.util.DataModelUtils;
import com.baidu.rigel.biplatform.ac.util.MetaNameUtil;
import com.baidu.rigel.biplatform.ma.report.model.ExtendArea;
import com.baidu.rigel.biplatform.ma.report.model.ExtendAreaType;
import com.baidu.rigel.biplatform.ma.report.model.Item;
import com.baidu.rigel.biplatform.ma.report.model.LinkInfo;
import com.baidu.rigel.biplatform.ma.report.model.LinkParams;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.baidu.rigel.biplatform.ma.report.model.PlaneTableCondition;
import com.baidu.rigel.biplatform.ma.report.model.ReportDesignModel;
import com.baidu.rigel.biplatform.ma.report.service.OlapLinkService;
import com.baidu.rigel.biplatform.ma.report.service.ReportDesignModelService;
import com.baidu.rigel.biplatform.ma.report.utils.QueryUtils;

/**
 * olapLinkService实现
 * 
 * @author majun04
 *
 */
@Service("olapLinkService")
public class OlapLinkServiceImpl implements OlapLinkService {
    /**
     * singleDImValue
     */
    private static final String SINGLE_DIM_VALUE = "singleDimValue";
    /**
     * uniqueName
     */
    private static final String UNIQUE_NAME = "uniqueName";
    /**
     * reportDesignModelService
     */
    @Resource
    private ReportDesignModelService reportDesignModelService;

    /*
     * (non-Javadoc)
     * 
     * @see com.baidu.rigel.biplatform.ma.report.service.OlapLinkService#getDesignModelListContainsPlaneTable()
     */
    public List<ReportDesignModel> getDesignModelListContainsPlaneTable() {
        ReportDesignModel[] allList = reportDesignModelService.queryAllModels(true);
        List<ReportDesignModel> forReturnList = new ArrayList<ReportDesignModel>();
        if (allList != null && allList.length > 0) {
            for (ReportDesignModel reportDesignModel : allList) {
                ExtendArea[] extendAreaList = reportDesignModel.getExtendAreaList();
                for (ExtendArea extendArea : extendAreaList) {
                    if (extendArea.getType().equals(ExtendAreaType.PLANE_TABLE)) {
                        forReturnList.add(reportDesignModel);
                        break;
                    }
                }
            }
        }
        return forReturnList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.biplatform.ma.report.service.OlapLinkService#getPlaneTableConditionList(com.baidu.rigel.biplatform
     * .ma.report.model.ReportDesignModel)
     */
    public List<String> getPlaneTableConditionList(ReportDesignModel planeTableDesignModel) {
        Map<String, PlaneTableCondition> conditionMap = planeTableDesignModel.getPlaneTableConditions();
        List<String> planeTableParamNameList = new ArrayList<String>();
        for (String key : conditionMap.keySet()) {
            PlaneTableCondition condition = conditionMap.get(key);
            planeTableParamNameList.add(condition.getName());
        }
        return planeTableParamNameList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.biplatform.ma.report.service.OlapLinkService#getOlapDims(com.baidu.rigel.biplatform.ma.report
     * .model.ReportDesignModel, com.baidu.rigel.biplatform.ma.report.model.ExtendArea)
     */
    public List<Dimension> getOlapDims(ReportDesignModel olapTableDesignModel, ExtendArea olapTableArea) {
        List<Dimension> dimList = new ArrayList<Dimension>();
        List<Dimension> conditionDims = this.getOlapTableConditionDims(olapTableDesignModel);
        List<Dimension> rowDims = this.getOlapTableRowDims(olapTableDesignModel, olapTableArea);
        dimList.addAll(conditionDims);
        dimList.addAll(rowDims);
        return dimList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.biplatform.ma.report.service.OlapLinkService#buildConditionMapFromRequestParams(java.lang.String)
     */
    public Map<String, Map<String, String>> buildConditionMapFromRequestParams(String uniqueName) {
        /**
         * 得到的点击行的维值表达式
         */
        String[] uniqNames = DataModelUtils.parseNodeUniqueNameToNodeValueArray(uniqueName);
        Map<String, Map<String, String>> conditionMap = new HashMap<String, Map<String, String>>();
        for (String metaName : uniqNames) {
            String singleDimName = MetaNameUtil.getDimNameFromUniqueName(metaName);
            String singleDImValue = MetaNameUtil.getNameFromMetaName(metaName);
            Map<String, String> uniqueMap = new HashMap<String, String>();
            uniqueMap.put(SINGLE_DIM_VALUE, singleDImValue);
            uniqueMap.put(UNIQUE_NAME, metaName);
            conditionMap.put(singleDimName, uniqueMap);
        }
        return conditionMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.baidu.rigel.biplatform.ma.report.service.OlapLinkService#buildLinkBridgeParams(com.baidu.rigel.biplatform
     * .ma.report.model.LinkInfo, java.util.Map)
     */
    public Map<String, LinkParams> buildLinkBridgeParams(LinkInfo linkInfo,
            Map<String, Map<String, String>> conditionMap) {
        Map<String, LinkParams> linkBridgeParams = new HashMap<String, LinkParams>();

        for (String paramName : linkInfo.getParamMapping().keySet()) {
            LinkParams linkParams = new LinkParams();
            linkParams.setParamName(paramName);
            String dimName = linkInfo.getParamMapping().get(paramName);
            linkParams.setDimName(dimName);
            Map<String, String> uniqueMap = conditionMap.get(dimName);
            String dimValue = uniqueMap.get(SINGLE_DIM_VALUE);
            String metaName = uniqueMap.get(UNIQUE_NAME);
            linkParams.setOriginalDimValue(dimValue);
            linkParams.setUniqueName(metaName);
            linkBridgeParams.put(paramName, linkParams);
        }

        return linkBridgeParams;
    }

    /**
     * 根据报表设计模型，得到报表工具查询条件维度集合
     * 
     * @param olapTableDesignModel 多维报表设计模型
     * @return 返回报表公共查询条件维度组成的集合对象
     */
    private List<Dimension> getOlapTableConditionDims(ReportDesignModel olapTableDesignModel) {
        ExtendArea[] extendAreaArray = olapTableDesignModel.getExtendAreaList();
        List<Dimension> dimList = new ArrayList<Dimension>();
        for (ExtendArea extendArea : extendAreaArray) {
            boolean isFilter = QueryUtils.isFilterArea(extendArea.getType());
            if (isFilter) {
                Item item = extendArea.listAllItems().values().toArray(new Item[0])[0];
                Cube cube = olapTableDesignModel.getSchema().getCubes().get(extendArea.getCubeId());
                String dimId = item.getOlapElementId();
                Dimension dim = cube.getDimensions().get(dimId);
                dimList.add(dim);
            }
        }
        return dimList;
    }

    /**
     * 根据报表设计模型和table所在区域对象，得到放到table列上的维度集合
     * 
     * @param olapTableDesignModel 多维报表设计模型
     * @param olapTableArea 多维报表area区域对象
     * @return 返回放到table列上的维度集合
     */
    private List<Dimension> getOlapTableRowDims(ReportDesignModel olapTableDesignModel, ExtendArea olapTableArea) {
        LogicModel olapTableLogicModel = olapTableArea.getLogicModel();
        Cube cube = olapTableDesignModel.getSchema().getCubes().get(olapTableArea.getCubeId());
        Item[] items = olapTableLogicModel.getRows();
        List<Dimension> dimList = new ArrayList<Dimension>();
        for (Item dimItem : items) {
            String dimId = dimItem.getOlapElementId();
            Dimension dim = cube.getDimensions().get(dimId);
            dimList.add(dim);
        }
        return dimList;
    }
}
