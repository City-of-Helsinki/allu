import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {ErrorInfo} from './error-info';
import {HttpUtil} from '../../util/http.util';
import {Router} from '@angular/router';
import {HttpResponse, HttpStatus} from '../../util/http-response';
import {findTranslation} from '../../util/translations';

@Injectable()
export class ErrorHandler {
  constructor(private router: Router) {}

  handle(error: any, message?: string): Observable<any> {
    const response = HttpUtil.extractHttpResponse(error);
    console.error('Status:', response.status, 'original message:', response.message);

    const title = this.mapStatusToTitle(response);

    if (HttpStatus.UNAUTHORIZED === response.status) {
      this.router.navigate(['/home']);
    }
    return Observable.throw(new ErrorInfo(title, message));
  }

  private mapStatusToTitle(response: HttpResponse): string {
    switch (response.status) {
      case HttpStatus.BAD_REQUEST:
      case HttpStatus.UNAUTHORIZED:
      case HttpStatus.FORBIDDEN:
      case HttpStatus.INTERNAL_SERVER_ERROR:
        return findTranslation(['httpStatus', HttpStatus[response.status]]);
      default:
        return findTranslation('httpStatus.UNKNOWN');
    }
  }
}
