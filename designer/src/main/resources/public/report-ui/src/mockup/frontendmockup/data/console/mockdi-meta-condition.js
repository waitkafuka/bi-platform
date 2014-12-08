// 外壳的mockup
(function() {
	
    var NS = xmock.data.console.MetaCondition = {};
    
    function random () {
        return Math.round(Math.random() * 10000000);
    }
            
    function META_CONDITION_IND_DIM(url, options) {

        // 指标维度列表
        // 0：不可选，1：可选，2：已选中
        var metaDataWrap = {
            inds: [],
            dims: []
        };

        // 指标
        for (var i = 0; i < 30; i ++) {
            var uName = 'IND_UNIQUE_' + i;
            metaDataWrap.inds.push(
                { 
                    status: 1, 
                    uniqName: uName, 
                    calcColumnRefInd: uName == 'IND_UNIQUE_7' 
                        // 表示是某个指标的计算列
                        ? ['IND_UNIQUE_2', 'IND_UNIQUE_4'] 
                        : void 0,
                    caption: '指标一二三四五六七八九十一二三四完了_' + i,
                    name : 'IND_NAME'+i
                }
            );
        }

        // 维度
        for (var i = 0; i < 20; i ++) {
            metaDataWrap.dims.push(
                { 
                    status: 1, 
                    uniqName: 'DIM_UNIQUE_' + i, 
                    caption: (i == 0 ? '时间' : '维度_' + i),
                    isTimeDim: i == 0,
                    fixed: i == 5 || i == 12,
                    align: i == 10 ? 'RIGHT' : (i == 11 || i == 12 ? 'LEFT' : void 0),
                    isConfig: i%2 == 0 ? true : false,
                    name : 'DIM_NAME' + i
                }
            );
        }

        // 当前选中
        var selectedWrap = {
            ROW: [],
            FILTER: []
        };

        var index4Selected = ['ROW', 'FILTER'];
        // 行/轴选中
        for (var i = 0, o; i < 4; i ++) {
            o = metaDataWrap.dims[i + 4];
            selectedWrap['ROW'].push(
                { 
                    status: 1, 
                    uniqName: o.uniqName, 
                    caption: o.caption
                }
            );
        }

        // 有指标也有维度的列
        var lineName = 'COLUMN_' + parseInt(random() % 100);
        var line = [];
        for (var j = 0, o; j < 3; j ++) {
            o = metaDataWrap.inds[j + 1];
            line.push(
                { 
                    status: 1, 
                    uniqName: o.uniqName, 
                    caption: o.caption 
                }
            );
        }        
        for (var j = 0, o; j < 3; j ++) {
            o = metaDataWrap.dims[j + 1];
            line.push(
                { 
                    status: 1, 
                    uniqName: o.uniqName, 
                    caption: o.caption 
                }
            );
        }
        index4Selected.push(lineName);
        selectedWrap[lineName] = line;

        // 列/系列选中
        for (var i = 0, line; i < 3; i ++) {
            var lineName = 'COLUMN_' + parseInt(random() % 100);
            line = [];
            for (var j = 0, o; j < 3; j ++) {
                o = metaDataWrap.dims[j + 5 + 3 * i];
                line.push(
                    { 
                        status: 1, 
                        uniqName: o.uniqName, 
                        caption: o.caption 
                    }
                );
            }
            index4Selected.push(lineName);
            selectedWrap[lineName] = line;
        }

        var seriesTypes = {};
        for (var k = 0, name; name = index4Selected[k]; k ++) {
            seriesTypes[name] = k % 2 == 0 ? 'line' : 'bar';
        }

        // filter选中
        for (var i = 0, o; i < 5; i ++) {
            o = metaDataWrap.dims[i + 12];
            selectedWrap['FILTER'].push(
                { 
                    status: 1, 
                    uniqName: o.uniqName, 
                    caption: o.caption 
                }
            );
        }

        var metaStatusData = {
            indMetas: {
                validMetaNames: ['IND_UNIQUE_2', 'IND_UNIQUE_4', 'IND_UNIQUE_6', 'IND_UNIQUE_7', 'IND_UNIQUE_8', 'IND_UNIQUE_9'],
                selectedMetaNames: ['IND_UNIQUE_1','IND_UNIQUE_3','IND_UNIQUE_5']
            },
            dimMetas: {
                validMetaNames: ['DIM_UNIQUE_3', 'DIM_UNIQUE_5'],
                selectedMetaNames: ['DIM_UNIQUE_6']
            }
        };
        var result={
            status: 0,
            statusInfo: 'meta',
            data: {
                metaData: metaDataWrap,
                reportTemplateId: 'meta' + Math.random(),
                selected: selectedWrap,
                index4Selected: index4Selected,
                // 图的类型
                seriesTypes: seriesTypes,
                metaStatusData: metaStatusData
            }
        }
        console.log(result);
        return {
            status: 0,
            statusInfo: 'meta',
            data: {
                metaData: metaDataWrap,
                reportTemplateId: 'meta' + Math.random(),
                selected: selectedWrap,
                index4Selected: index4Selected,
                // 图的类型
                seriesTypes: seriesTypes,
                metaStatusData: metaStatusData
            }
        }
    }

    function META_CONDITION_SELECT() {
        
        var metaStatusData = {
            indMetas: {
                validMetaNames: ['IND_UNIQUE_2', 'asdfasd', 'asdfsaddsdf'],
                selectedMetaNames: ['IND_UNIQUE_5']
            },
            dimMetas: {
                validMetaNames: ['DIM_UNIQUE_3', 'DIM_UNIQUE_5'],
                selectedMetaNames: ['DIM_UNIQUE_6']
            }
        };

        return {
            status: 0,
            statusInfo: 'meta',
            data: { metaStatusData: metaStatusData }
        }
    }

    function META_CONDITION_LIST_SELECT_CHART(url, options) {
        return {
            status: 0,
            statusInfo: 'meta',
            data: {}
        }
    }

    function META_CONDITION_LIST_SELECT_TABLE(url, options) {
        return {
            status: 0,
            statusInfo: 'meta',
            data: {}
        }
    }

    function META_CONDITION_COL_CONFIG_GET(url, options) {
        return {
            status: 0,
            statusInfo: 'meta',
            // TODO
            data: {}
        }
    }

    function META_CONDITION_COL_CONFIG_SUBMIT(url, options) {
        return {
            status: 0,
            statusInfo: 'meta',
            // TODO
            data: {}
        }
    }

    function META_CONDITION_CANDIDATE_INIT(url, options) {
        var odata = META_CONDITION_IND_DIM(url, options);
        var validMeasureName = ['IND_UNIQUE_2', 'IND_UNIQUE_4', 'IND_UNIQUE_6'];
        var validDimensionNames = ['DIM_UNIQUE_3'];
        return {
            status: 0,
            statusInfo: 'meta',
            data: {
                metaData: odata.data.metaData,
                validDimensionNames: validDimensionNames,
                validMeasureName: validMeasureName
            }
        }
    }

    function META_CONDITION_CANDIDATE_SUBMIT(url, options) {
        var data = {
        };

        return {
            status: 0,
            statusInfo: 'meta',
            // TODO
            data: data
        }
    }

    function CONSOLE_CHART_CONFIG_INIT(url, options) {
        var data = {
            yAxises: {
                left: {
                    unitName: '元啊元'
                },
                right: {
                    unitName: '元啊元'
                }
            },
            index4Selected: ['COLUMN_4', 'COLUMN_3', 'COLUMN_2', 'COLUMN_1', 'COLUMN_0'],
            series: {
                COLUMN_0: {
                    type: 'line',
                    yAxisName: 'left'
                },
                COLUMN_1: {
                    type: 'bar',
                    yAxisName: 'right'

                },
                COLUMN_2: {
                    type: 'beaker',
                    yAxisName: 'right'
                },
                COLUMN_3: {
                    type: 'pie',
                    yAxisName: 'left'
                },
                COLUMN_4: {
                    type: 'line',
                    yAxisName: 'right'
                }
            }            
        };

        return {
            status: 0,
            statusInfo: 'meta',
            // TODO
            data: data
        }
    }

    function CONSOLE_CHART_CONFIG_SUBMIT(url, options) {
        var data = {

        };

        return {
            status: 0,
            statusInfo: 'meta',
            // TODO
            data: data
        }
    }

    function ROWHEAD_CONFIG_INIT(url, options) {

        var config = {
            '0':'NONE',
            '1':'LINK',
            '2':'PLUS_MINUS'
        };
        return {
            status: 0,
            statusInfo: 'meta',
            data: {
                drillTypeConfig: config,
                candicates: ['NONE','LINK','PLUS_MINUS']
            }
        }
    }

    function ROWHEAD_CONFIG_SUBMIT() {
        return {
            status: 0,
            statusInfo: 'SUCCESS',
            data: { '':'' }
        }
    }

    function DIMSHOW_CONFIG_INIT(url, options) {

        var childrenShowConfig = {
            '0':'CHILDREN_FOLLOW',
            '1':'SINGLE_NODE',
            '2':'SINGLE_NODE'
        };
        var fatherShowConfig = {
            '0':'EXCLUDE',
            '1':'INCLUDE',
            '2':'EXCLUDE'
        };
        return {
            status: 0,
            statusInfo: 'meta',
            data: {
                childrenShowConfig: childrenShowConfig,
                fatherShowConfig: fatherShowConfig,
                childrenType: ['SINGLE_NODE','CHILDREN_FOLLOW'],
                fatherType: ['INCLUDE','EXCLUDE']
            }
        }
    }

    function DIMSHOW_CONFIG_SUBMIT() {
        return {
            status: 0,
            statusInfo: 'SUCCESS',
            data: { '':'' }
        }
    }

    NS.META_CONDITION_IND_DIM = META_CONDITION_IND_DIM;
    // NS.META_CONDITION_IND_DIM = {"data":{"selected":{"ROW":[],"COLUMN_0":[{"cubeName":"mid_is_day_opp_pool","status":0,"visible":true,"fixed":false,"name":"contract_wait_back","uniqName":"mid_is_day_opp_pool!contract_wait_back","caption":"合同待返回","selected":false,"customUniqueName":"mid_is_day_opp_pool!contract_wait_back"},{"cubeName":"mid_is_day_opp_pool","status":0,"visible":true,"fixed":false,"name":"wait_qualification_verify","uniqName":"mid_is_day_opp_pool!wait_qualification_verify","caption":"待资质审核","selected":false,"customUniqueName":"mid_is_day_opp_pool!wait_qualification_verify"},{"cubeName":"mid_is_day_opp_pool","status":0,"visible":true,"fixed":false,"name":"qualification_verify_reject","uniqName":"mid_is_day_opp_pool!qualification_verify_reject","caption":"资质审核驳回","selected":false,"customUniqueName":"mid_is_day_opp_pool!qualification_verify_reject"},{"cubeName":"mid_is_day_opp_pool","status":0,"visible":true,"fixed":false,"name":"qualification_verify_shelve","uniqName":"mid_is_day_opp_pool!qualification_verify_shelve","caption":"资质审核搁置","selected":false,"customUniqueName":"mid_is_day_opp_pool!qualification_verify_shelve"},{"cubeName":"mid_is_day_opp_pool","status":0,"visible":true,"fixed":false,"name":"account_verify_reject","uniqName":"mid_is_day_opp_pool!account_verify_reject","caption":"开户审核驳回","selected":false,"customUniqueName":"mid_is_day_opp_pool!account_verify_reject"},{"cubeName":"mid_is_day_opp_pool","status":0,"visible":true,"fixed":false,"name":"account_verify_shelve","uniqName":"mid_is_day_opp_pool!account_verify_shelve","caption":"开户审核已搁置","selected":false,"customUniqueName":"mid_is_day_opp_pool!account_verify_shelve"},{"cubeName":"mid_is_day_opp_pool","status":0,"visible":true,"fixed":false,"name":"account_verify_need_for_retrial","uniqName":"mid_is_day_opp_pool!account_verify_need_for_retrial","caption":"开户审核需再审","selected":false,"customUniqueName":"mid_is_day_opp_pool!account_verify_need_for_retrial"},{"cubeName":"mid_is_day_opp_pool","status":0,"visible":true,"fixed":false,"name":"wait_account_verify","uniqName":"mid_is_day_opp_pool!wait_account_verify","caption":"待开户审核","selected":false,"customUniqueName":"mid_is_day_opp_pool!wait_account_verify"},{"cubeName":"mid_is_day_opp_pool","status":0,"visible":true,"fixed":false,"name":"wait_account_confirmed","uniqName":"mid_is_day_opp_pool!wait_account_confirmed","caption":"待开户确认","selected":false,"customUniqueName":"mid_is_day_opp_pool!wait_account_confirmed"},{"cubeName":"mid_is_day_opp_pool","status":0,"visible":true,"fixed":false,"name":"have_account_not_add_money","uniqName":"mid_is_day_opp_pool!have_account_not_add_money","caption":"已开户未加款","selected":false,"customUniqueName":"mid_is_day_opp_pool!have_account_not_add_money"},{"cubeName":"mid_is_day_opp_pool","status":0,"visible":true,"fixed":false,"name":"have_deal_done_not_online","uniqName":"mid_is_day_opp_pool!have_deal_done_not_online","caption":"已成单未上线","selected":false,"customUniqueName":"mid_is_day_opp_pool!have_deal_done_not_online"},{"cubeName":"mid_is_day_opp_pool","status":0,"visible":true,"fixed":false,"name":"order_cancel_processing","uniqName":"mid_is_day_opp_pool!order_cancel_processing","caption":"订单撤销处理中","selected":false,"customUniqueName":"mid_is_day_opp_pool!order_cancel_processing"},{"cubeName":"mid_is_day_opp_pool","status":0,"visible":true,"fixed":false,"name":"order_cancel_processing_complete","uniqName":"mid_is_day_opp_pool!order_cancel_processing_complete","caption":"订单撤销处理已完成","selected":false,"customUniqueName":"mid_is_day_opp_pool!order_cancel_processing_complete"}],"FILTER":[{"isShareDim":false,"cubeName":"mid_is_day_opp_pool","isTimeDim":true,"asFilter":false,"isHidden":false,"status":0,"fixed":false,"name":"record_add_date","uniqName":"record_add_date","caption":"时间","selected":false,"customUniqueName":"record_add_date"}]},"yAxises":{"left":{"name":"left","unitName":"元"}},"seriesTypes":{"COLUMN_0":"line"},"index4Selected":["ROW","FILTER","COLUMN_0"],"metaStatusData":{"selectCubeNum":0},"metaData":{"dims":[{"isShareDim":false,"cubeName":"mid_is_day_opp_pool","isTimeDim":false,"asFilter":false,"isHidden":false,"status":1,"fixed":false,"name":"is_dim_pos","uniqName":"mid_is_day_opp_pool!is_dim_pos","caption":"IS岗位","selected":false},{"isShareDim":true,"cubeName":"mid_is_day_opp_pool","isTimeDim":false,"asFilter":false,"isHidden":false,"status":1,"fixed":false,"name":"dim_product_line","uniqName":"dim_product_line","caption":"产品线","selected":false},{"isShareDim":false,"cubeName":"mid_is_day_opp_pool","isTimeDim":true,"asFilter":false,"isHidden":false,"status":1,"fixed":false,"name":"record_add_date","uniqName":"mid_is_day_opp_pool!record_add_date","caption":"时间","selected":false},{"isShareDim":true,"cubeName":"mid_is_day_opp_pool","isTimeDim":false,"asFilter":false,"isHidden":false,"status":1,"fixed":false,"name":"dim_trade_info","uniqName":"dim_trade_info","caption":"行业","selected":false}],"inds":[{"status":1,"visible":true,"fixed":false,"name":"new_increase_protect_cust_num","uniqName":"mid_is_day_opp_pool!new_increase_protect_cust_num","caption":"新增客保总量","selected":false},{"status":1,"visible":true,"fixed":false,"name":"is_turn_out_opp_num","uniqName":"mid_is_day_opp_pool!is_turn_out_opp_num","caption":"转出商机量","selected":false},{"status":1,"visible":true,"fixed":false,"name":"valid_turn_out_opp_num","uniqName":"mid_is_day_opp_pool!valid_turn_out_opp_num","caption":"有效转出商机量","selected":false},{"status":1,"visible":true,"fixed":false,"name":"success_turn_out_opp_num","uniqName":"mid_is_day_opp_pool!success_turn_out_opp_num","caption":"成功转出商机量","selected":false},{"status":1,"visible":true,"fixed":false,"name":"reject_opp_num","uniqName":"mid_is_day_opp_pool!reject_opp_num","caption":"商机驳回量","selected":false},{"status":1,"visible":true,"fixed":false,"name":"reject_before_noon_opp_num","uniqName":"mid_is_day_opp_pool!reject_before_noon_opp_num","caption":"12点前驳回量","selected":false},{"status":1,"visible":true,"fixed":false,"name":"submit_order_num","uniqName":"mid_is_day_opp_pool!submit_order_num","caption":"提单量","selected":false},{"status":1,"visible":true,"fixed":false,"name":"payment_received_order_num","uniqName":"mid_is_day_opp_pool!payment_received_order_num","caption":"出单量","selected":false},{"status":1,"visible":true,"fixed":false,"name":"contract_back_num","uniqName":"mid_is_day_opp_pool!contract_back_num","caption":"合同返还量","selected":false},{"status":1,"visible":true,"fixed":false,"name":"qualification_verify_pass_num","uniqName":"mid_is_day_opp_pool!qualification_verify_pass_num","caption":"资质审核通过量","selected":false},{"status":1,"visible":true,"fixed":false,"name":"account_verify_pass_num","uniqName":"mid_is_day_opp_pool!account_verify_pass_num","caption":"开户审核通过量","selected":false},{"status":1,"visible":true,"fixed":false,"name":"deal_done_order_num","uniqName":"mid_is_day_opp_pool!deal_done_order_num","caption":"成单量","selected":false},{"status":1,"visible":true,"fixed":false,"name":"account_online_order_num","uniqName":"mid_is_day_opp_pool!account_online_order_num","caption":"上线量","selected":false},{"status":2,"visible":true,"fixed":false,"name":"contract_wait_back","uniqName":"mid_is_day_opp_pool!contract_wait_back","caption":"合同待返回","selected":false},{"status":2,"visible":true,"fixed":false,"name":"wait_qualification_verify","uniqName":"mid_is_day_opp_pool!wait_qualification_verify","caption":"待资质审核","selected":false},{"status":2,"visible":true,"fixed":false,"name":"qualification_verify_reject","uniqName":"mid_is_day_opp_pool!qualification_verify_reject","caption":"资质审核驳回","selected":false},{"status":2,"visible":true,"fixed":false,"name":"qualification_verify_shelve","uniqName":"mid_is_day_opp_pool!qualification_verify_shelve","caption":"资质审核搁置","selected":false},{"status":2,"visible":true,"fixed":false,"name":"account_verify_reject","uniqName":"mid_is_day_opp_pool!account_verify_reject","caption":"开户审核驳回","selected":false},{"status":2,"visible":true,"fixed":false,"name":"account_verify_shelve","uniqName":"mid_is_day_opp_pool!account_verify_shelve","caption":"开户审核已搁置","selected":false},{"status":2,"visible":true,"fixed":false,"name":"account_verify_need_for_retrial","uniqName":"mid_is_day_opp_pool!account_verify_need_for_retrial","caption":"开户审核需再审","selected":false},{"status":2,"visible":true,"fixed":false,"name":"wait_account_verify","uniqName":"mid_is_day_opp_pool!wait_account_verify","caption":"待开户审核","selected":false},{"status":2,"visible":true,"fixed":false,"name":"wait_account_confirmed","uniqName":"mid_is_day_opp_pool!wait_account_confirmed","caption":"待开户确认","selected":false},{"status":2,"visible":true,"fixed":false,"name":"have_account_not_add_money","uniqName":"mid_is_day_opp_pool!have_account_not_add_money","caption":"已开户未加款","selected":false},{"status":2,"visible":true,"fixed":false,"name":"have_deal_done_not_online","uniqName":"mid_is_day_opp_pool!have_deal_done_not_online","caption":"已成单未上线","selected":false},{"status":2,"visible":true,"fixed":false,"name":"order_cancel_processing","uniqName":"mid_is_day_opp_pool!order_cancel_processing","caption":"订单撤销处理中","selected":false},{"status":2,"visible":true,"fixed":false,"name":"order_cancel_processing_complete","uniqName":"mid_is_day_opp_pool!order_cancel_processing_complete","caption":"订单撤销处理已完成","selected":false},{"status":1,"visible":true,"fixed":false,"name":"is_turn_out_confirmed_opp_num","uniqName":"mid_is_day_opp_pool!is_turn_out_confirmed_opp_num","caption":"IS转出已确认商机","selected":false},{"status":1,"visible":true,"fixed":false,"name":"is_turn_out_not_confirmed_opp_num","uniqName":"mid_is_day_opp_pool!is_turn_out_not_confirmed_opp_num","caption":"IS转出未确认商机","selected":false},{"status":1,"visible":true,"fixed":false,"name":"is_turn_out_today_opp_num","uniqName":"mid_is_day_opp_pool!is_turn_out_today_opp_num","caption":"IS转出当日商机","selected":false},{"status":1,"visible":true,"fixed":false,"name":"is_turn_out_other_day_opp_num","uniqName":"mid_is_day_opp_pool!is_turn_out_other_day_opp_num","caption":"IS转出隔日商机","selected":false},{"status":1,"visible":true,"fixed":false,"name":"reject_before_noon_opps","uniqName":"mid_is_day_opp_pool!reject_before_noon_opps","caption":"12点前驳回商机","selected":false},{"status":1,"visible":true,"fixed":false,"name":"is_turn_out_opp_task_num","uniqName":"mid_is_day_opp_pool!is_turn_out_opp_task_num","caption":"IS转出商机任务量","selected":false},{"status":1,"visible":true,"fixed":false,"name":"is_confirm_opp","uniqName":"mid_is_day_opp_pool!is_confirm_opp","caption":"IS每日转出商机量","selected":false},{"status":1,"visible":true,"fixed":false,"name":"plane_complete_rate","uniqName":"mid_is_day_opp_pool!plane_complete_rate","caption":"IS转出商机计划完成率","selected":false}],"selectCubeNum":1,"cubeNames":["mid_is_day_opp_pool"]}},"status":0,"statusInfo":""};
    NS.META_CONDITION_SELECT = META_CONDITION_SELECT;
    NS.META_CONDITION_LIST_SELECT_CHART = META_CONDITION_LIST_SELECT_CHART;
    NS.META_CONDITION_LIST_SELECT_TABLE = META_CONDITION_LIST_SELECT_TABLE;
    NS.META_CONDITION_COL_CONFIG_GET = META_CONDITION_COL_CONFIG_GET;
    NS.META_CONDITION_COL_CONFIG_SUBMIT = META_CONDITION_COL_CONFIG_SUBMIT;
    NS.META_CONDITION_CANDIDATE_INIT = META_CONDITION_CANDIDATE_INIT;
    NS.META_CONDITION_CANDIDATE_SUBMIT = META_CONDITION_CANDIDATE_SUBMIT;
    NS.ROWHEAD_CONFIG_INIT = ROWHEAD_CONFIG_INIT;
    NS.ROWHEAD_CONFIG_SUBMIT = ROWHEAD_CONFIG_SUBMIT;
    NS.DIMSHOW_CONFIG_INIT = DIMSHOW_CONFIG_INIT;
    NS.DIMSHOW_CONFIG_SUBMIT = DIMSHOW_CONFIG_SUBMIT;
    
    // 图设置
    NS.META_CONDITION_ADD_SERIES_GROUP = META_CONDITION_IND_DIM;
    NS.META_CONDITION_REMOVE_SERIES_GROUP = META_CONDITION_IND_DIM;
    NS.CONSOLE_CHART_CONFIG_INIT = CONSOLE_CHART_CONFIG_INIT;
    NS.CONSOLE_CHART_CONFIG_SUBMIT = CONSOLE_CHART_CONFIG_SUBMIT;

    NS.REPORT_ROWMERGE_KEY_SUBMIT = function(url,options){
        return {
            status : 0,
            statusInfo : 'set rowmerge success',
            data : {}
        }
    }

})();