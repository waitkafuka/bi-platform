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
        $out+='>\n                多选\n            </option>\n        </select>\n\n        <!-- 单选下拉框默认值 -->\n        <div class="select-default"\n            ';
        if($data.compMold && $data.compMold==="ECUI_SELECT"){
        $out+='\n                style="display: inline-block;"\n            ';
        }else{
        $out+='\n                style="display: none;"\n            ';
        }
        $out+='>\n            <span>&nbsp;&nbsp;下拉框默认值：</span>\n            <span class="select-default-name">\n                全部\n                ';
        $each(xAxis,function(item,$index){
        $out+='\n                    （';
        $out+=$escape(item.caption);
        $out+='）\n                ';
        });
        $out+='\n            </span>\n            <input class="select-default-value j-select-setAll"\n                style="vertical-align: bottom;" type="checkbox"\n                data-comp-id="';
        $out+=$escape(compId);
        $out+='"\n                value="全部（';
        $each(xAxis,function(item,$index){
        $out+=$escape(item.caption);
        });
        $out+='）"\n                ';
        if($data.compAll==="true"){
        $out+='\n                    checked="checked"\n                    ';
        }else{
        $out+='\n                ';
        }
        $out+='\n            />\n        </div>\n        <!--<span>下拉框皮肤：</span>-->\n        <!--<select class="select-skin" data-comp-id="';
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