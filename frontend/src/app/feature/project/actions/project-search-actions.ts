import {Action} from '@ngrx/store';
import {ErrorInfo} from '@service/error/error-info';
import {ActionWithPayload} from '@feature/common/action-with-payload';
import {Project} from '@model/project/project';
import {SearchParameters} from '@feature/common/search-parameters';
import {ProjectSearchQuery} from '@model/project/project-search-query';
import {ActionWithTarget} from '@feature/allu/actions/action-with-target';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';
import {Page} from '@model/common/page';

export interface ProjectSearchParams extends SearchParameters<ProjectSearchQuery> {}

export enum ProjectSearchActionType {
  SetSearchQuery = '[ProjectSearch] Set search query',
  SetSort = '[ProjectSearch] Set search sort',
  SetPaging = '[ProjectSearch] Set search paging',
  ResetToFirstPage = '[ProjectSearch] Reset paging to first page',
  Search = '[ProjectSearch] Search projects tasks',
  SearchSuccess = '[ProjectSearch] Search projects tasks success',
  SearchFailed = '[ProjectSearch] Search projects tasks failed'
}

export class SetSearchQuery implements ActionWithTarget {
  readonly type = ProjectSearchActionType.SetSearchQuery;

  constructor(public targetType: ActionTargetType, public payload: ProjectSearchQuery) {}
}

export class SetSort implements ActionWithTarget {
  readonly type = ProjectSearchActionType.SetSort;

  constructor(public targetType: ActionTargetType, public payload: Sort) {}
}

export class SetPaging implements ActionWithTarget {
  readonly type = ProjectSearchActionType.SetPaging;

  constructor(public targetType: ActionTargetType, public payload: PageRequest) {}
}

export class ResetToFirstPage implements ActionWithTarget {
  readonly type = ProjectSearchActionType.ResetToFirstPage;

  constructor(public targetType: ActionTargetType) {}
}

export class Search implements Action {
  readonly type = ProjectSearchActionType.Search;
  readonly payload: ProjectSearchParams;

  constructor(private query: ProjectSearchQuery, private sort?: Sort, private pageRequest?: PageRequest) {
    this.payload = {query, sort, pageRequest};
  }
}

export class SearchSuccess implements Action {
  readonly type = ProjectSearchActionType.SearchSuccess;

  constructor(public payload: Page<Project>) {}
}

export class SearchFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ProjectSearchActionType.SearchFailed;

  constructor(public payload: ErrorInfo) {}
}

export type SearchActions =
  | SetSearchQuery
  | SetSort
  | SetPaging
  | ResetToFirstPage
  | Search
  | SearchSuccess
  | SearchFailed;
