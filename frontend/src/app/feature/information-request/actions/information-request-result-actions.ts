import {Action} from '@ngrx/store';
import {Application} from '@model/application/application';
import {Customer} from '@model/customer/customer';
import {KindsWithSpecifiers} from '@model/application/type/application-specifier';
import {Contact} from '@model/customer/contact';
import {InformationRequestResult} from '@feature/information-request/information-request-result';
import {CustomerRoleType} from '@model/customer/customer-role-type';
import {FieldValues} from '@feature/information-request/acceptance/field-select/field-select.component';
import {ActionWithTarget} from '@feature/allu/actions/action-with-target';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

export enum InformationRequestResultActionType {
  SetApplication = '[InformationRequestResult] Set result application',
  SetCustomer = '[InformationRequestResult] Set result customer',
  SetContact = '[InformationRequestResult] Set result contact',
  SetKindsWithSpecifiers = '[InformationRequestResult] Set result kinds with specifiers',
  UseCustomerForInvoicing = '[InformationRequestResult] Use customer also for invoicing',
  SetOtherInfo = '[InformationRequestResult] Set other application info',
  Save = '[InformationRequestResult] Save result of information request',
  SaveSuccess = '[InformationRequestResult] Save result of information request success'
}

export class SetApplication implements Action {
  readonly type = InformationRequestResultActionType.SetApplication;
  constructor(public payload: Application) {}
}

export class SetCustomer implements ActionWithTarget {
  readonly type = InformationRequestResultActionType.SetCustomer;
  constructor(readonly targetType: ActionTargetType, public payload: Customer) {}
}

export class SetContact implements Action {
  readonly type = InformationRequestResultActionType.SetContact;
  constructor(public payload: Contact) {}
}

export class SetKindsWithSpecifiers implements Action {
  readonly type = InformationRequestResultActionType.SetKindsWithSpecifiers;
  constructor(public payload: KindsWithSpecifiers) {}
}

export class UseCustomerForInvoicing implements Action {
  readonly type = InformationRequestResultActionType.UseCustomerForInvoicing;
  constructor(public payload: CustomerRoleType) {}
}

export class SetOtherInfo implements Action {
  readonly type = InformationRequestResultActionType.SetOtherInfo;
  constructor(public payload: FieldValues) {}
}

export class Save implements Action {
  readonly type = InformationRequestResultActionType.Save;
  constructor(public payload: InformationRequestResult) {}
}

export class SaveSuccess implements Action {
  readonly type = InformationRequestResultActionType.SaveSuccess;
  constructor(public payload: InformationRequestResult) {}
}

export type InformationRequestResultActions =
  | SetApplication
  | SetCustomer
  | SetContact
  | SetKindsWithSpecifiers
  | UseCustomerForInvoicing
  | SetOtherInfo
  | Save
  | SaveSuccess;
