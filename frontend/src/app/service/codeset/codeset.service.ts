import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt';
import {Observable} from 'rxjs/Observable';
import {ErrorHandler} from '../error/error-handler.service';
import {HttpUtil} from '../../util/http.util';
import {CodeSet} from '../../model/codeset/codeset';
import {CodeSetMapper} from '../mapper/codeset-mapper';

const CODESET_URL = '/api/codesets';

@Injectable()
export class CodeSetService {

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {
  }

  public getCountries(): Observable<Array<CodeSet>> {
    return this.authHttp.get(CODESET_URL + '/Country')
      .map(response => response.json())
      .map(codesets => codesets.map(codeSet => CodeSetMapper.mapBackend(codeSet)))
      .catch(err => this.errorHandler.handle(HttpUtil.extractMessage(err)));
  }
}
