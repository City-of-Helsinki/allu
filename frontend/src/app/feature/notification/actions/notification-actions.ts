import {ActionWithPayload} from '@feature/common/action-with-payload';
import {ErrorInfo} from '@service/error/error-info';

export enum NotificationActionType {
  NotifySuccess = '[Notification] Notify success',
  NotifyFailure = '[Notification] Notify failure'
}

export class NotifySuccess implements ActionWithPayload<string> {
  readonly type = NotificationActionType.NotifySuccess;
  constructor(public payload: string) {}
}

export class NotifyFailure implements ActionWithPayload<ErrorInfo> {
  readonly type = NotificationActionType.NotifyFailure;
  constructor(public payload: ErrorInfo) {}
}

export type NotificationActions =
  | NotifySuccess
  | NotifyFailure;
