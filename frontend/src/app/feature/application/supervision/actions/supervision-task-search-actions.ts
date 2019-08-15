import {SearchParameters} from '@feature/common/search-parameters';
import {SupervisionTaskSearchCriteria} from '@model/application/supervision/supervision-task-search-criteria';
import {ActionWithTarget} from '@feature/allu/actions/action-with-target';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';
import {Page} from '@model/common/page';
import {ActionWithPayload} from '@feature/common/action-with-payload';
import {ErrorInfo} from '@service/error/error-info';
import {SupervisionWorkItem} from '@model/application/supervision/supervision-work-item';

export enum SupervisionTaskSearchActionType {
  SetSearchQuery = '[SupervisionTaskSearch] Set search query',
  SetSort = '[SupervisionTaskSearch] Set search sort',
  SetPaging = '[SupervisionTaskSearch] Set search paging',
  ResetToFirstPage = '[SupervisionTaskSearch] Reset paging to first page',
  Search = '[SupervisionTaskSearch] Search supervision tasks',
  SearchSuccess = '[SupervisionTaskSearch] Search supervision tasks success',
  SearchFailed = '[SupervisionTaskSearch] Search supervision tasks failed',
  ToggleSelect = '[SupervisionTaskSearch] Toggle supervision task selection',
  ToggleSelectAll = '[SupervisionTaskSearch] Toggle all supervision tasks selection',
  ClearSelected = '[SupervisionTaskSearch] Clear selected supervision tasks'
}

export interface SupervisionTaskSearchParams extends SearchParameters<SupervisionTaskSearchCriteria> {}

export class SetSearchQuery implements ActionWithTarget {
  readonly type = SupervisionTaskSearchActionType.SetSearchQuery;

  constructor(public targetType: ActionTargetType, public payload: SupervisionTaskSearchCriteria) {}
}

export class SetSort implements ActionWithTarget {
  readonly type = SupervisionTaskSearchActionType.SetSort;

  constructor(public targetType: ActionTargetType, public payload: Sort) {}
}

export class SetPaging implements ActionWithTarget {
  readonly type = SupervisionTaskSearchActionType.SetPaging;

  constructor(public targetType: ActionTargetType, public payload: PageRequest) {}
}

export class ResetToFirstPage implements ActionWithTarget {
  readonly type = SupervisionTaskSearchActionType.ResetToFirstPage;

  constructor(public targetType: ActionTargetType) {}
}

export class Search implements ActionWithTarget {
  readonly type = SupervisionTaskSearchActionType.Search;
  readonly payload: SupervisionTaskSearchParams;

  constructor(public targetType: ActionTargetType, private query: SupervisionTaskSearchCriteria,
              private sort?: Sort, private pageRequest?: PageRequest) {
    this.payload = { query, sort, pageRequest };
  }
}

export class SearchSuccess implements ActionWithTarget {
  readonly type = SupervisionTaskSearchActionType.SearchSuccess;

  constructor(public targetType: ActionTargetType, public payload: Page<SupervisionWorkItem>) {}
}

export class SearchFailed implements ActionWithTarget, ActionWithPayload<ErrorInfo> {
  readonly type = SupervisionTaskSearchActionType.SearchFailed;

  constructor(public targetType: ActionTargetType, public payload: ErrorInfo) {}
}

export class ToggleSelect implements ActionWithTarget {
  readonly type = SupervisionTaskSearchActionType.ToggleSelect;

  constructor(public targetType: ActionTargetType, public payload: number) {}
}

export class ToggleSelectAll implements ActionWithTarget {
  readonly type = SupervisionTaskSearchActionType.ToggleSelectAll;

  constructor(public targetType: ActionTargetType) {}
}

export class ClearSelected implements ActionWithTarget {
  readonly type = SupervisionTaskSearchActionType.ClearSelected;

  constructor(public targetType: ActionTargetType) {}
}

export type SupervisionTaskSearchActions =
  | SetSearchQuery
  | SetSort
  | SetPaging
  | ResetToFirstPage
  | Search
  | SearchSuccess
  | SearchFailed
  | ToggleSelect
  | ToggleSelectAll
  | ClearSelected;
