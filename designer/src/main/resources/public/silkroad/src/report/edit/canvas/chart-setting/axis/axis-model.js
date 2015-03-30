/**
 * @file: 报表新建（编辑）-- 图形组件编辑模块 -- 双坐标轴设置模块model
 * @author: weiboxue
 * @depend:
 * @date: 2015-03-27
 */


define(['url'], function (Url) {

    //------------------------------------------
    // 模型类的声明
    //------------------------------------------

    var Model = Backbone.Model.extend({
        defaults: {},
        initialize: function () { },
        /**
         * 提交双坐标轴数据
         *
         * @param {Function} success 回调函数
         * @public
         */
        saveAxisInfo: function (data, success) {
            var formData = {
                position: JSON.stringify(data)
            };
            $.ajax({
                url: Url.getAxisList(this.get('reportId'), this.get('compId')),
                type: 'POST',
                data: formData,
                success: function () {
                    success();
                }
            });
        },
        /**
         * 获取双坐标轴设置信息
         *
         * @param {Function} success 回调函数
         * @public
         */
        getAxisList: function (success) {
            var that = this;
            $.ajax({
                url: Url.getAxisList(that.get('reportId'), that.get('compId')),
                type: 'get',
                success: function (data) {
                    var dimData = data.data;
                    // 当前指标容器
                    var dimList = [];
                    // 设定为默认设置
                    for (var i in dimData) {
                        var object = {};
                        object.caption = i;
                        object.axis = dimData[i];
                        dimList.push(object);
                    }
                    var list = {};
                    list.dim = dimList;
                    success(list);
                }
            });
        }
    });

    return Model;
});
