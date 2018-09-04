import {Action} from '@ngrx/store';
import {Application} from '../../../model/application/application';
import {Customer} from '../../../model/customer/customer';
import {KindsWithSpecifiers} from '../../../model/application/type/application-specifier';
import {Contact} from '@model/customer/contact';
import {ActionWithPayload} from '@feature/common/action-with-payload';
import {ErrorInfo} from '@service/error/error-info';
import {InformationRequestResult} from '@feature/information-request/information-request-result';
import {CustomerRoleType} from '@model/customer/customer-role-type';
import {FieldValues} from '@feature/information-request/acceptance/field-group-acceptance.component';

export enum InformationRequestResultActionType {
  SetApplication = '[InformationRequestResult] Set result application',
  SetCustomer = '[InformationRequestResult] Set result customer',
  SetContacts = '[InformationRequestResult] Set result contacts',
  SetContact = '[InformationRequestResult] Set result contact at index',
  SetKindsWithSpecifiers = '[InformationRequestResult] Set result kinds with specifiers',
  SetInvoicingCustomer = '[InformationRequestResult] Set result invoicing customer',
  UseCustomerForInvoicing = '[InformationRequestResult] Use customer also for invoicing',
  SetOtherInfo = '[InformationRequestResult] Set other application info',
  Save = '[InformationRequestResult] Save result of information request',
  SaveSuccess = '[InformationRequestResult] Save result of information request success',
  SaveFailed = '[InformationRequestResult] Save result of information request failed',
  CloseFailed = '[InformationRequestResult] Closing information request failed'
}

export class SetApplication implements Action {
  readonly type = InformationRequestResultActionType.SetApplication;
  constructor(public payload: Application) {}
}

export class SetCustomer implements Action {
  readonly type = InformationRequestResultActionType.SetCustomer;
  constructor(public payload: Customer) {}
}

export class SetContacts implements Action {
  readonly type = InformationRequestResultActionType.SetContacts;
  constructor(public payload: Contact[]) {}
}

export class SetContact implements Action {
  readonly type = InformationRequestResultActionType.SetContact;
  constructor(public payload: {contact: Contact, index: number}) {}
}

export class SetKindsWithSpecifiers implements Action {
  readonly type = InformationRequestResultActionType.SetKindsWithSpecifiers;
  constructor(public payload: KindsWithSpecifiers) {}
}

export class SetInvoicingCustomer implements Action {
  readonly type = InformationRequestResultActionType.SetInvoicingCustomer;
  constructor(public payload: Customer) {}
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

export class SaveFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = InformationRequestResultActionType.Save;
  constructor(public payload: ErrorInfo) {}
}

export class CloseFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = InformationRequestResultActionType.CloseFailed;
  constructor(public payload: ErrorInfo) {}
}

export type InformationRequestResultActions =
  | SetApplication
  | SetCustomer
  | SetContacts
  | SetContact
  | SetKindsWithSpecifiers
  | SetInvoicingCustomer
  | UseCustomerForInvoicing
  | SetOtherInfo
  | Save
  | SaveSuccess
  | SaveFailed
  | CloseFailed;
