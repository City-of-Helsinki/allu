import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {ChargeBasisEntry} from '@model/application/invoice/charge-basis-entry';
import {ChargeBasisActions, ChargeBasisActionType} from '@feature/application/invoicing/actions/charge-basis-actions';

export interface State extends EntityState<ChargeBasisEntry> {
  selectedId: number;
}

export const adapter: EntityAdapter<ChargeBasisEntry> = createEntityAdapter<ChargeBasisEntry>({
  selectId: (entry: ChargeBasisEntry) => entry.id
});

export const initialState: State = adapter.getInitialState({
  selectedId: undefined
});

export function reducer(state: State = initialState, action: ChargeBasisActions) {
  switch (action.type) {

    case ChargeBasisActionType.LoadSuccess: {
      return adapter.setAll(action.payload, state);
    }

    case ChargeBasisActionType.AddEntry: {
      return adapter.addOne(action.payload, state);
    }

    case ChargeBasisActionType.UpdateEntry:
    case ChargeBasisActionType.UpdateEntrySuccess: {
      return adapter.upsertOne(action.payload, state);
    }

    case ChargeBasisActionType.RemoveEntry: {
      return adapter.removeOne(action.payload, state);
    }

    default: {
      return {
        ...state
      };
    }
  }
}
