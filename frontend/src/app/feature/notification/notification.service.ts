import {EMPTY, Observable, of} from 'rxjs';
import {ErrorInfo} from '@service/error/error-info';
import {Some} from '@util/option';
import {findTranslation} from '@util/translations';
import {Injectable} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import * as fromNotification from './reducers';
import {select, Store} from '@ngrx/store';
import {filter} from 'rxjs/internal/operators';

@Injectable()
export class NotificationService {

  constructor(private toastService: ToastrService, private store: Store<fromNotification.State>) {
    this.store.pipe(
      select(fromNotification.getSuccess),
      filter(success => !!success),
    ).subscribe(success => this.translateSuccess(success.message));

    this.store.pipe(
      select(fromNotification.getError),
      filter(error => !!error)
    ).subscribe(error => this.errorInfo(error));
  }

  translateSuccess(key: string): void {
    this.success(findTranslation(key));
  }

  success(title: string, message?: string): void {
    this.toastService.success(message, title);
  }

  info(title: string, message?: string): void {
    this.toastService.info(message, title);
  }

  error(title: string, message?: string, disableTimeOut: boolean = true): void {
    this.toastService.error(message, title, {disableTimeOut});
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
