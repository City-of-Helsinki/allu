import {Action} from '@ngrx/store';
import {ErrorInfo} from '../../../service/error/error-info';

export enum RootErrorActionType {
  NotifyError = '[RootError] Notify error'
}

export class NotifyError implements Action {
  type = RootErrorActionType.NotifyError;

  constructor(public payload: ErrorInfo) {}
}
