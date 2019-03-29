import {MapSearchFilter} from '@service/map-search-filter';
import {Application} from '@model/application/application';
import {Action} from '@ngrx/store';

export enum ApplicationActionType {
  Search = '[MapApplication] Search applications',
  SearchSuccess = '[MapApplication] Search applications success'
}

export class Search implements Action {
  readonly type = ApplicationActionType.Search;
  constructor(public payload: MapSearchFilter) {}
}

export class SearchSuccess implements Action {
  readonly type = ApplicationActionType.SearchSuccess;
  constructor(public payload: Application[]) {}
}

export type ApplicationActions =
  | Search
  | SearchSuccess;
