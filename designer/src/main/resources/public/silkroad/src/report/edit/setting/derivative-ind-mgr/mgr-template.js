define(['template'], function (template) {
    function anonymous($data,$filename) {
        'use strict';
        $data=$data||{};
        var $utils=template.utils,$helpers=$utils.$helpers,$each=$utils.$each,indList=$data.indList,$value=$data.$value,$index=$data.$index,$escape=$utils.$escape,hasDerive=$data.hasDerive,$out='';$out+='<div class="data-sources-derive-inds c-f">\n    <ul class="data-sources-derive-inds-tab">\n        <li class="classification classification-focus j-classification f-l" id="j-tab-create"><span>创建计算列</span></li>\n        <li class="classification j-classification f-l" id="j-tab-select"><span>快速选择计算列</span></li>\n        <li class="classification j-classification f-l" id="j-tab-callback"><span>回调指标</span></li>\n    </ul>\n\n    <div class="norm-box" id="j-box-norm">\n        <div class="description">\n            <div class="description-create">\n                点击左侧指标，可进入右侧区域，参与计算；当前支持的运算包括+、-、*、/、%\n                <div class="derive-inds-error hide j-derive-inds-error"></div>\n            </div>\n            <div class="description-select hide">\n                可双击左侧指标，进入右侧区域，生成对应指标的计算列\n                <div class="derive-inds-error hide j-derive-inds-error"></div>\n            </div>\n        </div>\n        <div class="ind-cal-setting f-l">\n            ';
        $each(indList.data,function($value,$index){
        if($value.type == "COMMON"){
        $out+='\n            <div class="item ellipsis hover-bg">\n                <span class="j-ori-item" data-input="';
        $out+=$escape($value.name);
        $out+='" title="';
        $out+=$escape($value.name);
        $out+='（';
        $out+=$escape($value.id);
        $out+='）">';
        $out+=$escape($value.name);
        $out+='</span>\n                ';
        if($value.visible == 0){
        $out+='<span class="icon-letter collect j-method-type" data-id="';
        $out+=$escape($value.id);
        $out+='">';
        $out+=$escape(indList.map[$value.methodType]);
        $out+='</span>';
        }
        $out+='\n            </div>\n            ';
        }
        });
        $out+='\n        </div>\n        <div class="data-sources-derive-list">\n            <div class="data-sources-derive-list-create f-l" >\n                ';
        $each(indList.data,function($value,$index){
        $out+='\n                ';
        if($value.type == "CAL"){
        $out+='\n                <div class="item j-derive-item">\n                    <div class="form-common-text name">\n                        <input type="text" value="';
        $out+=$escape($value.caption);
        $out+='" id="';
        $out+=$escape($value.id);
        $out+='" class="j-input-datasource-address" placeholder="衍生指标名">\n                        <span class="form-common-text-validation hide j-validation">衍生指标名与公式不能为空</span>\n                    </div>\n                    =\n                    <div class="form-common-text">\n                        <input type="text" value="';
        $out+=$escape($value.formula);
        $out+='" class="j-value" placeholder="衍生指标公式">\n                        <span class="form-common-btn-extend form-common-btn-extend-absolute j-delete" title="删除此衍生指标">×</span>\n                    </div>\n                </div>\n                ';
        }
        $out+='\n                ';
        });
        $out+='\n                ';
        if(!hasDerive){
        $out+='\n                <div class="item j-derive-item">\n                    <div class="form-common-text name">\n                        <input type="text" class="j-input-datasource-address" placeholder="衍生指标名">\n                        <span class="form-common-text-validation hide j-validation">衍生指标名与公式不能为空</span>\n                    </div>\n                    =\n                    <div class="form-common-text">\n                        <input type="text" class="j-value" placeholder="衍生指标公式">\n                        <span class="form-common-btn-extend form-common-btn-extend-absolute j-delete" title="删除此衍生指标">×</span>\n                    </div>\n                </div>\n                ';
        }
        $out+='\n                <div class="item">\n                    <span class="text-btn fw-b j-add-derive"> + </span>\n                </div>\n                <div class="item hide j-derive-item-template">\n                    <div class="form-common-text name">\n                        <input type="text" class="j-input-datasource-address" placeholder="衍生指标名">\n                        <span class="form-common-text-validation hide j-validation">衍生指标名与公式不能为空</span>\n                    </div>\n                    =\n                    <div class="form-common-text">\n                        <input type="text" class="j-value" placeholder="衍生指标公式">\n                        <span class="form-common-btn-extend form-common-btn-extend-absolute j-delete" title="删除此衍生指标">×</span>\n                    </div>\n                </div>\n            </div>\n\n            <div class="data-sources-derive-list-select f-l hide j-data-sources-derive-list-select">\n\n                <div class="item">\n                    <label class="label-inds-item f-l">添加环比指标</label>\n                    <div class="area-inds-item j-area-inds-item-rr f-l">\n                        ';
        $each(indList.data,function($value,$index){
        $out+='\n                        ';
        if($value.type == "RR"){
        $out+='\n                        <div class="area-inds-item-ind j-area-inds-item-ind f-l" id="';
        $out+=$escape($value.id);
        $out+='" title="';
        $out+=$escape($value.caption);
        $out+='" name="';
        $out+=$escape($value.name);
        $out+='">\n                            ';
        $out+=$escape($value.caption);
        $out+='\n                        <span class="hide area-inds-item-ind-delete">\n                            x\n                        </span>\n                        </div>\n                        ';
        }
        $out+='\n                        ';
        });
        $out+='\n                        <input type="text" class="input-inds-item f-l"/>\n                    </div>\n                </div>\n                <div class="item">\n                    <label class="label-inds-item f-l">添加同比指标</label>\n                    <div class="area-inds-item j-area-inds-item-sr f-l">\n                        ';
        $each(indList.data,function($value,$index){
        $out+='\n                        ';
        if($value.type == "SR"){
        $out+='\n                        <div class="area-inds-item-ind j-area-inds-item-ind f-l" id="';
        $out+=$escape($value.id);
        $out+='" title="';
        $out+=$escape($value.caption);
        $out+='" name="';
        $out+=$escape($value.name);
        $out+='">\n                            ';
        $out+=$escape($value.caption);
        $out+='\n                        <span class="area-inds-item-ind-delete hide">\n                            x\n                        </span>\n                        </div>\n                        ';
        }
        $out+='\n                        ';
        });
        $out+='\n                        <input type="text" class="input-inds-item f-l"/>\n                    </div>\n                </div>\n            <span class="select-description f-l">\n                描述：双击左侧指标后进入右侧，新指标以标签的形式展现，移入出现删除按钮。添加后的指标出现在左侧指标区域\n            </span>\n            </div>\n        </div>\n    </div>\n    <div class="norm-box" id="j-box-callbackIndex">\n\n    </div>\n</div>';
        return $out;
    }
    return { render: anonymous };
});