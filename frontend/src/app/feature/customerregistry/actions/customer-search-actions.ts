import {Action} from '@ngrx/store';
import {Customer} from '../../../model/customer/customer';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '../../../service/error/error-info';
import {CustomerSearchQuery} from '../../../service/customer/customer-search-query';
import {Contact} from '../../../model/customer/contact';

export enum CustomerSearchActionType {
  Search = '[CustomerSearch] Search customer',
  SearchSuccess = '[CustomerSearch] Search customer success',
  SearchFailed = '[CustomerSearch] Search customer failed'
}

export class Search implements Action {
  readonly type = CustomerSearchActionType.Search;

  constructor(public payload: CustomerSearchQuery) {}
}

export class SearchSuccess implements Action {
  readonly type = CustomerSearchActionType.SearchSuccess;

  constructor(public payload: Customer[]) {}
}

export class SearchFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = CustomerSearchActionType.SearchFailed;

  constructor(public payload: ErrorInfo) {}
}

export type CustomerSearchActions =
  | Search
  | SearchSuccess
  | SearchFailed;
