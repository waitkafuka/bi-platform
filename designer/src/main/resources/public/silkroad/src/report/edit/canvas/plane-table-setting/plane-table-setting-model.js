/**
 * @file: 报表新建（编辑）-- 图形组件编辑模块
 * @author: lizhantong
 * date: 2015-03-25
 */

define(['url'], function (Url) {

    //------------------------------------------
    // 模型类的声明
    //------------------------------------------

    var Model = Backbone.Model.extend({
        defaults: {},
        initialize: function () { },
        /**
         * 获取数指标颜色设置数据
         *
         * @param {Function} success 回调函数
         * @public
         */
        getFieldFilterInfo: function (itemId, success) {
            var that = this;
            var data = {
                name: 'test',
                defaultValue: '默认值',
                sqlCondition: '='

            };
            success(data);
//            $.ajax({
//                url: Url.getFieldFilterInfo(that.get('reportId'), that.get('compId'), itemId),
//                type: 'get',
//                success: function (data) {
//                    var data = {
//                        name: 'test',
//                        defaultValue: '默认值',
//                        sqlCondition: '='
//
//                    };
//                    success(data.data);
//                }
//            });
        },
        saveFieldFilterInfo: function (fieldId, data, success) {
            var that = this;
            $.ajax({
                url: Url.getFieldFilterInfo(that.get('reportId'), that.get('compId'), fieldId),
                data: data,
                type: 'post',
                success: function () {
                    success();
                }
            });
        }
    });

    return Model;
});
