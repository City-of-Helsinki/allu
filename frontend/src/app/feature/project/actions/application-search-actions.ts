import {Action} from '@ngrx/store';
import {Application} from '../../../model/application/application';
import {ErrorInfo} from '../../../service/ui-state/error-info';
import {ActionWithPayload} from '../../common/action-with-payload';

export enum SearchActionType {
  Search = '[Search] Search applications',
  SearchSuccess = '[Search] Search applications success',
  SearchFailed = '[Search] Search applications failed'
}

export class Search implements Action {
  readonly type = SearchActionType.Search;

  constructor(public payload: string) {}
}

export class SearchSuccess implements Action {
  readonly type = SearchActionType.SearchSuccess;

  constructor(public payload: Application[]) {}
}

export class SearchFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = SearchActionType.SearchFailed;

  constructor(public payload: ErrorInfo) {}
}

export type SearchActions =
  | Search
  | SearchSuccess
  | SearchFailed;
