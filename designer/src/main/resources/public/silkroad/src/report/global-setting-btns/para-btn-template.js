define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$out='';$out+='<div class="j-global-box">\r\n    <img class="j-global-add" src="src/css/img/add.png"/>\r\n    <div class="hide j-con-global-attr">\r\n        <div class="j-global-attr">\r\n            <input type="text" placeholder="维度名称"/>\r\n            <select>\r\n                <option>时间</option>\r\n                <option>地点</option>\r\n                <option>人物</option>\r\n            </select>\r\n            <div class="j-global-close"></div>\r\n        </div>\r\n    </div>\r\n</div>';
        return $out;
    }
    return { render: anonymous };
});