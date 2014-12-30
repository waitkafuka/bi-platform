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
             */
            initialize: function () {
            },

            /**
             * 组件区域下拉框
             *
             */
            componentMenu: function () {
                return ComponentMenuTemplate.render();
            },

            /**
             * 功能区域切换菜单
             *
             * @public
             */
            shiftMenu : function (event) {
                var nowid = $(event.target).parent().attr('id');
                var $menu = $('.global-menus').not('#' + nowid);
                $menu.hide();
                var $nowmenu = $('.comp-menu').find('#' + nowid);
                if ($nowmenu.css('display') == 'none') {
                    $nowmenu.show();
                }
                else {
                    $nowmenu.hide();
                }

//                for (var i = 0; i<$menu.length; i ++) {
//                    if ($($menu[i]).attr('id') == id) {
//                        if ($($menu[i]).css('display') == 'none') {
//                            $($menu[i]).show();
//                        }
//                        else {
//                            $($menu[i]).hide();
//                        }
//                    }
//                }
            }

        });
    }
);