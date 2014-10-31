// 外壳的mockup
(function() {
	
    var NS = xmock.data.console.ConsoleFrame = {};
    
    function random () {
        return Math.round(Math.random() * 10000);
    }

    var menu = {
        selMenuId: 1010,
        menuList: [
            {
                menu: {
                    menuId: 1002,
                    menuName: "报表报表"
                },
                children: [
                    {
                        menu: {
                            menuPage: "di.console.editor.ui.OLAPEditor",
                            menuId: 1010,
                            menuName: "创建报表"
                        }
                    }, 
                    {
                        menu: {
                            menuPage: "di.console.mgr.ui.ReportListPage",
                            menuId: 1020,
                            menuName: "我的报表"
                        }
                    }
                ]
            }
        ]
    };

    var cubeMetaList = [
        {
            schemaName: 'schema_' + random(),
            root: {
                nodeName: 'node_' + random(),
                caption: '方块节点_' + random(),
                children: [
                    {
                        nodeName: 'node_' + random(),
                        caption: '方块节点_' + random()
                    },
                    {
                        nodeName: 'node_' + random(),
                        caption: '方块节点_' + random()
                    }
                ]
            }
        },
        {
            schemaName: 'schema_' + random(),
            root: {
                nodeName: 'node_' + random(),
                caption: '方块节点_' + random(),
                children: [
                    {
                        nodeName: 'node_' + random(),
                        caption: '方块节点_' + random()
                    },
                    {
                        nodeName: 'node_' + random(),
                        caption: '方块节点_' + random()
                    }
                ]
            }
        },
    ];
        
    NS.CUBE_META = {
        status : 0,
        message : 'OKOKmsg',
        data : {
            cubeTree: cubeMetaList
        }
    };

    var datasourceList = [
        { text: 'dsdsdsdsdsddsd' + Math.random(), value: Math.random() },
        { text: 'dsdsdsdsdsddsd' + Math.random(), value: Math.random() },
        { text: 'dsdsdsdsdsddsd' + Math.random(), value: Math.random() },
        { text: 'dsdsdsdsdsddsd' + Math.random(), value: Math.random() },
        { text: 'dsdsdsdsdsddsd' + Math.random(), value: Math.random() },
        { text: 'dsdsdsdsdsddsd' + Math.random(), value: Math.random() },
        { text: 'dsdsdsdsdsddsd' + Math.random(), value: Math.random() }
    ];

	NS.DATASOURCE_META = {
		status : 0,
		message : 'OKOKmsg',
		data : {
			datasourceList: datasourceList
		}
	};
	
})();