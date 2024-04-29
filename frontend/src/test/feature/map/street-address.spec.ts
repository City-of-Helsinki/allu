import {StreetAddress} from '@feature/map/street-address';

describe('StreetAddress', () => {
  it('should parse street name', () => {
    const address = 'Some street name';
    const streetAddress = StreetAddress.fromAddressString(address);
    expect(streetAddress.streetName).toEqual(address);
    expect(streetAddress.specifier).toBeUndefined();
  });

  it('should parse street name with number', () => {
    const streetName = 'Some street name with';
    const streetNumber = '15';
    const address = `${streetName} ${streetNumber}`;
    const streetAddress = StreetAddress.fromAddressString(address);
    expect(streetAddress.streetName).toEqual(streetName);
    expect(streetAddress.specifier).toEqual(streetNumber);
  });

  it('should parse street name and street number with letter', () => {
    const streetName = 'Some street name with';
    const streetNumber = '15a';
    const address = `${streetName} ${streetNumber}`;
    const streetAddress = StreetAddress.fromAddressString(address);
    expect(streetAddress.streetName).toEqual(streetName);
    expect(streetAddress.specifier).toEqual(streetNumber);
  });

  it('should parse street name and street number range', () => {
    const streetName = 'Some street name with';
    const streetNumber = '15-21';
    const address = `${streetName} ${streetNumber}`;
    const streetAddress = StreetAddress.fromAddressString(address);
    expect(streetAddress.streetName).toEqual(streetName);
    expect(streetAddress.specifier).toEqual(streetNumber);
  });

  it('should parse street name and street number and something else', () => {
    const streetName = 'Some street name with';
    const streetNumber = '15';
    const rest = 'a3';
    const address = `${streetName} ${streetNumber} ${rest}`;
    const streetAddress = StreetAddress.fromAddressString(address);
    expect(streetAddress.streetName).toEqual(streetName);
    expect(streetAddress.specifier).toEqual(`${streetNumber} ${rest}`);
  });
});
