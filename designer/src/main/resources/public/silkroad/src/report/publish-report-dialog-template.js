define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$escape=$utils.$escape,url=$data.url,type=$data.type,$out='';$out+='<div class="con-releaseBox">\n    <div class="con-head">\n        <a href="';
        $out+=$escape(url);
        $out+='" target="_blank"><div class="con-hRead"></div></a>\n        ';
        if(type == 'POST'){
        $out+='\n        <a href="javascript:;"><div class="con-hReturn j-report-list"></div></a>\n        ';
        }else{
        $out+='\n        <a href="javascript:;"><div class="con-hEdit j-report-edit"></div></a>\n        ';
        }
        $out+='\n    </div>\n    <div class="con-body">\n        <div class="con-report-url">\n            <ul class="con-url">\n                <li>你可以将下面的报表地址分享给好友，或者通过邮件的方式发送出去</li>\n                <li id="copyUrlBtnCopyContent" class="con-url-text con-br">';
        $out+=$escape(url);
        $out+='</li>\n            </ul>\n            <ul class="con-url-a" id="copyUrlBtnContainer">\n                <li id="copyUrlBtn"><a><span class="conspan">复制URL地址</span></a></li>\n                <li><a href="';
        $out+=$escape(url);
        $out+='" target="_blank"><span class="conspan">新窗口浏览</span></a></li>\n            </ul>\n        </div>\n        <div class="con-tiled">\n            <ul class="con-report">\n                <li class="con-report-title">平铺式报表</li>\n                <li>将下面的代码加入到你的网页HTML代码中，让用户在你的网页上看到报表</li>\n                <li id="copyTiledBtnCopyContent" class="con-report-text con-br">\n                    &lt;iframe height="600" allowTransparency="true"\n                    style="width:100%;border:none;overflow:auto;"\n                    frameborder="0"\n                    src="';
        $out+=$escape(url);
        $out+='"&gt;&lt;/iframe&gt;\n                </li>\n            </ul>\n            <ul id="copyTiledBtnContainer" class="con-copy">\n                <li id="copyTiledBtn"><a href="javascript:;"><span class="conspan">复制代码</span></a></li>\n            </ul>\n        </div>\n        <div class="con-embedded">\n            <ul class="con-report">\n                <li class="con-report-title">嵌入式报表</li>\n                <li>下面的代码，能够适应各业务系统的个性化布局展现，可以实现报表的自适应调整</li>\n                <li id="copyEmbeddedBtnCopyContent" class="con-report-text con-br">\n                    &lt;iframe height="600" allowTransparency="true"\n                    style="width:100%;border:none;overflow:auto;" frameborder="0"\n                    src="';
        $out+=$escape(url);
        $out+='"&gt;&lt;/iframe&gt;\n                </li>\n            </ul>\n            <ul id="copyEmbeddedBtnContainer" class="con-copy">\n                <li id="copyEmbeddedBtn"><a href="javascript:;"><span class="conspan">复制代码</span></a></li>\n            </ul>\n        </div>\n    </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});