/**
 * Created by weiboxue on 2014/11/30.
 */
/**
 * @file 报表的公共功能 - view
 * @author 赵晓强(longze_xq@163.com)
 * @date 2014-10-10
 */
define([
        'template',
        'dialog',
        'report/global-setting-btns/global-template'
    ],
    function (
        template,
        dialog,
        GlobalTemplate
    ) {
        return Backbone.View.extend({

            /**
             * 构造函数
             *
             * @constructor
             */
            initialize: function () {
                var that = this;
            },

            /**
             * 设置参数维度
             *
             * @public
             */
            setGlobal: function () {
                dialog.showDialog({
                    content: GlobalTemplate.render(),
                    title: '参数维度设置',
                    dialog: {
                        height: 400,
                        width: 400,
                        open: function () {
                            var $this = $(this);
                            // 删除维度事件绑定
                            $this.on('click', '.j-global-close', function () {
                                $(this).parent().remove();
                            });
                            // 创建维度事件绑定
                            $this.find('.j-global-add').click(function () {
                                var $clone = $('.j-con-global-attr').find('.j-global-attr').clone(true);
                                $('.j-global-box').append($clone);
                            });
                        },
                        buttons: {
                            '确认': function () {
                                $(this).dialog('close');
                            },
                            '取消': function () {
                                $(this).dialog('close');
                            }
                        }
                    }
                });
            }
        });
    }
);