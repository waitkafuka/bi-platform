/*
 * Copyright 2009 Young li Inc. All rights reserved.
 * 
 * path: string.js
 * author: erik
 * version: 1.1.0
 * date: 2009/12/16
 */

///import Youngli.string;

Youngli.string = Youngli.string || {};

Youngli.string.trim = function (source) {
    return String(source)
            .replace(new RegExp("(^[\\s\\t\\xa0\\u3000]+)|([\\u3000\\xa0\\s\\t]+\x24)", "g"), "");
};

/**
 * 将目标字符串进行驼峰化处理
 * 
 * @param {string} source 目标字符串
 * @return {string} 驼峰化处理后的字符串
 */
Youngli.string.toCamelCase = function (source) {
    return String(source).replace(/[-_]\D/g, function (match) {
                return match.charAt(1).toUpperCase();
            });
};

/**
 * 声明快捷方式
 */
var toCameCase = Youngli.string.toCamelCase;
var trim = Youngli.string.trim;
var string = Youngli.string;

