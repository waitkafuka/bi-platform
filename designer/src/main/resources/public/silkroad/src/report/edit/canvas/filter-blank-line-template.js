define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,dataFormat=$data.dataFormat,$formatItem=$data.$formatItem,name=$data.name,$escape=$utils.$escape,$out='';$out+='<!-- 其他操作 -->\r\n<div class="data-format">\r\n    <div class="data-format-alone">\r\n        <!--';
        $each(dataFormat,function($formatItem,name){
        $out+='-->\r\n        <!--<div class="data-format-alone-dim">-->\r\n            <!--<span>';
        $out+=$escape($formatItem.caption);
        $out+='：</span>-->\r\n            <!--<input type="text" name=';
        $out+=$escape(name);
        $out+=' value=';
        $out+=$escape($formatItem.format);
        $out+=' placeholder="请输入描述信息"/>-->\r\n        <!--</div>-->\r\n        <!--';
        });
        $out+='-->\r\n        <div class="data-format-black">\r\n            <span>是否过滤空白行：</span>\r\n            <input type="checkbox" name=';
        $out+=$escape(name);
        $out+=' />\r\n        </div>\r\n    </div>\r\n</div>';
        return $out;
    }
    return { render: anonymous };
});