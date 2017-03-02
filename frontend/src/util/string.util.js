"use strict";
/**
 * Helpers for string handling and conversions
 */
var StringUtil = (function () {
    function StringUtil() {
    }
    StringUtil.filterNumbers = function (stringArray) {
        return stringArray
            .map(function (str) { return +str; })
            .filter(function (nbr) { return !isNaN(nbr); });
    };
    StringUtil.filterStrings = function (stringArray) {
        return stringArray
            .filter(function (str) { return isNaN(+str); });
    };
    StringUtil.isEmpty = function (s) {
        return (!s || s.length === 0);
    };
    StringUtil.replaceNull = function (s) {
        return !!s ? s.replace('null', '-') : s;
    };
    return StringUtil;
}());
exports.StringUtil = StringUtil;
