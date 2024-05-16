import {Invoice} from '@model/application/invoice/invoice';
import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {InvoiceActions, InvoiceActionType} from '@feature/application/invoicing/actions/invoice-actions';

export interface State extends EntityState<Invoice> {
  selectedId: number;
}

export const adapter: EntityAdapter<Invoice> = createEntityAdapter<Invoice>({
  selectId: (invoice: Invoice) => invoice.id
});

export const initialState: State = adapter.getInitialState({
  selectedId: undefined
});

export function reducer(state: State = initialState, action: InvoiceActions) {
  switch (action.type) {
    case InvoiceActionType.LoadSuccess: {
      return adapter.setAll(action.payload, state);
    }

    default: {
      return {
        ...state
      };
    }
  }
}
