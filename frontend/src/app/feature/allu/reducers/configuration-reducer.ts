import {Configuration} from '@model/config/configuration';
import {ConfigurationActions, ConfigurationActionType} from '../../admin/configuration/actions/configuration-actions';
import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';

export function sortConfiguration(left: Configuration, right: Configuration) {
  return (left.type.toString().localeCompare(right.type.toString()))
    || (left.id - right.id);
}

export interface State extends EntityState<Configuration> {
  loading: boolean;
}

export const adapter: EntityAdapter<Configuration> = createEntityAdapter<Configuration>({
  selectId: (configuration: Configuration) => configuration.id,
  sortComparer: sortConfiguration
});

const initialState: State = adapter.getInitialState({
  loading: false
});

export function reducer(state: State = initialState, action: ConfigurationActions) {
  switch (action.type) {
    case ConfigurationActionType.LoadSuccess: {
      return adapter.setAll(action.payload, state);
    }

    case ConfigurationActionType.SaveSuccess: {
      return adapter.upsertOne(action.payload, state);
    }

    default: {
      return {...state};
    }
  }
}
