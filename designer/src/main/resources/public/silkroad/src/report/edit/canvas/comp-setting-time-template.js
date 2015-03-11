define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$escape=$utils.$escape,compId=$data.compId,$each=$utils.$each,xAxis=$data.xAxis,item=$data.item,$index=$data.$index,$out='';$out+='<div class="con-comp-setting-type1 j-comp-setting" data-comp-id="';
        $out+=$escape(compId);
        $out+='" data-comp-type="TIME_COMP">\n    <div class="data-axis-line data-axis-line-48 j-comp-setting-line j-line-x" data-axis-type="x">\n        <span class="letter">时间维度:</span>\n        ';
        $each(xAxis,function(item,$index){
        $out+='\n        <div class="item hover-bg j-root-line" data-id="';
        $out+=$escape(item.id);
        $out+='" data-name="';
        $out+=$escape(item.name);
        $out+='">\n            <span class="item-text j-item-text icon-font" title="';
        $out+=$escape(item.caption);
        $out+='（';
        $out+=$escape(item.name);
        $out+='）">\n            ';
        $out+=$escape(item.caption);
        $out+='（';
        $out+=$escape(item.name);
        $out+='）\n            </span>\n            <span class="icon hide j-delete" title="删除">×</span>\n        </div>\n        ';
        });
        $out+='\n    </div>\n    <div class="data-axis-line data-axis-line-48 data-btn-line">\n        <span class="letter">设置:</span>\n        <span class="icon-letter icon-letter-btn j-set-default-time">默认选中时间</span>\n        <span>时间选择类型：</span>\n        <select class="select-calendar-type" data-comp-id="';
        $out+=$escape(compId);
        $out+='" data-comp-type="TIME_COMP">\n            <option value="CAL_SELECT" ';
        if($data.compMold && $data.compMold==="CAL_SELECT"){
        $out+=' selected="selected"';
        }
        $out+='>\n            时间单选\n            </option>\n            <option value="DOUBLE_CAL_SELECT" ';
        if($data.compMold && $data.compMold==="DOUBLE_CAL_SELECT"){
        $out+=' selected="selected"';
        }
        $out+='>\n            时间双选\n            </option>\n        </select>\n    </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});