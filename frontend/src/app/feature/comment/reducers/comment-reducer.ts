import {CommentActions, CommentActionType} from '../actions/comment-actions';
import {Comment} from '../../../model/application/comment/comment';
import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';

export interface State extends EntityState<Comment> {
  loading: boolean;
}

export const adapter: EntityAdapter<Comment> = createEntityAdapter<Comment>({
  selectId: (comment: Comment) => comment.id
});


export const initialState: State = adapter.getInitialState({
  loading: false
});

export function reducer(state: State = initialState, action: CommentActions) {
  switch (action.type) {
    case CommentActionType.LoadSuccess: {
      return adapter.addAll(action.payload, {
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
      return adapter.addOne(action.payload, {...state});
    }

    case CommentActionType.RemoveSuccess: {
      return adapter.removeOne(action.payload, {...state});
    }

    default: {
      return {...state};
    }
  }
}

export const getLoading = (state: State) => state.loading;
