define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$escape=$utils.$escape,id=$data.id,$out='';$out+='<!--拖拽区域-->\n<div data-o_o-di="';
        $out+=$escape(id);
        $out+='.vctnr-fold">\n</div>\n<div class="di-o_o-line">\n    <div class="di-o_o-item" data-o_o-di="';
        $out+=$escape(id);
        $out+='.vpt-fold-ctrlbtn">\n    </div>\n</div>\n<div data-o_o-di="';
        $out+=$escape(id);
        $out+='.vpt-fold-body">\n    <div class="ka-block-table-meta" data-o_o-di="';
        $out+=$escape(id);
        $out+='.cnpt-table-meta">\n        <div class="ka-table-meta" data-o_o-di="';
        $out+=$escape(id);
        $out+='.vu-table-meta">\n        </div>\n    </div>\n</div>\n';
        return $out;
    }
    return { render: anonymous };
});