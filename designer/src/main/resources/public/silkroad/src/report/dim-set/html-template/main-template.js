define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$out='';$out+='<div class="dim-setting">\n    <ul class="dim-setting-head">\n        <li class="classification classification-focus" id="j-tab-normal"><span>普通维度</span></li>\n        <li class="classification" id="j-tab-date"><span>时间维度</span></li>\n        <li class="classification" id="j-tab-callback"><span>回调维度</span></li>\n        <li class="classification" id="j-tab-custom"><span>自定义维度</span></li>\n    </ul>\n    <div class="dim-setting-body j-dim-setting-body">\n    </div>\n    <div class="ta-c mt-100">\n        <span class="button button-flat-primary m-10 j-dim-set-prev">返回重新选择数据表</span>\n        <span class="button button-flat-primary m-10 j-dim-set-ok">完成</span>\n        <span class="button button-flat-primary m-10 j-dim-set-cancel">取消</span>\n    </div>\n</div>\n\n';
        return $out;
    }
    return { render: anonymous };
});