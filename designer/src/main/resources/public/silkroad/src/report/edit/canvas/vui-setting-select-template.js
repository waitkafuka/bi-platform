define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$escape=$utils.$escape,compId=$data.compId,$each=$utils.$each,xAxis=$data.xAxis,item=$data.item,$index=$data.$index,$out='';$out+='<div class="con-comp-setting-type1 j-comp-setting j-comp-select"  data-comp-id="';
        $out+=$escape(compId);
        $out+='" data-comp-type="SELECT">\n    <div class="data-axis-line data-axis-line-48 j-comp-setting-line j-line-x" data-axis-type="x">\n        <span class="letter">维度:</span>\n        ';
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
        $out+='\n    </div>\n    <div class="data-axis-line data-axis-line-48 data-btn-line">\n        <span class="letter">设置:</span>\n        <span>下拉框类型：</span>\n        <select class="select-type" data-comp-id="';
        $out+=$escape(compId);
        $out+='">\n            <option value="ECUI_SELECT" ';
        if($data.compMold && $data.compMold==="ECUI_SELECT"){
        $out+=' selected="selected"';
        }
        $out+='>\n                单选\n            </option>\n            <option value="ECUI_MULTI_SELECT" ';
        if($data.compMold && $data.compMold==="ECUI_MULTI_SELECT"){
        $out+=' selected="selected"';
        }
        $out+='>\n                多选\n            </option>\n        </select>\n        <!--<span>下拉框皮肤：</span>-->\n        <!--<select class="select-skin" data-comp-id="';
        $out+=$escape(compId);
        $out+='">-->\n            <!--<option value="classics" ';
        if($data.compSkin && $data.compSkin==="classics"){
        $out+=' selected="selected"';
        }
        $out+='>-->\n            <!--经典样式-->\n            <!--</option>-->\n            <!--<option value="lightblue" ';
        if($data.compSkin && $data.compSkin==="lightblue"){
        $out+=' selected="selected"';
        }
        $out+='>-->\n            <!--商桥样式-->\n            <!--</option>-->\n        <!--</select>-->\n    </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});