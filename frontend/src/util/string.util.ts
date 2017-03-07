/**
 * Helpers for string handling and conversions
 */
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
    return (!s || s.length === 0);
  }

  public static replaceNull(s: string): string {
    return !!s ? s.replace('null', '-') : s;
  }

  public static toPath(p: string | string[], separator?: string): string {
    let pathString = '';
    if (Array.isArray(p)) {
      pathString = p.join(separator || '.');
    } else {
      pathString = p;
    }
    return pathString;
  }
}
