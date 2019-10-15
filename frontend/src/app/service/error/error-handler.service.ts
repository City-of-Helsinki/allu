import {Observable, throwError} from 'rxjs';
import {Injectable} from '@angular/core';
import {ErrorInfo} from './error-info';
import {Router} from '@angular/router';
import {findTranslation} from '../../util/translations';
import {HttpErrorResponse} from '@angular/common/http';
import {HttpStatus} from '../../util/http-status';

@Injectable()
export class ErrorHandler {
  constructor(private router: Router) {}

  handle(error: HttpErrorResponse, message?: string): Observable<any> {
    console.error('Status:', error.statusText, 'original message:', error.message);
    if (error.error && error.error[0] && error.error[0].errorMessage) {
      message = error.error[0].errorMessage;
    }

    const title = this.mapStatusToTitle(error);

    this.handleRouting(error.status);

    return throwError(new ErrorInfo(title, message));
  }

  private mapStatusToTitle(error: HttpErrorResponse): string {
    switch (error.status) {
      case HttpStatus.BAD_REQUEST:
      case HttpStatus.UNAUTHORIZED:
      case HttpStatus.FORBIDDEN:
      case HttpStatus.NOT_FOUND:
      case HttpStatus.INTERNAL_SERVER_ERROR:
      case HttpStatus.GATEWAY_TIMEOUT:
      case HttpStatus.CONFLICT:
        return findTranslation(['httpStatus', HttpStatus[error.status]]);
      default:
        return findTranslation('httpStatus.UNKNOWN');
    }
  }

  private handleRouting(status: HttpStatus): void {
    if (HttpStatus.UNAUTHORIZED === status) {
      this.router.navigate(['/home']);
    } else if (HttpStatus.GATEWAY_TIMEOUT === status) {
      this.router.navigate(['/error']);
    }
  }
}
