import {findTranslation} from './translations';
const DIGITS = /(\d+)|(\D+)/g;

export class ArrayUtil {
  static naturalSort<T>(valueFn: (item: T) => string): (l: T, r: T) => number {
    return (left, right) => ArrayUtil.naturalCompare(valueFn(left), valueFn(right));
  }

  static naturalSortTranslated<T>(prefix: Array<string>, valueFn: (item: T) => string): (l: T, r: T) => number {
    return ArrayUtil.naturalSort((item: T) => findTranslation(prefix.concat([valueFn(item)])));
  }

  static first<T>(array: Array<T>, filterFn?: (item: T) => boolean): T {
    if (array) {
      const filter = filterFn || ((item: T) => true);
      return array.filter(filter)[0];
    } else {
      return undefined;
    }
  }

  static rest<T>(array: Array<T>, filterFn?: (item: T) => boolean): T[] {
    if (array) {
      const filter = filterFn || ((item: T) => true);
      return array.filter(filter).slice(1);
    } else {
      return undefined;
    }
  }

  static last<T>(array: Array<T>, filterFn?: (item: T) => boolean): T {
    if (array) {
      return ArrayUtil.first(array.reverse());
    } else {
      return undefined;
    }
  }

  static anyMatch<T>(left: T[], right: T[]): boolean {
    if (left === right) {
      return true;
    } else {
      return left.some(l => right.some(r => r === l));
    }
  }

  static containSame(left: Array<any>, right: Array<any>): boolean {
    const lengthEqual = left.length === right.length;
    const allItems = left.every(lItem => right.indexOf(lItem) >= 0);
    return lengthEqual && allItems;
  }

  static contains<T>(included: T[], tested: T): boolean {
    return included.indexOf(tested) >= 0;
  }

  static numberArrayEqual(left: Array<number>, right: Array<number>): boolean {
    return ArrayUtil.compareNumeric(left.slice(), right.slice()) === 0;
  }

  static createOrReplace<T>(array: Array<T>, item: T, predicate: (item: T) => boolean): Array<T> {
    if (array) {
      if (array.some(predicate)) {
        return array.map(original => {
          if (predicate.call(this, original)) {
            return item;
          } else {
            return original;
          }
        });
      } else {
        return array.concat(item);
      }
    } else {
      return [item];
    }
  }

  static createOrReplaceAt<T>(array: T[] = [], item: T, index: number): T[] {
    const result = [...array];
    if (index < result.length) {
      result[index] = item;
    } else {
      result.push(item);
    }
    return result;
  }

  static uniqueItem(valueFn?: (item) => any): (value: any, index: number, self: any[]) => boolean {
    return (value, index, self) => self
      .map(item => valueFn(item))
      .indexOf(valueFn(value)) === index;
  }

  static unique(value: any, index: number, self: any[]): boolean {
    return self.indexOf(value) === index;
  }

  static flatten(array: Array<any>): Array<any> {
    if (!Array.isArray(array)) {
      throw new Error(`Not an array ${ array}`);
    }

    if (array.length && array.every(entry => Array.isArray(entry))) {
      return array.reduce((prev, cur) => prev.concat(this.flatten(cur)));
    } else {
      return array;
    }
  }

  static map<T, R>(array: Array<T>, mapFn: (val: T) => R): Array<R> {
    return array ? array.map(mapFn) : undefined;
  }

  private static compareNumeric(left: Array<number>, right: Array<number>): number {
    while (left.length && right.length) {
      const leftHead = left.shift();
      const rightHead = right.shift();
      const result = leftHead - rightHead;
      if (result) {
        return result; // We got difference between values since result <> 0
      }
    }
    return left.length - right.length;
  }

  private static naturalCompare(left, right): number {
    return ArrayUtil.compareParts(ArrayUtil.toParts(left), ArrayUtil.toParts(right));
  }

  private static toParts(full): Array<[number, string]> {
    if (!!full) {
      const parts: Array<[number, string]> = [];
      full.replace(DIGITS, (match, numbers, text) => parts.push([numbers || Infinity, text || '']));
      return parts;
    } else {
      return [];
    }
  }

  private static compareParts(left: Array<[number, string]>, right: Array<[number, string]>): number {
    while (left.length && right.length) {
      const leftHead = left.shift();
      const rightHead = right.shift();
      const result = (leftHead[0] - rightHead[0]) || leftHead[1].localeCompare(rightHead[1], 'fi', { sensitivity: 'accent'});
      if (result) {
        return result; // We got difference between values since result <> 0
      }
    }
    return left.length - right.length;
  }
}


