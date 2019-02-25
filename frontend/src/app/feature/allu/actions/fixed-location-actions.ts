import {Action} from '@ngrx/store';
import {FixedLocationArea} from '@model/common/fixed-location-area';

export enum FixedLocationActionType {
  LoadSuccess = '[FixedLocation] Load city districts success'
}

export class LoadSuccess implements Action {
  readonly type = FixedLocationActionType.LoadSuccess;

  constructor(public payload: FixedLocationArea[]) {}
}

export type FixedLocationActions =
  | LoadSuccess;

