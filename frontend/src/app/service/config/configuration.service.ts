import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError} from 'rxjs/internal/operators';
import {ErrorHandler} from '../error/error-handler.service';
import {Configuration} from '@model/config/configuration';
import {HttpUtil} from '@util/http.util';
import {NumberUtil} from '@util/number.util';

const CODESET_URL = '/api/configurations';

@Injectable()
export class ConfigurationService {
  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  public getConfigurations(): Observable<Array<Configuration>> {
    return this.http.get<Configuration[]>(CODESET_URL).pipe(
      catchError(err => this.errorHandler.handle(HttpUtil.extractMessage(err)))
    );
  }

  public save(configuration: Configuration): Observable<Configuration> {
    if (NumberUtil.isExisting(configuration)) {
      return this.update(configuration.id, configuration);
    } else {
      throwError(new Error('Creating new configurations is not implemented'));
    }
  }

  private update(id: number, configuration: Configuration): Observable<Configuration> {
    const url = `${CODESET_URL}/${id}`;
    return this.http.put<Configuration>(url, JSON.stringify(configuration)).pipe(
      catchError(err => this.errorHandler.handle(HttpUtil.extractMessage(err)))
    );
  }
}
