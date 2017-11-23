import {StringUtil} from '../../util/string.util';

/**
 * Address class to parse and group up street name and number
 */
export class StreetAddress {
  private static DEFAULT_STREET_NUMBER = 1;

  constructor(public streetName: string, public streetNumber: number) {}

  public static fromAddressString(input: string) {
    const streetAddress = new StreetAddress('', undefined);

    if (input) {
      const parts = input.split(' ').filter(Boolean);
      const nameParts = StringUtil.filterStrings(parts);
      const numberParts = StringUtil.filterNumbers(parts);

      streetAddress.streetName = nameParts.join(' ');
      streetAddress.streetNumber = numberParts.length > 0 ? numberParts[0] : StreetAddress.DEFAULT_STREET_NUMBER;
    }
    return streetAddress;
  }
}
