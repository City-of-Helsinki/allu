import {ErrorInfo} from '../ui-state/error-info';
import {MaterializeUtil} from '../../util/materialize.util';

export class NotificationService {
  static message(message: string, timeVisible?: number): void {
    MaterializeUtil.toast(message, timeVisible);
  }

  static error(errorInfo: ErrorInfo, timeVisible?: number): void {
    MaterializeUtil.toast(errorInfo.message, timeVisible);
  }

  static errorMessage(message: string, timeVisible?: number): void {
    MaterializeUtil.toast(message, timeVisible);
  }
}
