import {Observable} from 'rxjs/Observable';
import {ErrorInfo} from '../ui-state/error-info';
import {MaterializeUtil} from '../../util/materialize.util';
import {Some} from '../../util/option';

export class NotificationService {
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

  static errorMessage(message: string, timeVisible?: number): void {
    MaterializeUtil.toast(message, timeVisible);
  }
}
