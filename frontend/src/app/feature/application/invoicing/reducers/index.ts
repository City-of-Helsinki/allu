import * as fromChargeBasis from './charge-basis-reducer';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import {ChargeBasisEntry} from '@model/application/invoice/charge-basis-entry';

export interface InvoicingState {
  chargeBasis: fromChargeBasis.State;
}

export interface State {
  invoicing: InvoicingState;
}

export const reducers: ActionReducerMap<InvoicingState> = {
  chargeBasis: fromChargeBasis.reducer
};

export const getInvoicingState = createFeatureSelector<InvoicingState>('invoicing');

export const getChargeBasisEntityState = createSelector(
  getInvoicingState,
  (state: InvoicingState) => state.chargeBasis
);

export const {
  selectIds: getChargeBasisEntryIds,
  selectEntities: getChargeBasisEntryEntities,
  selectAll: getAllChargeBasisEntries,
  selectTotal: getChargeBasisEntryTotal
} = fromChargeBasis.adapter.getSelectors(getChargeBasisEntityState);

export const getAllReferrableChargeBasisEntries = createSelector(
  getAllChargeBasisEntries,
  (entries: ChargeBasisEntry[]) => entries.filter(e => e.referrable)
);
