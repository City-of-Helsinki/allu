import {Action} from '@ngrx/store';
import {Invoice} from '@model/application/invoice/invoice';

export enum InvoiceActionType {
  Load = '[Invoice] Load invoices',
  LoadSuccess = '[Invoice] Load invoices success',
}

export class Load implements Action {
  readonly type = InvoiceActionType.Load;
}

export class LoadSuccess implements Action {
  readonly type = InvoiceActionType.LoadSuccess;
  constructor(public payload: Invoice[]) {}
}

export type InvoiceActions =
  | Load
  | LoadSuccess;
