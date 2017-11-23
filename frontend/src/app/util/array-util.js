"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var translations_1 = require("./translations");
var DIGITS = /(\d+)|(\D+)/g;
var ArrayUtil = /** @class */ (function () {
    function ArrayUtil() {
    }
    ArrayUtil.naturalSort = function (valueFn) {
        return function (left, right) { return ArrayUtil.naturalCompare(valueFn(left), valueFn(right)); };
    };
    ArrayUtil.naturalSortTranslated = function (prefix, valueFn) {
        return ArrayUtil.naturalSort(function (item) { return translations_1.findTranslation(prefix.concat([valueFn(item)])); });
    };
    ArrayUtil.first = function (array, filterFn) {
        if (array) {
            var filter = filterFn || (function (item) { return true; });
            return array.filter(filter)[0];
        }
        else {
            return undefined;
        }
    };
    ArrayUtil.last = function (array, filterFn) {
        if (array) {
            return ArrayUtil.first(array.reverse());
        }
        else {
            return undefined;
        }
    };
    ArrayUtil.containSame = function (left, right) {
        var lengthEqual = left.length === right.length;
        var allItems = left.every(function (lItem) { return right.indexOf(lItem) >= 0; });
        return lengthEqual && allItems;
    };
    ArrayUtil.numberArrayEqual = function (left, right) {
        return ArrayUtil.compareNumeric(left.slice(), right.slice()) === 0;
    };
    ArrayUtil.createOrReplace = function (array, item, predicate) {
        var _this = this;
        if (array.some(predicate)) {
            return array.map(function (original) {
                if (predicate.call(_this, original)) {
                    return item;
                }
                else {
                    return original;
                }
            });
        }
        else {
            return array.concat(item);
        }
    };
    ArrayUtil.compareNumeric = function (left, right) {
        while (left.length && right.length) {
            var leftHead = left.shift();
            var rightHead = right.shift();
            var result = leftHead - rightHead;
            if (result) {
                return result; // We got difference between values since result <> 0
            }
        }
        return left.length - right.length;
    };
    ArrayUtil.naturalCompare = function (left, right) {
        return ArrayUtil.compareParts(ArrayUtil.toParts(left), ArrayUtil.toParts(right));
    };
    ArrayUtil.toParts = function (full) {
        if (!!full) {
            var parts_1 = [];
            full.replace(DIGITS, function (match, numbers, text) { return parts_1.push([numbers || Infinity, text || '']); });
            return parts_1;
        }
        else {
            return [];
        }
    };
    ArrayUtil.compareParts = function (left, right) {
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
