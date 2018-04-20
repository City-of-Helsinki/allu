import {Action} from '@ngrx/store';
import {CityDistrict} from '../../../model/common/city-district';

export enum CityDistrictActionType {
  LoadSuccess = '[CityDistrict] Load city districts success'
}

export class LoadSuccess implements Action {
  readonly type = CityDistrictActionType.LoadSuccess;

  constructor(public payload: CityDistrict[]) {}
}

export type CityDistrictActions =
  | LoadSuccess;

