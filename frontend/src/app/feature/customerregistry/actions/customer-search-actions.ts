import {Customer} from '@model/customer/customer';
import {ActionWithPayload} from '@feature/common/action-with-payload';
import {ErrorInfo} from '@service/error/error-info';
import {CustomerSearchByType, CustomerSearchQuery} from '@service/customer/customer-search-query';
import {ActionWithTarget} from '@feature/allu/actions/action-with-target';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {Page} from '@model/common/page';
import {SearchParameters} from '@feature/common/search-parameters';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';

export enum CustomerSearchActionType {
  SetSearchQuery = '[CustomerSearch] Set search query',
  SetSort = '[CustomerSearch] Set search sort',
  SetPaging = '[CustomerSearch] Set search paging',
  Search = '[CustomerSearch] Search customer',
  SearchByType = '[CustomerSearch] Search customer by type',
  SearchSuccess = '[CustomerSearch] Search customer success',
  SearchFailed = '[CustomerSearch] Search customer failed',
  FindById = '[CustomerSearch] Find customer by id',
  FindByIdSuccess = '[CustomerSearch] Find customer by id success',
}

export interface CustomerSearchParams extends SearchParameters<CustomerSearchQuery> {}

export class SetSearchQuery implements ActionWithTarget {
  readonly type = CustomerSearchActionType.SetSearchQuery;

  constructor(public targetType: ActionTargetType, public payload: CustomerSearchQuery) {}
}

export class SetSort implements ActionWithTarget {
  readonly type = CustomerSearchActionType.SetSort;

  constructor(public targetType: ActionTargetType, public payload: Sort) {}
}

export class SetPaging implements ActionWithTarget {
  readonly type = CustomerSearchActionType.SetPaging;

  constructor(public targetType: ActionTargetType, public payload: PageRequest) {}
}

export class Search implements ActionWithTarget {
  readonly type = CustomerSearchActionType.Search;

  constructor(public targetType: ActionTargetType, public payload: CustomerSearchParams) {}
}

export class SearchByType implements ActionWithTarget {
  readonly type = CustomerSearchActionType.SearchByType;

  constructor(public targetType: ActionTargetType, public payload: CustomerSearchByType) {}
}

export class SearchSuccess implements ActionWithTarget {
  readonly type = CustomerSearchActionType.SearchSuccess;

  constructor(public targetType: ActionTargetType, public payload: Page<Customer>) {}
}

export class SearchFailed implements ActionWithTarget, ActionWithPayload<ErrorInfo> {
  readonly type = CustomerSearchActionType.SearchFailed;

  constructor(public targetType: ActionTargetType, public payload: ErrorInfo) {}
}

export class FindById implements ActionWithTarget {
  readonly type = CustomerSearchActionType.FindById;

  constructor(public targetType: ActionTargetType, public payload: number) {}
}

export class FindByIdSuccess implements ActionWithTarget {
  readonly type = CustomerSearchActionType.FindByIdSuccess;

  constructor(public targetType: ActionTargetType, public payload: Customer) {}
}

export type CustomerSearchActions =
  | SetSearchQuery
  | SetSort
  | SetPaging
  | Search
  | SearchByType
  | SearchSuccess
  | SearchFailed
  | FindById
  | FindByIdSuccess;
