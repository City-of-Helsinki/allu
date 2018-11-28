import {Action} from '@ngrx/store';
import {InvoicingPeriodLength} from '@feature/application/invoicing/invoicing-period/invoicing-period-length';
import {InvoicingPeriod} from '@feature/application/invoicing/invoicing-period/invoicing-period';

export enum InvoicingPeriodActionType {
  Load = '[InvoicingPeriod] Load invoicing period',
  LoadSuccess = '[InvoicingPeriod] Load invoicing period success',
  Change = '[InvoicingPeriod] Change invoicing period',
  Remove = '[InvoicingPeriod] Remove invoicing periods',
  RemoveSuccess = '[InvoicingPeriod] Remove invoicing periods success',
  OperationFailed = '[InvoicingPeriod] Operation failed'
}

export class Load implements Action {
  readonly type = InvoicingPeriodActionType.Load;
}

export class LoadSuccess implements Action {
  readonly type = InvoicingPeriodActionType.LoadSuccess;
  constructor(public payload: InvoicingPeriod[]) {}
}

export class Change implements Action {
  readonly type = InvoicingPeriodActionType.Change;
  constructor(public payload: InvoicingPeriodLength) {}
}

export class Remove implements Action {
  readonly type = InvoicingPeriodActionType.Remove;
}

export class RemoveSuccess implements Action {
  readonly type = InvoicingPeriodActionType.RemoveSuccess;
}

export class OperationFailed implements Action {
  readonly type = InvoicingPeriodActionType.OperationFailed;
}

export type InvoicingPeriodActions =
  | Load
  | LoadSuccess
  | Change
  | Remove
  | RemoveSuccess
  | OperationFailed;

export type InvoicingPeriodSuccessActions = LoadSuccess | RemoveSuccess;
export const invoicingPeriodSuccessActionTypes = [InvoicingPeriodActionType.LoadSuccess, InvoicingPeriodActionType.RemoveSuccess];
