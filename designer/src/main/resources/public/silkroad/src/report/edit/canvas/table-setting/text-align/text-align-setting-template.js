define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,indList=$data.indList,$ind=$data.$ind,name=$data.name,$escape=$utils.$escape,options=$data.options,$option=$data.$option,optionKey=$data.optionKey,dimList=$data.dimList,$dim=$data.$dim,$out='';$out+='<!--\r\n数据例子：\r\nvar demoData = {\r\n    options: {\r\n        \'left\': \'居左\'，\r\n        \'center\': \'居中\'，\r\n        \'right\': \'居右\'\r\n    },\r\n    indList: {\r\n        click: {\r\n            caption: \'\',\r\n            align: \'\'\r\n        }\r\n    },\r\n    dimList: {\r\n        click: {\r\n            caption: \'\',\r\n            align: \'\'\r\n        }\r\n   }\r\n};\r\n-->\r\n<!-- 指标颜色设置 -->\r\n<div class="text-align-set">\r\n    <div class="text-align-set-area">\r\n        <label>对各指标文本进行单独设置</label>\r\n        ';
        $each(indList,function($ind,name){
        $out+='\r\n        <div class="text-align-set-item">\r\n            <label>';
        $out+=$escape($ind.caption);
        $out+='：</label>\r\n            <select name="';
        $out+=$escape(name);
        $out+='">\r\n                ';
        $each(options,function($option,optionKey){
        $out+='\r\n                <option value=';
        $out+=$escape(optionKey);
        $out+='\r\n                ';
        if($ind.align && optionKey === $ind.align){
        $out+=' selected="selected"\r\n                ';
        }
        $out+='>';
        $out+=$escape($option);
        $out+='</option>\r\n                ';
        });
        $out+='\r\n            </select>\r\n        </div>\r\n        ';
        });
        $out+='\r\n    </div>\r\n    <!--<div class="text-align-set-area">-->\r\n        <!--<label>对各维度文本进行单独设置</label>-->\r\n        <!--';
        $each(dimList,function($dim,name){
        $out+='-->\r\n        <!--<div class="text-align-set-item">-->\r\n            <!--<label>';
        $out+=$escape($dim.caption);
        $out+='：</label>-->\r\n            <!--<select name="';
        $out+=$escape(name);
        $out+='">-->\r\n                <!--';
        $each(options,function($option,optionKey){
        $out+='-->\r\n                <!--<option value=';
        $out+=$escape(optionKey);
        $out+='-->\r\n                <!--';
        if($dim.align && optionKey === $dim.align){
        $out+=' selected="selected"-->\r\n                <!--';
        }
        $out+='>';
        $out+=$escape($option);
        $out+='</option>-->\r\n                <!--';
        });
        $out+='-->\r\n            <!--</select>-->\r\n        <!--</div>-->\r\n        <!--';
        });
        $out+='-->\r\n    <!--</div>-->\r\n</div>\r\n';
        return $out;
    }
    return { render: anonymous };
});