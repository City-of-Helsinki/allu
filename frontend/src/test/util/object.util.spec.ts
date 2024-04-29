import {Dictionary, ObjectUtil, upsert} from '../../app/util/object.util';
describe('Object util', () => {
  it('should clone normal properties', () => {
    const original = {
      num: 123,
      text: 'abc'
    };

    expect(ObjectUtil.clone(original)).toEqual(original);
  });

  it('should clone arrays', () => {
    const original = {
      arrayOfObjects: [{val: 1}, {val: 2}],
      arrayOfNumbers: [1, 2, 3],
      arrayOfStrings: ['a', 'b', 'c']
    };

    expect(ObjectUtil.clone(original)).toEqual(original);
  });

  it('should clone nested arrays', () => {
    const original = [1];

    expect(ObjectUtil.clone(original)).toEqual(original);
  });

  it('should clone dates', () => {
    const original = {
      date: new Date()
    };

    expect(ObjectUtil.clone(original)).toEqual(original);
  });

  it('should clone deep objects', () => {
    const original = {
      layerOne: {
        layerTwo: {
          layerThree: {
            value: 1
          }
        }
      }
    };

    expect(ObjectUtil.clone(original)).toEqual(original);
  });

  it('upsert should update existing value in dictionary', () => {
    const dict: Dictionary<{id: number, value: string}> = {
      1: {id: 1, value: 'one'},
      2: {id: 2, value: 'two'},
    };

    const updatedDict = upsert(dict, 2, {id: 2, value: 'three'});
    expect(updatedDict[2]).toEqual({id: 2, value: 'three'});
  });

  it('upsert should insert non-existing value to dictionary', () => {
    const dict: Dictionary<{id: number, value: string}> = {
      1: {id: 1, value: 'one'},
      2: {id: 2, value: 'two'},
    };

    const updatedDict = upsert(dict, 3, {id: 3, value: 'three'});
    expect(updatedDict[3]).toEqual({id: 3, value: 'three'});
  });
});
