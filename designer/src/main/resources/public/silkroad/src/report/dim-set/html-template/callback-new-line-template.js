define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$escape=$utils.$escape,lineIndex=$data.lineIndex,$each=$utils.$each,currDims=$data.currDims,$dim=$data.$dim,$index=$data.$index,boxIndex=$data.boxIndex,refreshType=$data.refreshType,$out='';$out+='<!--\nvar demoData = {\n                currDims: [\n                    {"id": "dim1", "name": "维度1"},\n                    {"id": "dim2", "name": "维度2"},\n                    {"id": "dim3", "name": "维度3"}\n                ],\n                boxIndex: 1,\n                lineIndex: 2\n            };\n-->\n<div class="callback-relation-box c-f j-callback-relation-box" bodyIndex=';
        $out+=$escape(lineIndex);
        $out+='>\n    <span class="callback-broken-line"></span>\n    <div class="callback-relation-content">\n        <div class="first-part c-f">\n            <span>选择被关联表：</span>\n            <select>\n                <option value="0">请选择</option>\n                ';
        $each(currDims,function($dim,$index){
        $out+='\n                <option value=';
        $out+=$escape($dim.id);
        $out+='>';
        $out+=$escape($dim.name);
        $out+='\n                </option>\n                ';
        });
        $out+='\n            </select>\n        </div>\n        <div class="callback-relation-content-two-part c-f">\n            <span class="callback-address-name">选择回调字段：</span>\n            <input type="text" name="" id="" class="callback-address-input j-callback-address-input" value="" />\n            <span class="callback-address-prompt">\n                例如：http://10.46.133.66:8999/pfplat/callbackmock.action\n            </span>\n        </div>\n        <div class="callback-relation-content-three-part c-f">\n            <span class="callback-cache-name">选取缓存类型：</span>\n            <div>\n                <input type="radio" value="1" class="callback-cache-type-right-input" name="callback-cache-body';
        $out+=$escape(boxIndex);
        $out+='-box';
        $out+=$escape(lineIndex);
        $out+='"\n                ';
        if(refreshType === 1){
        $out+=' checked="checked" ';
        }
        $out+=' />\n                <label class="callback-cache-type-right-label">\n                    无需缓存（数据量大，不推荐）\n                </label>\n            </div>\n            <div>\n                <input type="radio" value="2" class="callback-cache-type-right-input" name="callback-cache-body';
        $out+=$escape(boxIndex);
        $out+='-box';
        $out+=$escape(lineIndex);
        $out+='"\n                ';
        if(refreshType === 2){
        $out+=' checked="checked" ';
        }
        $out+=' />\n                <label class="callback-cache-type-right-label">\n                    在数据刷新后立刻刷新缓存\n                </label>\n            </div>\n            <div class="ml-89">\n                <input type="radio" value="3" class="callback-cache-type-right-input" name="callback-cache-body';
        $out+=$escape(boxIndex);
        $out+='-box';
        $out+=$escape(lineIndex);
        $out+='"\n                ';
        if(refreshType === 3){
        $out+=' checked="checked" ';
        }
        $out+=' />\n                    <span class="callback-cache-type-right-label">间隔\n                    <input type="text" class="callback-cache-type-interval j-callback-cache-type-interval" value="" />\n                    秒刷新一次缓存</span>\n            </div>\n        </div>\n    </div>\n    <span class="delete j-callback-delete"></span>\n    <span class="add j-callback-add"></span>\n</div>\n\n\n';
        return $out;
    }
    return { render: anonymous };
});