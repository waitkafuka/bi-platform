define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,factTables=$data.factTables,$item=$data.$item,$index=$data.$index,$escape=$utils.$escape,prefixs=$data.prefixs,index=$data.index,$out='';$out+='<div class="title fs-14">请选择要使用的事实表（可多选）</div>\n<ul>\n    ';
        $each(factTables,function($item,$index){
        $out+='\n    <li class="data-line c-p';
        if($item.selected){
        $out+=' selected';
        }
        $out+=' j-item" data-id="';
        $out+=$escape($item.id);
        $out+='">';
        $out+=$escape($item.name);
        $out+='</li>\n    ';
        });
        $out+='\n</ul>\n<div class="con-set-group j-root-set-group">\n    <span class="btn-has-icon btn-has-icon-info c-p j-set-group">添加分表匹配规则</span>\n    ';
        $each(prefixs,function($item,index){
        $out+='\n    <div class="form-common-line j-item">\n        <div class="form-common-text form-common-text-big">\n            <input type="text" class="" placeholder="分表匹配规则" value="';
        $out+=$escape($item);
        $out+='"/>\n            <span class="form-common-text-validation hide"></span>\n            <span class="form-common-btn-extend form-common-btn-extend-absolute j-delete" title="删除">×</span>\n        </div>\n    </div>\n    ';
        });
        $out+='\n    <div class="form-common-line hide j-template">\n        <div class="form-common-text form-common-text-big">\n            <input type="text" class="" placeholder="分表匹配规则"/>\n            <span class="form-common-text-validation hide"></span>\n            <span class="form-common-btn-extend form-common-btn-extend-absolute j-delete" title="删除">×</span>\n        </div>\n    </div>\n</div>\n\n';
        return $out;
    }
    return { render: anonymous };
});