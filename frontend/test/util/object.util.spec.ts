import {ObjectUtil} from '../../src/app/util/object.util';
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
});
