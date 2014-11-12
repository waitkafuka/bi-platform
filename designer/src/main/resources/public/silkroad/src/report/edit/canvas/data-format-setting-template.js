define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,dataformatOptions=$data.dataformatOptions,$optionValue=$data.$optionValue,optionKey=$data.optionKey,$escape=$utils.$escape,defaultFormat=$data.defaultFormat,dataformat=$data.dataformat,ind=$data.ind,indkey=$data.indkey,$out='';$out+='<div class="data-format">\r\n    <div class="data-format-default">\r\n        <span>指标默认数据格式：</span>\r\n        <select name="default">\r\n            <option value="">请选择</option>\r\n            ';
        $each(dataformatOptions,function($optionValue,optionKey){
        $out+='\r\n            <option value=';
        $out+=$escape(optionKey);
        $out+=' ';
        if(optionKey === defaultFormat){
        $out+=' selected="selected" ';
        }
        $out+='>';
        $out+=$escape($optionValue);
        $out+='</option>\r\n            ';
        });
        $out+='\r\n        </select>\r\n    </div>\r\n    <div class="data-format-alone">\r\n        <span>对各指标进行单独设置</span>\r\n        ';
        $each(dataformat,function(ind,indkey){
        $out+='\r\n        <div class="data-format-alone-dim">\r\n            <span>';
        $out+=$escape(ind.name);
        $out+='：</span>\r\n            <select name="';
        $out+=$escape(indkey);
        $out+='">\r\n                <option value="">请选择</option>\r\n                ';
        $each(dataformatOptions,function($optionValue,optionKey){
        $out+='\r\n                <option value=';
        $out+=$escape(optionKey);
        $out+=' ';
        if(optionKey === ind.format){
        $out+=' selected="selected"\r\n                ';
        }
        $out+='>';
        $out+=$escape($optionValue);
        $out+='</option>\r\n                ';
        });
        $out+='\r\n            </select>\r\n        </div>\r\n        ';
        });
        $out+='\r\n    </div>\r\n</div>';
        return $out;
    }
    return { render: anonymous };
});