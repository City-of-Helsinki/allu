import {Action} from '@ngrx/store';
import {FeatureCollection, GeometryObject} from 'geojson';

export enum UserAreaActionType {
  Load = '[UserArea] Load user areas',
  LoadSuccess = '[UserArea] Load user areas success',
  LoadFailed = '[UserArea] Load user areas failed'
}

export class Load implements Action {
  readonly type = UserAreaActionType.Load;
}

export class LoadSuccess implements Action {
  readonly type = UserAreaActionType.LoadSuccess;

  constructor(public payload: FeatureCollection<GeometryObject>) {}
}

export class LoadFailed implements Action {
  readonly type = UserAreaActionType.LoadFailed;
}

export type UserAreaActions =
  | Load
  | LoadSuccess
  | LoadFailed;
