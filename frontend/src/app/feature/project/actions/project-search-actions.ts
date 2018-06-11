import {Action} from '@ngrx/store';
import {ErrorInfo} from '../../../service/error/error-info';
import {ActionWithPayload} from '../../common/action-with-payload';
import {Project} from '../../../model/project/project';

export enum ProjectSearchActionType {
  Search = '[ProjectSearch] Search projects',
  SearchSuccess = '[ProjectSearch] Search projects success',
  SearchFailed = '[ProjectSearch] Search projects failed'
}

export class Search implements Action {
  readonly type = ProjectSearchActionType.Search;

  constructor(public payload: string) {}
}

export class SearchSuccess implements Action {
  readonly type = ProjectSearchActionType.SearchSuccess;

  constructor(public payload: Project[]) {}
}

export class SearchFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ProjectSearchActionType.SearchFailed;

  constructor(public payload: ErrorInfo) {}
}

export type SearchActions =
  | Search
  | SearchSuccess
  | SearchFailed;
