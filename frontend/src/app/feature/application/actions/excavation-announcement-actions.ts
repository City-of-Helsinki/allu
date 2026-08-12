import {Action} from '@ngrx/store';
import {RequiredTasks} from '@model/application/required-tasks';

export enum ExcavationAnnouncementActionType {
  SetRequiredTasks = '[ExcavationAnnouncement] Set required tasks'
}

export class SetRequiredTasks implements Action {
  readonly type = ExcavationAnnouncementActionType.SetRequiredTasks;
  constructor(public payload: RequiredTasks) {}
}

export type ExcavationAnnouncementActions =
  | SetRequiredTasks;
