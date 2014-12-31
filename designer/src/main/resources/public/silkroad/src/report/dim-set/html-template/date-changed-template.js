define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,tableId=$data.tableId,$each=$utils.$each,currDims=$data.currDims,$dim=$data.$dim,$index=$data.$index,$escape=$utils.$escape,fields=$data.fields,$level=$data.$level,$field=$data.$field,dateFormatOptions=$data.dateFormatOptions,$format=$data.$format,$out='';if(tableId === "0" || tableId === "ownertable"){
        $out+='\n<!--内置维度-->\n<div class="date-relation-owner-two-part c-f j-date-two-part">\n    <span>选择时间字段：</span>\n    <select>\n        <option value="0">请选择</option>\n        ';
        $each(currDims,function($dim,$index){
        $out+='\n        <option value=';
        $out+=$escape($dim.id);
        $out+='>';
        $out+=$escape($dim.name);
        $out+='\n        </option>\n        ';
        });
        $out+='\n    </select>\n    <span>粒度：</span>\n    <select class="j-owner-date-level-select">\n        <option value="0">请选择</option>\n        ';
        $each(fields,function($level,$index){
        $out+='\n        <option value=';
        $out+=$escape($level.id);
        $out+='>';
        $out+=$escape($level.name);
        $out+='\n        </option>\n        ';
        });
        $out+='\n    </select>\n    <span>时间格式：</span>\n    <select class="j-owner-date-type-select">\n        <option value="0">请选择</option>\n    </select>\n</div>\n';
        }else{
        $out+='\n<!--普通维度-->\n<div class="date-relation-normal-two-part c-f j-date-two-part">\n    <span>指定关联字段：</span>\n    <select>\n        <option value="0">请选择</option>\n        ';
        $each(currDims,function($dim,$index){
        $out+='\n        <option value=';
        $out+=$escape($dim.id);
        $out+='>';
        $out+=$escape($dim.name);
        $out+='\n        </option>\n        ';
        });
        $out+='\n    </select>\n    <span class="equal">=</span>\n    <select>\n        <option value="0">请选择</option>\n        ';
        $each(fields,function($field,$index){
        $out+='\n        <option value=';
        $out+=$escape($field.id);
        $out+='>';
        $out+=$escape($field.name);
        $out+='\n        </option>\n        ';
        });
        $out+='\n    </select>\n</div>\n<div class="date-relation-normal-three-part j-date-three-part">\n    <span class="date-relation-normal-three-part-name">日期格式：</span>\n    <div class="date-relation-normal-three-part-box c-f">\n        ';
        $each(fields,function($field,$index){
        $out+='\n        <div class="date-relation-normal-three-part-box-date-format c-f">\n            <span>';
        $out+=$escape($field.name);
        $out+='</span>\n            <select formatKey=';
        $out+=$escape($field.id);
        $out+='>\n                <option value="0">请选择</option>\n                ';
        $each(dateFormatOptions[$field.id],function($format,$index){
        $out+='\n                <option value=';
        $out+=$escape($format);
        $out+='>';
        $out+=$escape($format);
        $out+='\n                </option>\n                ';
        });
        $out+='\n            </select>\n        </div>\n        ';
        });
        $out+='\n    </div>\n</div>\n';
        }
        $out+='\n\n\n';
        return $out;
    }
    return { render: anonymous };
});