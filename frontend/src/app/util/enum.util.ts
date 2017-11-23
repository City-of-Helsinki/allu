export class EnumUtil {

  /**
   * Extracts enum values as strings from given enum type.
   *
   * @param enumType
   * @returns {Array<string>}
   */
  public static enumValues(enumType: any): Array<string> {
    let enumNames: Array<string> = [];
    for (let item in enumType) {
      if (isNaN(parseInt(item, 10))) {
        enumNames.push(item);
      }
    }
    return enumNames;
  }
}
