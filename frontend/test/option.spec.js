"use strict";
exports.__esModule = true;
require("../src/util/option.ts");
var option_1 = require("../src/util/option");
var option_2 = require("../src/util/option");
describe('Option', function () {
    it('Should be defined according to class', function () {
        expect(option_1.None().isDefined()).toBeFalsy('None was defined');
        expect(option_1.Some(1).isDefined()).toBeTruthy('Some was not defined');
    });
    it('Some should return value', function () {
        expect(option_1.Some(1).value()).toBe(1, 'Some returned unexpected value');
    });
    it('None should throw when asked for value', function () {
        expect(function () { return option_1.None().value(); }).toThrow(new Error('No value'));
    });
    it('Some should map value', function () {
        expect(option_1.Some(1).map(function (val) { return val + 1; }).value()).toBe(2, 'Some map (1+1) did not yield 2');
    });
    it('None should map as none', function () {
        expect(option_1.None().map(function (val) { return val + 1; })).toEqual(jasmine.any(option_2.NoneOpt));
    });
    it('Some should create None when passed value is undefined', function () {
        expect(option_1.Some(undefined).map(function (val) { return val + 1; })).toEqual(jasmine.any(option_2.NoneOpt));
    });
    it('Some.do should work with value 0', function () {
        var val = 1;
        option_1.Some(0)["do"](function (v) { return val = v; });
        expect(val).toBe(0);
    });
});
