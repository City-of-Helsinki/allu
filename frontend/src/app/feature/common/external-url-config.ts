export interface ExternalUrlConfig {
  prefix: string;
  url: string;
  urlSuffix?: string;
}

export const externalUrls = {
  lupapiste: {
    prefix: 'lp-091',
    url: 'https://www.lupapiste.fi/app/fi/authority#!/application',
    urlSuffix: 'applicationSummary'
  }
};

function createUrl(config: ExternalUrlConfig, identificationNumber: string): string {
  return config
    ? `${config.url}/${identificationNumber}/${config.urlSuffix}`
    : undefined;
}

export function getByIdentifier(identificationNumber: string): string {
  const matching =  Object.keys(externalUrls)
    .map(key => externalUrls[key])
    .filter(link => !!identificationNumber && identificationNumber.startsWith(link.prefix));

  return matching.length > 0
    ? createUrl(matching[0], identificationNumber)
    : undefined;
}

