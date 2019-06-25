/**
 * Helpers for string handling and conversions
 */
import {formatDate} from '@angular/common';

export class StringUtil {
  public static filterNumbers(stringArray: string[]): number[] {
    return stringArray
      .map(str => +str)
      .filter(nbr => !isNaN(nbr));
  }

  public static filterStrings(stringArray: string[]): string[] {
    return stringArray
      .filter(str => isNaN(+str));
  }

  public static isEmpty(s: string): boolean {
    return (s === undefined || s === null || s.length === 0);
  }

  public static replaceNull(s: string): string {
    return !!s ? s.replace('null', '') : s;
  }

  public static toPath(p: string | string[], separator?: string): string {
    let pathString = '';
    if (Array.isArray(p)) {
      const pathParts = p.filter(item => !StringUtil.isEmpty(item));
      pathString = pathParts.join(separator || '.');
    } else {
      pathString = p;
    }
    return pathString;
  }

  public static capitalize(s: string): string {
    if (s && s.length) {
      return s.charAt(0).toLocaleUpperCase() + s.slice(1);
    } else {
      return s;
    }
  }
}

export function flattenToString(obj: any, localeId?: string): string {
  let returnVal = '';

  Object.values(obj).forEach((val) => {
    if (val instanceof Date) {
      returnVal = returnVal + ' ' + formatDate(val, 'short', localeId);
    } else if (typeof val !== 'object') {
      returnVal = returnVal + ' ' + val;
    } else if (val !== null) {
      returnVal = returnVal + ' ' + flattenToString(val);
    }
  });

  return returnVal.trim().toLowerCase();
}
