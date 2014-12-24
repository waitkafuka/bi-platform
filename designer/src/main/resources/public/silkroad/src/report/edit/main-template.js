define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,cubeList=$data.cubeList,$value=$data.$value,$index=$data.$index,$escape=$utils.$escape,$out='';$out+='<div class="con-report-edit">\n    <div class="data-sources-setting overflow j-data-sources-setting">\n        <div class="overflow j-scroll-data-sources">\n            <div class="con-data-table p-r c-f">\n                <select class="data-table-select j-cube-select">\n                    ';
        $each(cubeList,function($value,$index){
        $out+='\n                    <option value="';
        $out+=$escape($value.id);
        $out+='">';
        $out+=$escape($value.name);
        $out+='</option>\n                    ';
        });
        $out+='\n                </select>\n                <span class="icon-data-sources j-icon-data-sources" title="数据模型相关设置"></span>\n            </div>\n            <div class="con-ind j-data-sources-setting-con-ind"></div>\n            <div class="con-dim j-data-sources-setting-con-dim"></div>\n        </div>\n    </div>\n    <div class="canvas j-canvas">\n        <div class="j-globalbtn"></div>\n        <div class="comp-setting j-con-comp-setting"></div>\n        <div class="report j-report"></div>\n        <span class="button button-flat-primary button-save-report j-button-save-report">保存</span>\n        <span class="button button-flat-primary button-publish-report j-button-publish-report">发布</span>\n    </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});