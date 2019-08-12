import * as fromChargeBasis from './charge-basis-reducer';
import * as fromCustomer from './invoicing-customer-reducer';
import * as fromInvoice from './invoice-reducer';
import * as fromInvoicingPeriod from './invoicing-period-reducer';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import {ChargeBasisEntry} from '@model/application/invoice/charge-basis-entry';
import {ArrayUtil} from '@util/array-util';
import {TimeUtil} from '@util/time.util';

export interface InvoicingState {
  invoice: fromInvoice.State;
  chargeBasis: fromChargeBasis.State;
  customer: fromCustomer.State;
  periods: fromInvoicingPeriod.State;
}

export interface State {
  invoicing: InvoicingState;
}

export const reducers: ActionReducerMap<InvoicingState> = {
  invoice: fromInvoice.reducer,
  chargeBasis: fromChargeBasis.reducer,
  customer: fromCustomer.reducer,
  periods: fromInvoicingPeriod.reducer
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

export const getCustomerEntityState = createSelector(
  getInvoicingState,
  (state: InvoicingState) => state.customer
);

export const getInvoicingCustomer = createSelector(
  getCustomerEntityState,
  fromCustomer.getCustomer
);

export const getInvoicingCustomerLoading = createSelector(
  getCustomerEntityState,
  fromCustomer.getLoading
);

export const getInvoiceEntityState = createSelector(
  getInvoicingState,
  (state: InvoicingState) => state.invoice
);

export const {
  selectIds: getInvoiceIds,
  selectEntities: getInvoiceEntities,
  selectAll: getAllInvoices,
  selectTotal: getInvoicesTotal
} = fromInvoice.adapter.getSelectors(getInvoiceEntityState);

export const getSortedInvoicesByDate = createSelector(
  getAllInvoices,
  invoices => invoices
    ? invoices.sort((l, r) => TimeUtil.compareTo(l.invoicableTime, r.invoicableTime))
    : invoices
);

export const getEarliestInvoicable = createSelector(
  getSortedInvoicesByDate,
  invoices => ArrayUtil.first(invoices, (i) => !!i.invoicableTime)
);


/**
 * Invoicing periods
 */
export const getInvoicingPeriodEntityState = createSelector(
  getInvoicingState,
  (state: InvoicingState) => state.periods
);

export const {
  selectIds: getPeriodIds,
  selectEntities: getPeriodEntities,
  selectAll: getAllPeriods,
  selectTotal: getPeriodTotal
} = fromInvoicingPeriod.adapter.getSelectors(getInvoicingPeriodEntityState);

export const getPeriodsProcessing = createSelector(
  getInvoicingPeriodEntityState,
  fromInvoicingPeriod.getProcessing
);
