import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {ErrorInfo} from '../ui-state/error-info';
import {HttpUtil} from '../../util/http.util';

@Injectable()
export class ErrorHandler {
  handle(error: any, message?: string): Observable<any> {
    let response = HttpUtil.extractHttpResponse(error);
    console.error('Status:', response.status, 'original message:', response.message);
    return Observable.throw(ErrorInfo.of(response, message));
  }
}
