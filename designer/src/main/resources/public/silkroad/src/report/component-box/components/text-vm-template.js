define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$out='';$out+='<!--text区域-->\r\n<div class="comp-box di-o_o-line">\r\n    <div class="text-div div-write j-comp-div" tabindex="-1">\r\n        <div class="j-comp-report" id="">点击进行输入</div>\r\n    </div>\r\n    <input type="text" class="text-div hide j-comp-text" style="display: none;"/>\r\n</div>';
        return $out;
    }
    return { render: anonymous };
});