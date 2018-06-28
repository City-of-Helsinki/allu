import {Action} from '@ngrx/store';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '../../../service/error/error-info';
import {CodeSetTypeMap} from '../../../model/codeset/codeset';

export enum CodeSetActionType {
  LoadSuccess = '[CodeSet] Load code set success',
  LoadFailed = '[CodeSet] Load code set failed',
}

export class LoadSuccess implements Action {
  readonly type = CodeSetActionType.LoadSuccess;

  constructor(public payload: CodeSetTypeMap) {}
}

export class LoadFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = CodeSetActionType.LoadFailed;

  constructor(public payload: ErrorInfo) {}
}

export type CodeSetActions =
  | LoadSuccess
  | LoadFailed;

