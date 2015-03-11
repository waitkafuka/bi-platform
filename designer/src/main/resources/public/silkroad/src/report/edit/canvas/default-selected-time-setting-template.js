define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,list=$data.list,item=$data.item,$index=$data.$index,$escape=$utils.$escape,$out='';$out+='<div class="con-tab">\n    <!--<span class="item">静止时间</span>-->\n    <span class="item">动态时间</span>\n</div>\n<div class="con-tab-content">\n    ';
        $each(list,function(item,$index){
        $out+='\n    ';
        if(item.type == 'D'){
        $out+='\n    <div class="item j-item" data-type="D">\n        <div class="title">日粒度设置</div>\n        <div class="content">\n            <input type="text" name="singleDateSetting" value="';
        $out+=$escape(item.defaultSelectedVal);
        $out+='"/>\n            <select>\n                <option value="D">日</option>\n            </select>\n        </div>\n    </div>\n    ';
        }
        $out+='\n\n    ';
        if(item.type == 'W'){
        $out+='\n    <div class="item j-item" data-type="W">\n        <div class="title">周粒度设置</div>\n        <div class="content">\n            <input type="text" name="singleDateSetting" value="';
        $out+=$escape(item.defaultSelectedVal);
        $out+='"/>\n            <select>\n                <option value="D"';
        if(item.defaultSelectedUnit == 'D'){
        $out+=' selected';
        }
        $out+='>日</option>\n                <option value="W"';
        if(item.defaultSelectedUnit == 'W'){
        $out+=' selected';
        }
        $out+='>周</option>\n            </select>\n        </div>\n    </div>\n    ';
        }
        $out+='\n\n    ';
        if(item.type == 'M'){
        $out+='\n    <div class="item j-item" data-type="M">\n        <div class="title">月粒度设置</div>\n        <div class="content">\n            <input type="text" name="singleDateSetting" value="-1"/>\n            <select>\n                <option value="D"';
        if(item.defaultSelectedUnit == 'D'){
        $out+=' selected';
        }
        $out+='>日</option>\n                <option value="W"';
        if(item.defaultSelectedUnit == 'W'){
        $out+=' selected';
        }
        $out+='>周</option>\n                <option value="M"';
        if(item.defaultSelectedUnit == 'M'){
        $out+=' selected';
        }
        $out+='>月</option>\n            </select>\n        </div>\n    </div>\n    ';
        }
        $out+='\n\n    ';
        if(item.type == 'Q'){
        $out+='\n    <div class="item j-item" data-type="Q">\n        <div class="title">季粒度设置</div>\n        <div class="content">\n            <input type="text" name="singleDateSetting" value="-1"/>\n            <select>\n                <option value="D"';
        if(item.defaultSelectedUnit == 'D'){
        $out+=' selected';
        }
        $out+='>日</option>\n                <option value="W"';
        if(item.defaultSelectedUnit == 'W'){
        $out+=' selected';
        }
        $out+='>周</option>\n                <option value="M"';
        if(item.defaultSelectedUnit == 'M'){
        $out+=' selected';
        }
        $out+='>月</option>\n                <option value="Q"';
        if(item.defaultSelectedUnit == 'Q'){
        $out+=' selected';
        }
        $out+='>季</option>\n            </select>\n        </div>\n    </div>\n    ';
        }
        $out+='\n\n    ';
        if(item.type == 'Y'){
        $out+='\n    <div class="item j-item" data-type="Y">\n        <div class="title">季粒度设置</div>\n        <div class="content">\n            <input type="text" name="singleDateSetting" value="-1"/>\n            <select>\n                <option value="D"';
        if(item.defaultSelectedUnit == 'D'){
        $out+=' selected';
        }
        $out+='>日</option>\n                <option value="W"';
        if(item.defaultSelectedUnit == 'W'){
        $out+=' selected';
        }
        $out+='>周</option>\n                <option value="M"';
        if(item.defaultSelectedUnit == 'M'){
        $out+=' selected';
        }
        $out+='>月</option>\n                <option value="Q"';
        if(item.defaultSelectedUnit == 'Q'){
        $out+=' selected';
        }
        $out+='>季</option>\n                <option value="Y"';
        if(item.defaultSelectedUnit == 'Y'){
        $out+=' selected';
        }
        $out+='>年</option>\n            </select>\n        </div>\n    </div>\n    ';
        }
        $out+='\n    ';
        });
        $out+='\n</div>';
        return $out;
    }
    return { render: anonymous };
});