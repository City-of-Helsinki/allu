import {Action} from '@ngrx/store';
import {Application} from '../../../model/application/application';
import {Customer} from '../../../model/customer/customer';
import {KindsWithSpecifiers} from '../../../model/application/type/application-specifier';
import {Contact} from '@model/customer/contact';

export enum InformationRequestResultActionType {
  SetApplication = '[InformationRequestResult] Set result application',
  SetCustomer = '[InformationRequestResult] Set result customer',
  SetContacts = '[InformationRequestResult] Set result contacts',
  SetContact = '[InformationRequestResult] Set result contact at index',
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

export type InformationRequestResultActions =
  | SetApplication
  | SetCustomer
  | SetContacts
  | SetContact
  | SetKindsWithSpecifiers;
