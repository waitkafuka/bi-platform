/**
 * @file 工具栏菜单-view
 * @author weiboxue(wbx_901118@sina.com)
 * @date 2014-12-24
 */
define([
        'report/global-menu-btns/component-menu-template'
    ],
    function (
        ComponentMenuTemplate
        ) {
        return Backbone.View.extend({

            /**
             * 构造函数
             *
             * @constructor
             */
            initialize: function () {
                var that = this;
                this.$el.find('.j-global-menu').append(ComponentMenuTemplate);
            }

        });
    }
);