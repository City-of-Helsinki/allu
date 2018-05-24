import {EMPTY, Observable, of} from 'rxjs';
import {ErrorInfo} from '../error/error-info';
import {Some} from '../../util/option';
import {findTranslation} from '../../util/translations';
import {Injectable} from '@angular/core';
import {ToastyService} from 'ng2-toasty';

@Injectable()
export class NotificationService {

  constructor(private toasty: ToastyService) {}

  translateSuccess(key: string): void {
    this.success(findTranslation(key));
  }

  success(title: string, message?: string): void {
    this.toasty.success({
      title: title,
      msg: message
    });
  }

  info(title: string, message?: string): void  {
    this.toasty.info({
      title: title,
      msg: message
    });
  }

  error(title: string, message?: string): void {
    this.toasty.error({
      title: title,
      msg: message
    });
  }

  errorInfo(errorInfo: ErrorInfo): void {
    this.error(errorInfo.title, errorInfo.message);
  }

  errorCatch<T>(errorInfo: ErrorInfo, returnValue?: T): Observable<T> {
    this.errorInfo(errorInfo);
    return Some(returnValue)
      .map(val => of(val))
      .orElse(EMPTY);
  }

  translateError(errorInfo: ErrorInfo): void {
    this.error(findTranslation(errorInfo.message));
  }

  translateErrorMessage(key: string): void {
    this.error(findTranslation(key));
  }
}
