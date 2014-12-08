// 外壳的mockup
(function() {
    
    var NS = xmock.data.console;
    var isArray = xutil.lang.isArray;

    function random () {
        return Math.round(Math.random() * 10000000);
    }
    
    // NS.MOLD_QUERY = function (url, options) {
    //     return {
    //             "data": {
    //                 moldFiles: [
    //                 'MOLD_aaa.vm',
    //                 'MOLD_aaa.json',
    //                 'simple-cond-chart-table.vm',
    //                 'simple-cond-chart-table.json',
    //                 'cond-plane-chart-link-1.vm',
    //                 'cond-plane-chart-link-1.json',
    //                 'cond-pivot-meta-1.vm',
    //                 'cond-pivot-meta-1.json'
    //                 ]
    //             },
    //             "status": 0,
    //             "statusInfo": ""
            
    //     };
    // };

    NS.PAHNTOMJS_INFO = function (url, options) {
        return {
                data: {
                     phantomjsServerUrl: 'http://10.10.101.11:8899/',
                     screenShotMessage:'hahaha',
                     perviewUrl:'www.baidu.com'
                },
                "status": 0,
                "statusInfo": ""
            
        };
    };


    NS.MOLD_QUERY = function (url, options) {
        return {
                "data": {
                    moldFiles: [
                        {
                            "fileName": "MOLD_aaa.vm",
                            "desc": "asdasdsada"
                        },
                        {
                            "fileName": "MOLD_aaa.json",
                            "desc": "111111"
                        },
                        {
                            "fileName": "simple-cond-chart-table.vm",
                            "desc": "334324"
                        },
                        {
                            "fileName": "simple-cond-chart-table.json",
                            "desc": "5656"
                        },
                        {
                            "fileName": "cond-plane-chart-link-1.vm",
                            "desc": "平面表,图联动"
                        },
                        {
                            "fileName": "cond-plane-chart-link-1.json",
                            "desc": "平面表,图联动"
                        },
                        {
                            "fileName": "cond-plane-chart-link-1.vm",
                            "desc": "SoQuick"
                        },
                        {
                            "fileName": "cond-plane-chart-link-1.json",
                            "desc": "SoQuick||hahahaha||wqeqeqw"
                        }
                        
                    ]
                },
                "status": 0,
                "statusInfo": ""
            
        };
    };

    NS.REPORT_QUERY = function (url, options) {
        return {
        	    "data": {
        	        "savedReportInfo": [
                        {
                            "id": "try-1234",
                            "name": "第一个测试用的模板"
                        },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1013864809",
        	                "name": "new_OS销售过程本期商机分析-时间趋势分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-102133365",
        	                "name": "majun-IS外呼接通率报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1060720482",
        	                "name": "IA新增账户明细报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1081473445",
        	                "name": "OS拜访商机报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1204473809",
        	                "name": "IS客保量及状态统计报表明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1206813876",
        	                "name": "piao_test"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1264098705",
        	                "name": "IS外呼接通量报表2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1279949894",
        	                "name": "订单流程耗时表（支持成单订单流转周期表）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1312990339",
        	                "name": "OS关键指标订单信息表-平面2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1372777620",
        	                "name": "IS关键环节统计报表（订单信息）合同返还量"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1507520469",
        	                "name": "OS商机拜访报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1511207018",
        	                "name": "OS商机资源报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1522201856",
        	                "name": "yn-IS转出商机-线索来源"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1605377756",
        	                "name": "IS-商机转接实时监控报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1674548244",
        	                "name": "OS-销售过程核心指标分析—部门对比分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1762893565",
        	                "name": "IS关键环节统计报表（订单信息）成单量"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1812089412",
        	                "name": "yn-IS转出商机-明细2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1883404522",
        	                "name": "OS关键指标订单信息表-平面5"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1907969804",
        	                "name": "OS商机拜访"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1938502852",
        	                "name": "OS-每日可拜访商机实时监控表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1946672421",
        	                "name": "IS外呼通话时长报表-\"不同拨打意向\"跳转表格"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1974771840",
        	                "name": "OS到款至上线状态分布报表V0.2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1987222322",
        	                "name": "IA新增客户消费统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-2046430956",
        	                "name": "平面报表-转出商机明细-OS-1"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-258808128",
        	                "name": "OS关键指标订单信息表-平面1"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-290498121",
        	                "name": "OS-销售过程核心指标分析—时间趋势分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-364465078",
        	                "name": "yn-IS关键环节统计报表（商机）-明细3"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-365609981",
        	                "name": "平面报表-拜访明细-OS-2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-38587261",
        	                "name": "线索来源明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-393750567",
        	                "name": "平面报表-IS客保商机意向度报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-394178395",
        	                "name": "yn-IS转出商机-明细1"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-459990839",
        	                "name": "IS关键环节统计报表（订单信息）资质审核通过量"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-462282785",
        	                "name": "平面报表-转出商机明细-OS-3"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-500647998",
        	                "name": "商机来源"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-500669555",
        	                "name": "xxx-IS客保商机意向度报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-502842352",
        	                "name": "sj-v01-IS接通量交叉维度统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-525457363",
        	                "name": "IS关键环节周期报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-529818807",
        	                "name": "IS客保流转周期分析报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-640885253",
        	                "name": "yn-IS转出商机-商机来源"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-715076059",
        	                "name": "majun--IA账户消费趋势分析报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-831957738",
        	                "name": "yn-IS关键环节统计报表（商机信息）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-857001335",
        	                "name": "IS到款至上线状态分布报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-963276529",
        	                "name": "OS销售过程本期商机分析-时间趋势分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-966470575",
        	                "name": "拜访商机明细表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-987011184",
        	                "name": "sj-v02-IS接通量交叉维度统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-995245750",
        	                "name": "sjOS111到款至上线状态分布报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1017819220",
        	                "name": "majun-IA考核账户消费统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1054739572",
        	                "name": "IS关键环节统计报表（订单信息）上线量"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1168118882",
        	                "name": "IS关键环节统计报表(订单信息)"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1177804380",
        	                "name": "new_IS外呼电话量报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1202149515",
        	                "name": "IS客保量及状态统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1219041136",
        	                "name": "xxx-OS已到款未上线订单报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1236262570",
        	                "name": "OS关键指标报表-商机信息表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1239588873",
        	                "name": "yn-IS转出商机量"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1239751040",
        	                "name": "OS消费报表明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1334457004",
        	                "name": "IS到款至上线状态分布报表（朴）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1381635608",
        	                "name": "majun--合同明细报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1406784352",
        	                "name": "平面报表-转出商机明细表-0"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1486056926",
        	                "name": "IS关键环节统计报表（订单信息）开户审核通过量"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1530006562",
        	                "name": "test_zhangerhuan"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1567730422",
        	                "name": "线索来源"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1573927971",
        	                "name": "IS客保商机明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1625469829",
        	                "name": "IS关键环节周期"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1688251269",
        	                "name": "yn-IS关键环节统计报表（商机）-明细2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1692848389",
        	                "name": "IS客保量及状态统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1723315528",
        	                "name": "yn-IS消费报表-明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1761621826",
        	                "name": "OS到款至上线状态分布报表V0.2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1771855749",
        	                "name": "majun-OS订单报表（明细报表）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1785190566",
        	                "name": "IS关键环节统计报表(订单)-提单量明细表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1871577193",
        	                "name": "yn-IS关键环节统计报表-明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^192435873",
        	                "name": "xxx-IS到款至上线状态分布报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^2041618504",
        	                "name": "sjOS到款至上线状态分布报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^2065412729",
        	                "name": "OS关键指标-订单信息表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^2101690649",
        	                "name": "OS关键指标订单信息表-平面3"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^2107188459",
        	                "name": "majun-OS订单报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^2147419878",
        	                "name": "test11111"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^222181342",
        	                "name": "OS-销售过程本期商机分析-部门对比分析（多日商机池月表）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^292497240",
        	                "name": "IS外呼通话时长报表-\"不同拨打来源\"跳转表格"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^391064590",
        	                "name": "yn-IS消费报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^413245732",
        	                "name": "OS-销售过程本期商机分析-部门对比分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^454948787",
        	                "name": "OS到款至上线状态分布报表V0.2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^480555835",
        	                "name": "IS关键环节统计报表（订单信息）出单量明细表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^48299371",
        	                "name": "OS消费报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^559421886",
        	                "name": "sj-v01-IS接通量交叉维度统计报表-维度交叉表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^578418758",
        	                "name": "IS外呼通话时长报表-\"不同拨打来源\"跳转表格"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^588571625",
        	                "name": "yn-OS-销售过程遗留商机分析—时间趋势分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^599720134",
        	                "name": "OS商机资源报表-1"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^619399134",
        	                "name": "OS-销售过程遗留商机分析-部门对比分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^628835578",
        	                "name": "OS-销售过程核心指标分析—部门对比分析（多日商机池月表）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^643464153",
        	                "name": "IS外呼通话时长报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^711065501",
        	                "name": "OS关键指标订单信息表-平面4"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^791286422",
        	                "name": "majun-IA考核（明细表）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^813531804",
        	                "name": "平面报表-转出商机明细-OS-4"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^845782862",
        	                "name": "OS到款至上线状态分布报表V0.1"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^868759772",
        	                "name": "OS到款至上线状态分布报表V0.2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^91976083",
        	                "name": "商机来源跳转明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^95473000",
        	                "name": "IS外呼接通量报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^977255663",
        	                "name": "sj-v03-IS接通量交叉维度统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^984644544",
        	                "name": "IS外呼电话量报表"
        	            }
        	        ],
        	        "releaseReportInfo": [
                        {
                            "id": "try-1234",
                            "name": "release第一个测试用的模板"
                        },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1013864809",
        	                "name": "new_OS销售过程本期商机分析-时间趋势分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-102133365",
        	                "name": "majun-IS外呼接通率报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1060720482",
        	                "name": "IA新增账户明细报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1081473445",
        	                "name": "OS拜访商机报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1204473809",
        	                "name": "IS客保量及状态统计报表明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1206813876",
        	                "name": "piao_test"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1264098705",
        	                "name": "IS外呼接通量报表2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1279949894",
        	                "name": "订单流程耗时表（支持成单订单流转周期表）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1312990339",
        	                "name": "OS关键指标订单信息表-平面2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1372777620",
        	                "name": "IS关键环节统计报表（订单信息）合同返还量"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1507520469",
        	                "name": "OS商机拜访报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1511207018",
        	                "name": "OS商机资源报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1522201856",
        	                "name": "yn-IS转出商机-线索来源"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1605377756",
        	                "name": "IS-商机转接实时监控报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1674548244",
        	                "name": "OS-销售过程核心指标分析—部门对比分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1762893565",
        	                "name": "IS关键环节统计报表（订单信息）成单量"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1812089412",
        	                "name": "yn-IS转出商机-明细2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1883404522",
        	                "name": "OS关键指标订单信息表-平面5"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1907969804",
        	                "name": "OS商机拜访"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1938502852",
        	                "name": "OS-每日可拜访商机实时监控表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1946672421",
        	                "name": "IS外呼通话时长报表-\"不同拨打意向\"跳转表格"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1974771840",
        	                "name": "OS到款至上线状态分布报表V0.2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1987222322",
        	                "name": "IA新增客户消费统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-2046430956",
        	                "name": "平面报表-转出商机明细-OS-1"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-258808128",
        	                "name": "OS关键指标订单信息表-平面1"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-290498121",
        	                "name": "OS-销售过程核心指标分析—时间趋势分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-364465078",
        	                "name": "yn-IS关键环节统计报表（商机）-明细3"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-365609981",
        	                "name": "平面报表-拜访明细-OS-2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-38587261",
        	                "name": "线索来源明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-393750567",
        	                "name": "平面报表-IS客保商机意向度报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-394178395",
        	                "name": "yn-IS转出商机-明细1"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-459990839",
        	                "name": "IS关键环节统计报表（订单信息）资质审核通过量"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-462282785",
        	                "name": "平面报表-转出商机明细-OS-3"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-500647998",
        	                "name": "商机来源"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-500669555",
        	                "name": "xxx-IS客保商机意向度报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-502842352",
        	                "name": "sj-v01-IS接通量交叉维度统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-525457363",
        	                "name": "IS关键环节周期报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-529818807",
        	                "name": "IS客保流转周期分析报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-640885253",
        	                "name": "yn-IS转出商机-商机来源"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-715076059",
        	                "name": "majun--IA账户消费趋势分析报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-831957738",
        	                "name": "yn-IS关键环节统计报表（商机信息）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-857001335",
        	                "name": "IS到款至上线状态分布报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-963276529",
        	                "name": "OS销售过程本期商机分析-时间趋势分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-966470575",
        	                "name": "拜访商机明细表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-987011184",
        	                "name": "sj-v02-IS接通量交叉维度统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-995245750",
        	                "name": "sjOS111到款至上线状态分布报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1017819220",
        	                "name": "majun-IA考核账户消费统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1054739572",
        	                "name": "IS关键环节统计报表（订单信息）上线量"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1168118882",
        	                "name": "IS关键环节统计报表(订单信息)"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1177804380",
        	                "name": "new_IS外呼电话量报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1202149515",
        	                "name": "IS客保量及状态统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1219041136",
        	                "name": "xxx-OS已到款未上线订单报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1236262570",
        	                "name": "OS关键指标报表-商机信息表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1239588873",
        	                "name": "yn-IS转出商机量"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1239751040",
        	                "name": "OS消费报表明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1334457004",
        	                "name": "IS到款至上线状态分布报表（朴）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1381635608",
        	                "name": "majun--合同明细报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1406784352",
        	                "name": "平面报表-转出商机明细表-0"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1486056926",
        	                "name": "IS关键环节统计报表（订单信息）开户审核通过量"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1530006562",
        	                "name": "test_zhangerhuan"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1567730422",
        	                "name": "线索来源"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1573927971",
        	                "name": "IS客保商机明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1625469829",
        	                "name": "IS关键环节周期"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1688251269",
        	                "name": "yn-IS关键环节统计报表（商机）-明细2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1692848389",
        	                "name": "IS客保量及状态统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1723315528",
        	                "name": "yn-IS消费报表-明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1761621826",
        	                "name": "OS到款至上线状态分布报表V0.2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1771855749",
        	                "name": "majun-OS订单报表（明细报表）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1785190566",
        	                "name": "IS关键环节统计报表(订单)-提单量明细表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1871577193",
        	                "name": "yn-IS关键环节统计报表-明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^192435873",
        	                "name": "xxx-IS到款至上线状态分布报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^2041618504",
        	                "name": "sjOS到款至上线状态分布报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^2065412729",
        	                "name": "OS关键指标-订单信息表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^2101690649",
        	                "name": "OS关键指标订单信息表-平面3"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^2107188459",
        	                "name": "majun-OS订单报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^2147419878",
        	                "name": "test11111"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^222181342",
        	                "name": "OS-销售过程本期商机分析-部门对比分析（多日商机池月表）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^292497240",
        	                "name": "IS外呼通话时长报表-\"不同拨打来源\"跳转表格"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^391064590",
        	                "name": "yn-IS消费报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^413245732",
        	                "name": "OS-销售过程本期商机分析-部门对比分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^454948787",
        	                "name": "OS到款至上线状态分布报表V0.2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^480555835",
        	                "name": "IS关键环节统计报表（订单信息）出单量明细表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^48299371",
        	                "name": "OS消费报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^559421886",
        	                "name": "sj-v01-IS接通量交叉维度统计报表-维度交叉表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^578418758",
        	                "name": "IS外呼通话时长报表-\"不同拨打来源\"跳转表格"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^588571625",
        	                "name": "yn-OS-销售过程遗留商机分析—时间趋势分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^599720134",
        	                "name": "OS商机资源报表-1"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^619399134",
        	                "name": "OS-销售过程遗留商机分析-部门对比分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^628835578",
        	                "name": "OS-销售过程核心指标分析—部门对比分析（多日商机池月表）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^643464153",
        	                "name": "IS外呼通话时长报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^711065501",
        	                "name": "OS关键指标订单信息表-平面4"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^791286422",
        	                "name": "majun-IA考核（明细表）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^813531804",
        	                "name": "平面报表-转出商机明细-OS-4"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^845782862",
        	                "name": "OS到款至上线状态分布报表V0.1"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^868759772",
        	                "name": "OS到款至上线状态分布报表V0.2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^91976083",
        	                "name": "商机来源跳转明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^95473000",
        	                "name": "IS外呼接通量报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^977255663",
        	                "name": "sj-v03-IS接通量交叉维度统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^984644544",
        	                "name": "IS外呼电话量报表"
        	            }
        	        ],
        	        "preReportInfo": [
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1013864809",
        	                "name": "预发布_new_OS销售过程本期商机分析-时间趋势分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-102133365",
        	                "name": "majun-IS外呼接通率报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1060720482",
        	                "name": "IA新增账户明细报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1081473445",
        	                "name": "OS拜访商机报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1204473809",
        	                "name": "IS客保量及状态统计报表明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1206813876",
        	                "name": "piao_test"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1264098705",
        	                "name": "IS外呼接通量报表2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1279949894",
        	                "name": "订单流程耗时表（支持成单订单流转周期表）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1312990339",
        	                "name": "OS关键指标订单信息表-平面2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1372777620",
        	                "name": "IS关键环节统计报表（订单信息）合同返还量"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1507520469",
        	                "name": "OS商机拜访报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1511207018",
        	                "name": "OS商机资源报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1522201856",
        	                "name": "yn-IS转出商机-线索来源"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1605377756",
        	                "name": "IS-商机转接实时监控报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1674548244",
        	                "name": "OS-销售过程核心指标分析—部门对比分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1762893565",
        	                "name": "IS关键环节统计报表（订单信息）成单量"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1812089412",
        	                "name": "yn-IS转出商机-明细2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1883404522",
        	                "name": "OS关键指标订单信息表-平面5"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1907969804",
        	                "name": "OS商机拜访"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1938502852",
        	                "name": "OS-每日可拜访商机实时监控表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1946672421",
        	                "name": "IS外呼通话时长报表-\"不同拨打意向\"跳转表格"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1974771840",
        	                "name": "OS到款至上线状态分布报表V0.2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-1987222322",
        	                "name": "IA新增客户消费统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-2046430956",
        	                "name": "平面报表-转出商机明细-OS-1"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-258808128",
        	                "name": "OS关键指标订单信息表-平面1"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-290498121",
        	                "name": "OS-销售过程核心指标分析—时间趋势分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-364465078",
        	                "name": "yn-IS关键环节统计报表（商机）-明细3"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-365609981",
        	                "name": "平面报表-拜访明细-OS-2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-38587261",
        	                "name": "线索来源明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-393750567",
        	                "name": "平面报表-IS客保商机意向度报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-394178395",
        	                "name": "yn-IS转出商机-明细1"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-459990839",
        	                "name": "IS关键环节统计报表（订单信息）资质审核通过量"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-462282785",
        	                "name": "平面报表-转出商机明细-OS-3"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-500647998",
        	                "name": "商机来源"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-500669555",
        	                "name": "xxx-IS客保商机意向度报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-502842352",
        	                "name": "sj-v01-IS接通量交叉维度统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-525457363",
        	                "name": "IS关键环节周期报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-529818807",
        	                "name": "IS客保流转周期分析报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-640885253",
        	                "name": "yn-IS转出商机-商机来源"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-715076059",
        	                "name": "majun--IA账户消费趋势分析报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-831957738",
        	                "name": "yn-IS关键环节统计报表（商机信息）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-857001335",
        	                "name": "IS到款至上线状态分布报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-963276529",
        	                "name": "OS销售过程本期商机分析-时间趋势分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-966470575",
        	                "name": "拜访商机明细表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-987011184",
        	                "name": "sj-v02-IS接通量交叉维度统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^-995245750",
        	                "name": "sjOS111到款至上线状态分布报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1017819220",
        	                "name": "majun-IA考核账户消费统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1054739572",
        	                "name": "IS关键环节统计报表（订单信息）上线量"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1168118882",
        	                "name": "IS关键环节统计报表(订单信息)"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1177804380",
        	                "name": "new_IS外呼电话量报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1202149515",
        	                "name": "IS客保量及状态统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1219041136",
        	                "name": "xxx-OS已到款未上线订单报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1236262570",
        	                "name": "OS关键指标报表-商机信息表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1239588873",
        	                "name": "yn-IS转出商机量"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1239751040",
        	                "name": "OS消费报表明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1334457004",
        	                "name": "IS到款至上线状态分布报表（朴）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1381635608",
        	                "name": "majun--合同明细报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1406784352",
        	                "name": "平面报表-转出商机明细表-0"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1486056926",
        	                "name": "IS关键环节统计报表（订单信息）开户审核通过量"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1530006562",
        	                "name": "test_zhangerhuan"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1567730422",
        	                "name": "线索来源"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1573927971",
        	                "name": "IS客保商机明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1625469829",
        	                "name": "IS关键环节周期"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1688251269",
        	                "name": "yn-IS关键环节统计报表（商机）-明细2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1692848389",
        	                "name": "IS客保量及状态统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1723315528",
        	                "name": "yn-IS消费报表-明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1761621826",
        	                "name": "OS到款至上线状态分布报表V0.2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1771855749",
        	                "name": "majun-OS订单报表（明细报表）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1785190566",
        	                "name": "IS关键环节统计报表(订单)-提单量明细表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^1871577193",
        	                "name": "yn-IS关键环节统计报表-明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^192435873",
        	                "name": "xxx-IS到款至上线状态分布报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^2041618504",
        	                "name": "sjOS到款至上线状态分布报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^2065412729",
        	                "name": "OS关键指标-订单信息表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^2101690649",
        	                "name": "OS关键指标订单信息表-平面3"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^2107188459",
        	                "name": "majun-OS订单报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^2147419878",
        	                "name": "test11111"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^222181342",
        	                "name": "OS-销售过程本期商机分析-部门对比分析（多日商机池月表）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^292497240",
        	                "name": "IS外呼通话时长报表-\"不同拨打来源\"跳转表格"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^391064590",
        	                "name": "yn-IS消费报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^413245732",
        	                "name": "OS-销售过程本期商机分析-部门对比分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^454948787",
        	                "name": "OS到款至上线状态分布报表V0.2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^480555835",
        	                "name": "IS关键环节统计报表（订单信息）出单量明细表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^48299371",
        	                "name": "OS消费报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^559421886",
        	                "name": "sj-v01-IS接通量交叉维度统计报表-维度交叉表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^578418758",
        	                "name": "IS外呼通话时长报表-\"不同拨打来源\"跳转表格"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^588571625",
        	                "name": "yn-OS-销售过程遗留商机分析—时间趋势分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^599720134",
        	                "name": "OS商机资源报表-1"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^619399134",
        	                "name": "OS-销售过程遗留商机分析-部门对比分析"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^628835578",
        	                "name": "OS-销售过程核心指标分析—部门对比分析（多日商机池月表）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^643464153",
        	                "name": "IS外呼通话时长报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^711065501",
        	                "name": "OS关键指标订单信息表-平面4"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^791286422",
        	                "name": "majun-IA考核（明细表）"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^813531804",
        	                "name": "平面报表-转出商机明细-OS-4"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^845782862",
        	                "name": "OS到款至上线状态分布报表V0.1"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^868759772",
        	                "name": "OS到款至上线状态分布报表V0.2"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^91976083",
        	                "name": "商机来源跳转明细"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^95473000",
        	                "name": "IS外呼接通量报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^977255663",
        	                "name": "sj-v03-IS接通量交叉维度统计报表"
        	            },
        	            {
        	                "id": "PERSISTENT^_^virtualdatasource^_^^_^984644544",
        	                "name": "IS外呼电话量报表"
        	            }
        	        ]
        	    },
        	    "status": 0,
        	    "statusInfo": ""
        	
        };
    };

    NS.CONSOLE_VTPL_LIST = function (url, options) {
        return {
            status: 0,
            message: 'OKOKmsg',
            data: {
                vtplInfo: [
                    { id: 'try' }
                    // { name: '报表1', id: 'try' },
                    // { name: '报表2', id: 'try-1234' },
                    // { name: '简单条件图表', id: 'simple-cond-chart-table' }
                ]
            }
        };
    };

    NS.CONSOLE_MOLD_LIST = function (url, options) {
        return {
            status: 0,
            message: 'OKOKmsg',
            data: {
                moldFiles: [
                    'MOLD_aaa.vm',
                    'MOLD_aaa.json',
                    'simple-cond-chart-table.vm',
                    'simple-cond-chart-table.json',
                    'cond-plane-chart-link-1.vm',
                    'cond-plane-chart-link-1.json',
                    'cond-pivot-meta-1.vm',
                    'cond-pivot-meta-1.json'
                ]
            }
        };
    };

    NS.CONSOLE_SAVE_TPL = function (url, options) {
        return {
            status: 0,
            message: 'OKOKmsg',
            data: {
                virtualTemplateId: 'savedtage'
            }
        };
    };

    NS.CONSOLE_GET_COND = function (url, options) {
        return {
            status: 0,
            message: 'OKOKmsg',
            data: {
                templateDims: {
                    "AAAAA_REPORT_TEPALTE_ID": [
                        {
                            name: 'Pos1',
                            caption: 'CAPTION',
                            cubeName: 'CUBENAME',
                            schemaName: 'SCHEMANAME',
                            levels: ['LEVEL1', 'LEVE2']
                        }, 
                        {
                            name: 'Pos2',
                            caption: 'CAPTION2',
                            cubeName: 'CUBENAME2',
                            schemaName: 'SCHEMANAME2',
                            levels: ['LEVEL1', 'LEVEL2']
                        }
                    ],
                    "BBBB_REPORT_TEMPALTE_ID": [
                        {
                            name: 'Pos1',
                            caption: 'CAPTION1',
                            cubeName: 'CUBENAME1',
                            schemaName: 'SCHEMANAME',
                            levels: ['LEVEL1', 'LEVE2']
                        }, 
                        {
                            name: 'Pos2',
                            caption: 'CAPTION2',
                            cubeName: 'CUBENAME2',
                            schemaName: 'SCHEMANAME2',
                            levels: ['LEVEL1', 'LEVEL2']
                        },
                        {
                            name: 'Pos3',
                            caption: 'CAPTION2',
                            cubeName: 'CUBENAME2',
                            schemaName: 'SCHEMANAME2',
                            levels: ['LEVEL1', 'LEVEL2']
                        }
                    ],
                    "RTPL_VIRTUAL_ID": [
                        {
                            name: 'Pos1',
                            caption: 'CAPTION',
                            cubeName: 'CUBENAME',
                            schemaName: 'SCHEMANAME',
                            levels: ['LEVEL1', 'LEVE2']
                        },
                        {
                            name: 'Pos2',
                            caption: 'CAPTION2',
                            cubeName: 'CUBENAME2',
                            schemaName: 'SCHEMANAME2',
                            levels: ['LEVEL1', 'LEVEL2']
                        },
                        {
                            name: 'Pos3',
                            caption: 'CAPTION2',
                            cubeName: 'CUBENAME2',
                            schemaName: 'SCHEMANAME2',
                            levels: ['LEVEL1', 'LEVEL2']
                        },
                        {
                            name: 'Pos4',
                            caption: 'CAPTION2',
                            cubeName: 'CUBENAME2',
                            schemaName: 'SCHEMANAME2',
                            levels: ['LEVEL1', 'LEVEL2']
                        }
                    ]
                }
            }
        };
    };

    NS.CONSOLE_EXIST_COND = function (url, options) {
        var param = xmock.parseParam(options.data);
        var reportTemplateIdList = param.reportTemplateIdList;
        if (!isArray(reportTemplateIdList)) {
            reportTemplateIdList = [reportTemplateIdList];
        }
        if (param.virtualTemplateId) {
            reportTemplateIdList.push(param.virtualTemplateId);
        }

        var templateDims = {};
        var rand = 1;

        for (var i = 0, rid; i < reportTemplateIdList.length; i ++) {
            if (rid = reportTemplateIdList[i]) {
                if (!templateDims[rid]) {
                    templateDims[rid] = [];
                }
                templateDims[rid].push(
                    {
                        name: 'Pos' + rand,
                        caption: 'CAPTION' + rand,
                        cubeName: 'CUBENAME' + rand,
                        schemaName: 'SCHEMANAME' + rand,
                        level: 'LEVEL' + rand
                    }
                );
                rand ++;
            }
        }

        return {
            status: 0,
            message: 'OKOKmsg',
            data: {
                templateDims: templateDims
            }
        };
    };

    NS.CONSOLE_DS_LIST = function (url, options) {
        return {
            status: 0,
            message: 'OKOKmsg',
            data: {
                reportTemplateList: [
                    { reportTemplateId: '111RTPL_VIRTUAL_ID', reportTemplateType: 'RTPL_VIRTUAL',reportTemplateName: 'hahhaahha' },
                    { reportTemplateId: 'BBBB_REPORT_TEMPALTE_ID', reportTemplateType: 'RTPL_OLAP_TABLE' ,reportTemplateName: 'hahhaahha1'},
                    { reportTemplateId: 'AAAAA_REPORT_TEPALTE_ID', reportTemplateType: 'RTPL_PLANE_TABLE' ,reportTemplateName: 'hahhaahha2'},
                    { reportTemplateId: 'AAAAA_REPORT_TEPALTE_ID1', reportTemplateType: 'RTPL_OLAP_CHART' ,reportTemplateName: 'hahhaahha3'},
                    { reportTemplateId: 'RTPL_VIRTUAL_ID5', reportTemplateType: 'RTPL_OLAP_TABLE' ,reportTemplateName: 'hahhaahha4'},
                    { reportTemplateId: 'AAAAA_REPORT_TEPALTE_ID-plane', reportTemplateType: 'RTPL_PLANE_TABLE',reportTemplateName: 'hahhaahha5' },
                    { reportTemplateId: 'RTPL_VIRTUAL_ID6', reportTemplateType: 'RTPL_PLANE_TABLE' ,reportTemplateName: 'hahhaahha6'},
                    { reportTemplateId: 'BBBB_REPORT_TEMPALTE_ID88', reportTemplateType: 'RTPL_PLANE_TABLE' ,reportTemplateName: 'hahhaahha7'}
                ]
            }
        };
    };

    NS.CONSOLE_TO_PRE = function (url, options) {
        return {
            status: 0,
            message: 'OKOKmsg',
            data: {}
        };
    };

    NS.CONSOLE_TO_RELEASE = function (url, options) {
        return {
            status: 0,
            message: 'OKOKmsg',
            data: {}
        };
    };

})();