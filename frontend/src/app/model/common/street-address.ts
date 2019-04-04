import {StringUtil} from '@util/string.util';
import {ArrayUtil} from '@util/array-util';

const STREET_LETTER_REGEX = /\d+\s*[a-zA-Z]$/;
const NUMBER_FILTER = /[a-zA-Z]$/;
const LETTER_FILTER = /^\d+/;

/**
 * Address class to parse and group up street name and number
 */
export class StreetAddress {

  constructor(public streetName: string, public streetNumber: number, public streetLetter: string) {}

  public static fromAddressString(input: string) {
    const streetAddress = new StreetAddress('', undefined, undefined);

    if (input) {
      if (STREET_LETTER_REGEX.test(input)) {
        streetAddress.streetName = input.replace(STREET_LETTER_REGEX, '').trim();
        const housePart = input.substring(input.search(STREET_LETTER_REGEX));
        streetAddress.streetNumber = Number(housePart.replace(NUMBER_FILTER, '').trim());
        streetAddress.streetLetter = housePart.replace(LETTER_FILTER, '').trim();
      } else {
        const parts = input.split(' ').filter(Boolean);
        const nameParts = StringUtil.filterStrings(parts);
        const numberParts = StringUtil.filterNumbers(parts);

        streetAddress.streetName = nameParts.join(' ');
        streetAddress.streetNumber = ArrayUtil.first(numberParts);
      }
    }
    return streetAddress;
  }
}
