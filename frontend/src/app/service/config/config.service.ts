import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt';
import {Observable} from 'rxjs/Observable';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';
import {UiConfiguration} from '../../model/config/ui-configuration';
import {ReplaySubject} from 'rxjs/ReplaySubject';

const CONFIG_URL = '/api/uiconfig';

@Injectable()
export class ConfigService {

  private configuration$ = new ReplaySubject<UiConfiguration>();

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {
    this.loadConfiguration();
  }

  getConfiguration(): Observable<UiConfiguration> {
    return this.configuration$.asObservable().first();
  }

  isProduction(): Observable<boolean> {
    return this.getConfiguration().map(conf => conf.production);
  }

  private loadConfiguration(): void {
    this.authHttp.get(CONFIG_URL)
      .map(response => response.json())
      .catch(error => this.errorHandler.handle(error, findTranslation('config.error.fetch')))
      .subscribe(config => this.configuration$.next(config));
  }
}
