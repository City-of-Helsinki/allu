import {Injectable} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';
import {UiConfiguration} from '../../model/config/ui-configuration';
import {EnvironmentType} from '../../model/config/environment-type';
import {HttpClient} from '@angular/common/http';
import {catchError, first, map} from 'rxjs/internal/operators';

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
    return this.configuration$.asObservable().pipe(first());
  }

  isStagingOrProduction(): Observable<boolean> {
    return this.getConfiguration().pipe(
      map(conf => (conf.environment === EnvironmentType.STAGING || conf.environment === EnvironmentType.PRODUCTION))
    );
  }

  private loadConfiguration(): void {
    this.http.get<BackendUiConfiguration>(CONFIG_URL).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('config.error.fetch')))
    ).subscribe(config => this.configuration$.next(this.mapConfiguration(config)));
  }

  private mapConfiguration(configJson: BackendUiConfiguration): UiConfiguration {
    return new UiConfiguration(
      EnvironmentType[configJson.environment],
      configJson.oauth2AuthorizationEndpointUrl,
      configJson.versionNumber);
  }
}
