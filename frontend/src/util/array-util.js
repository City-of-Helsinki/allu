"use strict";
var translations_1 = require("./translations");
var DIGITS = /(\d+)|(\D+)/g;
var ArrayUtil = (function () {
    function ArrayUtil() {
    }
    ArrayUtil.naturalSort = function (valueFn) {
        return function (left, right) { return ArrayUtil.naturalCompare(valueFn(left), valueFn(right)); };
    };
    ArrayUtil.naturalSortTranslated = function (prefix, valueFn) {
        return ArrayUtil.naturalSort(function (item) { return translations_1.findTranslation(prefix.concat([valueFn(item)])); });
    };
    ArrayUtil.naturalCompare = function (left, right) {
        return ArrayUtil.compare(ArrayUtil.toParts(left), ArrayUtil.toParts(right));
    };
    ArrayUtil.toParts = function (full) {
        var parts = [];
        full.replace(DIGITS, function (match, numbers, text) { return parts.push([numbers || Infinity, text || '']); });
        return parts;
    };
    ArrayUtil.compare = function (left, right) {
        while (left.length && right.length) {
            var leftHead = left.shift();
            var rightHead = right.shift();
            var result = (leftHead[0] - rightHead[0]) || leftHead[1].localeCompare(rightHead[1], 'fi', { sensitivity: 'accent' });
            if (result) {
                return result; // We got difference between values since result <> 0
            }
        }
        return left.length - right.length;
    };
    return ArrayUtil;
}());
exports.ArrayUtil = ArrayUtil;
