import {WorkQueueTab} from '@feature/workqueue/workqueue-tab';
import {WorkqueueActions, WorkQueueActionType} from '@feature/workqueue/actions/workqueue-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

export interface State {
  tab: WorkQueueTab;
}

export const initialState: State = {
  tab: WorkQueueTab.OWN
};

export function reducer(state: State = initialState, action: WorkqueueActions) {
  switch (action.type) {
    case WorkQueueActionType.SetTab: {
      return {
        ...state,
        tab: action.payload
      };
    }

    default: {
      return {...state};
    }
  }
}

export const getTab = (state: State) => state.tab;

export function createReducerFor(targetType: ActionTargetType) {
  return function(state: State, action: WorkqueueActions) {
    if (targetType === action.targetType) {
      return reducer(state, action);
    } else {
      return state;
    }
  };
}
