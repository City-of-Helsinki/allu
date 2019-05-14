import {StringUtil} from '@util/string.util';

const NUMBER_MATCH = /^\d+/;

/**
 * Address class to parse and group up street name and number
 */
export class StreetAddress {

  constructor(public streetName: string, public specifier: string) {}

  public static fromAddressString(input: string = ''): StreetAddress {
    const inputParts = input.split(' ');
    return inputParts.reduce((address: StreetAddress, part: string) => {
      const specifierEncounteredEarlier = !StringUtil.isEmpty(address.specifier);
      const specifierEncountered = !StringUtil.isEmpty(address.streetName) && part.match(NUMBER_MATCH);

      if (specifierEncounteredEarlier || specifierEncountered) {
        address.specifier = address.specifier ? `${address.specifier} ${part}` : part;
      } else {
        address.streetName = address.streetName ? `${address.streetName} ${part}` : part;
      }
      return address;
    }, new StreetAddress('', undefined));
  }
}
