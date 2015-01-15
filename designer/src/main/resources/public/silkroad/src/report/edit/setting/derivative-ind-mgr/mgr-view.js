/**
 * @file: 报表新建（编辑）-- 衍生指标管理View
 * @author: lizhantong(lztlovely@126.com)
 * @depend:
 * @date: 2014-09-23
 */
define(
    [
        'template',
        'dialog',
        'common/float-window',
        'report/edit/setting/derivative-ind-mgr/mgr-template',
        'report/edit/setting/derivative-ind-mgr/mgr-model'
    ],
    function (
        template,
        dialog,
        FloatWindow,
        mgrTemplate,
        Model
    ) {

    return Backbone.View.extend({

        //------------------------------------------
        // 公共方法区域
        //------------------------------------------

        /**
         * 构造函数
         *
         * @constructor
         */
        initialize: function () {
            this.model = new Model();
        },
        /**
         * 打开衍生指标管理窗口
         *
         * @public
         */
        openDialog: function () {
            var html;
            var that = this;
            var indList = window.dataInsight.main.model.get('indList');
            var hasDerive = false;

            // 组合衍生指标管理渲染html需要的数据并根据数据获取html
            for (var i = 0, len = indList.data.length; i < len; i++) {
                if (indList.data[i].type === 'CAL') {
                    hasDerive = true;
                    break;
                }
            }
            html = mgrTemplate.render({indList: indList, hasDerive: hasDerive});
            // 弹出衍生指标管理窗口
            dialog.showDialog({
                dialog: {
                    height: 405,
                    width: 600,
                    resizable: false,
                    open: function () {
                        // 由于是新添加的dom，所以在弹窗打开后需要做一些初始化工作
                        that.el = $(this);
                        $(this).on('focusin', '.j-value', function (event) {
                            that.focusInput = event.target;
                        });
                        $(this).on('click', '.area-inds-item', function () {
                            that.focusInput = $(this)[0];
                            $(this).find('input').focus();
                        });
                        $(this).on('click', '.j-delete', function (event) {
                            dialog.confirm('确定删除吗？', function(){
                                var id = $(event.target)
                                    .parent()
                                    .parent()
                                    .find('.j-input-datasource-address')
                                    .attr('id');

                                if (id) {
                                    that.model.deleteInd(id, function () {
                                        $(event.target).parents('.j-derive-item').remove();
                                    });
                                }
                                else {
                                    $(event.target).parents('.j-derive-item').remove();
                                }
                            });
                        });
                        $('.j-ori-item', $(this)).click(function (event) {
                            that._clickOriItem.call(that, event);
                        });
                        $('.j-add-derive', $(this)).click(function (event) {
                            that._addDerive.call(that, event);
                        });
                        // tab切换事件
                        $(this).on('click', '.j-classification', function (event) {
                            that._tabBox(event);
                            that._tabClick(event);
                        });
                        $(this).on('click', '.area-inds-item-ind-delete', function (event) {
                            that._deleteSrRrInd($(event.target));
                        });
                    },
                    buttons: {
                        "提交": function () {
                            var $dialogDom = $(this);
                            console.log($dialogDom);
                            that._submitMethodTypeValue($dialogDom, function () {
                                $dialogDom.dialog('close');
                            });
                        },
                        '取消': function () {
                            var $dialogDom = $(this);
                            that.model.updateLeftPanel(function () {
                                $dialogDom.dialog('close');
                            });
                        }
                    }
                },
                content: html,
                title: '衍生指标管理'
            });

        },
        /**
         * 销毁
         * @public
         */
        destroy: function () {
            this.model.clear({silent: true});
            delete this.model;
        },
        //------------------------------------------
        // 私有方法区域
        //------------------------------------------

        /**
         * 获取计算列，同环比需要提交的数据
         * @param {$HTMLEelment} $dom 衍生指标管理dom元素
         * @param {event} closeDialog 衍生指标管理窗口关闭函数
         * @private
         */
        _submitMethodTypeValue: function ($dom, closeDialog) {
            var that = this;
            var result = {
                extendInds: {}
            };

            result.calDeriveInds = this._getDeriveData($dom);
            result.extendInds.rr = this._getSrRrData(1, $dom);
            result.extendInds.sr = this._getSrRrData(2, $dom);
            result.callback = this._getCallBackData($dom);

            that.model.submitMethodTypeValue(result, closeDialog);
        },
        /**
         * 新增一行计算列
         *
         * @param {event} event 点击新增按钮触发的事件
         * @private
         */
        _addDerive: function(event){
            var $addLine = $(event.target).parent();
            var $html = $('.j-derive-item-template', this.el).clone();

            $html.removeClass('hide j-derive-item-template').addClass('j-derive-item');
            $addLine.before($html);
        },
        /**
         * 点击左侧指标事件
         *
         * @param {event} event 点击事件
         * @private
         */
        _clickOriItem: function (event) {
            var that = this;
            var $target = $(event.target);
            var value = $target.attr('data-input');
            var $focusInput = $(this.focusInput);

            // 如果是同环比
            if ($focusInput.hasClass('j-area-inds-item-sr')
                || $focusInput.hasClass('j-area-inds-item-rr')
                ) {
                $focusInput.hasClass('j-area-inds-item-sr')
                && (value = value + '_sr');
                $focusInput.hasClass('j-area-inds-item-rr')
                && (value = value + '_rr');

                var hasInd = false;
                var itemHtml = [
                    '<div class="area-inds-item-ind j-area-inds-item-ind f-l" title="',
                    value,
                    '">',
                    value,
                    '<span class="hide area-inds-item-ind-delete">x</span>',
                    '</div>'
                ].join('');

                $focusInput.find('.j-area-inds-item-ind').each(function () {
                    hasInd = ($(this).attr('title') === value) ? true : false;
                });
                if (hasInd) {
                    return;
                }
                $focusInput.find('input').before(itemHtml).focus();
            }
            else {
                if (this.focusInput !== undefined) {
                    $focusInput.val($focusInput.val() + '${' + value + '}');
                }
                else {
                    // 在此处可做友好提示等处理
                }
            }

        },
        /**
         * 删除同环比指标
         *
         * @param {$HTMLEelment} $target 删除按钮元素
         * @private
         */
        _deleteSrRrInd: function ($target) {
            var that = this;
            dialog.confirm('确定删除吗？', function(){
                var id = $target.parent().attr('id');

                if (id) {
                    that.model.deleteInd(id, function () {
                        $target.parent().remove();
                    });
                }
                else {
                    $target.parent().remove();
                }
            });
        },
        /**
         * 获取计算列的数据
         *
         * @param {$HTMLEelment} $dom 衍生指标管理dom元素
         * @private
         * @return {Array} data 计算列指标数组
         */
        _getDeriveData: function ($dom) {
            var data = [];
            // 获取创建计算列
            $dom.find('.j-derive-item').each(function () {
                var $inputs = $(this).find('input');
                var $ind = $inputs.eq(0);
                var item = {
                    'id':  $ind.attr('id') || '',
                    'name': '',
                    'caption': $ind.val(),
                    'formula': $inputs.eq(1).val()
                };
                data.push(item);
            });
            return data;
        },
        /**
         * 获取回调指标数据
         *
         * @param {$HTMLEelment} $dom 衍生指标管理dom元素
         * @private
         * @return {Array} data 计算列指标数组
         */
        _getCallBackData: function ($dom) {
            var data = [];
            // 获取创建计算列
            $dom.find('.j-callback-index-all').eq(0).find('.callback-form').each(function () {
                var $inputs = $(this).find('.callback-text').eq(0);
                var item = {
                    'id':  $inputs.attr('id') || '',
                    'name': $inputs.find('.call-name').eq(0).val() || '',
                    'caption': $inputs.find('.call-caption').eq(0).val() || '',
                    'url': $inputs.find('.call-url').eq(0).val() || '',
                    'properties': {}
                };
                var pro = item.properties;
                pro.timeOut = $inputs.find('.call-timeout').val() || '';
                data.push(item);
            });
            return data;
        },
        /**
         * 获取同比（环比）指标的数据
         *
         * @param {number} type 1：环比；2：同比
         * @param {$HTMLEelment} $dom 衍生指标管理dom元素
         * @private
         * @return {Array} data 同环比指标数组
         */
        _getSrRrData: function (type, $dom) {
            var data = [];
            var mainClas = (type == 1)
                ? ' .j-area-inds-item-rr'
                : ' .j-area-inds-item-sr';
            var rrStr = '.j-data-sources-derive-list-select'
                + mainClas
                + ' .j-area-inds-item-ind';
            var $rrItems = $dom.find(rrStr);

            $rrItems.each(function () {
                data.push({
                    id: $(this).attr('id') || '',
                    name: $(this).attr('name') || $(this).attr('title'),
                    caption: $(this).attr('title')
                });
            });
            return data;
        },
        /**
         * 计算列与同环比tab切换
         *
         * @param {event} event tab事件
         * @private
         */
        _tabClick: function (event) {
            var $target;
            var tabId;
            var tabIdCode;

            if (event.target.tagName.toLowerCase() === 'span') {
                $target = $(event.target).parent();
            }
            else if (event.target.tagName.toLowerCase() === 'li') {
                $target = $(event.target);
            }

            $target.addClass('classification-focus')
                .siblings()
                .removeClass('classification-focus');

            tabId = $target.attr('id');
            tabIdCode =  tabId.split('-');
            $('.description-' + tabIdCode[tabIdCode.length-1])
                .show()
                .siblings()
                .hide();
            $('.data-sources-derive-list-' + tabIdCode[tabIdCode.length-1])
                .show()
                .siblings()
                .hide();
        },
        /**
         * callback回调指标切换
         *
         * @param {event} event tab事件
         * @private
         */
        _tabBox: function (event) {
            var id;
            if (event.target.tagName.toLowerCase() == 'span') {
                id = $(event.target).parent().attr('id');
                fnTabBox(id);
            }
            else if (event.target.tagName.toLowerCase() == 'li') {
                id = $(event.target).attr('id');
                fnTabBox(id);
            }

            function fnTabBox(id) {
                if (id == 'j-tab-callback') {
                    $('.norm-box').hide();
                    $('#j-box-callbackIndex').show();
                }
                else {
                    $('.norm-box').hide();
                    $('#j-box-norm').show();
                }
            }
        }
    });
});