define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,indList=$data.indList,$value=$data.$value,$index=$data.$index,$escape=$utils.$escape,$out='';$out+='<div class="title">\n    指标\n    <span class="icon-data-sources j-setting-derive-inds derivative-ind-setting" title="管理衍生指标"></span>\n</div>\n<div class="j-con-org-ind con-org-ind">\n    ';
        $each(indList.data,function($value,$index){
        $out+=' ';
        if($value.type == "COMMON" && $value.visible == true){
        $out+='\n    <div class="item c-m hover-bg j-root-line j-org-ind j-olap-element';
        if($value.canToDim){
        $out+=' j-can-to-dim';
        }
        $out+='" data-id="';
        $out+=$escape($value.id);
        $out+='">\n        <span class="item-text ellipsis j-item-text" title="';
        $out+=$escape($value.tag);
        $out+='（';
        $out+=$escape($value.name);
        $out+='）">\n            ';
        $out+=$escape($value.caption);
        $out+='（';
        $out+=$escape($value.name);
        $out+='）\n        </span>\n        <span class="icon-letter collect j-method-type" title="点击设置指标汇总方式">\n            ';
        $out+=$escape(indList.map[$value.aggregator]);
        $out+='\n        </span>\n    </div>\n    ';
        }
        });
        $out+='\n    ';
        $each(indList.data,function($value,$index){
        $out+=' ';
        if($value.type == "CALLBACK"){
        $out+='\n    <div class="item c-m hover-bg j-root-line j-org-ind j-olap-element" data-id="';
        $out+=$escape($value.id);
        $out+='">\n        <span class="item-text ellipsis j-item-text" title="';
        $out+=$escape($value.tag);
        $out+='（';
        $out+=$escape($value.name);
        $out+='）">\n            ';
        $out+=$escape($value.caption);
        $out+='（';
        $out+=$escape($value.name);
        $out+='）\n        </span>\n        <span class="icon-letter collect j-method-type" title="点击设置指标汇总方式">\n            ';
        $out+=$escape(indList.map[$value.aggregator]);
        $out+='\n        </span>\n    </div>\n    ';
        }
        });
        $out+='\n</div>\n';
        $each(indList.data,function($value,$index){
        if($value.type == "CAL" || $value.type == "RR" || $value.type == "SR"){
        $out+='\n<div class="item c-m hover-bg j-olap-element j-cal-ind" data-id="';
        $out+=$escape($value.id);
        $out+='">\n    <span class="item-text ellipsis fw-b" title="';
        $out+=$escape($value.caption);
        $out+='">';
        $out+=$escape($value.caption);
        $out+='</span>\n</div>\n';
        }
        });
        return $out;
    }
    return { render: anonymous };
});