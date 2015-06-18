/**
* callback 数据是当前选中三级分类的name的数组
**/
(function ($) {
	var defaults = {};
    var checkStatusCache = {};
    var textCache = [];
    var clickCheckbox = {};

	function createDom(ele) {
		$('<div class="sxwzbText"><p class="sxwzbButton"><span></span></p></div><div class="sxwzbContent"></div>').appendTo(ele);
        ele.css({
            'position': 'relative',
            'font-family': '"Microsoft YaHei", Arial, sans-serif;'
        });
	}

	function renderCheckbox(data) {
		var htmlArray = [];
		for (var i = 0, l = data.length; i < l; i++) {
			// 构建大分类
			htmlArray.push('<div data-value="'+ data[i].name +'" class="sxwzbTitle">'+ data[i].caption +'</div>');

			var children = data[i].children;
			htmlArray.push('<div class="sxwzbBoxs">');
			if (!children){
				continue;
			}
			// 构建分类2
			for (var j = 0, le = children.length; j < le; j++) {
				htmlArray.push('<div class="sxwzbType"><div class="sxwzbCategories">'
					+ '<input type="checkbox" id=' + children[j].name + ' class="sxwzbCheckBoxP">'
					+ '<label for='+ children[j].name + '>'
					+  children[j].caption
					+ '</label>'
					+ '</div><div class="sxwzbSubdivisions">'
					);
				var grandson = children[j].children;
				if (!grandson) {
					continue;
				}
				// 构建分类3
				for (var m = 0, n = grandson.length; m < n; m++) {
                    checkStatusCache[grandson[m].name] = checkStatusCache[grandson[m].name] || grandson[m].selected;
					htmlArray.push('<div class="sxwzbSubdivision">'
						+ '<input class="sxwzbCheckBoxC" data-name="' + grandson[m].name +'" type="checkbox" id=' + grandson[m].name + ' ' + (grandson[m].selected === "true"? "checked": "")+'>'
						+ '<label for='+ grandson[m].name + '>'
						+  grandson[m].caption
						+ '</label>'
						+ '</div>'
						);
                    grandson[m].selected === "true" && textCache.push(grandson[m].caption);
				}
				htmlArray.push('</div></div>');
			}
			htmlArray.push('</div>');
		}
		htmlArray.push('<div class="sxwzbButtons"><span id="btnColOk" class="uiButton uiButton-ok">确定</span>'
                			+ '<span id="btnColCancel" class="uiButton uiButton-circle">取消</span></div>');
		return htmlArray.join('');
	}
    // 文本框显示选中元素
    function renderInitStatus(text) {
        var text = [];
        $.each($('input[type="checkbox"]:checked'), function(i, item){
            text.push($(item).siblings('label').text());
        })
        $('.sxwzbButton span').text(text);
    }

    // 判断是否为全选
    function judgeCheckbox() {
        $.each($('.sxwzbType'), function(i, item){
            var childNode = $(item).find('.sxwzbSubdivisions input[type="checkbox"]'),
                checkedItem = $(item).find('.sxwzbSubdivisions input[type="checkbox"]:checked');

            var checkStatus = ((childNode.length === checkedItem.length)
                                && childNode.length > 0)? true: false;
            $(item).find('.sxwzbCheckBoxP').prop('checked', checkStatus);
        })
    }

    function bindEvent(options) {
        $('.sxwzbButton').click(function(e) {   
            $('.sxwzbContent').toggle();
        })
        /**
        * checkStatus当前checkbox选中状态
        * parentNode 当前父元素sxwzbType
        * childNode 二级分类
        * checkboxP 一级分类
        **/
        $('input[type="checkbox"]').click(function(e) {
            var self = $(this);
            // 当前checkbox选中状态
            var checkStatus = self.prop('checked'),
                parentNode = $(this).parents('.sxwzbType'),
                childNode = parentNode.find('.sxwzbSubdivisions input[type="checkbox"]'),
                checkboxP = parentNode.find('.sxwzbCheckBoxP'),
                checkedItem = parentNode.find('.sxwzbSubdivisions input[type="checkbox"]:checked');
            // 点击一级分类时选中所有二级分类
            if (self.hasClass('sxwzbCheckBoxP')) {
                $.each(childNode, function(i, item) {
                    clickCheckbox[$(item).attr('data-name')] = $(item).prop('checked')? false: true;
                    $(item).prop('checked', checkStatus);
                })
            } else {
                // 全部选中时，选中前面的大类
                var checkStatus1 = ((childNode.length === checkedItem.length)
                                && childNode.length > 0)? true: false;
                checkboxP.prop('checked', checkStatus1);
                clickCheckbox[$(self).attr('data-name')] = checkStatus;
            }
        })

        $('#btnColOk').click(function(e) {
            var sxwzbCheckBoxC = $('.sxwzbCheckBoxC');
            var idArray = [];
            var textArray = [];
            $.each(sxwzbCheckBoxC, function(i, item) {
                if ($(item).prop('checked')) {
                    idArray.push($(item).attr('data-name'));
                    textArray.push($(item).siblings('label').text());
                }
            })
            // 重新设置文本框中为选中的元素
            renderInitStatus(textArray.join(','));
            // callback
            if (options.clickCallback) {
                options.clickCallback(idArray.join(','));
            }
            // 重置clickCheckbox
            $.extend(checkStatusCache, clickCheckbox);
            clickCheckbox = {};
            // 关闭窗口
            $('.sxwzbButton').trigger('click');
        })

        $('#btnColCancel').click(function(e){
            $('.sxwzbButton').trigger('click');
            for (var i in clickCheckbox) {
                $('#' + i).prop('checked', clickCheckbox[i] ===true? false: true);
                delete clickCheckbox[i];
            }
            judgeCheckbox();
        })
    }
    $.fn.screenXingWeiZhiBiao = function (options) {
        var options = $.extend(defaults, options);

        createDom(this);
        $('.sxwzbContent').append(renderCheckbox(options.data));
        judgeCheckbox();
        // $(renderCheckbox(options.data)).appendTo($('.sxwzbContent'));
        renderInitStatus(textCache.join(','));

        bindEvent(options);
    }
})(jQuery);