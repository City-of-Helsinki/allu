import {Action} from '@ngrx/store';
import {WorkQueueTab} from '@feature/workqueue/workqueue-tab';

export enum WorkQueueActionType {
  SetTab = '[WorkQueue] Set tab'
}

export class SetTab implements Action {
  readonly type = WorkQueueActionType.SetTab;

  constructor(public payload: WorkQueueTab) {}
}

export type WorkqueueActions =
  | SetTab;
