define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,dim=$data.dim,$cube=$data.$cube,i=$data.i,$escape=$utils.$escape,cubes=$data.cubes,dateRelationTables=$data.dateRelationTables,$dateRelationTable=$data.$dateRelationTable,j=$data.j,$dim=$data.$dim,$index=$data.$index,defaultDate=$data.defaultDate,$level=$data.$level,$format=$data.$format,$field=$data.$field,$out='';$out+='<div class="dim-container-date hide j-date-main">\n    <ul class="date-column-names c-f">\n        <li class="date-column-names-main-table"><span>主数据表</span></li>\n        <li class="date-column-names-setting"><span>配置区</span></li>\n    </ul>\n    <!--循环cube列表（dim.normal）-->\n    ';
        $each(dim.date,function($cube,i){
        $out+='\n    <div class="date-main-box c-f j-date-main-box">\n        <span class="cube-name" cubeId=';
        $out+=$escape($cube.cubeId);
        $out+=' title=';
        $out+=$escape(cubes[$cube.cubeId].name);
        $out+='>';
        $out+=$escape(cubes[$cube.cubeId].name);
        $out+='</span>\n        <span class="straight-line"></span>\n        ';
        if($cube.children[0].relationTable === "0" || $cube.children[0].relationTable === "ownertable"){
        $out+='\n        <!--内置维度-->\n        <div class="date-relation-owner">\n            <div class="date-relation-owner-first-part c-f">\n                <span>选择被关联表：</span>\n                <select class="j-relation-table-select">\n                    <option value="0">请选择</option>\n                    <option value="ownertable"\n                    ';
        if($cube.children[0].relationTable==="ownertable"){
        $out+='\n                    selected = "selected"';
        }
        $out+='>内置表</option>\n                    ';
        $each(dateRelationTables,function($dateRelationTable,j){
        $out+='\n                    <option value=';
        $out+=$escape($dateRelationTable.id);
        $out+='>';
        $out+=$escape($dateRelationTable.name);
        $out+='</option>\n                    ';
        });
        $out+='\n                </select>\n            </div>\n            <div class="date-relation-owner-two-part c-f j-date-two-part">\n                <span>选择时间字段：</span>\n                <select>\n                    <option value="0">请选择</option>\n                    ';
        $each(cubes[$cube.cubeId].currDims,function($dim,$index){
        $out+='\n                    <option value=';
        $out+=$escape($dim.id);
        $out+='\n                    ';
        if($dim.id === $cube.children[0].currDim){
        $out+='selected="selected"\n                    ';
        }
        $out+='>';
        $out+=$escape($dim.name);
        $out+='\n                    </option>\n                    ';
        });
        $out+='\n                </select>\n                <span>粒度：</span>\n                <select class="j-owner-date-level-select">\n                    <option value="0">请选择</option>\n                    ';
        $each(defaultDate.level,function($level,$index){
        $out+='\n                    <option value=';
        $out+=$escape($level.id);
        $out+='\n                    ';
        if($level.id === $cube.children[0].field){
        $out+='selected="selected"\n                    ';
        }
        $out+='>';
        $out+=$escape($level.name);
        $out+='\n                    </option>\n                    ';
        });
        $out+='\n                </select>\n                <span>时间格式：</span>\n                <select class="j-owner-date-type-select">\n                    <option value="0">请选择</option>\n                    ';
        $each(defaultDate.level,function($level,$index){
        $out+='\n                    ';
        if($level.id === $cube.children[0].field){
        $out+='\n                    ';
        $each(defaultDate.dateFormatOptions[$cube.children[0].field],function($format,$index){
        $out+='\n                    <option value=';
        $out+=$escape($format);
        $out+='\n                    ';
        if($format === $cube.children[0].format){
        $out+='selected="selected"\n                    ';
        }
        $out+='>';
        $out+=$escape($format);
        $out+='</option>\n                    ';
        });
        $out+='\n                    ';
        }
        $out+='\n                    ';
        });
        $out+='\n                </select>\n            </div>\n        </div>\n        ';
        }else{
        $out+='\n        <!--普通维度-->\n        <div class="date-relation-normal">\n            <div class="first-part c-f">\n                <span>选择被关联表：</span>\n                <!--内置表为0-->\n                <select class="j-relation-table-select">\n                    <option value="0">请选择</option>\n                    <option value="ownertable"\n                    ';
        if($cube.children[0].relationTable==="ownertable"){
        $out+='\n                    selected = "selected"';
        }
        $out+='>内置表</option>\n                    ';
        $each(dateRelationTables,function($dateRelationTable,j){
        $out+='\n                    <option value=';
        $out+=$escape($dateRelationTable.id);
        $out+='\n                    ';
        if($dateRelationTable.id === $cube.children[0].relationTable){
        $out+='\n                    selected = "selected"\n                    ';
        }
        $out+='>';
        $out+=$escape($dateRelationTable.name);
        $out+='</option>\n                    ';
        });
        $out+='\n                </select>\n            </div>\n            <div class="date-relation-normal-two-part c-f j-date-two-part">\n                <span>指定关联字段：</span>\n                <select>\n                    <option value="0">请选择</option>\n                    ';
        $each(cubes[$cube.cubeId].currDims,function($dim,$index){
        $out+='\n                    <option value=';
        $out+=$escape($dim.id);
        $out+='\n                    ';
        if($dim.id === $cube.children[0].currDim){
        $out+='selected="selected"\n                    ';
        }
        $out+='>';
        $out+=$escape($dim.name);
        $out+='\n                    </option>\n                    ';
        });
        $out+='\n                </select>\n                <span class="equal">=</span>\n\n                <select>\n                    <option value="0">请选择</option>\n                    ';
        $each(dateRelationTables,function($dateRelationTable,$index){
        $out+='\n                    ';
        if($dateRelationTable.id === $cube.children[0].relationTable){
        $out+='\n                    ';
        $each($dateRelationTable.fields,function($field,$index){
        $out+='\n                    <option value=';
        $out+=$escape($field.id);
        $out+='\n                    ';
        if($field.id === $cube.children[0].field){
        $out+='selected="selected"\n                    ';
        }
        $out+='>';
        $out+=$escape($field.name);
        $out+='\n                    </option>\n                    ';
        });
        $out+='\n                    ';
        }
        $out+='\n                    ';
        });
        $out+='\n                </select>\n            </div>\n            <!--需要去后台获取-->\n            <div class="date-relation-normal-three-part j-date-three-part">\n                <span class="date-relation-normal-three-part-name">日期格式：</span>\n                <div class="date-relation-normal-three-part-box c-f">\n                    ';
        $each(dateRelationTables,function($dateRelationTable,$index){
        $out+='\n                    ';
        if($dateRelationTable.id === $cube.children[0].relationTable){
        $out+='\n                    ';
        $each($dateRelationTable.fields,function($field,$index){
        $out+='\n                    <div class="date-relation-normal-three-part-box-date-format c-f">\n                        <span>';
        $out+=$escape($field.name);
        $out+='</span>\n                        <select>\n                            <option value="0">请选择</option>\n                            ';
        $each($dateRelationTable.dateFormatOptions[$field.id],function($format,$index){
        $out+='\n                            <option value="';
        $out+=$escape($format);
        $out+='" ';
        if($format === $cube.children[0].dateLevel[$field.id]){
        $out+=' selected="selected" ';
        }
        $out+='>';
        $out+=$escape($format);
        $out+='\n                            </option>\n                            ';
        });
        $out+='\n                        </select>\n                    </div>\n                    ';
        });
        $out+='\n                    ';
        }
        $out+='\n                    ';
        });
        $out+='\n                </div>\n            </div>\n        </div>\n        ';
        }
        $out+='\n        <span class="date-error-msg j-date-error-msg hide"></span>\n    </div>\n   ';
        });
        $out+='\n</div>\n\n';
        return $out;
    }
    return { render: anonymous };
});