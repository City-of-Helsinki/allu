
const ComparisonUtil = require('../util/comparison-util');

describe('Test util', () => {
  describe('Deep compare', () => {
    it('should find equal', () => {
      let a = {a: 1, b: 2};
      let b = {a: 1, b: 2};
      expect(ComparisonUtil.deepCompareNonNull('', a, b)).toEqual([]);
    });

    it('should find missing key', () => {
      let a = {a: 1, b: 2, c: 3};
      let b = {a: 1, b: 2};
      expect(ComparisonUtil.deepCompareNonNull('', a, b)).toEqual([{ path: '/c', original: 3, compared: undefined }]);
    });

    it('should find missing key recursively', () => {
      let a = {a: {a1: 11, a2: 22}, b: 2};
      let b = {a: {a1: 11}, b: 2};
      expect(ComparisonUtil.deepCompareNonNull('', a, b)).toEqual([{ path: '/a/a2', original:22, compared: undefined }]);
    });

    it('should find different value', () => {
      let a = {a: 1, b: 3};
      let b = {a: 1, b: 2};
      expect(ComparisonUtil.deepCompareNonNull('', a, b)).toEqual([{ path: '/b', original: 3, compared: 2 }]);
    });

    it('should find different value recursively', () => {
      let a = {a: {a1: 11, a2: 22}, b: 2};
      let b = {a: {a1: 11, a2: 33}, b: 2};
      expect(ComparisonUtil.deepCompareNonNull('', a, b)).toEqual([{ path: '/a/a2', original:22, compared: 33 }]);
    });
  });
});
