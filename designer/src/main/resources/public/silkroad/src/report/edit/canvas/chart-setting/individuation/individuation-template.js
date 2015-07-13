define(['template'], function (template) {
    function anonymous($data,$filename
        /**/) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,appearance=$data.appearance,$out='';$out+='<!--\n数据例子：\nvar demoData = {\n    indList: {\n        click: {\n            caption: \'\',\n            axisName: \'\'\n        }\n    }\n};\n-->\n<!-- 指标颜色设置 -->\n<div class="dialog-content">\n    <div class="base-setting-box c-f j-appearance-setting">\n        <span class="f-l">外观设置</span>\n        <div class="base-setting-item f-l j-appearance-item">\n            <input type="checkbox" class="f-l c-p" name="isShowInds" ';
        if(appearance.isShowInds==true){
        $out+=' checked="checked" ';
        }
        $out+=' />\n            <label class="f-l ml-2 mt-1">是否显示指标区域</label>\n        </div>\n    </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});