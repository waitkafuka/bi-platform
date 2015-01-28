/**
 * @file
 * @author 赵晓强(longze_xq@163.com)
 * @date 2014-7-28
 */
define(['url'], function (Url) {

    return Backbone.Model.extend({

        /**
         * 构造函数
         *
         * @param {Object} option 初始化配置项
         * @param {boolen} option.isEdit 是否是编辑
         * @constructor
         */
        initialize: function (option) {
        },

        /**
         * 获取运行时报表Id（其实是通知后台做运行时处理，此Id已经对前端做了透明处理）
         *
         * @param {function} success 异步成功后的回调函数
         * @param {string} reportId 报表id
         * @param {string} type 报表皮肤id
         * @public
         */
        getSkinType: function (reportId, type, success) {
            var that = this;
            $.ajax({
                url:Url.getSkinType(reportId, type),
                type: 'POST',
                success: function () {
                    success();
                }
            });
        }
    });
});