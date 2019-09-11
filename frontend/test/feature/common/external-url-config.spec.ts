import {getByIdentifier, externalUrls} from '@feature/common/external-url-config';

describe('external url config', () => {
  it('should return config when prefix matches', () => {
    const identificationNumber = 'lp-091111';
    const config = getByIdentifier(identificationNumber);
    expect(config).toBeTruthy();
    const expected = `${externalUrls.lupapiste.url}/${identificationNumber}/${externalUrls.lupapiste.urlSuffix}`;
    expect(config).toEqual(expected);
  });

  it('should handle null value', () => {
    const config = getByIdentifier(null);
    expect(config).toBeUndefined();
  });
});
