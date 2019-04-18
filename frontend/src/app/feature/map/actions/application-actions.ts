import {MapSearchFilter} from '@service/map-search-filter';
import {Application} from '@model/application/application';
import {Action} from '@ngrx/store';
import {ActionWithTarget} from '@feature/allu/actions/action-with-target';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

export enum ApplicationActionType {
  Search = '[MapApplication] Search applications',
  SearchSuccess = '[MapApplication] Search applications success'
}

export class Search implements ActionWithTarget {
  readonly type = ApplicationActionType.Search;
  constructor(public targetType: ActionTargetType, public payload: MapSearchFilter) {}
}

export class SearchSuccess implements Action {
  readonly type = ApplicationActionType.SearchSuccess;
  constructor(public targetType: ActionTargetType, public payload: Application[]) {}
}

export type ApplicationActions =
  | Search
  | SearchSuccess;
