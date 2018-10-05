import {DocumentActions, DocumentActionType} from '@feature/decision/actions/document-actions';
import {DecisionTab} from '@feature/decision/documents/decision-tab';

export interface State {
  tab: DecisionTab;
}

const initialState: State = {
  tab: DecisionTab.DECISION
};

export function reducer(state: State = initialState, action: DocumentActions) {
  switch (action.type) {
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

export const getTab = (state: State) => state.tab;
