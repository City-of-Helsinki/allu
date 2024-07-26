import {Action} from '@ngrx/store';
import {StructureMeta} from '../../../model/application/meta/structure-meta';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '../../../service/error/error-info';

export enum ApplicationMetaActionType {
  Load = '[ApplicationMeta] Load application metadata',
  LoadSuccess = '[ApplicationMeta] Load application metadata success',
  LoadFailed = '[ApplicationMeta] Load application metadata failed',
}

export class Load implements Action {
  readonly type = ApplicationMetaActionType.Load;
}

export class LoadSuccess implements Action {
  readonly type = ApplicationMetaActionType.LoadSuccess;

  constructor(public payload: StructureMeta) {}
}

export class LoadFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ApplicationMetaActionType.LoadFailed;

  constructor(public payload: ErrorInfo) {}
}

export type ApplicationMetaActions =
  | Load
  | LoadSuccess
  | LoadFailed;
