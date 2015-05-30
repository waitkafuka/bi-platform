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
        $out+='" placeholder="名称"/>\r\n    <select class="condition">\r\n        ';
        if(!$data.isMeasure){
        $out+='\r\n        <option value="EQ" ';
        if($data.sqlCondition=='EQ'){
        $out+='selected=selected ';
        }
        $out+='>等于</option>\r\n        <option value="NOT_EQ" ';
        if($data.sqlCondition=='NOT_EQ'){
        $out+='selected=selected ';
        }
        $out+='>不等于</option>\r\n        <option value="LT" ';
        if($data.sqlCondition=='LT'){
        $out+='selected=selected ';
        }
        $out+='>小于</option>\r\n        <option value="GT" ';
        if($data.sqlCondition=='GT'){
        $out+='selected=selected ';
        }
        $out+='>大于</option>\r\n        <option value="LT_EQ" ';
        if($data.sqlCondition=='LT_EQ'){
        $out+='selected=selected ';
        }
        $out+='>小于等于</option>\r\n        <option value="GT_EQ" ';
        if($data.sqlCondition=='GT_EQ'){
        $out+='selected=selected ';
        }
        $out+='>大于等于</option>\r\n        <option value="IN" ';
        if($data.sqlCondition=='IN'){
        $out+='selected=selected ';
        }
        $out+='>in</option>\r\n        <option value="BETWEEN_AND" ';
        if($data.sqlCondition=='BETWEEN_AND'){
        $out+='selected=selected ';
        }
        $out+='>between-and</option>\r\n        ';
        }else{
        $out+='\r\n        <option value="EQ" ';
        if($data.sqlCondition=='EQ'){
        $out+='selected=selected ';
        }
        $out+='>等于</option>\r\n        <option value="IN" ';
        if($data.sqlCondition=='IN'){
        $out+='selected=selected ';
        }
        $out+='>in</option>\r\n        ';
        }
        $out+='\r\n    </select>\r\n    <input type="text" class="default-value" value="';
        $out+=$escape($data.defaultValue);
        $out+='" placeholder="默认值"/>\r\n</div>';
        return $out;
    }
    return { render: anonymous };
});