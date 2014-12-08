// 外壳的mockup
(function() {
	
    var NS = xmock.data.console.OLAPEditor = {};

    function random () {
        return Math.round(Math.random() * 10000000);
    }
    
    function OLAP_REPORT_INIT(url, options) {
        return {
            status : 0,
            message : 'OKOKmsg',
            data : {
                reportTemplateId: '123412342134_sessionloaded_new' + random(),
                reportTemplateName: 'my报表'
            }
        };
    }

    function OLAP_SAVE(url, options) {
        return {
            status : 0,
            message : 'OKOKmsg',
            data : {
                reportTemplateId: '123412342134_transient_new_saved' + random()
            }
        };
    }

    NS.OLAP_REPORT_INIT = OLAP_REPORT_INIT;
	NS.OLAP_SAVE = OLAP_SAVE;

    NS.DATA_FORMAT_SET = function(url,options){
        var param = xmock.parseParam(options.data);
        //console.log('submit success,default format is:' + param.format);
        return{
            status : 0,
            message : 'submit success,default format is:' + param.format,
            data:{

            }
        }
    }

    NS.GET_TEMPLATE_INFO = function(){
        return{
            status : 0,
            message : 'init success!',
            data:{
                defaultFormat:'I.DD%',
                type : 'ROWMERGE',
                rmKey : 'DIM_NAME1',
                measures : [
                    {
                        name : '测试指标1',
                        uniqueName : '[Measures].[testMeasure1]',
                        format : 'I,III.DD'
                    },{
                        name : '很长很长的指标名称-测试指标2',
                        uniqueName : '[Measures].[testMeasure2]'
                    },{
                        name : '指标3',
                        uniqueName : '[Measures].[testMeasure3]',
                        format : 'I,III'
                    },{
                        name : '测试指标4',
                        uniqueName : '[Measures].[testMeasure4]',
                        format : 'I.DD%'
                    },{
                        name : '测试测试测试测试测试指标5',
                        uniqueName : '[Measures].[testMeasure5]',
                        format : 'I,III'
                    }   
                ]
            }
        }
    }

})();