import {Action} from '@ngrx/store';
import {CityDistrict} from '../../../model/common/city-district';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '../../../service/error/error-info';

export enum CityDistrictActionType {
  LoadSuccess = '[CityDistrict] Load city districts success',
  LoadFailed = '[CityDistrict] Load city districts failed',
}

export class LoadSuccess implements Action {
  readonly type = CityDistrictActionType.LoadSuccess;

  constructor(public payload: CityDistrict[]) {}
}

export class LoadFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = CityDistrictActionType.LoadFailed;

  constructor(public payload: ErrorInfo) {}
}

export type CityDistrictActions =
  | LoadSuccess
  | LoadFailed;

