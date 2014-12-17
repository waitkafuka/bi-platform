define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,dataFormat=$data.dataFormat,$formatItem=$data.$formatItem,name=$data.name,$escape=$utils.$escape,$out='';$out+='<!-- 指标提示信息设置 -->\r\n<div class="data-format">\r\n    <div class="data-format-alone">\r\n        <span>对各指标提示信息进行单独设置</span>\r\n        ';
        $each(dataFormat,function($formatItem,name){
        $out+='\r\n        <div class="data-format-alone-dim">\r\n            <span>';
        $out+=$escape($formatItem.caption);
        $out+='：</span>\r\n            <input type="text"/s>\r\n        </div>\r\n        ';
        });
        $out+='\r\n    </div>\r\n</div>';
        return $out;
    }
    return { render: anonymous };
});