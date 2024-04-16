import {InvoicingPeriodActions, InvoicingPeriodActionType} from '@feature/application/invoicing/actions/invoicing-period-actions';
import {InvoicingPeriod} from '@feature/application/invoicing/invoicing-period/invoicing-period';
import {createEntityAdapter, EntityAdapter, EntityState} from '@ngrx/entity';
import {compareTo} from '@app/model/application/application-status';

export function sortInvoicingPeriods(left: InvoicingPeriod, right: InvoicingPeriod) {
  if (left.startTime && right.startTime) {
    return left.startTime.getTime() - right.startTime.getTime();
  } else if (left.invoicableStatus && right.invoicableStatus) {
    return compareTo(left.invoicableStatus, right.invoicableStatus);
  }
  return 0;
}

export interface State extends EntityState<InvoicingPeriod> {
  processing: boolean;
}
export const adapter: EntityAdapter<InvoicingPeriod> = createEntityAdapter<InvoicingPeriod>({
  selectId: (period: InvoicingPeriod) => period.id,
  sortComparer: sortInvoicingPeriods
});

export const initialState: State = adapter.getInitialState({
  processing: false,
});

export function reducer(state: State = initialState, action: InvoicingPeriodActions) {
  switch (action.type) {
    case InvoicingPeriodActionType.Load:
    case InvoicingPeriodActionType.Change:
    case InvoicingPeriodActionType.Remove: {
      return {
        ...state,
        processing: true,
      };
    }

    case InvoicingPeriodActionType.LoadSuccess: {
      return adapter.setAll(action.payload, {
        ...state,
        processing: false,
      });
    }

    case InvoicingPeriodActionType.RemoveSuccess: {
      return adapter.removeAll({
        ...state,
        processing: false
      });
    }

    case InvoicingPeriodActionType.OperationFailed: {
      return {
        ...state,
        processing: false
      };
    }

    default: {
      return {...state};
    }
  }
}

export const getProcessing = (state: State) => state.processing;
