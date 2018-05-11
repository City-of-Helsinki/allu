import {Action} from '@ngrx/store';
import {Application} from '../../../model/application/application';
import {ErrorInfo} from '../../../service/error/error-info';
import {ActionWithPayload} from '../../common/action-with-payload';

export enum ApplicationSearchActionType {
  Search = '[Search] Search applications',
  SearchSuccess = '[Search] Search applications success',
  SearchFailed = '[Search] Search applications failed'
}

export class Search implements Action {
  readonly type = ApplicationSearchActionType.Search;

  constructor(public payload: string) {}
}

export class SearchSuccess implements Action {
  readonly type = ApplicationSearchActionType.SearchSuccess;

  constructor(public payload: Application[]) {}
}

export class SearchFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ApplicationSearchActionType.SearchFailed;

  constructor(public payload: ErrorInfo) {}
}

export type SearchActions =
  | Search
  | SearchSuccess
  | SearchFailed;
