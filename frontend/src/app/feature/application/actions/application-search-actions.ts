import {Application} from '@model/application/application';
import {ErrorInfo} from '@service/error/error-info';
import {ActionWithPayload} from '@feature/common/action-with-payload';
import {ActionWithTarget} from '@feature/allu/actions/action-with-target';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {ApplicationSearchQuery} from '@model/search/ApplicationSearchQuery';
import {Page} from '@model/common/page';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';

export enum ApplicationSearchActionType {
  SetSearchQuery = '[ApplicationSearch] Set search query',
  SetSort = '[ApplicationSearch] Set search sort',
  SetPaging = '[ApplicationSearch] Set search paging',
  ResetToFirstPage = '[ApplicationSearch] Reset paging to first page',
  Search = '[ApplicationSearch] Search applications',
  SearchSuccess = '[ApplicationSearch] Search applications success',
  SearchFailed = '[ApplicationSearch] Search applications failed',
  SearchByNameOrId = '[ApplicationSearch] Search applications by name or id',
  ToggleSelect = '[ApplicationSearch] Toggle application selection',
  ToggleSelectAll = '[ApplicationSearch] Toggle all applications selection',
  ClearSelected = '[ApplicationSearch] Clear selected applications'
}

export interface SearchParams {
  query: ApplicationSearchQuery;
  sort?: Sort;
  pageRequest?: PageRequest;
}

export class SetSearchQuery implements ActionWithTarget {
  readonly type = ApplicationSearchActionType.SetSearchQuery;

  constructor(public targetType: ActionTargetType, public payload: ApplicationSearchQuery) {}
}

export class SetSort implements ActionWithTarget {
  readonly type = ApplicationSearchActionType.SetSort;

  constructor(public targetType: ActionTargetType, public payload: Sort) {}
}

export class SetPaging implements ActionWithTarget {
  readonly type = ApplicationSearchActionType.SetPaging;

  constructor(public targetType: ActionTargetType, public payload: PageRequest) {}
}

export class ResetToFirstPage implements ActionWithTarget {
  readonly type = ApplicationSearchActionType.ResetToFirstPage;

  constructor(public targetType: ActionTargetType) {}
}

export class Search implements ActionWithTarget {
  readonly type = ApplicationSearchActionType.Search;
  readonly payload: SearchParams;

  constructor(public targetType: ActionTargetType, private query: ApplicationSearchQuery,
              private sort?: Sort, private pageRequest?: PageRequest) {
    this.payload = { query, sort, pageRequest };
  }
}

export class SearchByNameOrId implements ActionWithTarget {
  readonly type = ApplicationSearchActionType.SearchByNameOrId;

  constructor(public targetType: ActionTargetType, public payload: string) {}
}

export class SearchSuccess implements ActionWithTarget {
  readonly type = ApplicationSearchActionType.SearchSuccess;

  constructor(public targetType: ActionTargetType, public payload: Page<Application>) {}
}

export class SearchFailed implements ActionWithTarget, ActionWithPayload<ErrorInfo> {
  readonly type = ApplicationSearchActionType.SearchFailed;

  constructor(public targetType: ActionTargetType, public payload: ErrorInfo) {}
}

export class ToggleSelect implements ActionWithTarget {
  readonly type = ApplicationSearchActionType.ToggleSelect;

  constructor(public targetType: ActionTargetType, public payload: number) {}
}

export class ToggleSelectAll implements ActionWithTarget {
  readonly type = ApplicationSearchActionType.ToggleSelectAll;

  constructor(public targetType: ActionTargetType) {}
}

export class ClearSelected implements ActionWithTarget {
  readonly type = ApplicationSearchActionType.ClearSelected;

  constructor(public targetType: ActionTargetType) {}
}

export type ApplicationSearchActions =
  | SetSearchQuery
  | SetSort
  | SetPaging
  | ResetToFirstPage
  | Search
  | SearchSuccess
  | SearchFailed
  | SearchByNameOrId
  | ToggleSelect
  | ToggleSelectAll
  | ClearSelected;
