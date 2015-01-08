define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,componentList=$data.componentList,group=$data.group,$index=$data.$index,$escape=$utils.$escape,item=$data.item,$out='';$out+='<div class="con-component-box j-con-component-box">\n\n    <!--<select id="component-group-selector">-->\n        <!--';
        $each(componentList,function(group,$index){
        $out+='-->\n        <!--<option value="';
        $out+=$escape(group.id);
        $out+='">';
        $out+=$escape(group.caption);
        $out+='</option>-->\n        <!--';
        });
        $out+='-->\n    <!--</select>-->\n\n    ';
        $each(componentList,function(group,$index){
        $out+='\n    <div class="con-component j-con-component" data-group-id="';
        $out+=$escape(group.id);
        $out+='">\n        ';
        $each(group.items,function(item,$index){
        $out+='\n        <div class="component-item ';
        $out+=$escape(item.iconClass);
        $out+=' j-component-item" data-component-type="';
        $out+=$escape(item.type);
        $out+='" data-mold="';
        $out+=$escape(item.mold);
        $out+='">\n            <img src="src/css/img/global-menu/comp-menu-';
        $out+=$escape(item.iconClass);
        $out+='.png"/>\n            ';
        $out+=$escape(item.caption);
        $out+='\n        </div>\n        ';
        });
        $out+='\n    </div>\n    ';
        });
        $out+='\n</div>';
        return $out;
    }
    return { render: anonymous };
});