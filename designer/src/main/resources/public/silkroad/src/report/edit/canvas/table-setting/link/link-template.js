define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,columnDefine=$data.columnDefine,$column=$data.$column,name=$data.name,$escape=$utils.$escape,planeTableList=$data.planeTableList,$planeTable=$data.$planeTable,$index=$data.$index,$out='';$out+='<!--\n数据例子：\nvar demoData = {\n    "columnDefine": [\n        {"text": "文本1", "value": "text1", "selectedTable": "table1"},\n        {"text": "文本2", "value": "text2", "selectedTable": "table2"},\n        {"text": "文本3", "value": "text3", "selectedTable": "table3"}\n    ],\n    "planeTableList": [\n        {\n            "text": "表1",\n            "value": "table1"\n        },\n        {\n            "text": "表2",\n            "value": "table2"\n},\n        {\n            "text": "表3",\n            "value": "table3"\n        }\n    ]\n};\n-->\n<!-- 指标颜色设置 -->\n<div class="table-link-set">\n    <div class="table-link-set-area j-table-link-set-column-table">\n        <label>请设置列与跳转表格的关系</label>\n        <div class="table-link-set-area-items">\n            ';
        $each(columnDefine,function($column,name){
        $out+='\n            <div class="table-link-set-item">\n                <label class="left" data-value="';
        $out+=$escape($column.value);
        $out+='">';
        $out+=$escape($column.text);
        $out+='：</label>\n                <select class="right w-p-40 mr-10 j-table-link-set-plane-table">\n                    <option value="">请选择</option>\n                    ';
        $each(planeTableList,function($planeTable,$index){
        $out+='\n                    <option value=';
        $out+=$escape($planeTable.value);
        $out+='\n                    ';
        if($column.selectedTable === $planeTable.value){
        $out+=' selected="selected"\n                    ';
        }
        $out+='>';
        $out+=$escape($planeTable.text);
        $out+='</option>\n                    ';
        });
        $out+='\n                </select>\n                <span type="button" class="form-common-input-button j-next ';
        if(!$column.selectedTable){
        $out+='hide';
        }
        $out+='">设置参数</span>\n            </div>\n            ';
        });
        $out+='\n        </div>\n    </div>\n    <div class="table-link-set-area j-table-link-set-param-table hide">\n        <label>请设置参数与跳转表格的关系</label>\n        <div class="table-link-set-area-items j-table-link-set-param-items">\n        </div>\n        <div class="f-r mt-5">\n            <span type="button" class="form-common-input-button mr-10 j-back">上一步</span>\n            <span type="button" class="form-common-input-button j-ok">完成</span>\n        </div>\n\n    </div>\n</div>\n';
        return $out;
    }
    return { render: anonymous };
});