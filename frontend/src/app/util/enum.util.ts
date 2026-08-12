export class EnumUtil {

  /**
   * Extracts enum values as strings from given enum type.
   *
   * @param enumType
   * @returns {Array<string>}
   */
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  public static enumValues(enumType: any): Array<string> {
    const enumNames: Array<string> = [];
    for (const item in enumType) {
      if (isNaN(parseInt(item, 10))) {
        enumNames.push(item);
      }
    }
    return enumNames;
  }
}
