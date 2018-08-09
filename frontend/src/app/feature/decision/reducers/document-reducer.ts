import {DocumentActions, DocumentActionType} from '@feature/decision/actions/document-actions';
import {DecisionTab} from '@feature/decision/documents/decision-tab';

export interface State {
  showActions: boolean;
  tab: DecisionTab;
}

const initialState: State = {
  showActions: false,
  tab: DecisionTab.DECISION
};

export function reducer(state: State = initialState, action: DocumentActions) {
  switch (action.type) {

    case DocumentActionType.ShowActions: {
      return {
        ...state,
        showActions: action.payload
      };
    }

    case DocumentActionType.SetTab: {
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

export const getShowActions = (state: State) => state.showActions;

export const getTab = (state: State) => state.tab;
