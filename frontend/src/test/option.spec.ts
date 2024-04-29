import '../app/util/option.ts';
import {Some, None} from '../app/util/option';
import {NoneOpt} from '../app/util/option';

describe('Option', () => {
  it('Should be defined according to class', () => {
    expect(None().isDefined()).toBeFalsy('None was defined');
    expect(Some(1).isDefined()).toBeTruthy('Some was not defined');
  });

  it('Some should return value', () => {
    expect(Some(1).value()).toBe(1, 'Some returned unexpected value');
  });

  it('None should throw when asked for value', () => {
    expect(() => None().value()).toThrow(new Error('No value'));
  });

  it('Some should map value', () => {
    expect(Some(1).map(val => val + 1).value()).toBe(2, 'Some map (1+1) did not yield 2');
  });

  it('None should map as none', () => {
    expect(None().map(val => val + 1)).toEqual(jasmine.any(NoneOpt));
  });

  it('Some should create None when passed value is undefined', () => {
    expect(Some(undefined).map(val => val + 1)).toEqual(jasmine.any(NoneOpt));
  });

  it('Some.do should work with value 0', () => {
    let val = 1;
    Some(0).do(v => val = v);
    expect(val).toBe(0);
  });
});
