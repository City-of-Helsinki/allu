import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {catchError, map} from 'rxjs/internal/operators';
import {ErrorHandler} from '../error/error-handler.service';
import {Configuration} from '@model/config/configuration';
import {HttpUtil} from '@util/http.util';

const CODESET_URL = '/api/configurations';

@Injectable()
export class ConfigurationService {
  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  public getConfigurations(): Observable<Array<Configuration>> {
    return this.http.get<Configuration[]>(CODESET_URL).pipe(
      map(configuration => configuration),
      catchError(err => this.errorHandler.handle(HttpUtil.extractMessage(err)))
    );
  }
}
