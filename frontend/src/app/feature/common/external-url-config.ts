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
  const matchingConfigs =  matching(identificationNumber);

  return matchingConfigs.length > 0
    ? createUrl(matchingConfigs[0], identificationNumber)
    : undefined;
}

const matching = (identificationNumber: string) => {
  if (!!identificationNumber) {
    return Object.keys(externalUrls)
      .map(key => externalUrls[key])
      .filter(link => identificationNumber.toUpperCase().startsWith(link.prefix.toUpperCase()));
  } else {
    return [];
  }
};
