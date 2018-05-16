import {Action} from '@ngrx/store';
import {ErrorInfo} from '../../../service/error/error-info';
import {ActionWithPayload} from '../../common/action-with-payload';
import {Comment} from '../../../model/application/comment/comment';
import {CommentTargetType} from '../../../model/application/comment/comment-target-type';

export enum CommentActionType {
  Load = '[Comment] Load comments',
  LoadSuccess = '[Comment] Load comments success',
  LoadFailed = '[Comment] Load comments failed',
  Save = '[Comment] Save comment',
  SaveSuccess = '[Comment] Save comment success',
  SaveFailed = '[Comment] Save comment failed',
  Remove = '[Comment] Remove comment',
  RemoveSuccess = '[Comment] Remove comment success',
  RemoveFailed = '[Comment] Remove comment failed',
}

export interface CommentAction extends Action {
  targetType: CommentTargetType;
}

export class Load implements CommentAction {
  readonly type = CommentActionType.Load;

  constructor(public targetType: CommentTargetType) {}
}

export class LoadSuccess implements CommentAction {
  readonly type = CommentActionType.LoadSuccess;

  constructor(public targetType: CommentTargetType, public payload: Comment[]) {}
}

export class LoadFailed implements CommentAction, ActionWithPayload<ErrorInfo> {
  readonly type = CommentActionType.LoadFailed;

  constructor(public targetType: CommentTargetType, public payload: ErrorInfo) {}
}

export class Save implements CommentAction {
  readonly type = CommentActionType.Save;

  constructor(public targetType: CommentTargetType, public payload: Comment) {}
}

export class SaveSuccess implements CommentAction {
  readonly type = CommentActionType.SaveSuccess;

  constructor(public targetType: CommentTargetType, public payload: Comment) {}
}

export class SaveFailed implements CommentAction, ActionWithPayload<ErrorInfo> {
  readonly type = CommentActionType.SaveFailed;

  constructor(public targetType: CommentTargetType, public payload: ErrorInfo) {}
}

export class Remove implements CommentAction {
  readonly type = CommentActionType.Remove;

  constructor(public targetType: CommentTargetType, public payload: number) {}
}

export class RemoveSuccess implements CommentAction {
  readonly type = CommentActionType.RemoveSuccess;

  constructor(public targetType: CommentTargetType, public payload: number) {}
}

export class RemoveFailed implements CommentAction, ActionWithPayload<ErrorInfo> {
  readonly type = CommentActionType.RemoveFailed;

  constructor(public targetType: CommentTargetType, public payload: ErrorInfo) {}
}

export type CommentActions =
  | Load
  | LoadSuccess
  | LoadFailed
  | Save
  | SaveSuccess
  | SaveFailed
  | Remove
  | RemoveSuccess
  | RemoveFailed;
