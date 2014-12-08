// 外壳的mockup
(function() {
	
    var NS = xmock.data.console.DimTree = {};
    
    var dimTree = {
            uniqName : '[myUniqName]',
            name : 'myDimName',
            caption : '全国',
            selected : true,
            children : [
                {
                    uniqName : 'all$[Time.Weekly].[All Time.Weeklys]',
                    caption : '全部',
                    selected : true
                },
                {
                    uniqName : 2,
                    caption : '安徽安安安安安安安安安安安安安安安安安安安安安',
                    children : [
                        {
                            uniqName : 3,
                            caption : '合肥',
                            selected : true,
                            children : [
                                {
                                    uniqName : 11,
                                    caption : '虹桥'
                                }
                            ]
                        },
                        {
                            uniqName : 4,
                            caption : '六安',
                            selected : true,
                            children : [
                                {
                                    uniqName : 10,
                                    caption : '双河'
                                }
                            ]
                        }
                    ]
                },
                {
                    uniqName : 5,
                    caption : '浙江',
                    children : [
                        {
                            uniqName : 6,
                            caption : '杭州'
                        },
                        {
                            uniqName : 7,
                            caption : '富阳'
                        },
                        {
                            uniqName : 8,
                            caption : '嘉兴'
                        }
                    ]
                },
                {
                    uniqName : 5,
                    caption : '浙江',
                    children : [
                        {
                            uniqName : 6,
                            caption : '杭州'
                        },
                        {
                            uniqName : 7,
                            caption : '富阳',
                            selected : true,
                            children : [
                                {
                                    uniqName : 6,
                                    caption : '杭州'
                                },
                                {
                                    uniqName : 7,
                                    caption : '富阳'
                                },
                                {
                                    uniqName : 8,
                                    caption : '嘉兴'
                                }
                            ]
                        },
                        {
                            uniqName : 8,
                            caption : '嘉兴'
                        }
                    ]
                },
                {
                    uniqName : 5,
                    caption : '浙江',
                    children : [
                        {
                            uniqName : 'all$[Time.Weekly].[All Time.Weeklys]',
                            caption : '全部',
                            selected : true
                        },
                        {
                            uniqName : 6,
                            caption : '杭州',
                            selected : true,
                            children : [
                        {
                            uniqName : 6,
                            caption : '杭州'
                        },
                        {
                            uniqName : 7,
                            caption : '富阳'
                        },
                        {
                            uniqName : 8,
                            caption : '嘉兴'
                        }
                    ]
                        },
                        {
                            uniqName : 7,
                            caption : '富阳'
                        },
                        {
                            uniqName : 8,
                            caption : '嘉兴'
                        },
                        {
                            uniqName : 100,
                            caption : 'wenzhou'
                        }
                    ]
                }
            ]
        };

    var level = [
            {
                'uniqName' : 'level2',
                'caption' : '层级2222222222222222222222222'
            },
            {
                'uniqName' : 'level3',
                'caption' : '层级3',
                'selected' : true
            },
            {
                'uniqName' : 'level4',
                'caption' : '层级4',
                'selected' : true
            }
        ];

	NS.DIM_TREE = function(url, options) {
        var normalDimData = {
                isTimeDim: false,
                schemaName: "datainsight",
                dimName: "Time",
                dimTree: {
                    children: [dimTree]
                },
                hierarchyLevelUniqueNames: {
                    myDimName: level
                }
        };

        return {
            status: 0,
            message: 'OKOKmsg',
            data: { 
                timeType: 0,
                timeSelects: {
                    start: '2013-01-05',
                    end: '2013-01-23'
                },
                dimTree: normalDimData
            }
        };

        // return {"data":{"dimTree":{"timeType":0,"dimTree":{"drillDown":false,"children":[{"drillDown":false,"hasAll":true,"name":"Time","uniqName":"[Time].[All Times]","caption":"时间","selected":false}],"name":"Time","uniqName":"[Time]","caption":"时间","selected":false},"hierarchyLevelUniqueNames":{"Time":[{"name":"(All)","uniqName":"[Time].[(All)]","caption":"(All)","selected":false}]},"cubeName":"OS_CUSTOMER","dimName":"Time"}},"status":0,"statusInfo":""};
    };

    NS.DIM_SELECT_SAVE = {
		status: 0,
		message: 'OKOKmsg',
		data: 0
    }
	
})();