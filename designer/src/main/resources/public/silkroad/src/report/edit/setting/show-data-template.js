define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,oriInd=$data.oriInd,$value=$data.$value,$index=$data.$index,$escape=$utils.$escape,oriDim=$data.oriDim,$out='';$out+='<div class="data-sources-setting data-sources-set-show-data">\n    <div class="j-oriInd">\n        <div class="title">指标</div>\n        ';
        $each(oriInd,function($value,$index){
        $out+='\n        <div class="item ellipsis" title="';
        $out+=$escape($value.name);
        $out+='（';
        $out+=$escape($value.id);
        $out+='）">\n            <label><input class="checkbox" type="checkbox" ';
        if($value.selected){
        $out+='checked';
        }
        $out+=' value="';
        $out+=$escape($value.id);
        $out+='">';
        $out+=$escape($value.name);
        $out+='（';
        $out+=$escape($value.id);
        $out+='）</label>\n        </div>\n        ';
        });
        $out+='\n    </div>\n    <div class="j-oriDim">\n        <div class="title">维度</div>\n        ';
        $each(oriDim,function($value,$index){
        $out+='\n        <div class="item ellipsis" title="';
        $out+=$escape($value.name);
        $out+='（';
        $out+=$escape($value.id);
        $out+='）">\n            <label><input class="checkbox" type="checkbox" ';
        if($value.selected){
        $out+='checked';
        }
        $out+=' value="';
        $out+=$escape($value.id);
        $out+='">';
        $out+=$escape($value.name);
        $out+='（';
        $out+=$escape($value.id);
        $out+='）</label>\n        </div>\n        ';
        });
        $out+='\n    </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});