/**
 * @file
 * @author 赵晓强(v_zhaoxiaoqiang@baidu.com)
 * @date 2014-8-4
 */
define([
        'template',
        'dialog',
        'report/edit/component-box/main-model',
        'report/edit/component-box/main-template'
    ],
    function (
        template,
        dialog,
        MainModel,
        mainTemplate
    ) {

        return Backbone.View.extend({
            // 事件
            events: {
                'change #component-group-selector': 'showCompByGroup',
                'click .j-component-box-fold': 'fold'
            },

            /**
             * 构造函数
             *
             * @param {Object} option 初始化配置项
             * @constructor
             */
            initialize: function (option) {
                var that = this;
                this.model = new MainModel({
                    id: this.id
                });
                this.canvasView = option.canvasView;

                this.$el.append(mainTemplate.render(this.model.config));
                this.showCompByGroup();
                this.initDrag();
            },

            /**
             * 按分组显示组件
             *
             * @public
             */
            showCompByGroup: function () {
                var $cbox = this.$el.find('.j-con-component-box');
                $cbox.find('.j-con-component').hide();

                var selectedComponentGroupId = ''
                    + $cbox.find('#component-group-selector').val();
                var compConSelector = ''
                    + '.j-con-component[data-group-id='
                    + selectedComponentGroupId + ']';

                $cbox.find(compConSelector).show();
                this.currentGroupId = selectedComponentGroupId;
            },

            /**
             * 初始化组件工具箱中的组件可被拖动
             *
             * @public
             */
            initDrag: function () {
                var that = this;

                // 定义可从组件箱中拖拽出
                $('.j-con-component .j-component-item', this.$el).draggable({
                    //revert: "invalid",
                    appendTo: ".j-report",
                    helper: "clone",
                    cursorAt: {
                        left: 5,
                        top: 5
                    },
                    opacity: 1, // 被拖拽元素的透明度
                    // 修正由于滚动条产生的偏移
                    drag: function (event, ui) {
                        var $report = $(this);
                        var scrollTop = $report.scrollTop();
                        // TODO 功能待完善
                    },
                    start: function (event, ui) {

                        var compType = ui.helper.attr('data-component-type');
                        var compData = that.model.getComponentData(compType);
                        //ui.helper.attr('data-o_o-di', compData.renderData[0].id);
                        ui.helper.html('临时展示').css({
                            'width': compData.defaultWidth + 'px',
                            'height': compData.defaultHeight + 'px',
                            'cursor': 'move',
                            'background': '#ffffff'
                        }).addClass('active shell-component');

                        // 添加参考线
                        that.canvasView.addGuides(ui.helper);
                    }
                });
            },

            /**
             * 折叠/展开组件工具箱
             * @param {event} event 点击事件
             *
             * @public
             */
            fold: function (event) {
                var $target = $(event.target);
                var $box = $target.parents('.j-con-component-box');
                var boxSize = 25;

                if ($box.width() > boxSize) {
                    $box.width(boxSize).height(boxSize);
                    $target.html('+');
                    $box.find('select').hide();
                }
                else {
                    $box.width(160).height('auto');
                    $target.html('-');
                    $box.find('select').show();
                }
            },

            /**
             * 销毁view
             *
             * @public
             */
            destroy: function () {
                // 销毁 model
                this.model.clear({silent: true});
                // 停止监听model事件
                this.stopListening();
                // 解绑jq事件
                this.$el.unbind().empty();
            }
        });
    });