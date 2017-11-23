import {Observable} from 'rxjs/Observable';
import {ErrorInfo} from '../ui-state/error-info';
import {MaterializeUtil} from '../../util/materialize.util';
import {Some} from '../../util/option'; import {findTranslation} from '../../util/translations';

export class NotificationService {
  static translateMessage(key: string, timeVisible?: number): void {
    NotificationService.message(findTranslation(key), timeVisible);
  }

  static message(message: string, timeVisible?: number): void {
    MaterializeUtil.toast(message, timeVisible);
  }

  static error(errorInfo: ErrorInfo, timeVisible?: number): void {
    MaterializeUtil.toast(errorInfo.message, timeVisible);
  }

  static errorCatch<T>(errorInfo: ErrorInfo, returnValue?: T, timeVisible?: number): Observable<T> {
    this.error(errorInfo, timeVisible);
    return Some(returnValue)
      .map(val => Observable.of(val))
      .orElse(Observable.empty());
  }

  static translateError(errorInfo: ErrorInfo, timeVisible?: number): void {
    NotificationService.errorMessage(findTranslation(errorInfo.message), timeVisible);
  }

  static translateErrorMessage(key: string, timeVisible?: number): void {
    NotificationService.errorMessage(findTranslation(key), timeVisible);
  }

  static errorMessage(message: string, timeVisible?: number): void {
    MaterializeUtil.toast(message, timeVisible);
  }
}
