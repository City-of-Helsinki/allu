import {WorkQueueTab} from '@feature/workqueue/workqueue-tab';
import {ActionWithTarget} from '@feature/allu/actions/action-with-target';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

export enum WorkQueueActionType {
  SetTab = '[WorkQueue] Set tab'
}

export class SetTab implements ActionWithTarget {
  readonly type = WorkQueueActionType.SetTab;

  constructor(public targetType: ActionTargetType, public payload: WorkQueueTab) {}
}

export type WorkqueueActions =
  | SetTab;
