import {Action} from '@ngrx/store';
import {Geocoordinates} from '@model/common/geocoordinates';
import {PostalAddress} from '@model/common/postal-address';

export enum AddressSearchActionType {
  Search = '[AddressSearch] Search',
  SearchSuccess = '[AddressSearch] Search success',
  FetchCoordinates = '[AddressSearch] Fetch coordinates',
  FetchCoordinatesSuccess = '[AddressSearch] Fetch coordinates success',
  ClearCoordinates = '[AddressSearch] Clear coordinates'
}

export class Search implements Action {
  readonly type = AddressSearchActionType.Search;
  constructor(public payload: string) {}
}

export class SearchSuccess implements Action {
  readonly type = AddressSearchActionType.SearchSuccess;
  constructor(public payload: PostalAddress[]) {}
}

export class FetchCoordinates implements Action {
  readonly type = AddressSearchActionType.FetchCoordinates;
  constructor(public payload: string) {}
}

export class FetchCoordinatesSuccess implements Action {
  readonly type = AddressSearchActionType.FetchCoordinatesSuccess;
  constructor(public payload: Geocoordinates) {}
}

export class ClearCoordinates implements Action {
  readonly type = AddressSearchActionType.ClearCoordinates;
}

export type AddressSearchActions =
  | Search
  | SearchSuccess
  | FetchCoordinates
  | FetchCoordinatesSuccess
  | ClearCoordinates;
