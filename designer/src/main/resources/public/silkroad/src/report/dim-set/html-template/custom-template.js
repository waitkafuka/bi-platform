define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,dim=$data.dim,$cube=$data.$cube,i=$data.i,$escape=$utils.$escape,cubes=$data.cubes,$field=$data.$field,$index=$data.$index,j=$data.j,$out='';$out+='<div class="dim-container-custom hide">\n    <ul class="custom-column-names c-f">\n        <li class="custom-column-main-table"><span>主数据表</span></li>\n        <li class="custom-column-main-table-fields"><span>主表字段</span></li>\n        <li class="custom-column-create-dim"><span>关联数据表</span></li>\n    </ul>\n    <div class="custom-main j-custom-main">\n        ';
        $each(dim.custom,function($cube,i){
        $out+='\n        <div class="custom-main-box c-f j-custom-main-box">\n            <span class="cube-name" cubeId=';
        $out+=$escape($cube.cubeId);
        $out+=' title=';
        $out+=$escape(cubes[$cube.cubeId].name);
        $out+='>';
        $out+=$escape(cubes[$cube.cubeId].name);
        $out+='</span>\n            <span class="straight-line"></span>\n            <div class="custom-main-table-fields-box" bodyIndex=';
        $out+=$escape(i);
        $out+='>\n                    <ul>\n                        ';
        $each(cubes[$cube.cubeId].allFields,function($field,$index){
        $out+='\n                        <li class="j-custom-field" bodyIndex=';
        $out+=$escape(i);
        $out+='>';
        $out+=$escape($field.name);
        $out+='</li>\n                        ';
        });
        $out+='\n                    </ul>\n            </div>\n            <div class="custom-create-new-dim-container">\n                ';
        $each($cube.children,function($line,j){
        $out+='\n                <div class="custom-create-new-dim-box j-custom-relation-box">\n                    <div class="custom-create-new-dim-texts">\n                        <input type="text" placeholder="请输入新维度的名称" value="';
        $out+=$escape($line.dimName);
        $out+='" />\n                        <textarea placeholder="请输入创建维度的逻辑语句" class="j-custom-sql" bodyIndex=';
        $out+=$escape(i);
        $out+='>';
        $out+=$escape($line.sql);
        $out+='</textarea>\n                        <span class="';
        if($line.sql === ''){
        $out+='custom-create-new-dim-texts-wrong';
        }else{
        $out+='custom-create-new-dim-texts-right';
        }
        $out+='"></span>\n                    </div>\n                    <span class="delete j-custom-delete"></span>\n                    ';
        if(j === ($cube.children.length-1)){
        $out+='\n                    <span class="add j-custom-add"></span>\n                    ';
        }
        $out+='\n                </div>\n                ';
        });
        $out+='\n            </div>\n            <span class="custom-error-msg j-custom-error-msg hide"></span>\n        </div>\n        ';
        });
        $out+='\n    </div>\n    <span class="prompt mt-30">注：右下角的图标用于校验当前语句是否正确</span>\n</div>';
        return $out;
    }
    return { render: anonymous };
});