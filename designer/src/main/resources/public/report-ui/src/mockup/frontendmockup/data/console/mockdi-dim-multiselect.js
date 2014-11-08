// 外壳的mockup
(function() {
	
    var NS = xmock.data.console.MultiSelect = {};
    

	NS.MULTI_SELECT = function(url, options) {

        return {
            "data": {
                "dimValue": [
                    {
                        "name": "[Store].[Store Country]",
                        "caption": "Store Country",
                        "selected": true,
                        "children": [
                            {
                                "name": "all$[Store].[Store Country]",
                                "caption": "全部",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Canada]",
                                "caption": "Canada",
                                "selected": true
                            },
                            {
                                "name": "[Store].[Mexico]",
                                "caption": "Mexico",
                                "selected": true
                            },
                            {
                                "name": "[Store].[USA]",
                                "caption": "USA",
                                "selected": false
                            }
                        ]
                    },
                    {
                        "name": "[Store].[Store State]",
                        "caption": "Store State",
                        "selected": false,
                        "children": [
                            {
                                "name": "all$[Store].[Store State]",
                                "caption": "全部",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Canada].[BC]",
                                "caption": "BC",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[DF]",
                                "caption": "DF",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[Guerrero]",
                                "caption": "Guerrero",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[Jalisco]",
                                "caption": "Jalisco",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[Veracruz]",
                                "caption": "Veracruz",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[Yucatan]",
                                "caption": "Yucatan",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[Zacatecas]",
                                "caption": "Zacatecas",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[CA]",
                                "caption": "CA",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[OR]",
                                "caption": "OR",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[WA]",
                                "caption": "WA",
                                "selected": false
                            }
                        ]
                    },
                    {
                        "name": "[Store].[Store City]",
                        "caption": "Store City",
                        "selected": false,
                        "children": [
                            {
                                "name": "all$[Store].[Store City]",
                                "caption": "全部",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Canada].[BC].[Vancouver]",
                                "caption": "Vancouver",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Canada].[BC].[Victoria]",
                                "caption": "Victoria",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[DF].[Mexico City]",
                                "caption": "Mexico City",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[DF].[San Andres]",
                                "caption": "San Andres",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[Guerrero].[Acapulco]",
                                "caption": "Acapulco",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[Jalisco].[Guadalajara]",
                                "caption": "Guadalajara",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[Veracruz].[Orizaba]",
                                "caption": "Orizaba",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[Yucatan].[Merida]",
                                "caption": "Merida",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[Zacatecas].[Camacho]",
                                "caption": "Camacho",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[Zacatecas].[Hidalgo]",
                                "caption": "Hidalgo",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[CA].[Alameda]",
                                "caption": "Alameda",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[CA].[Beverly Hills]",
                                "caption": "Beverly Hills",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[CA].[Los Angeles]",
                                "caption": "Los Angeles",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[CA].[San Diego]",
                                "caption": "San Diego",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[CA].[San Francisco]",
                                "caption": "San Francisco",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[OR].[Portland]",
                                "caption": "Portland",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[OR].[Salem]",
                                "caption": "Salem",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[WA].[Bellingham]",
                                "caption": "Bellingham",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[WA].[Bremerton]",
                                "caption": "Bremerton",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[WA].[Seattle]",
                                "caption": "Seattle",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[WA].[Spokane]",
                                "caption": "Spokane",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[WA].[Tacoma]",
                                "caption": "Tacoma",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[WA].[Walla Walla]",
                                "caption": "Walla Walla",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[WA].[Yakima]",
                                "caption": "Yakima",
                                "selected": false
                            }
                        ]
                    },
                    {
                        "name": "[Store].[Store Name]",
                        "caption": "Store Name",
                        "selected": false,
                        "children": [
                            {
                                "name": "all$[Store].[Store Name]",
                                "caption": "全部",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Canada].[BC].[Vancouver].[Store 19]",
                                "caption": "Store 19",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Canada].[BC].[Victoria].[Store 20]",
                                "caption": "Store 20",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[DF].[Mexico City].[Store 9]",
                                "caption": "Store 9",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[DF].[San Andres].[Store 21]",
                                "caption": "Store 21",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[Guerrero].[Acapulco].[Store 1]",
                                "caption": "Store 1",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[Jalisco].[Guadalajara].[Store 5]",
                                "caption": "Store 5",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[Veracruz].[Orizaba].[Store 10]",
                                "caption": "Store 10",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[Yucatan].[Merida].[Store 8]",
                                "caption": "Store 8",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[Zacatecas].[Camacho].[Store 4]",
                                "caption": "Store 4",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[Zacatecas].[Hidalgo].[Store 12]",
                                "caption": "Store 12",
                                "selected": false
                            },
                            {
                                "name": "[Store].[Mexico].[Zacatecas].[Hidalgo].[Store 18]",
                                "caption": "Store 18",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[CA].[Alameda].[HQ]",
                                "caption": "HQ",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[CA].[Beverly Hills].[Store 6]",
                                "caption": "Store 6",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[CA].[Los Angeles].[Store 7]",
                                "caption": "Store 7",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[CA].[San Diego].[Store 24]",
                                "caption": "Store 24",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[CA].[San Francisco].[Store 14]",
                                "caption": "Store 14",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[OR].[Portland].[Store 11]",
                                "caption": "Store 11",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[OR].[Salem].[Store 13]",
                                "caption": "Store 13",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[WA].[Bellingham].[Store 2]",
                                "caption": "Store 2",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[WA].[Bremerton].[Store 3]",
                                "caption": "Store 3",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[WA].[Seattle].[Store 15]",
                                "caption": "Store 15",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[WA].[Spokane].[Store 16]",
                                "caption": "Store 16",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[WA].[Tacoma].[Store 17]",
                                "caption": "Store 17",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[WA].[Walla Walla].[Store 22]",
                                "caption": "Store 22",
                                "selected": false
                            },
                            {
                                "name": "[Store].[USA].[WA].[Yakima].[Store 23]",
                                "caption": "Store 23",
                                "selected": false
                            }
                        ]
                    }
                ]
            },
            "statusInfo": ""
}

        // return {"data":{"dimTree":{"timeType":0,"dimTree":{"drillDown":false,"children":[{"drillDown":false,"hasAll":true,"name":"Time","uniqName":"[Time].[All Times]","caption":"时间","selected":false}],"name":"Time","uniqName":"[Time]","caption":"时间","selected":false},"hierarchyLevelUniqueNames":{"Time":[{"name":"(All)","uniqName":"[Time].[(All)]","caption":"(All)","selected":false}]},"cubeName":"OS_CUSTOMER","dimName":"Time"}},"status":0,"statusInfo":""};
    };

    NS.DIM_SELECT_SAVE = {
		status: 0,
		message: 'OKOKmsg',
		data: 0
    }
	
})();