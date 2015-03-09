define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,dim=$data.dim,$cube=$data.$cube,i=$data.i,$escape=$utils.$escape,cubes=$data.cubes,j=$data.j,$dim=$data.$dim,$index=$data.$index,relationTables=$data.relationTables,$relationTable=$data.$relationTable,$field=$data.$field,$out='';$out+='<div class="dim-container-normal">\n    <ul class="normal-column-names c-f">\n        <li><span>主数据表</span></li>\n        <li><span>主表字段</span></li>\n        <li><span>关联数据表</span></li>\n        <li><span>关联表字段</span></li>\n    </ul>\n    <div class="normal-main j-normal-main">\n        <!--循环cube列表（dim.normal）-->\n        ';
        $each(dim.normal,function($cube,i){
        $out+='\n        <div class="normal-main-box c-f j-normal-main-box">\n            <span class="cube-name" cubeId =';
        $out+=$escape($cube.cubeId);
        $out+=' title=';
        $out+=$escape(cubes[$cube.cubeId].name);
        $out+='>';
        $out+=$escape(cubes[$cube.cubeId].name);
        $out+='</span>\n            <span class="normal-cube-open j-normal-cube-open"></span>\n            <div class="normal-relation-container j-normal-relation-container c-f">\n                <!--循环cube中的行（dim.normal.children）-->\n                ';
        $each($cube.children,function($line,j){
        $out+='\n                <div class="normal-relation-box j-normal-relation-box">\n                    ';
        if(j !==0 ){
        $out+='\n                        <span class="normal-broken-line"></span>\n                    ';
        }
        $out+='\n                    <select class="normal-relation-box-select-fields mr-20">\n                        <option value="0">请选择</option>\n                        <!-- 循环每一行中的主表字段(cubes.cube1.currDims)-->\n                        ';
        $each(cubes[$cube.cubeId].currDims,function($dim,$index){
        $out+='\n                        <option value=';
        $out+=$escape($dim.id);
        $out+='\n                            ';
        if($dim.id === $line.currDim){
        $out+='selected="selected"\n                            ';
        }
        $out+='>';
        $out+=$escape($dim.name);
        $out+='\n                        </option>\n                        ';
        });
        $out+='\n                    </select>\n                    <span class="equal">=</span>\n                    <select class="normal-relation-box-select-table mr-30 j-normal-relation-table-select" >\n                        <option value="0">请选择</option>\n                        <!-- 循环关联数据表(relationTables)-->\n                        ';
        $each(relationTables,function($relationTable,$index){
        $out+='\n                        <option value=';
        $out+=$escape($relationTable.id);
        $out+='\n                        ';
        if($relationTable.id === $line.relationTable){
        $out+='selected="selected"\n                        ';
        }
        $out+='>';
        $out+=$escape($relationTable.name);
        $out+='\n                        </option>\n                        ';
        });
        $out+='\n                    </select>\n                    <select class="normal-relation-box-select-fields mr-10">\n                        <option value="0">请选择</option>\n                        <!-- 循环关联数据表(relationTables)-->\n                        ';
        $each(relationTables,function($relationTable,$index){
        $out+='\n                        <!-- 如果关联数据表等于当前行的的关联表,那么就循环此关联表中的字段-->\n                        ';
        if($relationTable.id === $line.relationTable){
        $out+='\n                            ';
        $each($relationTable.fields,function($field,$index){
        $out+='\n                                <option value=';
        $out+=$escape($field.id);
        $out+='\n                                ';
        if($field.id === $line.field){
        $out+='selected="selected"\n                                ';
        }
        $out+='>';
        $out+=$escape($field.name);
        $out+='\n                                </option>\n                            ';
        });
        $out+='\n                        ';
        }
        $out+='\n                        ';
        });
        $out+='\n                    </select>\n                    <span class="delete j-normal-delete"></span>\n                    ';
        if($cube.children.length === (j+1)){
        $out+='\n                        <span class="add j-normal-add"></span>\n                    ';
        }
        $out+='\n                </div>\n\n                ';
        });
        $out+='\n            </div>\n            <span class="normal-error-msg j-normal-error-msg hide"></span>\n        </div>\n        ';
        });
        $out+='\n    </div>\n    <span class="prompt mt-30">注：建立关联后，默认将关联表的所有字段全部取出</span>\n</div>';
        return $out;
    }
    return { render: anonymous };
});