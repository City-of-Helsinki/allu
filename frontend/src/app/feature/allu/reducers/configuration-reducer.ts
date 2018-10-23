import {Configuration} from '@model/config/configuration';
import {ConfigurationActions, ConfigurationActionType} from '../actions/configuration-actions';
import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';

export interface State extends EntityState<Configuration> {
  loading: boolean;
}

export const adapter: EntityAdapter<Configuration> = createEntityAdapter<Configuration>({
  selectId: (configuration: Configuration) => configuration.id
});

const initialState: State = adapter.getInitialState({
  loading: false
});

export function reducer(state: State = initialState, action: ConfigurationActions) {
  switch (action.type) {
    case ConfigurationActionType.LoadSuccess: {
      return adapter.addAll(action.payload, state);
    }

    default: {
      return {...state};
    }
  }
}
