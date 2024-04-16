import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {User} from '@model/user/user';
import {UserActions, UserActionType} from '@feature/allu/actions/user-actions';

export function sortConfiguration(left: User, right: User) {
  return (left.userName.toString().localeCompare(right.userName.toString()));
}

export interface State extends EntityState<User> {
  loading: boolean;
  selectedId: number;
}

export const adapter: EntityAdapter<User> = createEntityAdapter<User>({
  selectId: (configuration: User) => configuration.id,
  sortComparer: sortConfiguration
});

const initialState: State = adapter.getInitialState({
  loading: false,
  selectedId: undefined
});

export function reducer(state: State = initialState, action: UserActions) {
  switch (action.type) {
    case UserActionType.Load: {
      return {
        ...state,
        loading: true
      };
    }

    case UserActionType.LoadSuccess: {
      return adapter.setAll(action.payload, {
        ...state,
        loading: false
      });
    }

    default: {
      return {...state};
    }
  }
}
