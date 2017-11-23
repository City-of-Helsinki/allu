"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/**
 * Helpers for string handling and conversions
 */
var StringUtil = /** @class */ (function () {
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
        return !!s ? s.replace('null', '') : s;
    };
    StringUtil.toPath = function (p, separator) {
        var pathString = '';
        if (Array.isArray(p)) {
            pathString = p.join(separator || '.');
        }
        else {
            pathString = p;
        }
        return pathString;
    };
    StringUtil.toUppercase = function (s) {
        return s ? s.toLocaleUpperCase() : s;
    };
    StringUtil.capitalize = function (s) {
        if (s && s.length) {
            return s.charAt(0).toLocaleUpperCase() + s.slice(1);
        }
        else {
            return s;
        }
    };
    return StringUtil;
}());
exports.StringUtil = StringUtil;
