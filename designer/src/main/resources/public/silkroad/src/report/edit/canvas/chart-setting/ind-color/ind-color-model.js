/**
 * @file: 报表新建（编辑）-- 图形组件编辑模块 -- 指标设置模块model
 * @author: lizhantong
 * @depend:
 * @date: 2015-03-25
 */


define(['url', 'core/helper'], function (Url, Helper) {

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
        getIndColorList: function (success) {
            $.ajax({
                url: Url.getIndColorList(this.get('reportId'), this.get('compId')),
                type: 'get',
                success: function (data) {
                    var sourceData = data.data;
                    var targetData;
                    var indList;

                    if (!$.isObjectEmpty(sourceData)) {
                        // 组合数据格式列表项
                        targetData = {indList: {}};
                        /**
                         * 后端返回的数据格式，name:format
                         * 需要组合成的数据格式：name: { format: '', caption: ''}
                         * 获取左侧所有指标，遍历,为了获取caption
                         *
                         */
                        indList = Helper.getIndList();
                        for(var i = 0, iLen = indList.length; i < iLen; i ++) {
                            var name = indList[i].name;
                            if (sourceData.hasOwnProperty(name)) {
                                var formatObj = {
                                    color: sourceData[name],
                                    caption: indList[i].caption
                                };
                                targetData.indList[name] = formatObj;
                            }
                        }
                    }
                    success(targetData);
                }
            });
        },
        /**
         * 提交指标颜色设置信息
         *
         * @param {Function} success 回调函数
         * @public
         */
        saveIndColorInfo: function (data, success) {
            var compId = this.get('compId');
            var formData = {
                areaId: compId,
                colorFormat: JSON.stringify(data)
            };
            $.ajax({
                url: Url.getIndColorList(this.get('reportId'), compId),
                type: 'POST',
                data: formData,
                success: function () {
                    success();
                }
            });
        }
    });

    return Model;
});
