import {Action} from '@ngrx/store';
import {StructureMeta} from '../../../model/application/meta/structure-meta';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '../../../service/error/error-info';

export enum ProjectMetaActionType {
  LoadSuccess = '[ProjectMeta] Load project metadata success',
  LoadFailed = '[ProjectMeta] Load project metadata failed',
}

export class LoadSuccess implements Action {
  readonly type = ProjectMetaActionType.LoadSuccess;

  constructor(public payload: StructureMeta) {}
}

export class LoadFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ProjectMetaActionType.LoadFailed;

  constructor(public payload: ErrorInfo) {}
}

export type ProjectMetaActions =
  | LoadSuccess
  | LoadFailed;
