import {Action} from '@ngrx/store';
import {ErrorInfo} from '../../../service/error/error-info';
import {ActionWithPayload} from '../../common/action-with-payload';

export enum InvoicingActionType {
  SetRecipient = '[Invoicing] Set invoice recipient',
  SetRecipientSuccess = '[Invoicing] Set invoice recipient success'
}

export class SetRecipient implements Action {
  readonly type = InvoicingActionType.SetRecipient;

  constructor(public payload: number) {}
}

export class SetRecipientSuccess implements Action {
  readonly type = InvoicingActionType.SetRecipientSuccess;

  constructor(public payload: number) {}
}

export type InvoicingActions =
  | SetRecipient
  | SetRecipientSuccess;
