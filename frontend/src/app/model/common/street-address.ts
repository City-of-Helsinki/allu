import {StringUtil} from '../../util/string.util';

// If location is area, not a street api uses 0
export const DEFAULT_STREET_AREA_NUMBER = 0;
// If location is street and no number is provided use 1 as default
export const DEFAULT_STREET_NUMBER = 1;

const STREET_LETTER_REGEX = /\d+\s*[a-zA-Z]$/;
const NUMBER_FILTER = /[a-zA-Z]$/;
const LETTER_FILTER = /^\d+/;

/**
 * Address class to parse and group up street name and number
 */
export class StreetAddress {

  constructor(public streetName: string, public streetNumber: number, public streetLetter: string) {}

  public static fromAddressString(input: string, defaultStreetNumber = DEFAULT_STREET_NUMBER) {
    const streetAddress = new StreetAddress('', undefined, '');

    if (input) {
      if (STREET_LETTER_REGEX.test(input)) {
        streetAddress.streetName = input.replace(STREET_LETTER_REGEX, '').trim();
        const houseParth = input.substring(input.search(STREET_LETTER_REGEX));
        streetAddress.streetNumber = Number(houseParth.replace(NUMBER_FILTER, '').trim());
        streetAddress.streetLetter = houseParth.replace(LETTER_FILTER, '').trim();
      } else {
        const parts = input.split(' ').filter(Boolean);
        const nameParts = StringUtil.filterStrings(parts);
        const numberParts = StringUtil.filterNumbers(parts);

        streetAddress.streetName = nameParts.join(' ');
        streetAddress.streetNumber = numberParts.length > 0 ? numberParts[0] : defaultStreetNumber;
      }
    }
    return streetAddress;
  }
}
