export class ObjectUtil {
  static filter(source: any, filterFn: (fieldName: string) => any) {
    if (typeof source !== 'object') {
      throw new Error('Filtered object must be object');
    } else {
      return Object.keys(source)
        .filter(key => filterFn(key))
        .reduce((prev: any, key: string) => {
          prev[key] = source[key];
          return prev;
        }, {});
    }
  }

  static clone(source: any) {
    if (typeof source !== 'object') {
      return source;
    } else if (source.constructor === Array) {
      return source.map(val => ObjectUtil.clone(val));
    } else {
      let key;
      let value;
      const cloned = Object.create(source);

      for (key in source) {
        if (source.hasOwnProperty(key)) {
          value = source[key];

          if (!!value && value instanceof Date) {
            cloned[key] = new Date(value.getTime());
          } else if (!!value && value.constructor === Array) {
            cloned[key] = value.map(entry => ObjectUtil.clone(entry));
          } else if (!!value && typeof value === 'object') {
            cloned[key] = ObjectUtil.clone(value);
          } else {
            cloned[key] = value;
          }
        }
      }
      return cloned;
    }
  }

  static equal(first: any, second: any): boolean {
    if (first === second) {
      return true;
    } else if (first === undefined || second === undefined) {
      return false;
    } else {
      let prop;
      for (prop in first) {
        if (first.hasOwnProperty(prop) !== second.hasOwnProperty(prop)) {
          return false;
        } else if (typeof first[prop] !== typeof second[prop]) {
          return false;
        } else {
          switch (typeof (first[prop])) {
            case 'object':
            case 'function':
              if (!ObjectUtil.equal(first[prop], second[prop])) {
                return false;
              }
              break;
            default:
              if (first[prop] !== second[prop]) {
                return false;
              }
              break;
          }
        }
      }
      return true;
    }
  }
}

export interface Dictionary<T> {
  [key: string]: T;
}

export function toDictionary<T>(items: T[], keyFn: (item: T) => string): Dictionary<T> {
  return items.reduce((prev: { [key: string]: T }, cur: T) => {
    prev[keyFn(cur)] = cur;
    return prev;
  }, {});
}

export type ValueType = undefined | 'number' | 'string' | 'boolean' | 'date';

export function isNumber(value: any): boolean {
  return typeof value === 'number';
}

export function isString(value: any): boolean {
  return typeof value === 'string';
}

export function isBoolean(value: any): boolean {
  return typeof value === 'boolean';
}

export function isDate(value: any): boolean {
  return value instanceof Date;
}

export function typeOfValue(value: any): ValueType {
  if (isNumber(value)) {
    return 'number';
  } else if (isString(value)) {
    return 'string';
  } else if (isBoolean(value)) {
    return 'boolean';
  } else if (isDate(value)) {
    return 'date';
  } else {
    return undefined;
  }
}
