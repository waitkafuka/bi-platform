define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$out='';$out+='<div class="global-menus component-menu j-all-menus" id="component">\r\n</div>';
        return $out;
    }
    return { render: anonymous };
});