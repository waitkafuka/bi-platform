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
    var RANGE_CALENDAR = $namespace().RangeCalendar =
            inheritsObject(XOBJECT, constructor);
    var RANGE_CALENDAR_CLASS = RANGE_CALENDAR.prototype;
    
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
        var me = this;
        var calendarId = 'range-cal-' + (new Date()).getTime();
        var html = "<input type='text' readonly='readonly' "
            + "style='width: 250px' "
            + "id='" + calendarId + "'"
            + "/>";
        options.el.innerHTML = html;
        var rangeCal = new Kalendae.Input(calendarId, {
            direction:'past',
            months: 2,
            mode: 'range'
        });
        options.vuiType = 'Range_Calendar';
        rangeCal.subscribe('change', function (date, action) {
            // 判断如果没有选择时间范围，不做任何事情
            if (this.getSelected().indexOf(' - ') > 0) {
                me.currentDate = this.getSelected();
                me.notify('calChangeDate', me.currentDate);
            }
        });
        this.rangeCal = rangeCal;
    };
    
    /**
     * 设置数据
     *
     * @public
     * @param {Object} data 数据
     * @param {(Object|Array}} data.datasource 数据集
     * @param {*} data.value 当前数据
     */
    RANGE_CALENDAR_CLASS.setData = function (data) {
        var startDateOpt = data.rangeTimeTypeOpt.startDateOpt;
        var endDateOpt = data.rangeTimeTypeOpt.endDateOpt;
        this.rangeCal.setSelected(Kalendae.moment().subtract({d:Math.abs(startDateOpt)}));
        this.rangeCal.addSelected(Kalendae.moment().add('days',endDateOpt));
        this._oData = data;
    };

    /**
     * 得到当前值
     *
     * @public
     * @return {*} 当前数据
     */
    RANGE_CALENDAR_CLASS.getValue = function () {
        if (this.currentDate === undefined) {
            // 设置默认值
            return null;
        }
        var saveDataObj = this.convertInputValue2SaveData(this.currentDate);
        var data = {
            start: saveDataObj.dataStartStr,
            end: saveDataObj.dataEndStr,
            granularity: 'D'
        };

        return data;
    };

    /**
     * 通过input value获取时间时间格式YYYY-MM-DD
     *
     * @public
     * @return {*} 当前数据
     */
    RANGE_CALENDAR_CLASS.convertInputValue2SaveData = function (inputValue) {
        var dateArr = inputValue.split(' - ');
        return {
            dataStartStr: dateArr[0].replace(' ', ''),
            dataEndStr: dateArr[1].replace(' ', '')
        };
    };

})();