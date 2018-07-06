import {Action} from '@ngrx/store';
import {ActionWithPayload} from '@feature/common/action-with-payload';
import {ErrorInfo} from '@service/error/error-info';
import {Contact} from '@model/customer/contact';

export enum ContactSearchActionType {
  LoadByCustomer = '[ContactSearch] Load contacts by customer',
  LoadByCustomerSuccess = '[ContactSearch] Load contacts by customer success',
  LoadByCustomerFailed = '[ContactSearch] Load contacts by customer failed',
  Search = '[ContactSearch] Search contacts',
}

export class LoadByCustomer implements Action {
  readonly type = ContactSearchActionType.LoadByCustomer;

  constructor(public payload: number) {}
}

export class LoadByCustomerSuccess implements Action {
  readonly type = ContactSearchActionType.LoadByCustomerSuccess;

  constructor(public payload: Contact[]) {}
}

export class LoadByCustomerFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ContactSearchActionType.LoadByCustomerFailed;

  constructor(public payload: ErrorInfo) {}
}

export class Search implements Action {
  readonly type = ContactSearchActionType.Search;

  constructor(public payload: string) {}
}

export type ContactSearchActions =
  | LoadByCustomer
  | LoadByCustomerSuccess
  | LoadByCustomerFailed
  | Search;
