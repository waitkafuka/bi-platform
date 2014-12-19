define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$out='';$out+='<!-- 其他操作 -->\r\n<div class="data-format">\r\n    <div class="data-format-alone">\r\n        <div class="data-format-black">\r\n            <span>是否过滤空白行：</span>\r\n            <input type="checkbox"\r\n            ';
        if($data.dataFormat.value == "false" ){
        $out+='\r\n\r\n            ';
        }else{
        $out+='\r\n                checked="checked"\r\n            ';
        }
        $out+=' />\r\n        </div>\r\n    </div>\r\n</div>';
        return $out;
    }
    return { render: anonymous };
});