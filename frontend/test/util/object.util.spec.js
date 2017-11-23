"use strict";
exports.__esModule = true;
var object_util_1 = require("../../src/util/object.util");
describe('Object util', function () {
    it('should clone normal properties', function () {
        var original = {
            num: 123,
            text: 'abc'
        };
        expect(object_util_1.ObjectUtil.clone(original)).toEqual(original);
    });
    it('should clone arrays', function () {
        var original = {
            arrayOfObjects: [{ val: 1 }, { val: 2 }],
            arrayOfNumbers: [1, 2, 3],
            arrayOfStrings: ['a', 'b', 'c']
        };
        expect(object_util_1.ObjectUtil.clone(original)).toEqual(original);
    });
    it('should clone dates', function () {
        var original = {
            date: new Date()
        };
        expect(object_util_1.ObjectUtil.clone(original)).toEqual(original);
    });
    it('should clone deep objects', function () {
        var original = {
            layerOne: {
                layerTwo: {
                    layerThree: {
                        value: 1
                    }
                }
            }
        };
        expect(object_util_1.ObjectUtil.clone(original)).toEqual(original);
    });
});
