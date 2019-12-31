import {Contact} from '@model/customer/contact';
import {ErrorInfo} from '@service/error/error-info';
import {Action} from '@ngrx/store';

export enum ContactActionType {
  FindById = '[Contact] Find contact by id',
  FindByIdComplete = '[Contact] Find contact by id completed'
}

export class FindById implements Action {
  readonly type = ContactActionType.FindById;
  constructor(public payload: number) {}
}

export class FindByIdComplete implements Action {
  readonly type = ContactActionType.FindByIdComplete;
  constructor(public payload: {
    contact: Contact,
    error?: ErrorInfo;
  }) {}
}

export type ContactActions =
  | FindById
  | FindByIdComplete;
