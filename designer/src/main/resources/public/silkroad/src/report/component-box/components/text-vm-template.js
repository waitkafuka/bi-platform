define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$out='';$out+='<!--text区域-->\r\n<div class="comp-box di-o_o-line">\r\n    <div class="comp-write div-write" id="comp-div" tabindex="-1">\r\n        <div id="comp-report">点击进行输入</div>\r\n    </div>\r\n    <input type="text" class="comp-write hide" id="comp-text"/>\r\n</div>';
        return $out;
    }
    return { render: anonymous };
});