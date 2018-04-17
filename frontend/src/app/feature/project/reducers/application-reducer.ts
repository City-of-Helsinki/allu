import {ApplicationActions, ApplicationActionTypes} from '../actions/application-actions';
import {Application} from '../../../model/application/application';
import {Sort} from '../../../model/common/sort';
import {PageRequest} from '../../../model/common/page-request';
import {Page, add as addToPage, remove as removeFromPage} from '../../../model/common/page';

export interface State {
  sort: Sort;
  pageRequest: PageRequest;
  loading: boolean;
  page: Page<Application>;
}

const initialState: State = {
  sort: undefined,
  pageRequest: undefined,
  loading: false,
  page: new Page<Application>()
};

export function reducer(state: State = initialState, action: ApplicationActions) {
  switch (action.type) {
    case ApplicationActionTypes.Load: {
      return {
        ...state,
        loading: true,
        sort: action.payload.sort,
        pageRequest: action.payload.pageRequest
      };
    }

    case ApplicationActionTypes.LoadSuccess: {
      return {
        ...state,
        loading: false,
        page: action.payload
      };
    }

    case ApplicationActionTypes.LoadFailed: {
      return {
        ...state,
        loading: false,
        page: new Page<Application>()
      };
    }

    case ApplicationActionTypes.AddSuccess: {
      return {
        ...state,
        page: addToPage(state.page, action.payload),
        sort: undefined,
        pageRequest: undefined
      };
    }

    case ApplicationActionTypes.RemoveSuccess: {
      return {
        ...state,
        page: removeFromPage(state.page, action.payload),
        sort: undefined,
        pageRequest: undefined
      };
    }

    default:
      return {...state};
  }
}

export const getLoading = (state: State) => state.loading;

export const getPage = (state: State) => state.page;
