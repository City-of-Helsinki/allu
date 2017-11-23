"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var moment = require("moment");
exports.MIN_YEAR = 1972;
exports.MAX_YEAR = 9999;
exports.MIN_DATE = new Date('1972-01-01T00:00:00');
exports.MAX_DATE = new Date('9999-12-31T23:59:59');
exports.UI_PIPE_DATE_FORMAT = 'dd.MM.yyyy'; // Used by angular date pipe
exports.UI_DATE_FORMAT = 'DD.MM.YYYY';
exports.UI_DATE_TIME_FORMAT = 'DD.MM.YYYY HH:mm';
var HISTORY_DATE_TIME_FORMAT = 'YYYY-MM-DDTHH:mm:ssZ';
var HISTORY_DATE_FORMAT = 'DD.MM.YYYY';
exports.WINTER_TIME_START = moment('1972-12-01');
exports.WINTER_TIME_END = moment('1972-05-14');
var DAYS_IN_WEEK = 7;
/**
 * Helpers for time related UI functionality.
 */
var TimeUtil = /** @class */ (function () {
    function TimeUtil() {
    }
    TimeUtil.getUiDateString = function (time) {
        return time ? moment(time).format(exports.UI_DATE_FORMAT).toString() : undefined;
    };
    TimeUtil.getUiDateTimeString = function (time) {
        return time ? moment(time).format(exports.UI_DATE_TIME_FORMAT).toString() : undefined;
    };
    TimeUtil.getDateFromUi = function (dateString) {
        var m = this.toMoment(dateString);
        return m ? m.toDate() : undefined;
    };
    TimeUtil.getStartDateFromUi = function (dateString) {
        return dateString ? moment(dateString, exports.UI_DATE_FORMAT).startOf('day').toDate() : undefined;
    };
    TimeUtil.getEndDateFromUi = function (dateString) {
        return dateString ? moment(dateString, exports.UI_DATE_FORMAT).endOf('day').toDate() : undefined;
    };
    TimeUtil.yearFromDate = function (date) {
        return date ? moment(date).year() : undefined;
    };
    TimeUtil.datePlusWeeks = function (date, plusWeeks) {
        var asWeeks = DAYS_IN_WEEK * plusWeeks;
        return date ? moment(date).day(asWeeks).toDate() : undefined;
    };
    TimeUtil.dateWithYear = function (date, year) {
        if (date && year) {
            var baseDate = moment(date);
            return baseDate.year(year).toDate();
        }
        else {
            return undefined;
        }
    };
    TimeUtil.dateFromBackend = function (dateString) {
        return dateString ? moment(dateString).toDate() : undefined;
    };
    TimeUtil.dateToBackend = function (date) {
        return date ? date.toISOString() : undefined;
    };
    TimeUtil.formatHistoryDateTimeString = function (dateTime) {
        return dateTime ? moment(dateTime, HISTORY_DATE_TIME_FORMAT).format(HISTORY_DATE_FORMAT).toString() : undefined;
    };
    TimeUtil.minimum = function () {
        var dates = [];
        for (var _i = 0; _i < arguments.length; _i++) {
            dates[_i] = arguments[_i];
        }
        var moments = dates.map(function (date) { return moment(date); });
        return moment.min.apply(moment, moments).toDate();
    };
    TimeUtil.maximum = function () {
        var dates = [];
        for (var _i = 0; _i < arguments.length; _i++) {
            dates[_i] = arguments[_i];
        }
        var moments = dates.map(function (date) { return moment(date); });
        return moment.max.apply(moment, moments).toDate();
    };
    TimeUtil.add = function (baseDate, amount, unit) {
        if (baseDate === void 0) { baseDate = new Date(); }
        return moment(baseDate).add(amount, unit).toDate();
    };
    /**
     * Returns end of given day i.e. any date 1.1.2001 would be converted to 1.1.2001 23:59.
     *
     * @param date
     * @returns {Date}  end of given day i.e. any date 1.1.2001 would be converted to 1.1.2001 23:59.
     */
    TimeUtil.getEndOfDay = function (date) {
        return moment(date).endOf('day').toDate();
    };
    /**
     * Returns whether first argument is before second
     *
     * @returns {boolean} true when first date is before second or given dates are undefined, otherwise false.
     */
    TimeUtil.isBefore = function (first, second) {
        if (first && second) {
            return moment(first).isBefore(moment(second));
        }
        else {
            return true;
        }
    };
    TimeUtil.isBetweenInclusive = function (date, start, end) {
        return moment(date).isBetween(start, end, undefined, '[]');
    };
    TimeUtil.compareTo = function (left, right) {
        if (left > right) {
            return 1;
        }
        else if (left < right) {
            return -1;
        }
        else {
            return 0;
        }
    };
    TimeUtil.isInWinterTime = function (date) {
        var checked = moment(date).year(exports.WINTER_TIME_START.year());
        return checked.isSameOrAfter(exports.WINTER_TIME_START) || checked.isBefore(exports.WINTER_TIME_END);
    };
    TimeUtil.toWinterTimeEnd = function (date) {
        var checked = moment(date).year(exports.WINTER_TIME_START.year());
        var year = date.getFullYear();
        if (checked.isSameOrAfter(exports.WINTER_TIME_START)) {
            year = year + 1;
        }
        return moment(exports.WINTER_TIME_END).year(year).toDate();
    };
    TimeUtil.toMoment = function (dateString, format) {
        if (format === void 0) { format = exports.UI_DATE_FORMAT; }
        if (dateString) {
            var m = moment(dateString, format);
            return m.isValid() ? m : undefined;
        }
        else {
            return undefined;
        }
        ;
    };
    return TimeUtil;
}());
exports.TimeUtil = TimeUtil;
