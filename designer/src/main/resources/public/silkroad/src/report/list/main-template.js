define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,reportList=$data.reportList,$item=$data.$item,index=$data.index,$escape=$utils.$escape,$out='';$out+='<div class="j-root-report-list">\n    <div class="con-common-line">\n        <div class="con-common-max-min">\n            <span class="btn-has-icon btn-has-icon-new c-p j-add-report">新建报表</span>\n        </div>\n    </div>\n    <div class="con-report-list con-common-max-min">\n        <table cellspacing="0">\n            <thead>\n            <tr>\n                <th class="report-index">序号</th>\n                <th class="report-name">报表名称</th>\n                <th class="report-btns">操作按钮</th>\n            </tr>\n            </thead>\n            <tbody>\n            ';
        $each(reportList,function($item,index){
        $out+='\n            <tr class="report-line j-root-line" data-id="';
        $out+=$escape($item.id);
        $out+='" data-theme="';
        $out+=$escape($item.theme);
        $out+='">\n                <td>';
        $out+=$escape(index + 1);
        $out+='</td>\n                <td><a class="text c-p ellipsis j-show-report" title="点击预览">';
        $out+=$escape($item.name);
        $out+='</a>\n                </td>\n                <td>\n                    <span class="btn-has-icon btn-has-icon-copy c-p j-copy-report">创建副本</span>\n                    <span class="btn-has-icon btn-has-icon-info c-p j-info-report j-show-publish-info"\n                              title="报表的发布信息">查看发布信息</span>\n                    <span class="btn-has-icon btn-has-icon-edit c-p j-edit-report">编辑</span>\n                    <span class="btn-has-icon btn-has-icon-delete c-p j-delete-report">删除</span>\n                </td>\n            </tr>\n            ';
        });
        $out+='\n            </tbody>\n        </table>\n        ';
        if(reportList.length == 0){
        $out+='\n        <div class="empty-data ta-c">暂无数据</div>\n        ';
        }
        $out+='\n    </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});