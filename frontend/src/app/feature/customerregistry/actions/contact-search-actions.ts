import {ActionWithPayload} from '@feature/common/action-with-payload';
import {ErrorInfo} from '@service/error/error-info';
import {Contact} from '@model/customer/contact';
import {ActionWithTarget} from '@feature/allu/actions/action-with-target';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {ContactSearchQuery} from '@service/customer/contact-search-query';
import {SearchParameters} from '@feature/common/search-parameters';

export enum ContactSearchActionType {
  LoadByCustomer = '[ContactSearch] Load contacts by customer',
  LoadByCustomerSuccess = '[ContactSearch] Load contacts by customer success',
  LoadByCustomerFailed = '[ContactSearch] Load contacts by customer failed',
  SearchForCurrentCustomer = '[ContactSearch] Search contacts for current customer',
  Search = '[ContactSearch] Search contacts',
  SearchSuccess = '[ContactSearch] Search contacts success'
}

export interface ContactSearchParameters extends SearchParameters<ContactSearchQuery> {}

export class LoadByCustomer implements ActionWithTarget {
  readonly type = ContactSearchActionType.LoadByCustomer;

  constructor(public targetType: ActionTargetType, public payload: number) {}
}

export class LoadByCustomerSuccess implements ActionWithTarget {
  readonly type = ContactSearchActionType.LoadByCustomerSuccess;

  constructor(public targetType: ActionTargetType, public payload: Contact[]) {}
}

export class LoadByCustomerFailed implements ActionWithTarget, ActionWithPayload<ErrorInfo> {
  readonly type = ContactSearchActionType.LoadByCustomerFailed;

  constructor(public targetType: ActionTargetType, public payload: ErrorInfo) {}
}

export class SearchForCurrentCustomer implements ActionWithTarget {
  readonly type = ContactSearchActionType.SearchForCurrentCustomer;

  constructor(public targetType: ActionTargetType, public payload: string) {}
}

export class Search implements ActionWithTarget {
  readonly type = ContactSearchActionType.Search;

  constructor(public targetType: ActionTargetType, public payload: ContactSearchParameters) {}
}

export class SearchSuccess implements ActionWithTarget {
  readonly type = ContactSearchActionType.SearchSuccess;

  constructor(public targetType: ActionTargetType, public payload: Contact[]) {}
}

export type ContactSearchActions =
  | LoadByCustomer
  | LoadByCustomerSuccess
  | LoadByCustomerFailed
  | SearchForCurrentCustomer
  | Search
  | SearchSuccess;
