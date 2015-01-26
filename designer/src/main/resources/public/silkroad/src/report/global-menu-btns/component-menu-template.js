define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$out='';$out+='<div class="global-menus component-menu j-all-menus" id="component">\n</div>\n<div class="global-menus skin-menu j-all-menus" id="skin-report">\n    <div class="skin-menu-box">\n        <div class=\'skin-type\'><div class="classic-skin skin-pic"></div><div class="skin-name">经典</div></div>\n        <div class=\'skin-type\' style="margin-right: 5px;"><div class="lightBlue-skin skin-pic"></div><div class="skin-name">商桥</div></div>\n    </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});