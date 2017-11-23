import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {ErrorInfo} from '../ui-state/error-info';
import {HttpUtil} from '../../util/http.util';
import {Router} from '@angular/router';
import {HttpStatus} from '../../util/http-response';

@Injectable()
export class ErrorHandler {
  constructor(private router: Router) {}

  handle(error: any, message?: string): Observable<any> {
    const response = HttpUtil.extractHttpResponse(error);
    console.error('Status:', response.status, 'original message:', response.message);

    if (HttpStatus.UNAUTHORIZED === response.status) {
      this.router.navigate(['/home']);
    }
    return Observable.throw(ErrorInfo.of(response, message));
  }
}
