import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';
import {UiConfiguration} from '../../model/config/ui-configuration';
import {EnvironmentType} from '../../model/config/environment-type';
import {ReplaySubject} from 'rxjs/ReplaySubject';
import {HttpClient} from '@angular/common/http';

export const CONFIG_URL = '/api/uiconfig';

interface BackendUiConfiguration {
  environment: string;
  oauth2AuthorizationEndpointUrl: string;
  versionNumber: string;
}

@Injectable()
export class ConfigService {

  private configuration$ = new ReplaySubject<UiConfiguration>();

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
    this.loadConfiguration();
  }

  getConfiguration(): Observable<UiConfiguration> {
    return this.configuration$.asObservable().first();
  }

  isStagingOrProduction(): Observable<boolean> {
    return this.getConfiguration().map(conf =>
      (conf.environment === EnvironmentType.STAGING || conf.environment === EnvironmentType.PRODUCTION));
  }

  private loadConfiguration(): void {
    this.http.get<BackendUiConfiguration>(CONFIG_URL)
      .catch(error => this.errorHandler.handle(error, findTranslation('config.error.fetch')))
      .subscribe(config => this.configuration$.next(this.mapConfiguration(config)));
  }

  private mapConfiguration(configJson: BackendUiConfiguration): UiConfiguration {
    return new UiConfiguration(
      EnvironmentType[configJson.environment],
      configJson.oauth2AuthorizationEndpointUrl,
      configJson.versionNumber);
  }
}
