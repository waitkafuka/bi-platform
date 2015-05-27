define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$escape=$utils.$escape,$out='';$out+='<!--\r\n数据例子：\r\nvar demoData = {\r\n    type: \'\',\r\n    name: \'\',\r\n    defaultValue: \'\',\r\n    sqlContidion: \'\'\r\n};\r\n-->\r\n<div class="silkroad-data-field-filter-set">\r\n    <span class="field-id" data-id="';
        $out+=$escape($data.id);
        $out+='" title="';
        $out+=$escape($data.text);
        $out+='">';
        $out+=$escape($data.text);
        $out+='</span>\r\n    <input class="field-name" value="';
        $out+=$escape($data.name);
        $out+='" placeholder="名称"/>\r\n    <select class="condition">\r\n        <option value="=" ';
        if($data.sqlCondition=='='){
        $out+='selected=selected ';
        }
        $out+='> = </option>\r\n        <option value="<>" ';
        if($data.sqlCondition=='<>'){
        $out+='selected=selected ';
        }
        $out+='> <> </option>\r\n        <option value="<=" ';
        if($data.sqlCondition=='<='){
        $out+='selected=selected ';
        }
        $out+='> <= </option>\r\n        <option value="<" ';
        if($data.sqlCondition=='<'){
        $out+='selected=selected ';
        }
        $out+='> < </option>\r\n        <option value=">" ';
        if($data.sqlCondition=='>'){
        $out+='selected=selected ';
        }
        $out+='> > </option>\r\n        <option value="in" ';
        if($data.sqlCondition=='in'){
        $out+='selected=selected ';
        }
        $out+='> in </option>\r\n        <option value="between and" ';
        if($data.sqlCondition=='between and'){
        $out+='selected=selected ';
        }
        $out+='> between and </option>\r\n    </select>\r\n    <input type="text" class="default-value" value="';
        $out+=$escape($data.defaultValue);
        $out+='" placeholder="默认值"/>\r\n</div>';
        return $out;
    }
    return { render: anonymous };
});