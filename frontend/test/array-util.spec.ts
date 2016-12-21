import {ArrayUtil} from '../src/util/array-util';

describe('ErrorType', () => {
  it('should sort alphabetical values', () => {
    let array = ['b', 'c', 'a'];
    expect(array.sort(ArrayUtil.naturalSort((item: string) => item))).toEqual(['a', 'b', 'c']);
  });

  it('should sort numerical characters', () => {
    let array = ['50', '5', '1', '2'];
    expect(array.sort(ArrayUtil.naturalSort((item: string) => item))).toEqual(['1', '2', '5', '50']);
  });

  it('should sort aphanumerics', () => {
    let array = ['efg 20', 'abc 2', 'abc 10', 'abc 1'];
    expect(array.sort(ArrayUtil.naturalSort((item: string) => item))).toEqual(['abc 1', 'abc 2', 'abc 10', 'efg 20']);
  });

  it('should sort umlauts', () => {
    let array = ['ääkköset', 'aakkoset'];
    expect(array.sort(ArrayUtil.naturalSort((item: string) => item))).toEqual(['aakkoset', 'ääkköset']);
  });

  it('should handle numbers between text', () => {
    let array = ['abc 5676 abc', 'abc 123 abc'];
    expect(array.sort(ArrayUtil.naturalSort((item: string) => item))).toEqual(['abc 123 abc', 'abc 5676 abc']);
  });

  it('should sort objects based on field', () => {
    let array = [{key: 'b'}, {key: 'c'}, {key: 'a'}];
    expect(array.sort(ArrayUtil.naturalSort((item: {key: string} ) => item.key)))
      .toEqual([{key: 'a'}, {key: 'b'}, {key: 'c'}]);
  });
});
