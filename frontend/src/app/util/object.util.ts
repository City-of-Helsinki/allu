import isEqualWith from 'lodash/isEqualWith';
import set from 'lodash/set';

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

  static set(source: any, path: string | string[], value: any): any {
    const cloned = ObjectUtil.clone(source);
    return set(cloned, path, value);
  }
}

export interface DictionaryNum<T> {
  [id: number]: T | undefined;
}

export abstract class Dictionary<T> implements DictionaryNum<T> {
  [key: string]: T;
}

export function toDictionary<T>(items: T[], keyFn: (item: T) => string | number): Dictionary<T> {
  return items.reduce((prev: Dictionary<T>, cur: T) => {
    prev[keyFn(cur)] = cur;
    return prev;
  }, {});
}

export function upsert<T>(dictionary: Dictionary<T>, key: string | number, value: T): Dictionary<T> {
  return {
    ...dictionary,
    [key]: value
  };
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

export function isDefined(val: any): boolean {
  return val !== undefined && val !== null;
}

export function isEqualWithSkip(left: any, right: any, skippedFields: string[] = []): boolean {
  const skipped = toDictionary(skippedFields, item => item);
  return isEqualWith(left, right, (l, r, key: string) => {
    if (skipped[key]) {
      return !isDefined(l) || !isDefined(r) || l === r;
    } else {
      return undefined;
    }
  });
}
