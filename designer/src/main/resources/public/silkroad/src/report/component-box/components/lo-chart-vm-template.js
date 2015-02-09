define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$escape=$utils.$escape,id=$data.id,$out='';$out+='<!-- liteolap 图 -->\n<div data-o_o-di="';
        $out+=$escape(id);
        $out+='.cnpt-liteolapchart-meta">\n    <div class="di-o_o-line">\n        <span class="di-o_o-item">选择指标：</span>\n        <span class="di-o_o-item" data-o_o-di="';
        $out+=$escape(id);
        $out+='.vu-liteolapchart-meta">\n        </span>\n    </div>\n</div>\n<div class="di-o_o-block" data-o_o-di="';
        $out+=$escape(id);
        $out+='.cnpt-liteolapchart">\n    <div data-o_o-di="';
        $out+=$escape(id);
        $out+='.vu-liteolapchart" style="height: 260px">\n    </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});