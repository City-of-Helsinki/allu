import {Action} from '@ngrx/store';
import {Application} from '../../../model/application/application';
import {Customer} from '../../../model/customer/customer';
import {KindsWithSpecifiers} from '../../../model/application/type/application-specifier';

export enum InformationRequestResultActionType {
  SetApplication = '[InformationRequestResult] Set result application',
  SetCustomer = '[InformationRequestResult] Set result customer',
  SetKindsWithSpecifiers = '[InformationRequestResult] Set result kinds with specifiers'
}

export class SetApplication implements Action {
  readonly type = InformationRequestResultActionType.SetApplication;
  constructor(public payload: Application) {}
}

export class SetCustomer implements Action {
  readonly type = InformationRequestResultActionType.SetCustomer;
  constructor(public payload: Customer) {}
}

export class SetKindsWithSpecifiers implements Action {
  readonly type = InformationRequestResultActionType.SetKindsWithSpecifiers;
  constructor(public payload: KindsWithSpecifiers) {}
}

export type InformationRequestResultActions =
  | SetApplication
  | SetCustomer
  | SetKindsWithSpecifiers;
