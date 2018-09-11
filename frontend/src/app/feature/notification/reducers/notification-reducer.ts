import {ErrorInfo} from '@service/error/error-info';
import {NotificationActions, NotificationActionType} from '@feature/notification/actions/notification-actions';

export interface State {
  success: string;
  error: ErrorInfo;
}

const initialState: State = {
  success: undefined,
  error: undefined
};

export function reducer(state: State = initialState, action: NotificationActions) {
  switch (action.type) {
    case NotificationActionType.NotifySuccess: {
      return {...state, success: action.payload};
    }

    case NotificationActionType.NotifyFailure: {
      return {...state, error: action.payload};
    }

    default: {
      return {...state};
    }
  }
}

export const getSuccess = (state: State) => state.success;
export const getError = (state: State) => state.error;
