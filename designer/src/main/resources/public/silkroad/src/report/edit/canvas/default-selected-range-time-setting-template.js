define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$escape=$utils.$escape,item=$data.item,$out='';$out+='<div class="con-tab">\n    <!--<span class="item">静止时间</span>-->\n    <span class="item">动态时间</span>\n</div>\n<div class="con-tab-content">\n\n        <div class="item j-item" data-type="D">\n            <div class="title">开始时间日粒度设置</div>\n            <div class="content">\n                <input type="text" name="startDateSetting" value="';
        $out+=$escape(item.start);
        $out+='"/>\n                <select>\n                    <option value="D">日</option>\n                </select>\n            </div>\n            <div class="title">结束时间日粒度设置</div>\n            <div class="content">\n                <input type="text" name="endDateSetting" value="';
        $out+=$escape(item.end);
        $out+='"/>\n                <select>\n                    <option value="D">日</option>\n                </select>\n            </div>\n        </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});