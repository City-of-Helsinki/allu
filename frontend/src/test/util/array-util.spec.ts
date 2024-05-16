import {ArrayUtil} from '../../app/util/array-util';

describe('Array util', () => {
  it('should sort alphabetical values', () => {
    const array = ['b', 'c', 'a'];
    expect(array.sort(ArrayUtil.naturalSort((item: string) => item))).toEqual(['a', 'b', 'c']);
  });

  it('should sort numerical characters', () => {
    const array = ['50', '5', '1', '2'];
    expect(array.sort(ArrayUtil.naturalSort((item: string) => item))).toEqual(['1', '2', '5', '50']);
  });

  it('should sort aphanumerics', () => {
    const array = ['efg 20', 'abc 2', 'abc 10', 'abc 1'];
    expect(array.sort(ArrayUtil.naturalSort((item: string) => item))).toEqual(['abc 1', 'abc 2', 'abc 10', 'efg 20']);
  });

  it('should sort umlauts', () => {
    const array = ['ääkköset', 'aakkoset'];
    expect(array.sort(ArrayUtil.naturalSort((item: string) => item))).toEqual(['aakkoset', 'ääkköset']);
  });

  it('should handle numbers between text', () => {
    const array = ['abc 5676 abc', 'abc 123 abc'];
    expect(array.sort(ArrayUtil.naturalSort((item: string) => item))).toEqual(['abc 123 abc', 'abc 5676 abc']);
  });

  it('should sort objects based on field', () => {
    const array = [{key: 'b'}, {key: 'c'}, {key: 'a'}];
    expect(array.sort(ArrayUtil.naturalSort((item: {key: string} ) => item.key)))
      .toEqual([{key: 'a'}, {key: 'b'}, {key: 'c'}]);
  });

  it('should flatten array with 2 levels deep', () => {
    const array = [[1, 2], [3, 4]];
    expect(ArrayUtil.flatten(array)).toEqual([1, 2, 3, 4]);
  });

  it('should result equal on same array', () => {
    const array = [1, 2, 3];
    expect(ArrayUtil.numberArrayEqual(array, array)).toBeTruthy();
  });

  it('should result not equal when other is undefined', () => {
    const array = [1, 2, 3];
    const otherArray = undefined;
    expect(ArrayUtil.numberArrayEqual(array, otherArray)).toBeFalsy();
  });

  it('should result equal on arrays with same values', () => {
    const array = [1, 2, 3];
    const otherArray = [1, 2, 3];
    expect(ArrayUtil.numberArrayEqual(array, otherArray)).toBeTruthy();
  });

  it('should result not equal on arrays with different values', () => {
    const array = [1, 2, 3];
    const otherArray = [1, 2, 4];
    expect(ArrayUtil.numberArrayEqual(array, otherArray)).toBeFalsy();
  });

  it('should result not equal on arrays with different length', () => {
    const array = [1, 2, 3];
    const otherArray = [1, 2, 3, 3];
    expect(ArrayUtil.numberArrayEqual(array, otherArray)).toBeFalsy();
  });
});
