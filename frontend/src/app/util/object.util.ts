import isEqualWith from 'lodash/isEqualWith';
import set from 'lodash/set';

export class ObjectUtil {
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  static filter(source: any, filterFn: (fieldName: string) => any) {
    if (typeof source !== 'object') {
      throw new Error('Filtered object must be object');
    } else {
      return Object.keys(source)
        .filter(key => filterFn(key))
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
        .reduce((prev: any, key: string) => {
          prev[key] = source[key];
          return prev;
        }, {});
    }
  }

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
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
        if (Object.prototype.hasOwnProperty.call(source, key)) {
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

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
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

export type ValueType = undefined | 'number' | 'string' | 'boolean' | 'date';

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
export function isNumber(value: any): boolean {
  return typeof value === 'number';
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
export function isString(value: any): boolean {
  return typeof value === 'string';
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
export function isBoolean(value: any): boolean {
  return typeof value === 'boolean';
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
export function isDate(value: any): boolean {
  return value instanceof Date;
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
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

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
export function isDefined(val: any): boolean {
  return val !== undefined && val !== null;
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
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
