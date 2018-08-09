import {Action} from '@ngrx/store';
import {DecisionTab} from '@feature/decision/documents/decision-tab';

export enum DocumentActionType {
  ShowActions = '[Decision documents] Set showing actions',
  SetTab = '[Decision documents] Set current tab'
}

export class ShowActions implements Action {
  readonly type = DocumentActionType.ShowActions;
  constructor(public payload: boolean) {}
}

export class SetTab implements Action {
  readonly type = DocumentActionType.SetTab;
  constructor(public payload: DecisionTab) {}
}

export type DocumentActions =
  | ShowActions
  | SetTab;
