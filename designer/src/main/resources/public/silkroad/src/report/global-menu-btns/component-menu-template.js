define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$out='';$out+='<div class="global-menus component-menu j-all-menus" id="component">\n</div>\n<div class="global-menus skin-menu j-all-menus" id="skin-report">\n    <div class="skin-menu-box">\n        <div class=\'skin-type j-skin-btn\' id="di">\n            <div class="classic-skin skin-pic"></div>\n            <div class="skin-name">经典</div>\n        </div>\n        <div class=\'skin-type j-skin-btn\' id="bb">\n            <div class="lightBlue-skin skin-pic"></div>\n            <div class="skin-name">商桥</div>\n        </div>\n    </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});