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
}
