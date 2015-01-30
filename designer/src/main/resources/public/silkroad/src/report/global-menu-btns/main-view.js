/**
 * @file 工具栏菜单-view
 * @author weiboxue(wbx_901118@sina.com)
 * @date 2014-12-24
 */
define([
        'dialog',
        'report/global-menu-btns/component-menu-template',
        'report/global-menu-btns/main-model'
    ],
    function (
        dialog,
        ComponentMenuTemplate,
        MenuMainModel
        ) {
        return Backbone.View.extend({
            /**
             * 构造函数
             *
             */
            initialize: function () {
                this.model = new MenuMainModel();
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

            },

            /**
             * 更换皮肤
             *
             * @public
             */
            chanceTheme : function (event) {
                // 皮肤类型
                var type = '';
                var $this = $(event.target);
                // 报表id
                var reportId = window.dataInsight.main.id;
                if ($this.attr('class').indexOf('j-skin-btn') != -1) {
                    type = $this.attr('id');
                }
                else {
                    type = $this.parent().attr('id');
                }
                this.model.getSkinType(reportId, type);
                // 更换link里面的路径
                $('.link-skin').attr(
                    'href', 'silkroad/asset/'
                    + type
                    + '/css/-di-product-debug.css');
                $('.skin-menu').hide();

                // 更换线上link里面的路径
//                $('.link-skin').attr(
//                    'href', 'asset/'
//                        + type
//                        + '/css/-di-product-min.css');
//                $('.skin-menu').hide();
            },

            /**
             * 更换皮肤
             *
             * @public
             */
            referenceLine : function (event) {
                var imglineurl = 'url(' + '/silkroad/src/css/img/grid.png)';
                var imgemptyurl = 'url(' + '/silkroad/src/css/img/grid-empty.png)';
                var $report = $('.report');
                console.log($report.css('background-image'));
                if ($report.css('background-image') != imglineurl) {
                    $report.css('background-image', imgemptyurl);
                    dialog.alert('背景参考线已关闭');
                }
                else {
                    $report.css('background-image', imglineurl);
                    dialog.alert('背景参考线已打开');
                }
//                // 获取全部参考线
//                var $line = $('.j-guide-line');
//                var lineNum = $line.length;
//                // 根据参考线个数进行对应操作
//                if (lineNum == 0) {
//                    dialog.warning('未添加组件，或未找到参考线请添加组件并重试');
//                }
//                else {
//                    if ($line.is(':visible')) {
//                        $line.hide();
//                        dialog.alert('参考线已关闭，再次点击启用。');
//                    }
//                    else {
//                        $line.show();
//                        dialog.alert('参考线已打开，再次点击关闭。');
//                    }
//                }
            }

        });
    }
);