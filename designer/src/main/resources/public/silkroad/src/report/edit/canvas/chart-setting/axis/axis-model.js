/**
 * @file: 报表新建（编辑）-- 图形组件编辑模块 -- 双坐标轴设置模块model
 * @author: 魏博学
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
                postion: JSON.stringify(data)
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
//                    // 获取当前指标
//                    var nowDim = window.dataInsight.main.model.attributes.dimList;
//                    var dimData = data.data;
//                    // 当前指标容器
//                    var dimList = [];
//                    // 设定为默认设置
//                    for (var i = 0; i < nowDim.length; i ++) {
//                        var object = {};
//                        object.caption = nowDim[i].caption;
//                        object.axis = '0';
//                        dimList.push(object);
//                    }
//                    // 通过请求数据进行初始化设置
//                    for (i in dimList) {
//                        if (dimData != {}) {
//                            for (j in dimData) {
//                                if (i == j) {
//                                    dimList[i].axis = dimData[j];
//                                }
//                            }
//                        }
//                    }
//                    var list = {};
//                    list.dim = dimList;
//                    success(list);
                    var sourceData = data.data;
                    var targetData;
                    var indList;

                    if (sourceData) {
                        // 组合数据格式列表项
                        targetData = {
                            dataFormat: {}
                        };
                        /**
                         * 后端返回的数据格式，name:format
                         * 需要组合成的数据格式：name: { format: '', caption: ''}
                         * 获取左侧所有指标，遍历,为了获取caption
                         *
                         */
                        indList = dataInsight.main.model.get('indList').data;
                        for(var i = 0, iLen = indList.length; i < iLen; i ++) {
                            var name = indList[i].name;
                            if (sourceData.hasOwnProperty(name)) {
                                var formatObj = {
                                    format: sourceData[name],
                                    caption: indList[i].caption
                                };
                                targetData.dataFormat[name] = formatObj;
                            }
                        }
                        targetData.defaultFormat = sourceData.defaultFormat;
                    }
                    success(targetData);
                }
            });
        }
    });

    return Model;
});
