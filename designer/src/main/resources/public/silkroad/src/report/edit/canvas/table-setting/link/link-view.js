/**
 * @file: 报表新建（编辑）-- 表格组件编辑模块 -- 跳转设置view lzt
 *
 * @author: lizhantong
 * @depend:
 * @date: 2015-06-10
 */
define(
    [
        'dialog',
        'report/edit/canvas/table-setting/link/link-model',
        'report/edit/canvas/table-setting/link/link-template',
        'report/edit/canvas/table-setting/link/link-param-template',
        'report/edit/canvas/table-setting/link/link-param-add-template'
    ],
    function (
        dialog,
        LinkModel,
        LinkSettingTemplate,
        LinkSettingParamTemplate,
        LinkParamSettingAddTemplate
    ) {

        //------------------------------------------
        // 视图类的声明
        //------------------------------------------
        var View = Backbone.View.extend({
            events: {
                'click .j-set-link': 'dialogLinkSetting'
            },

            //------------------------------------------
            // 公共方法区域
            //------------------------------------------

            /**
             * 报表组件的编辑模块 初始化函数
             *
             * @param {$HTMLElement} option.el
             * @param {string} option.reportId 报表的id
             * @param {Object} option.canvasView 画布的view
             * @constructor
             */
            initialize: function (option) {
                var that = this;

                that.model = new LinkModel({
                    canvasModel: option.canvasView.model,
                    reportId: option.reportId
                });
                this.model.set('compId', this.$el.find('.j-comp-setting').attr('data-comp-id'));
            },

            /**
             * 获取列跳转平面表设置数据
             *
             * @param {event} event 点击事件
             * @public
             */
            dialogLinkSetting: function (event) {
                var that = this;
                that.model.getColumnLinkPlaneList(function (data) {
                    that._openColumnLinkPlaneDialog(data);
                    that.bindEvents();
                });
            },
            /**
             * 绑定跳转设置中的所有事件
             *
             * @public
             */
            bindEvents: function() {
                var that = this;
                var $next = $('.j-table-link-set-column-table .j-next');
                var $back = $('.j-table-link-set-param-table .j-back');
                var $ok = $('.j-table-link-set-param-table .j-ok');
                $next.unbind();
                $back.unbind();
                $ok.unbind();
                $next.bind('click', function () {
                    that.saveColumnTableRelation($(this));
                });
                $back.bind('click', function () {
                    $('.j-table-link-set-column-table').show();
                    $('.j-table-link-set-param-table').hide();
                });
                $ok.bind('click', function () {
                    that.saveParamRelation();
                });
            },
            /**
             * 显示参数设置
             *
             * @public
             */
            showParamSetting: function (param) {
                var that = this;
                that.model.getParamSetList(param, function (data) {
                    $('.j-table-link-set-param-items').html(
                        LinkSettingParamTemplate.render(data)
                    );
//                    $('.j-param-set-add').unbind();
//                    $('.j-param-set-add').bind('click', function () {
//                        $(this).before(
//                            LinkParamSettingAddTemplate.render(data)
//                        );
//                    });
                });
            },
            /**
             * 保存列跳转平面表设置
             *
             * @public
             */
            saveColumnTableRelation: function (target) {
                var that = this;
                var curParam = {};
                var data = [];
                var items = $('.j-table-link-set-column-table .table-link-set-item');
                items.each(function () {
                    var $this = $(this);
                    var id = $this.find('label').attr('data-value');
                    var value = $this.find('select').val();
                    data.push({
                        id: id,
                        selectedTable: value
                    });
                });
                curParam.linkInfo = JSON.stringify(data);
                var nextParam = {};
                nextParam.planeTableId = target.prev('select').val();
                nextParam.olapElementId = target.prev().prev().attr('data-value');
                that.olapElementId = nextParam.olapElementId;
                that.model.saveColumnTableRelation(curParam, function () {
                    $('.j-table-link-set-column-table').hide();
                    $('.j-table-link-set-param-table').show();
                    that.showParamSetting(nextParam);
                });
            },
            /**
             * 保存参数设置
             *
             * @public
             */
            saveParamRelation: function () {
                var that = this;
                var items = $('.j-table-link-set-param-table .table-link-set-item');
                var data = [];
                var param = {};
                items.each(function () {
                    var $this = $(this);
                    var paramName = $this.find('label').attr('data-value');
                    var selectedDim = $this.find('select').val();
                    data.push({
                        paramName: paramName,
                        selectedDim: selectedDim
                    });
                });
                param.mappingInfo = JSON.stringify(data);
                param.olapElementId = that.olapElementId;
                that.model.saveParamRelation(param, function () {
                    that.$dialog.dialog('close');
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
            },

            //------------------------------------------
            // 私有方法区域
            //------------------------------------------

            /**
             * 打开文本对齐设置弹出框
             *
             * @param {Object} data
             * @private
             */
            _openColumnLinkPlaneDialog: function(data) {
                var that = this,
                    html;

                if ($.isEmptyObject(data.columnDefine)) {
                    dialog.alert('没有指标');
                    return;
                }

                html = LinkSettingTemplate.render(data);
                that.$dialog = dialog.showDialog({
                    title: '跳转设置',
                    content: html,
                    dialog: {
                        width: 440,
                        height: 400,
                        resizable: false
                    }
                });
            }
        });

        return View;
    });