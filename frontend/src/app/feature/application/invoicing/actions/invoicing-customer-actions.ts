import {Action} from '@ngrx/store';
import {Customer} from '@model/customer/customer';

export enum InvoicingCustomerActionType {
  Load = '[InvoicingCustomer] Load invoicing customer',
  LoadSuccess = '[InvoicingCustomer] Load invoicing customer success',
  SetRecipient = '[Invoicing] Set invoicing recipient',
  SetRecipientSuccess = '[Invoicing] Set invoicing recipient success'
}

export class Load implements Action {
  readonly type = InvoicingCustomerActionType.Load;
}

export class LoadSuccess implements Action {
  readonly type = InvoicingCustomerActionType.LoadSuccess;
  constructor(public payload: Customer) {}
}

export class SetRecipient implements Action {
  readonly type = InvoicingCustomerActionType.SetRecipient;

  constructor(public payload: number) {}
}

export class SetRecipientSuccess implements Action {
  readonly type = InvoicingCustomerActionType.SetRecipientSuccess;

  constructor(public payload: number) {}
}

export type InvoicingCustomerActions =
  | Load
  | LoadSuccess
  | SetRecipient
  | SetRecipientSuccess;
