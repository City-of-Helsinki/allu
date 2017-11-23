"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var CENTS = 100;
var NumberUtil = /** @class */ (function () {
    function NumberUtil() {
    }
    NumberUtil.isDefined = function (num) {
        return !!num || (num === 0);
    };
    NumberUtil.isNumeric = function (num) {
        return num !== undefined && num !== '' && !isNaN(num);
    };
    NumberUtil.isBetween = function (val, min, max) {
        return NumberUtil.isDefined(val) && (min <= val) && (val <= max);
    };
    NumberUtil.toEuros = function (cents) {
        return NumberUtil.isDefined(cents) ? Math.round(cents / CENTS) : undefined;
    };
    NumberUtil.toCents = function (euros) {
        return NumberUtil.isDefined(euros) ? euros * CENTS : undefined;
    };
    return NumberUtil;
}());
exports.NumberUtil = NumberUtil;
