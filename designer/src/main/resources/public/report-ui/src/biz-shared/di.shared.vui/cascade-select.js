/**
 * Created by weiboxue on 2/4/2015.
 */
/**
 * di.shared.vui.HiddenInput
 * Copyright 2014 Baidu Inc. All rights reserved.
 *
 * @file:    隐藏的输入，用于传递报表引擎外部传来的参数
 * @author:  luowenlei(luowenlei)
 * @depend:  xui, xutil
 */


$namespace('di.shared.vui');

(function () {

    //------------------------------------------
    // 引用
    //------------------------------------------

    var inheritsObject = xutil.object.inheritsObject;
    var extend = xutil.object.extend;
    var XOBJECT = xui.XObject;

    //------------------------------------------
    // 类型声明
    //------------------------------------------

    /**
     * 用于传递报表引擎外部传来的参数
     *
     * @class
     * @extends xui.XView
     * @param {Object} options
     * @param {HTMLElement} options.el 容器元素
     */
    var CASCADE_SELECT = $namespace().CascadeSelect =
        inheritsObject(XOBJECT, constructor);
    var CASCADE_SELECT_CLASS = CASCADE_SELECT.prototype;

    //------------------------------------------
    // 方法
    //------------------------------------------

    /**
     * 构造函数
     *
     * @private
     * @param {Object} options 参数
     */
    function constructor(options) {
        this._el = options.el;
    }

    /**
     * 设置数据
     *
     * @public
     * @param {Object} data 数据
     * @param {(Object|Array}} data.datasource 数据集
     * @param {*} data.value 当前数据
     */
    CASCADE_SELECT_CLASS.setData = function (data) {
        var me = this,
            def = me.$di('getDef'),
            compId = def.compId;
            selElId = compId + '-0',
            html = ['<select id="', selElId, '">'],
            data = data.datasource;

        me._compId = compId;
        me._curIndex = 0;
        me._selValue = '';
        me._allSel = def.selectAllDim.length;

        // 渲染第一个select
        for(var i = 0, len = data.length; i < len; i ++) {
            html.push(
                '<option value="', data[i].value, '">', data[i].text,
                '</option>'
            );
        }
        html = html.join('') + '</select>';
        $(this._el).html(html);

        // 初始化第一个下拉框
        $('#' + selElId, me._el).dropkick({
            mobile: true,
            change: function() {
                me._curIndex = 0;
                me._selValue = this.value;
                // 当前下拉框点击时，如果还有子下拉框，就重新去渲染子下拉框
                if(me._curIndex < me._allSel - 1) {
                    me.getNextLevel(this.value);
                }
                // 反之，就触发对外的查询事件
                else {
                    me.notify('cascadeSelectChange');
                }
            }
        });
        $('select[id='+ selElId +']').remove();
        me._selValue = data[0].value;

        // 当初始化完第一个下拉框，如果是多级，就去触发第二个
        if(me._curIndex < me._allSel - 1) {
            me.getNextLevel(data[0].value);
        }
        else {
            me.notify('cascadeSelectChange');
        }
    };

    /**
     * 设置数据
     *
     * @public
     * @param {string} parent 父节点
     */
    CASCADE_SELECT_CLASS.getNextLevel = function(parent) {
        var me = this;
        var option = {
            param: {
                componentId: me._compId,
                uniqueName: parent
            },
            callback: me.renderNextLevel,
            input: me
        };
        me.notify('cascadeGetNextLevel', option);
    };

    /**
     * 渲染下一个下拉框
     *
     * @public
     * @param {Object} data 数据
     * @param {(Object|Array}} data.compId.datasource 数据集
     * @param {*} data.value 当前数据
     */
    CASCADE_SELECT_CLASS.renderNextLevel = function(data) {
        var me = this,
            compId = me._compId,
            dif = me._allSel - 1 - me._curIndex; // 用来确定是否当前下拉框还有子下拉框

        data = data[compId].datasource;
        if (data instanceof Array && dif !== 0) {
            // 先移除掉触发者以后的所有下拉框
            for (var i = dif - 1; i >= 0; i --) {
                var _cur = me._curIndex + (dif - i);
                var selId = compId + '-' + _cur;
                $('.dk-select').each(function() {
                    if($(this).attr('id').indexOf(selId) > 0) {
                        $(this).remove();
                    }
                });
            }
            // 渲染触发者的下一个下拉框
            var selElId = compId + '-' + (++ me._curIndex);
            var html = ['<select id="', selElId, '">'];
            for (var i = 0, len = data.length; i < len; i ++) {
                html.push(
                    '<option value="', data[i].value, '">', data[i].text,
                    '</option>'
                );
            }
            html = html.join('') + '</select>';
            $(this._el).append(html);

            // 初始化下拉框,使用闭包主要是为了保存当前下拉框的顺序
            (function (x) {
                $('#' + selElId).dropkick({
                    mobile: true,
                    change: function() {
                        me._curIndex = x;
                        me._selValue = this.value;
                        if(me._curIndex < me._allSel - 1) {
                            me.getNextLevel(this.value);
                        }
                        else {
                            me.notify('cascadeSelectChange');
                        }
                    }
                });
                $('select[id='+ selElId +']').remove();
            })(me._curIndex);

            dif = me._allSel - 1 - me._curIndex;
            isGoToNext = dif > 0 ? true : false;

            me._selValue = data[0].value;
            if (isGoToNext) {
                me.getNextLevel(data[0].value);
            }
            else {
                me.notify('cascadeSelectChange');
            }
        }
    };

    /**
     * 得到当前值
     *
     * @public
     * @return {*} 当前数据
     */
    CASCADE_SELECT_CLASS.getValue = function () {
        var me = this;
        return me._selValue;
    };

})();


