define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,indList=$data.indList,$ind=$data.$ind,name=$data.name,$escape=$utils.$escape,$out='';$out+='<!--\n数据例子：\nvar demoData = {\n    measureId: \'\',\n    reocrdSize: \'\',\n    topType: \'\',\n    indList: [\n\n    ],\n    topTypeList: {\n        desc: bottom,\n        asc: top,\n        none: none\n    }\n};\n-->\n<!-- 指标颜色设置 -->\n<div class="ind-color">\n    <div class="ind-color-alone">\n        <span>对各指标颜色进行单独设置</span>\n        ';
        $each(indList,function($ind,name){
        $out+='\n        <div class="ind-color-alone-ind">\n            <span>';
        $out+=$escape($ind.caption);
        $out+='：</span>\n            <input type="text" name="';
        $out+=$escape(name);
        $out+='" value="';
        $out+=$escape($ind.color);
        $out+='" />\n        </div>\n        ';
        });
        $out+='\n    </div>\n</div>\n';
        return $out;
    }
    return { render: anonymous };
});