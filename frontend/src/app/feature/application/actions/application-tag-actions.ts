import {ApplicationTag} from '../../../model/application/tag/application-tag';
import {ApplicationTagType} from "@model/application/tag/application-tag-type";
import {Action} from '@ngrx/store';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '../../../service/error/error-info';

export enum ApplicationTagActionType {
  Load = '[ApplicationTag] Load tags',
  LoadSuccess = '[ApplicationTag] Load tags success',
  LoadFailed = '[ApplicationTag] Load tags failed',
  Add = '[ApplicationTag] Add tag',
  AddSuccess = '[ApplicationTag] Add tag success',
  AddFailed = '[ApplicationTag] Add tag failed',
  Remove = '[ApplicationTag] Remove tag',
  RemoveSuccess = '[ApplicationTag] Remove tag success',
  RemoveFailed = '[ApplicationTag] Remove tag failed',
  Save = '[ApplicationTag] Save current tags',
  SaveSuccess = '[ApplicationTag] Save tags success',
  SaveFailed = '[ApplicationTag] Save tags failed',
}

export class Load implements Action {
  readonly type = ApplicationTagActionType.Load;
}

export class LoadSuccess implements Action {
  readonly type = ApplicationTagActionType.LoadSuccess;

  constructor(public payload: ApplicationTag[]) {}
}

export class LoadFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ApplicationTagActionType.LoadFailed;

  constructor(public payload: ErrorInfo) {}
}

export class Add implements Action {
  readonly type = ApplicationTagActionType.Add;

  constructor(public payload: ApplicationTag) {}
}

export class AddSuccess implements Action {
  readonly type = ApplicationTagActionType.AddSuccess;

  constructor(public payload: ApplicationTag) {}
}

export class AddFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ApplicationTagActionType.AddFailed;

  constructor(public payload: ErrorInfo) {}
}

export class Remove implements Action {
  readonly type = ApplicationTagActionType.Remove;

  constructor(public payload: ApplicationTagType) {}
}

export class RemoveSuccess implements Action {
  readonly type = ApplicationTagActionType.RemoveSuccess;

  constructor(public payload: ApplicationTagType) {}
}

export class RemoveFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ApplicationTagActionType.RemoveFailed;

  constructor(public payload: ErrorInfo) {}
}

export class Save implements Action {
  readonly type = ApplicationTagActionType.Save;
}

export class SaveSuccess implements Action {
  readonly type = ApplicationTagActionType.SaveSuccess;

  constructor(public payload: ApplicationTag[]) {}
}

export class SaveFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ApplicationTagActionType.SaveFailed;

  constructor(public payload: ErrorInfo) {}
}

export type ApplicationTagActions =
  | Load
  | LoadSuccess
  | LoadFailed
  | Add
  | AddSuccess
  | AddFailed
  | Remove
  | RemoveSuccess
  | RemoveFailed
  | Save
  | SaveSuccess
  | SaveFailed;
