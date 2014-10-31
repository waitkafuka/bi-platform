/**
 * @file: 报表组件组合后渲染的入口
 * @author: lizhantong(lztlovely@126.com)
 * date:     2014/07/31
 */

define(function () {

    //------------------------------------------
    // 引用
    //------------------------------------------
    var GLOBAL_MODEL = di.shared.model.GlobalModel;
    var Engine = di.shared.model.Engine;
    var engine;
    var globalModel;
    // TODO:需要后端返回
    var options =  {
        webRoot: '/silkroad',
        mold: false,
        phase: 'dev',
        // 需要问后端
        externalParam: {
            xxxxexternal: '++==asfd',
            yyyyexternal: 12344
        },
        // 先这么用
        globalType: 'PRODUCT',
        // diAgent: 'STUB', // 为测试方便，注释掉
        // 报表ID
        reportId: 'ffe53e458cb13b69b0717c5b949babfb',
        // 生产线
        bizKey: 'ditry',
        // 服务器时间
        serverTime: (new Date()).getTime(),
        // 先这么用吧
        extraOpt: (window.__$DI__NS$__ || {}).OPTIONS
    };

    //------------------------------------------
    // 对外提供接口
    //------------------------------------------
    var enter = {
        start: start,
        dispose: dispose
    };

    /**
     * 画布中的报表引擎入口
     *
     */
    function start(o) {
        var jsonArray = [];
        jsonArray.push(o.rptJson);
        o.parentEl.innerHTML = o.rptHtml;
        o.reportId && (options.reportId = o.reportId);
        options.reportBody = o.parentEl.parent;

        if (!globalModel) {
            globalModel = GLOBAL_MODEL(options);
        }

        engine = new Engine(options);
        engine.start(engine.mergeDepict(jsonArray));
    }


    /**
     * 画布中的报表释放
     *
     */
    function dispose() {
        engine.dispose();
    }

    return enter;
});