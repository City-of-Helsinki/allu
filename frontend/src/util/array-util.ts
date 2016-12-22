import {findTranslation} from './translations';
const DIGITS = /(\d+)|(\D+)/g;

export class ArrayUtil {
  static naturalSort<T>(valueFn: (item: T) => string): (l: T, r: T) => number {
    return (left, right) => ArrayUtil.naturalCompare(valueFn(left), valueFn(right));
  }

  static naturalSortTranslated<T>(prefix: Array<string>, valueFn: (item: T) => string): (l: T, r: T) => number {
    return ArrayUtil.naturalSort((item: T) => findTranslation(prefix.concat([valueFn(item)])));
  }


  private static naturalCompare(left, right): number {
    return ArrayUtil.compare(ArrayUtil.toParts(left), ArrayUtil.toParts(right));
  }

  private static toParts(full): Array<[number, string]> {
    let parts: Array<[number, string]> = [];
    full.replace(DIGITS, (match, numbers, text) => parts.push([numbers || Infinity, text || '']));
    return parts;
  }

  private static compare(left: Array<[number, string]>, right: Array<[number, string]>): number {
    while (left.length && right.length) {
      let leftHead = left.shift();
      let rightHead = right.shift();
      let result = (leftHead[0] - rightHead[0]) || leftHead[1].localeCompare(rightHead[1], 'fi', { sensitivity: 'accent'});
      if (result) {
        return result; // We got difference between values since result <> 0
      }
    }
    return left.length - right.length;
  }
}


