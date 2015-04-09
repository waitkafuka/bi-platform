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
    var encodeHTML = xutil.string.encodeHTML;
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
        alert('constrcutor');
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
        alert();
    };

    /**
     * 得到当前值
     *
     * @public
     * @return {*} 当前数据
     */
    CASCADE_SELECT_CLASS.getValue = function () {
    };

})();


