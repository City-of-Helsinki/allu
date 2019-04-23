import {Action} from '@ngrx/store';
import {FixedLocation} from '@model/common/fixed-location';

export enum FixedLocationActionType {
  LoadSuccess = '[FixedLocation] Load city districts success'
}

export class LoadSuccess implements Action {
  readonly type = FixedLocationActionType.LoadSuccess;

  constructor(public payload: FixedLocation[]) {}
}

export type FixedLocationActions =
  | LoadSuccess;

