import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {ErrorHandler} from '../error/error-handler.service';
import {HttpUtil} from '../../util/http.util';
import {CodeSet} from '../../model/codeset/codeset';
import {CodeSetMapper} from '../mapper/codeset-mapper';
import {HttpClient} from '@angular/common/http';
import {BackendCodeSet} from '../backend-model/backend-codeset';

const CODESET_URL = '/api/codesets';

@Injectable()
export class CodeSetService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  public getCountries(): Observable<Array<CodeSet>> {
    return this.http.get<BackendCodeSet[]>(CODESET_URL + '/Country')
      .map(codesets => codesets.map(codeSet => CodeSetMapper.mapBackend(codeSet)))
      .catch(err => this.errorHandler.handle(HttpUtil.extractMessage(err)));
  }
}
