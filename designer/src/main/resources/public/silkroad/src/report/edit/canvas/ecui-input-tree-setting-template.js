define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$escape=$utils.$escape,compId=$data.compId,$each=$utils.$each,xAxis=$data.xAxis,item=$data.item,$index=$data.$index,$out='';$out+='<div class="con-comp-setting-type1 j-comp-setting j-comp-single-drop-down-tree"  data-comp-id="';
        $out+=$escape(compId);
        $out+='" data-comp-type="SINGLE_DROP_DOWN_TREE">\n    <div class="data-axis-line data-axis-line-48 j-comp-setting-line j-line-x" data-axis-type="x">\n        <span class="letter">维度:</span>\n        ';
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
        $out+='\n    </div>\n    </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});