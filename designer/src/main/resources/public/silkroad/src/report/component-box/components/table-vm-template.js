define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$escape=$utils.$escape,id=$data.id,$out='';$out+='<!--table区域-->\n<div class="comp-box di-o_o-block" data-o_o-di="';
        $out+=$escape(id);
        $out+='">\n    <div class="di-o_o-line">\n        <div class="" data-o_o-di="';
        $out+=$escape(id);
        $out+='-vu-table-breadcrumb"></div>\n    </div>\n    <div class="di-o_o-line">\n        <div class="vu-table" data-o_o-di="';
        $out+=$escape(id);
        $out+='-vu-table" style="height: 160px;"></div>\n    </div>\n    <div class="di-o_o-line">\n        <div class="di-table-prompt">\n            <div class="di-table-count" data-o_o-di="';
        $out+=$escape(id);
        $out+='-vu-table-count">\n                符合查询条件的数据共\n                <span class="di-table-count-num">#{currRecordCount}</span>条。\n            </div>\n            <div class="di-table-down">\n                <div class="di-table-down-text">导出当前所有数据</div>\n                <div class="di-o_o-line di-table-down-btn" title="下载全量数据" style="display: inline-block; width: 30px;">\n                    <div class="di-o_o-item" data-o_o-di="';
        $out+=$escape(id);
        $out+='-vu-table-download" style="width: 30px; height: 18px;"></div>\n                </div>\n            </div>\n        </div>\n    </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});