import {Action} from '@ngrx/store';
import {DecisionTab} from '@feature/decision/documents/decision-tab';

export enum DocumentActionType {
  SetTab = '[Decision documents] Set current tab'
}

export class SetTab implements Action {
  readonly type = DocumentActionType.SetTab;
  constructor(public payload: DecisionTab) {}
}

export type DocumentActions =
  | SetTab;
