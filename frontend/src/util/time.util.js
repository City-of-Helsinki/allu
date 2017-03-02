"use strict";
var momentLib = require("moment");
// jQuery pickadate configuration: http://amsul.ca/pickadate.js/date/
exports.PICKADATE_PARAMETERS = [
    {
        selectMonths: true,
        selectYears: 15,
        firstDay: 'Ma',
        format: 'dd.mm.yyyy',
        monthsFull: ['Tammikuu', 'Helmikuu', 'Maaliskuu', 'Huhtikuu', 'Toukokuu', 'Kesäkuu',
            'Heinäkuu', 'Elokuu', 'Syyskuu', 'Lokakuu', 'Marraskuu', 'Joulukuu'],
        monthsShort: ['Tammi', 'Helmi', 'Maalis', 'Huhti', 'Touko', 'Kesä', 'Heinä', 'Elo', 'Syys', 'Loka', 'Marras', 'Joulu'],
        weekdaysFull: ['Su', 'Ma', 'Ti', 'Ke', 'To', 'Pe', 'La'],
        showMonthsShort: false,
        showWeekdaysFull: true // a bit counter intuitive way to get right abbreviations to be shown in calendar
    }
];
exports.MIN_DATE = new Date(0);
exports.MAX_DATE = new Date('2099-12-31T23:59:59');
exports.UI_DATE_FORMAT = 'dd.MM.yyyy'; // Used by angular date pipe
exports.UI_DATE_TIME_FORMAT = 'DD.MM.YYYY HH:mm';
var HISTORY_DATE_TIME_FORMAT = 'YYYY-MM-DDTHH:mm:ssZ';
var HISTORY_DATE_FORMAT = 'DD.MM.YYYY';
/**
 * Helpers for time related UI functionality.
 */
var TimeUtil = (function () {
    function TimeUtil() {
    }
    TimeUtil.getUiDateString = function (time) {
        return time ? momentLib(time).format('DD.MM.YYYY').toString() : undefined;
    };
    TimeUtil.getUiDateTimeString = function (time) {
        return time ? momentLib(time).format(exports.UI_DATE_TIME_FORMAT).toString() : undefined;
    };
    TimeUtil.getDateFromUi = function (dateString) {
        return dateString ? momentLib(dateString, 'DD.MM.YYYY').toDate() : undefined;
    };
    TimeUtil.dateFromBackend = function (dateString) {
        return dateString ? momentLib(dateString).toDate() : undefined;
    };
    TimeUtil.formatHistoryDateTimeString = function (dateTime) {
        return dateTime ? momentLib(dateTime, HISTORY_DATE_TIME_FORMAT).format(HISTORY_DATE_FORMAT).toString() : undefined;
    };
    /**
     * Returns end of given day i.e. any date 1.1.2001 would be converted to 1.1.2001 23:59.
     *
     * @param date
     * @returns {Date}  end of given day i.e. any date 1.1.2001 would be converted to 1.1.2001 23:59.
     */
    TimeUtil.getEndOfDay = function (date) {
        return momentLib(date).endOf('day').toDate();
    };
    /**
     * Returns whether first argument is before second
     *
     * @param first date as string
     * @param second date as string
     * @returns {boolean} true when first date is before second or given strings are undefined, otherwise false.
     */
    TimeUtil.isBefore = function (first, second) {
        if (!!first && !!second) {
            return TimeUtil.toMoment(first).isBefore(TimeUtil.toMoment(second));
        }
        else {
            return true;
        }
    };
    TimeUtil.isBetweenInclusive = function (date, start, end) {
        return momentLib(date).isBetween(start, end, undefined, '[]');
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
    TimeUtil.toMoment = function (dateString) {
        return dateString ? momentLib(dateString, 'DD.MM.YYYY') : undefined;
    };
    return TimeUtil;
}());
exports.TimeUtil = TimeUtil;
