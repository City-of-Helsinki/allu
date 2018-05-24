import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ErrorHandler} from '../error/error-handler.service';
import {HttpUtil} from '../../util/http.util';
import {CodeSet} from '../../model/codeset/codeset';
import {CodeSetMapper} from '../mapper/codeset-mapper';
import {HttpClient} from '@angular/common/http';
import {BackendCodeSet} from '../backend-model/backend-codeset';
import {catchError, map} from 'rxjs/internal/operators';

const CODESET_URL = '/api/codesets';

@Injectable()
export class CodeSetService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  public getCountries(): Observable<Array<CodeSet>> {
    return this.http.get<BackendCodeSet[]>(CODESET_URL + '/Country').pipe(
      map(codesets => codesets.map(codeSet => CodeSetMapper.mapBackend(codeSet))),
      catchError(err => this.errorHandler.handle(HttpUtil.extractMessage(err)))
    );
  }
}
