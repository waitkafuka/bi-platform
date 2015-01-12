define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,currDims=$data.currDims,$dim=$data.$dim,$index=$data.$index,$escape=$utils.$escape,relationTables=$data.relationTables,$relationTable=$data.$relationTable,$out='';$out+='<!--此模版需要数据\n var demoData = {\n        "currDims": [\n            {"id": "dim1", "name": "维度1"},\n            {"id": "dim2", "name": "维度2"},\n            {"id": "dim3", "name": "维度3"}\n        ],\n        "relationTables": [\n            {\n                id: "table1",\n                name: "表1",\n                // 指定关联字段全集\n                fields: [{id: "table1Fields1", name: "table1Fields1"},{id: "table1Fields2", name: "table1Fields2"}]\n            },\n            {\n                id: "table2",\n                name: "表2",\n                // 指定关联字段全集\n                fields: [{id: "table2Fields1", name: "table2Fields1"},{id: "table2Fields2", name: "table2Fields2"}]\n            }\n        ]\n };\n-->\n<div class="normal-relation-box j-normal-relation-box">\n    <span class="normal-broken-line"></span>\n    <select class="normal-relation-box-select-fields mr-20">\n        <!-- 循环每一行中的主表字段(cubes.cube1.currDims)-->\n        <option value="0">请选择</option>\n        ';
        $each(currDims,function($dim,$index){
        $out+='\n        <option value=';
        $out+=$escape($dim.id);
        $out+='>\n           ';
        $out+=$escape($dim.name);
        $out+='\n        </option>\n        ';
        });
        $out+='\n    </select>\n    <span class="equal">=</span>\n    <select class="normal-relation-box-select-table mr-30 j-normal-relation-table-select" >\n        <!-- 循环关联数据表(relationTables)-->\n        <option value="0">请选择</option>\n        ';
        $each(relationTables,function($relationTable,$index){
        $out+='\n        <option value=';
        $out+=$escape($relationTable.id);
        $out+='>';
        $out+=$escape($relationTable.name);
        $out+='\n        </option>\n        ';
        });
        $out+='\n    </select>\n    <select class="normal-relation-box-select-fields mr-10">\n        <!-- 循环关联数据表(relationTables)-->\n        <option value="0">请选择</option>\n    </select>\n    <span class="delete j-normal-delete"></span>\n    <span class="add j-normal-add"></span>\n</div>\n';
        return $out;
    }
    return { render: anonymous };
});