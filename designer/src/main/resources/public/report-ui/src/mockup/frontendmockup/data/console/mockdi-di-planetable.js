// 外壳的mockup
(function() {
	
    
    var dateToString = xutil.date.dateToString;

    function random () {
        return Math.round(Math.random() * 10000000);
    }

    var normalInit = function(url, options) {
        return {
            status: 0,
            message: 'OKOKmsg',
            data: { 
                sqlString: 'select * from asdf where aa = 5 and bb = 6', 
                datasourceName: "asdfasdfasdfasdfdfasdf",
                reportTemplateName: '平面报表adb'
            }
        };
    };
   
	var normalSqlSave = function(url, options) {
        return {
            status: 0,
            message: 'OKOKmsg',
            data: { reportTemplateId: 'asdjkllasjdkl' }
        };
    };
   
   	var normalColData = function(url, options) {
        return {
            status: 0,
            message: 'OKOKmsg',
            data: { 
                reportTemplateId: 'asdjkllasjdkl',
				columnJson: [  
				    {sqlKey: "a.col1", showName: "哈哈", paramKey: "AAAformURL",format: "I,III", orderby: "NONE", isDefaultShow: false},
					{sqlKey: "a.col1", showName: "哈哈", paramKey: "bbbformURL",format: "I,III", orderby: '', isDefaultShow: true},
					{sqlKey: "a.col1", showName: "哈哈", paramKey: "cccformURL",format: "I,III", orderby: '', isDefaultShow: false}
				]
			}
        };
    };
  
    var normalColSave = function(url, options) {
        return {
            status: 0,
            message: 'OKOKmsg',
            data: { 
                reportTemplateId: 'zxcvzxcvzxcv',
				columnJson: [
				    {sqlKey: "a.col1", showName: "哈哈", paramKey: "AAAformURL",format: "I,III", orderby: "NONE", isDefaultShow: false},
					{sqlKey: "a.col1", showName: "哈哈", paramKey: "aaaformURL",format: "I,III", orderby: '', isDefaultShow: true},
					{sqlKey: "a.col1", showName: "哈哈", paramKey: "ccformURL",format: "I,III", orderby: '', isDefaultShow: true}
				]
			}
        };
    };
   
    var normalCondData = function(url, options) {
        return {
            status: 0,
            message: 'OKOKmsg',
            data: { 
                reportTemplateId: '2143253245',
				columnJson: [
				    {sqlKey: "a.col1", paramKey: "zzzformURL" },
					{sqlKey: "a.col1", paramKey: "AAAformURL" },
					{sqlKey: "a.col1", paramKey: "cccformURL" }
				]
			}
        };
    };
   
    var normalCondSave = function(url, options) {
        return {
            status: 0,
            message: 'OKOKmsg',
            data: { 
                reportTemplateId: '09o5k65o',
                columnJson: [
                    {sqlKey: "a.col1", paramKey: "hhhformURL" },
                    {sqlKey: "a.col1", paramKey: "rrrrformURL" },
                    {sqlKey: "a.col1", paramKey: "hxsormURL" }
                ]
            }
        };
    };

    var normalPreviewData = function(url, options) {
        return {
            status: 0,
            message: 'OKOKmsg',
            data: { 
                reportTemplateId: '2143253245',
                columnJson: [
                    {sqlKey: "a.col1", paramKey: "collllllormURL", showName: 'zzz'},
                    {sqlKey: "a.col1", paramKey: "collllllormURL" },
                    {sqlKey: "a.col1", paramKey: "collllllormURL" }
                ],
                condJson: [
                    {sqlKey: "a.col1", paramKey: "condhformURL" },
                    {sqlKey: "a.col1", paramKey: "condrrformURL" },
                    {sqlKey: "a.col1", paramKey: "condsormURL" }
                ]
            }
        };
    };
   
    //------------------------------------------------------------------
    // 生产端
    //------------------------------------------------------------------
  
    var normalPlaneTableData = function(url, options) {
        var head = [ 
            { field: 'col1', title: '营业额', format: 'I,III.DD%', orderby: 'DESC' },
            { field: 'col2', title: '营业比率' },
            { field: 'col3', title: '营业内容', orderby: 'NONE' },
            { field: 'col4', title: '收入额', orderby: 'NONE' },
            { field: 'col5', title: '收入比率', orderby: 'NONE' },
            { field: 'col6', title: '收入内容', orderby: 'NONE' },
            { field: 'col7', title: 'MPT', orderby: 'NONE' }
        ];

        var data = [
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: 12341234, col2: 'zcvvzxc', col3: Math.random() * 1000, col4: '534656345', selected: true },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: 12341234, col2: 'zcvvzxc', col3: Math.random() * 1000, col4: '入个人是' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: 12341234, col2: 'zcvvzxc', col3: Math.random() * 1000, col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: 1234, col2: 'zcvvzxc', col3: Math.random() * 1000, col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: 566, col2: 'zcvvzxc', col3: Math.random() * 1000, col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: 7685678, col2: 'zcvvzxc', col3: Math.random() * 1000, col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: 324, col2: 'zcvvzxc', col3: Math.random() * 1000, col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: Math.random() * 1000, col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: Math.random() * 1000, col2: Math.random() * 1000, col3: null, col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: Math.random() * 1000, col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: '我人格w儿' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: Math.random() * 1000, col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: 'fer' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: Math.random() * 1000, col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: Math.random() * 1000, col2: Math.random() * 1000 + Math.random(), col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: Math.random() * 1000, col2: Math.random() * 1000 + Math.random(), col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: Math.random() * 1000, col2: Math.random() * 1000 + Math.random(), col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: (new Date()).getTime(), col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: (new Date()).getTime(), col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: (new Date()).getTime(), col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: (new Date()).getTime(), col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: (new Date()).getTime(), col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: (new Date()).getTime(), col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: (new Date()).getTime(), col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: (new Date()).getTime(), col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: (new Date()).getTime(), col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: (new Date()).getTime(), col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: (new Date()).getTime(), col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: (new Date()).getTime(), col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: (new Date()).getTime(), col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: (new Date()).getTime(), col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: '534656345' },
            { uniqueName: Math.random(), col5: Math.random() * 100, col6: Math.random() * 10, col7: Math.random() * 1000, col1: (new Date()).getTime(), col2: Math.random() * 1000, col3: 'xvzxcvxzc', col4: '534656345' }
        ];

        var pageInfo = {
            totalRecordCount: (new Date()).getTime() % 100000,
            pageSize: 20,
            currentPage: 1
        };

        return {
            status: 0,
            message: 'OKOKmsg',
            data: {
                head: head,
                data: data,
                pageInfo: pageInfo,
                reportTemplateId: Math.random(),
                actualSql: 'select actual from aaa where bbb = 3'
            }
        };
    };

    /**
     * row check
     */
    var normalPlaneTableSelect = function(url, options) {

        return {
            status: 0,
            message: 'OKOKmsg',
            data: { 
                reportTemplateId: Math.random(),
                selected: true
            }
        };
    };

  
    xmock.data.PLANE_TABLE_INIT = normalInit;
    xmock.data.PLANE_TABLE_COL_DATA = normalColData;
    xmock.data.PLANE_TABLE_COND_DATA = normalCondData;
    xmock.data.PLANE_TABLE_SQL_SAVE = normalSqlSave;
    xmock.data.PLANE_TABLE_COND_SAVE = normalCondSave;
    xmock.data.PLANE_TABLE_COL_SAVE = normalColSave;
    xmock.data.PLANE_TABLE_PREVIEW_DATA = normalPreviewData;
    xmock.data.PLANE_TABLE_DATA = normalPlaneTableData;
    xmock.data.PLANE_TABLE_SELECT = normalPlaneTableSelect;

})();