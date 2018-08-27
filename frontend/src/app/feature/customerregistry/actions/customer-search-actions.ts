import {Customer} from '../../../model/customer/customer';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '../../../service/error/error-info';
import {CustomerSearchQuery} from '../../../service/customer/customer-search-query';
import {CustomerSearchByType} from '@service/customer/customer-search-query';
import {ActionWithTarget} from '@feature/allu/actions/action-with-target';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

export enum CustomerSearchActionType {
  Search = '[CustomerSearch] Search customer',
  SearchByType = '[CustomerSearch] Search customer by type',
  SearchSuccess = '[CustomerSearch] Search customer success',
  SearchFailed = '[CustomerSearch] Search customer failed'
}

export class Search implements ActionWithTarget {
  readonly type = CustomerSearchActionType.Search;

  constructor(public targetType: ActionTargetType, public payload: CustomerSearchQuery) {}
}

export class SearchByType implements ActionWithTarget {
  readonly type = CustomerSearchActionType.SearchByType;

  constructor(public targetType: ActionTargetType, public payload: CustomerSearchByType) {}
}

export class SearchSuccess implements ActionWithTarget {
  readonly type = CustomerSearchActionType.SearchSuccess;

  constructor(public targetType: ActionTargetType, public payload: Customer[]) {}
}

export class SearchFailed implements ActionWithTarget, ActionWithPayload<ErrorInfo> {
  readonly type = CustomerSearchActionType.SearchFailed;

  constructor(public targetType: ActionTargetType, public payload: ErrorInfo) {}
}

export type CustomerSearchActions =
  | Search
  | SearchByType
  | SearchSuccess
  | SearchFailed;
