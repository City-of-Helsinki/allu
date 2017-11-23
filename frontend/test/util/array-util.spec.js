"use strict";
exports.__esModule = true;
var array_util_1 = require("../../src/util/array-util");
describe('Array util', function () {
    it('should sort alphabetical values', function () {
        var array = ['b', 'c', 'a'];
        expect(array.sort(array_util_1.ArrayUtil.naturalSort(function (item) { return item; }))).toEqual(['a', 'b', 'c']);
    });
    it('should sort numerical characters', function () {
        var array = ['50', '5', '1', '2'];
        expect(array.sort(array_util_1.ArrayUtil.naturalSort(function (item) { return item; }))).toEqual(['1', '2', '5', '50']);
    });
    it('should sort aphanumerics', function () {
        var array = ['efg 20', 'abc 2', 'abc 10', 'abc 1'];
        expect(array.sort(array_util_1.ArrayUtil.naturalSort(function (item) { return item; }))).toEqual(['abc 1', 'abc 2', 'abc 10', 'efg 20']);
    });
    it('should sort umlauts', function () {
        var array = ['ääkköset', 'aakkoset'];
        expect(array.sort(array_util_1.ArrayUtil.naturalSort(function (item) { return item; }))).toEqual(['aakkoset', 'ääkköset']);
    });
    it('should handle numbers between text', function () {
        var array = ['abc 5676 abc', 'abc 123 abc'];
        expect(array.sort(array_util_1.ArrayUtil.naturalSort(function (item) { return item; }))).toEqual(['abc 123 abc', 'abc 5676 abc']);
    });
    it('should sort objects based on field', function () {
        var array = [{ key: 'b' }, { key: 'c' }, { key: 'a' }];
        expect(array.sort(array_util_1.ArrayUtil.naturalSort(function (item) { return item.key; })))
            .toEqual([{ key: 'a' }, { key: 'b' }, { key: 'c' }]);
    });
});
