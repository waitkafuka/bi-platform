define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,dataSourcesList=$data.dataSourcesList,$item=$data.$item,index=$data.index,$escape=$utils.$escape,$out='';$out+='<div class="j-root-report-list">\n    <div class="con-common-line">\n        <div class="con-common-max-min">\n            <span class="btn-has-icon btn-has-icon-new c-p j-add-data-sources">新建数据源</span>\n        </div>\n    </div>\n    <div class="con-report-list con-common-max-min">\n        <table cellspacing="0">\n            <thead>\n            <tr>\n                <th class="report-index">序号</th>\n                <th class="report-name">数据源名称</th>\n                <th class="data-sources-btns">操作按钮</th>\n            </tr>\n            </thead>\n            <tbody>\n            ';
        $each(dataSourcesList,function($item,index){
        $out+='\n            <tr class="report-line j-root-line" data-id="';
        $out+=$escape($item.id);
        $out+='">\n                <td>\n                    ';
        $out+=$escape(index + 1);
        $out+='\n                </td>\n                <td>\n                    <a class="text c-p ellipsis" title="';
        $out+=$escape($item.name);
        $out+='">';
        $out+=$escape($item.name);
        $out+='</a>\n                </td>\n                <td>\n                    <span class="btn-has-icon btn-has-icon-edit c-p j-edit-data-sources">编辑</span>\n                    <span class="btn-has-icon btn-has-icon-delete c-p j-delete-data-sources">删除</span>\n                </td>\n            </tr>\n            ';
        });
        $out+='\n            </tbody>\n        </table>\n        ';
        if(dataSourcesList.length == 0){
        $out+='\n        <div class="empty-data ta-c">暂无数据</div>\n        ';
        }
        $out+='\n    </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});