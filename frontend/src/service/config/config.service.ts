import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt';
import {Observable} from 'rxjs/Observable';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';
import {Subject} from 'rxjs/Subject';
import {UiConfiguration} from '../../model/config/ui-configuration';

const CONFIG_URL = '/api/uiconfig';

@Injectable()
export class ConfigService {

  private configuration$ = new Subject<UiConfiguration>();

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {
    this.loadConfiguration();
  }

  getConfiguration(): Observable<UiConfiguration> {
    return this.configuration$.asObservable().first();
  }

  private loadConfiguration(): void {
    this.authHttp.get(CONFIG_URL)
      .map(response => response.json())
      .catch(error => this.errorHandler.handle(error, findTranslation('config.error.fetch')))
      .subscribe(config => this.configuration$.next(config));
  }
}
