"use strict";
var NumberUtil = (function () {
    function NumberUtil() {
    }
    NumberUtil.isDefined = function (num) {
        return !!num || (num === 0);
    };
    return NumberUtil;
}());
exports.NumberUtil = NumberUtil;
