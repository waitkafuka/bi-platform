define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,dimList=$data.dimList,$item=$data.$item,$index=$data.$index,$escape=$utils.$escape,$out='';$out+='<div class="j-global-box">\n    <img class="j-global-add" src="src/css/img/add.png"/>\n    <div class="j-con-global-attr">\n        <div class="j-global-attr">\n            <input type="text" class="parameter-name" placeholder="维度名称"/>\n            <select class="parameter-id">\n                ';
        $each(dimList,function($item,$index){
        $out+='\n                <option value="';
        $out+=$escape($item.id);
        $out+='">';
        $out+=$escape($item.caption);
        $out+='</option>\n                ';
        });
        $out+='\n            </select>\n            <input type="text" class="parameter-default" placeholder="默认值"/>\n            <input type="checkbox" class="parameter-needed"/>\n            <div class="j-global-close"></div>\n        </div>\n    </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});