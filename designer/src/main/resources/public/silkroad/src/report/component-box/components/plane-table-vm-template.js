define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$escape=$utils.$escape,id=$data.id,$out='';$out+='<!--table区域-->\r\n<div class="comp-box di-o_o-block" data-o_o-di="';
        $out+=$escape(id);
        $out+='">\r\n    <div class="di-o_o-line">\r\n        <div id="';
        $out+=$escape(id);
        $out+='-exhibition" class="ui-table-fieldset-exhibition"></div>\r\n    </div>\r\n    <div class="di-o_o-line">\r\n        <div data-o_o-di="';
        $out+=$escape(id);
        $out+='-vu-table-fieldsFilter"></div>\r\n        <div class="di-o_o-item" data-o_o-di="';
        $out+=$escape(id);
        $out+='-vu-table-download" style="display: inline-block;float: right;"></div>\r\n    </div>\r\n    <div class="di-o_o-line">\r\n        <div class="vu-plane-table" data-o_o-di="';
        $out+=$escape(id);
        $out+='-vu-table"></div>\r\n    </div>\r\n    <div class="di-o_o-line">\r\n        <div class="" data-o_o-di="';
        $out+=$escape(id);
        $out+='-vu-table-pager"></div>\r\n    </div>\r\n\r\n</div>';
        return $out;
    }
    return { render: anonymous };
});