/**
 * @file: 报表新建（编辑）-- 表格组件编辑模块
 *
 * @author: lizhantong
 * @depend:
 * @date: 2015-04-27
 */

define(
    [
        'report/edit/canvas/table-setting/table-setting-model',
        'report/edit/canvas/table-setting/text-align/text-align-view'
    ],
    function (
        TableSettingModel,
        textAlignView
    ) {
        //------------------------------------------
        // 视图类的声明
        //------------------------------------------

        /**
         * 表格设置视图类
         *
         * @class
         */
        var View = Backbone.View.extend({
            //------------------------------------------
            // 公共方法区域
            //------------------------------------------

            /**
             * 初始化函数
             *
             * @param {$HTMLElement} option.el
             * @param {string} option.reportId 报表的id
             * @param {Object} option.canvasView 画布的view
             * @constructor
             */
            initialize: function (option) {
                this.model = new TableSettingModel({
                    canvasModel: option.canvasView.model,
                    reportId: option.reportId
                });
                this.canvasView = option.canvasView;
                // 挂载topn设置视图
                this.textAlignView = new textAlignView({
                    el: this.el,
                    reportId: this.model.get('reportId'),
                    canvasView: this.canvasView
                });
            },
            /**
             * 销毁
             * @public
             */
            destroy: function () {
                this.stopListening();
                // 删除model
                this.model.clear({silent: true});
                delete this.model;
                // 在这里没有把el至为empty，因为在点击图行编辑时，会把图形编辑区域重置，无需在这里
                this.$el.unbind();
            }
        });

        return View;
    });