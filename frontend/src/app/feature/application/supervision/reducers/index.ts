import * as fromSupervisionTask from './supervision-task-reducers';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';

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
