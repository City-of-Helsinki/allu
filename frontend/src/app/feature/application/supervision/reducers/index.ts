import * as fromSupervisionTask from './supervision-task-reducers';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import {SupervisionTask} from '@model/application/supervision/supervision-task';
import {ArrayUtil} from '@util/array-util';
import {SupervisionTaskType} from '@model/application/supervision/supervision-task-type';
import {SupervisionTaskStatusType} from '@model/application/supervision/supervision-task-status-type';

export interface State {
  supervisionTasks: fromSupervisionTask.State;
}

export const reducers: ActionReducerMap<State> = {
  supervisionTasks: fromSupervisionTask.reducer
};

export const getSupervisionTasksState = createFeatureSelector<State>('supervisionTasks');

export const getSupervisionTasksEntityState = createSelector(
  getSupervisionTasksState,
  (state: State) => state.supervisionTasks
);

export const {
  selectIds: getSupervisionTaskIds,
  selectEntities: getSupervisionTaskEntities,
  selectAll: getAllSupervisionTasks,
  selectTotal: getSupervisionTaskTotal
} = fromSupervisionTask.adapter.getSelectors(getSupervisionTasksEntityState);

export const getSaving = createSelector(
  getSupervisionTasksEntityState,
  fromSupervisionTask.getSaving
);

export const getLoading = createSelector(
  getSupervisionTasksEntityState,
  fromSupervisionTask.getLoading
);

export const getTaskBy = (type: SupervisionTaskType, status: SupervisionTaskStatusType) => createSelector(
  getAllSupervisionTasks,
  (tasks: SupervisionTask[]) => ArrayUtil.first(tasks, task => task.type === type && task.status === status)
);

export const getOpenOperationalConditionTask = getTaskBy(SupervisionTaskType.OPERATIONAL_CONDITION, SupervisionTaskStatusType.OPEN);

export const hasTask = (type: SupervisionTaskType, status: SupervisionTaskStatusType) => createSelector(
  getTaskBy(type, status),
  task => !!task
);
