"use strict";
exports.__esModule = true;
var moment = require("moment");
var time_util_1 = require("../../src/util/time.util");
describe('Time util', function () {
    it('should show is in winter time', function () {
        expect(time_util_1.TimeUtil.isInWinterTime(moment('2017-12-01').toDate()))
            .toBe(true, 'Winter start date was not in winter time');
        expect(time_util_1.TimeUtil.isInWinterTime(moment('2018-05-13').toDate()))
            .toBe(true, 'Winter end date was not in winter time');
        expect(time_util_1.TimeUtil.isInWinterTime(moment('2017-01-01').toDate()))
            .toBe(true, 'Winter date was not in winter time');
    });
    it('should map given date to the end of winter time current year', function () {
        var original = moment('2017-01-01');
        var winterTimeEnd = time_util_1.TimeUtil.toWinterTimeEnd(original.toDate());
        expect(winterTimeEnd).toEqual(time_util_1.WINTER_TIME_END.year(original.year()).toDate());
    });
    it('should map given date before year change to winter time end next year', function () {
        var original = moment('2016-12-12');
        var winterTimeEnd = time_util_1.TimeUtil.toWinterTimeEnd(original.toDate());
        expect(winterTimeEnd).toEqual(time_util_1.WINTER_TIME_END.year(original.year() + 1).toDate());
    });
});
