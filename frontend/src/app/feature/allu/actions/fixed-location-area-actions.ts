import {Action} from '@ngrx/store';
import {FixedLocationArea} from '@model/common/fixed-location-area';

export enum FixedLocationAreaActionType {
  LoadSuccess = '[FixedLocationArea] Load fixed location areas success'
}

export class LoadSuccess implements Action {
  readonly type = FixedLocationAreaActionType.LoadSuccess;

  constructor(public payload: FixedLocationArea[]) {}
}

export type FixedLocationAreaActions =
  | LoadSuccess;

