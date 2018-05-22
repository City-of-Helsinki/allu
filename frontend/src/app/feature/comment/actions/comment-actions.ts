import {ErrorInfo} from '../../../service/error/error-info';
import {ActionWithPayload} from '../../common/action-with-payload';
import {Comment} from '../../../model/application/comment/comment';
import {ActionTargetType} from '../../allu/actions/action-target-type';
import {ActionWithTarget} from '../../allu/actions/action-with-target';

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
  ToggleDirection = '[Comment] Toggle comment sorting'
}

export class Load implements ActionWithTarget {
  readonly type = CommentActionType.Load;

  constructor(public targetType: ActionTargetType) {}
}

export class LoadSuccess implements ActionWithTarget {
  readonly type = CommentActionType.LoadSuccess;

  constructor(public targetType: ActionTargetType, public payload: Comment[]) {}
}

export class LoadFailed implements ActionWithTarget, ActionWithPayload<ErrorInfo> {
  readonly type = CommentActionType.LoadFailed;

  constructor(public targetType: ActionTargetType, public payload: ErrorInfo) {}
}

export class Save implements ActionWithTarget {
  readonly type = CommentActionType.Save;

  constructor(public targetType: ActionTargetType, public payload: Comment) {}
}

export class SaveSuccess implements ActionWithTarget {
  readonly type = CommentActionType.SaveSuccess;

  constructor(public targetType: ActionTargetType, public payload: Comment) {}
}

export class SaveFailed implements ActionWithTarget, ActionWithPayload<ErrorInfo> {
  readonly type = CommentActionType.SaveFailed;

  constructor(public targetType: ActionTargetType, public payload: ErrorInfo) {}
}

export class Remove implements ActionWithTarget {
  readonly type = CommentActionType.Remove;

  constructor(public targetType: ActionTargetType, public payload: number) {}
}

export class RemoveSuccess implements ActionWithTarget {
  readonly type = CommentActionType.RemoveSuccess;

  constructor(public targetType: ActionTargetType, public payload: number) {}
}

export class RemoveFailed implements ActionWithTarget, ActionWithPayload<ErrorInfo> {
  readonly type = CommentActionType.RemoveFailed;

  constructor(public targetType: ActionTargetType, public payload: ErrorInfo) {}
}

export class ToggleDirection implements ActionWithTarget {
  readonly type = CommentActionType.ToggleDirection;

  constructor(public targetType: ActionTargetType) {}
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
  | RemoveFailed
  | ToggleDirection;
