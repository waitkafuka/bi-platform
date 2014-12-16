/**
 * @file 参数维度-view
 * @author weiboxue(wbx_901118@sina.com)
 * @date 2014-12-1
 */
define([
        'template',
        'dialog',
        'report/global-setting-btns/btns-template'
    ],
    function (
        template,
        dialog,
        BtnsTemplate
    ) {
        var btnAttr = {
            ID: 'global-set-'
        };
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
                    content: BtnsTemplate.render(),
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
            },
            // 参数区域按钮属性
            btnBox: [
            // 暂时去掉此功能
//                {
//                    id: btnAttr.ID + 'Dimension',
//                    btnsHTML: '参数维度设置'
//                }
            ],

            /**
             * 创建按钮函数
             */
            createBtns: function () {
                var div = '';
                var btnBox = this.btnBox || [];
                if (btnBox.length == 0) {
                    div = '';
                }
                else {
                    for(var i = 0; i < btnBox.length; i ++) {
                        div += "<div id='" + btnBox[i].id + "'>" + btnBox[i].btnsHTML + "</div>"
                    }
                }
                return div;
            }
        });
    }
);