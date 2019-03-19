import {Action} from '@ngrx/store';
import {ChargeBasisEntry} from '@model/application/invoice/charge-basis-entry';

export enum ChargeBasisActionType {
  Load = '[Charge basis] Load charge basis entries',
  LoadSuccess = '[Charge basis] Load charge basis entries success',
  Save = '[Charge basis] Save charge basis entries',
  AddEntry = '[Charge basis] Add charge basis entry',
  UpdateEntry = '[Charge basis] Update charge basis entry',
  UpdateEntrySuccess = '[Charge basis] Update charge basis entry success',
  RemoveEntry = '[Charge basis] Remove charge basis entry',
  SetInvoicable = '[Charge basis] Set charge basis entry invoicable or not',
  SetInvoicableFailed = '[Charge basis] Setting charge basis entry invoicable failed',
}

export class Load implements Action {
  readonly type = ChargeBasisActionType.Load;
}

export class LoadSuccess implements Action {
  readonly type = ChargeBasisActionType.LoadSuccess;
  constructor(public payload: ChargeBasisEntry[]) {}
}

export class Save implements Action {
  readonly type = ChargeBasisActionType.Save;
  constructor(public payload: ChargeBasisEntry[]) {}
}

export class AddEntry implements Action {
  readonly type = ChargeBasisActionType.AddEntry;
  constructor(public payload: ChargeBasisEntry) {}
}

export class UpdateEntry implements Action {
  readonly type = ChargeBasisActionType.UpdateEntry;
  constructor(public payload: ChargeBasisEntry) {}
}

export class UpdateEntrySuccess implements Action {
  readonly type = ChargeBasisActionType.UpdateEntrySuccess;
  constructor(public payload: ChargeBasisEntry) {}
}

export class RemoveEntry implements Action {
  readonly type = ChargeBasisActionType.RemoveEntry;
  constructor(public payload: number) {}
}

export class SetInvoicable implements Action {
  readonly type = ChargeBasisActionType.SetInvoicable;
  readonly payload: {id: number, invoicable: boolean};
  constructor(private id: number, private invoicable: boolean) {
    this.payload = {id, invoicable};
  }
}

export class SetInvoicableFailed implements Action {
  readonly type = ChargeBasisActionType.SetInvoicableFailed;
  readonly payload: {id: number, invoicable: boolean};
  constructor(private id: number, private invoicable: boolean) {
    this.payload = {id, invoicable};
  }
}

export type ChargeBasisActions =
  | Load
  | LoadSuccess
  | Save
  | AddEntry
  | UpdateEntry
  | UpdateEntrySuccess
  | RemoveEntry
  | SetInvoicable
  | SetInvoicableFailed;

