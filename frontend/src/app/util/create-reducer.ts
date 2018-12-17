import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {ActionWithTarget} from '@feature/allu/actions/action-with-target';

export function createReducer<State, ActionType extends ActionWithTarget>(
  initialState: State,
  reducer: (state: any, action: ActionType) => State) {
  return (targetType: ActionTargetType) => {
    return (state: State = initialState, action: ActionType) => {
      if (targetType === action.targetType) {
        return reducer(state, action);
      } else {
        return state;
      }
    };
  };
}





