define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,dim=$data.dim,$cube=$data.$cube,i=$data.i,$escape=$utils.$escape,cubes=$data.cubes,j=$data.j,$dim=$data.$dim,$index=$data.$index,$out='';$out+='<div class="dim-container-callback hide j-callback-main">\n    <ul class="callback-column-names c-f">\n        <li class="callback-column-names-main-table"><span>主数据表</span></li>\n        <li class="callback-column-names-setting"><span>配置区</span></li>\n    </ul>\n    ';
        $each(dim.callback,function($cube,i){
        $out+='\n    <div class="callback-main-box c-f j-callback-main-box" bodyIndex=';
        $out+=$escape(i);
        $out+='>\n        <span class="cube-name" cubeId=';
        $out+=$escape($cube.cubeId);
        $out+=' title=';
        $out+=$escape(cubes[$cube.cubeId].name);
        $out+='>';
        $out+=$escape(cubes[$cube.cubeId].name);
        $out+='</span>\n        <span class="callback-cube-open j-callback-cube-open"></span>\n        <div class="callback-relation-container c-f">\n            ';
        $each($cube.children,function($line,j){
        $out+='\n            <div class="callback-relation-box c-f j-callback-relation-box" bodyIndex=';
        $out+=$escape(j);
        $out+='>\n                ';
        if(j !==0 ){
        $out+='\n                <span class="callback-broken-line"></span>\n                ';
        }
        $out+='\n                <div class="callback-relation-content">\n                    <div class="first-part c-f">\n                        <span>选择回调字段：</span>\n                        <select>\n                            <option value="0">请选择</option>\n                            ';
        $each(cubes[$cube.cubeId].currDims,function($dim,$index){
        $out+='\n                            <option value=';
        $out+=$escape($dim.id);
        $out+='\n                            ';
        if($dim.id === $line.currDim){
        $out+='selected="selected"\n                            ';
        }
        $out+='>';
        $out+=$escape($dim.name);
        $out+='\n                            </option>\n                            ';
        });
        $out+='\n                        </select>\n                    </div>\n                    <div class="callback-relation-content-two-part c-f">\n                        <span class="callback-address-name">填写回调地址：</span>\n                        <input type="text" name="" id="" class="callback-address-input j-callback-address-input" value="';
        $out+=$escape($line.address);
        $out+='" />\n                        <span class="callback-address-prompt">\n                            例如：http://10.46.133.66:8999/pfplat/callbackmock.action\n                        </span>\n                    </div>\n                    <div class="callback-relation-content-three-part c-f">\n                        <span class="callback-cache-name">选取缓存类型：</span>\n                        <div>\n                            <input type="radio" value="1" class="callback-cache-type-right-input " name="callback-cache-body';
        $out+=$escape(i);
        $out+='-box';
        $out+=$escape(j);
        $out+='"\n                            ';
        if($line.refreshType === 1){
        $out+=' checked="checked" ';
        }
        $out+=' />\n                            <label class="callback-cache-type-right-label">\n                                无需缓存（数据量大，不推荐）\n                            </label>\n                        </div>\n                        <div>\n                            <input type="radio" value="2" class="callback-cache-type-right-input" name="callback-cache-body';
        $out+=$escape(i);
        $out+='-box';
        $out+=$escape(j);
        $out+='"\n                            ';
        if($line.refreshType === 2){
        $out+=' checked="checked" ';
        }
        $out+=' />\n                            <label class="callback-cache-type-right-label">\n                                在数据刷新后立刻刷新缓存\n                            </label>\n                        </div>\n                        <div class="ml-89">\n                            <input type="radio" value="3" class="callback-cache-type-right-input" name="callback-cache-body';
        $out+=$escape(i);
        $out+='-box';
        $out+=$escape(j);
        $out+='"\n                            ';
        if($line.refreshType === 3){
        $out+=' checked="checked" ';
        }
        $out+='/>\n                                <span class="callback-cache-type-right-label">间隔\n                                <input type="text" class="callback-cache-type-interval j-callback-cache-type-interval" value="';
        $out+=$escape($line.interval);
        $out+='" />\n                                秒刷新一次缓存</span>\n                        </div>\n                    </div>\n                </div>\n                <span class="delete j-callback-delete"></span>\n                ';
        if(j === ($cube.children.length-1)){
        $out+='\n                <span class="add j-callback-add"></span>\n                ';
        }
        $out+='\n            </div>\n            ';
        });
        $out+='\n        </div>\n        <span class="callback-error-msg j-callback-error-msg hide"></span>\n    </div>\n    ';
        });
        $out+='\n</div>\n\n';
        return $out;
    }
    return { render: anonymous };
});