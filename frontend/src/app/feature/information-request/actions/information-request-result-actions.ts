import {Action} from '@ngrx/store';
import {Application} from '../../../model/application/application';
import {Customer} from '../../../model/customer/customer';

export enum InformationRequestResultActionType {
  SetApplication = '[InformationRequestResult] Set result application',
  SetCustomer = '[InformationRequestResult] Set result customer'
}

export class SetApplication implements Action {
  readonly type = InformationRequestResultActionType.SetApplication;
  constructor(public payload: Application) {}
}

export class SetCustomer implements Action {
  readonly type = InformationRequestResultActionType.SetCustomer;
  constructor(public payload: Customer) {}
}

export type InformationRequestResultActions =
  | SetApplication
  | SetCustomer;
