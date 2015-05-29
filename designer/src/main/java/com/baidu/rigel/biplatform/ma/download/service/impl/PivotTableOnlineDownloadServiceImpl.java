package com.baidu.rigel.biplatform.ma.download.service.impl;

import com.baidu.rigel.biplatform.ac.query.MiniCubeConnection;
import com.baidu.rigel.biplatform.ac.query.MiniCubeDriverManager;
import com.baidu.rigel.biplatform.ac.query.data.DataModel;
import com.baidu.rigel.biplatform.ac.query.data.DataSourceInfo;
import com.baidu.rigel.biplatform.ac.query.model.ConfigQuestionModel;
import com.baidu.rigel.biplatform.ac.query.model.QuestionModel;
import com.baidu.rigel.biplatform.ma.download.service.DownloadTableDataService;
import com.baidu.rigel.biplatform.ma.report.model.LogicModel;
import com.baidu.rigel.biplatform.ma.resource.utils.DataModelUtils;

/**
 * 多维表在线下载服务实现类
 * @author yichao.jiang 2015年5月25日 下午8:05:17
 */
public class PivotTableOnlineDownloadServiceImpl implements DownloadTableDataService {

    /**
     * 私有构造方法
     */
    public PivotTableOnlineDownloadServiceImpl() {
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String downloadTableData(QuestionModel questionModel, LogicModel logicModel) {
        // 强转为ConfigQuestionModel
        ConfigQuestionModel configQuestionModel = (ConfigQuestionModel) questionModel;
        // 数据连接信息
        DataSourceInfo dataSourceInfo = configQuestionModel.getDataSourceInfo();
        // 查询请求连接
        MiniCubeConnection connection = MiniCubeDriverManager.getConnection(dataSourceInfo);
        // 查询，并返回DataModel结果
        DataModel dataModel = connection.query(configQuestionModel);
        // 获取csv字符串
        String csvString = DataModelUtils.convertDataModel2CsvString(configQuestionModel.getCube(), dataModel);
        return csvString;
    }
}
