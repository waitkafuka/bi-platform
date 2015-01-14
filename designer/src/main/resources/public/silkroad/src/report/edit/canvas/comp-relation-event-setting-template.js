define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,outParamDim=$data.outParamDim,$option=$data.$option,key=$data.key,$escape=$utils.$escape,selectDim=$data.selectDim,outParamLevel=$data.outParamLevel,selectLevel=$data.selectLevel,$out='';$out+='<!--\n数据例子：\nvar demoData = {\n    outParamDim: {\n        id: \'\',\n        caption: \'\',\n        name: \'\'\n    },\n    levelData: {\n        \'level1\': \'当前级别\',\n        \'level12\': \'下一级别\'\n    },\n    selectDimId: \'\',\n    selectDimName: \'\',\n    selectLevel: \'\'\n};\n-->\n<div class="comp-relation-event">\n    <span>选择被关联组件</span>\n    <div class="comp-realtion-box">\n    </div>\n    <span>选择传出参数</span>\n    <div class="comp-realtion-param">\n        <span class="span-out-param">选择传出维度</span>\n        <select class="j-comp-relation-event-out-param">\n            ';
        $each(outParamDim,function($option,key){
        $out+='\n            <option value=';
        $out+=$escape($option.id);
        $out+='$';
        $out+=$escape($option.name);
        $out+='\n            ';
        if(selectDim && $option.id === selectDim){
        $out+='\n            selected="selected"\n            ';
        }
        $out+='>';
        $out+=$escape($option.caption);
        $out+='\n            </option>\n            ';
        });
        $out+='\n        </select>\n        <span class="span-level">选择当前还是下一级</span>\n        <select class="j-comp-relation-event-out-param-level">\n            ';
        $each(outParamLevel,function($option,key){
        $out+='\n            <option value=';
        $out+=$escape(key);
        $out+='\n            ';
        if(selectLevel && key === selectLevel){
        $out+='\n            selected="selected"\n            ';
        }
        $out+='>';
        $out+=$escape($option);
        $out+='\n            </option>\n            ';
        });
        $out+='\n        </select>\n    </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});