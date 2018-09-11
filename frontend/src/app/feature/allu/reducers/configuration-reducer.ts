import {ConfigurationKeyMap} from '@model/config/configuration';
import {ConfigurationActions, ConfigurationActionType} from '../actions/configuration-actions';

export interface State {
  configuration: ConfigurationKeyMap;
}

const initialState: State = {
  configuration: {}
};

export function reducer(state: State = initialState, action: ConfigurationActions) {
  switch (action.type) {
    case ConfigurationActionType.LoadSuccess: {
      return {...state, configuration: action.payload};
    }

    default: {
      return {...state};
    }
  }
}

export const getConfiguration = (state: State) => state.configuration;
