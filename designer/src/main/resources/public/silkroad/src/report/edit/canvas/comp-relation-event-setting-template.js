define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$out='';$out+='<!--\n数据例子：\nvar demoData = {\n    measureId: \'\',\n    reocrdSize: \'\',\n    topType: \'\',\n    indList: [\n\n    ],\n    topTypeList: {\n        desc: bottom,\n        asc: top,\n        none: none\n    }\n};\n-->\n<div class="comp-relation-event">\n    <span>选择被关联组件</span>\n    <div class="comp-realtion-box">\n    </div>\n    <span>选择传出参数</span>\n    <div class="comp-realtion-param">\n        <span class="span-out-param">选择传出维度</span>\n        <select></select>\n        <span class="span-level">选择当前还是下一级</span>\n        <select></select>\n    </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});