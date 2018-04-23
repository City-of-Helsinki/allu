import {Action} from '@ngrx/store';
import {Customer} from '../../../model/customer/customer';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '../../../service/ui-state/error-info';
import {CustomerSearchQuery} from '../../../service/customer/customer-search-query';
import {Contact} from '../../../model/customer/contact';

export enum CustomerSearchActionType {
  Search = '[Project] Search customer',
  SearchSuccess = '[Project] Search customer success',
  SearchFailed = '[Project] Search customer failed',
  LoadContacts = '[Project] Load contacts',
  LoadContactsSuccess = '[Project] Load contacts success',
  LoadContactsFailed = '[Project] Load contacts failed',
  SearchContacts = '[Project] Search contacts',
}

export class Search implements Action {
  readonly type = CustomerSearchActionType.Search;
  public payload: CustomerSearchQuery;

  constructor(type: string, name: string) {
    this.payload = {type, name};
  }
}

export class SearchSuccess implements Action {
  readonly type = CustomerSearchActionType.SearchSuccess;

  constructor(public payload: Customer[]) {}
}

export class SearchFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = CustomerSearchActionType.SearchFailed;

  constructor(public payload: ErrorInfo) {}
}

export class LoadContacts implements Action {
  readonly type = CustomerSearchActionType.LoadContacts;

  constructor(public payload: number) {}
}

export class LoadContactsSuccess implements Action {
  readonly type = CustomerSearchActionType.LoadContactsSuccess;

  constructor(public payload: Contact[]) {}
}

export class LoadContactsFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = CustomerSearchActionType.LoadContactsFailed;

  constructor(public payload: ErrorInfo) {}
}

export class SearchContacts implements Action {
  readonly type = CustomerSearchActionType.SearchContacts;

  constructor(public payload: string) {}
}

export type CustomerSearchActions =
  | Search
  | SearchSuccess
  | SearchFailed
  | LoadContacts
  | LoadContactsSuccess
  | LoadContactsFailed
  | SearchContacts;
