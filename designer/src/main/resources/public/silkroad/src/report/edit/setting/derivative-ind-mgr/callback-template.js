define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$out='';$out+='<div class="callback-form-box">\r\n    <div class="callback-form">\r\n        <div class="callback-name">\r\n            <div>回调指标名称:</div>\r\n            <div>显示名称:</div>\r\n            <div>回调地址:</div>\r\n            <div>请求超时时间:</div>\r\n        </div>\r\n        <div class="callback-text">\r\n            <input class="call-name" placeholder="由数字,字母,汉字组成"/>\r\n            <input class="call-caption" placeholder="由数字,字母,下划线组成并以数字开头"/>\r\n            <input class="call-url" placeholder="回调地址"/>\r\n            <input class="call-timeout" placeholder="有数字组成"/>\r\n        </div>\r\n    </div>\r\n    <div class="close-retractable callback-close" title="删除">×</div>\r\n    <div class="close-retractable callback-retractable " title="收进">-</div>\r\n    <div class="callback-title"><div></div></div>\r\n</div>';
        return $out;
    }
    return { render: anonymous };
});