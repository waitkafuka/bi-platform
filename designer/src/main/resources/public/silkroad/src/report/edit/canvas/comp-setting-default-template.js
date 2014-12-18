define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$escape=$utils.$escape,compId=$data.compId,compType=$data.compType,$each=$utils.$each,xAxis=$data.xAxis,item=$data.item,$index=$data.$index,yAxis=$data.yAxis,sAxis=$data.sAxis,$out='';$out+='<div class="con-comp-setting-type1 j-comp-setting" data-comp-id="';
        $out+=$escape(compId);
        $out+='" data-comp-type="';
        $out+=$escape(compType);
        $out+='">\n    <div class="data-axis-line data-axis-line-34 j-comp-setting-line j-line-x" data-axis-type="x">\n        <span class="letter">横轴:</span>\n        ';
        $each(xAxis,function(item,$index){
        $out+='\n        <div class="item hover-bg j-root-line c-m" data-id="';
        $out+=$escape(item.id);
        $out+='">\n            <span class="item-text j-item-text icon-font" title="';
        $out+=$escape(item.caption);
        $out+='（';
        $out+=$escape(item.name);
        $out+='）">\n            ';
        $out+=$escape(item.caption);
        if(item.name){
        $out+='（';
        $out+=$escape(item.name);
        $out+='）';
        }
        $out+='\n            </span>\n            <span class="icon hide j-delete" title="删除">×</span>\n        </div>\n        ';
        });
        $out+='\n    </div>\n    <div class="data-axis-line data-axis-line-34 j-comp-setting-line j-line-y" data-axis-type="y">\n        <span class="letter">纵轴:</span>\n        ';
        $each(yAxis,function(item,$index){
        $out+='\n        <div class="item hover-bg j-root-line c-m" data-id="';
        $out+=$escape(item.id);
        $out+='">\n            ';
        if($data.compType === "CHART"){
        $out+='\n                ';
        if(item.chartType === null){
        $out+='\n                    <span class="icon-chart bar j-icon-chart" chart-type="bar" ></span>\n                ';
        }else{
        $out+='\n                    <span class="icon-chart ';
        $out+=$escape(item.chartType);
        $out+=' j-icon-chart" chart-type="';
        $out+=$escape(item.chartType);
        $out+='" ></span>\n                ';
        }
        $out+='\n            ';
        }
        $out+='\n            <span class="item-text j-item-text icon-font" title="';
        $out+=$escape(item.caption);
        $out+='（';
        $out+=$escape(item.name);
        $out+='）">\n            ';
        $out+=$escape(item.caption);
        if(item.name){
        $out+='（';
        $out+=$escape(item.name);
        $out+='）';
        }
        $out+='\n            </span>\n            <span class="icon hide j-delete" title="删除">×</span>\n        </div>\n        ';
        });
        $out+='\n    </div>\n    <div class="data-axis-line data-axis-line-34 j-comp-setting-line j-line-s" data-axis-type="s">\n        <span class="letter">过滤轴:</span>\n        ';
        $each(sAxis,function(item,$index){
        $out+='\n        <div class="item hover-bg j-root-line" data-id="';
        $out+=$escape(item.id);
        $out+='">\n            <span class="item-text j-item-text" title="';
        $out+=$escape(item.caption);
        $out+='（';
        $out+=$escape(item.name);
        $out+='）">\n            ';
        $out+=$escape(item.caption);
        if(item.name){
        $out+='（';
        $out+=$escape(item.name);
        $out+='）';
        }
        $out+='\n            </span>\n            <span class="icon-letter j-delete" title="删除">×</span>\n        </div>\n        ';
        });
        $out+='\n    </div>\n    <div class="data-axis-line data-axis-line-48 data-btn-line">\n        <span class="letter">设置:</span>\n        <span class="icon-letter icon-letter-btn j-set-data-format">数据格式</span>\n        <span class="icon-letter icon-letter-btn j-norm-info-depict">指标信息描述</span>\n    </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});