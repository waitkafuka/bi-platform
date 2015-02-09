define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,$item=$data.$item,index=$data.index,$escape=$utils.$escape,$out='';$out+='<ul class="comp-setting-charticons">\n    ';
        $each($data,function($item,index){
        $out+='\n        ';
        if($item === false){
        $out+='\n            <li><span class="icon ';
        $out+=$escape(index);
        $out+='" chart-type="';
        $out+=$escape(index);
        $out+='"></span></li>\n        ';
        }else{
        $out+='\n            <li><span class="icon ';
        $out+=$escape(index);
        $out+=' ';
        $out+=$escape(index);
        $out+='-focus" chart-type="';
        $out+=$escape(index);
        $out+='"></span></li>\n        ';
        }
        $out+='\n    ';
        });
        $out+='\n</ul>';
        return $out;
    }
    return { render: anonymous };
});