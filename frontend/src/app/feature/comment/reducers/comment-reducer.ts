import {CommentActions, CommentActionType} from '../actions/comment-actions';
import {Comment} from '@model/application/comment/comment';
import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {SortDirection, toggle} from '@model/common/sort';
import {TimeUtil} from '@util/time.util';

export interface State extends EntityState<Comment> {
  loading: boolean;
  direction: SortDirection;
}

export const adapter: EntityAdapter<Comment> = createEntityAdapter<Comment>({
  selectId: (comment: Comment) => comment.id
});

export const initialState: State = adapter.getInitialState({
  loading: false,
  direction: <SortDirection>'desc'
});

export function reducer(state: State = initialState, action: CommentActions) {
  switch (action.type) {
    case CommentActionType.Load: {
      return adapter.removeAll({
        ...state,
        loading: true
      });
    }

    case CommentActionType.LoadSuccess: {
      return adapter.setAll(action.payload, {
        ...state,
        loading: false
      });
    }

    case CommentActionType.LoadFailed: {
      return {
        ...state,
        loading: false
      };
    }

    case CommentActionType.SaveSuccess: {
      return adapter.upsertOne(action.payload, {...state});
    }

    case CommentActionType.RemoveSuccess: {
      return adapter.removeOne(action.payload, {...state});
    }

    case CommentActionType.ToggleDirection: {
      return {
        ...state,
        direction: toggle(state.direction)
      };
    }

    default: {
      return {...state};
    }
  }
}

export const getLoading = (state: State) => state.loading;

export const getDirection = (state: State) => state.direction;

export function sort(direction: SortDirection) {
  return (left: Comment, right: Comment) => {
    const result = TimeUtil.compareTo(left.updateTime, right.updateTime);
    return direction === 'desc' ? -result : result;
  };
}
