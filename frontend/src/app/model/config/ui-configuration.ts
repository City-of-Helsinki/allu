import {EnvironmentType} from './environment-type';

export class UiConfiguration {
  constructor(
    public environment?: EnvironmentType,
    public oauth2AuthorizationEndpointUrl?: string,
    public versionNumber?: string
  ) {}
}
