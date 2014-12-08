/**
 * @file:    jquery常用方法扩展
 * @author:  lzt(lztlovely@126.com)
 * @date:    2014/07/14
 */

//------------------------------------------
// 包装器扩展区
//------------------------------------------

/**
 * 设置光标位置
 *
 * @param {number} position 光标位置
 */
$.fn.setCursorPosition = function(position){
    if(this.length === 0)  {
        return this;
    }

    return $(this).setSelection(position, position);
};

/**
 * 设置光选中内容
 * 如果是设定光标位置，那么selStart为光标位置，selEnd为1即可
 * 如果需要选中内容，那么selStart为选中内容开始位置，selEnd为选中内容长度
 * 真正选中内容的长度为：selEnd-1
 *
 * @param {number} selStart 选中内容开始位置
 * @param {number} selEnd 选中内容结束位置
 */
$.fn.setSelection = function(selStart, selEnd) {
    var input;
    if(this.length == 0) {
        return this;
    }
    input = this[0];

    if (input.createTextRange) {
        var range = input.createTextRange();
        range.collapse(true);
        range.moveEnd('character', selStart);
        range.moveStart('character', selEnd);
        range.select();
    } else if (input.setSelectionRange) {
        input.focus();
        input.setSelectionRange(selStart, selEnd);
    }

    return this;
};

/**
 * 得到焦点
 *
 * @param {number} position 光标位置
 */
$.fn.diFocus = function(position) {
    if (position && typeof position === 'number') {
        this.setCursorPosition(position);
    } else {
        this.setCursorPosition(this.val().length);
    }
    return this;
};

//------------------------------------------
// 实用工具函数扩展区
//------------------------------------------

/**
 * 获取光标位置
 *
 * @param {HTMLElement} el dom对象
 */
$.getCursorPosition = function (el) {
    var curIndex = -1;
    var range;

    if (el.setSelectionRange){    // W3C
        curIndex = el.selectionStart;
    } else {    // IE
        range = document.selection.createRange();
        range.moveStart("character", -el.value.length);
        curIndex = range.text.length;
    }
    return curIndex;
}