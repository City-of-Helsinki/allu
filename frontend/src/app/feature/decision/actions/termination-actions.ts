import {Action} from '@ngrx/store';
import {ActionWithPayload} from '@feature/common/action-with-payload';
import {ErrorInfo} from '@service/error/error-info';
import {TerminationInfo} from '@feature/decision/termination/termination-info';

export enum TerminationActionType {
  Load = '[Termination] Load termination',
  LoadSuccess = '[Termination] Load termination success',
  LoadFailed = '[Termination] Load termination failed',
  Terminate = '[Termination] Terminate termination',
  TerminationDraftSuccess = '[Termination] Termination draft success',
  TerminationDraftFailed = '[Termination] Termination draft failed',
  MoveTerminationToDecision = '[Termination] Move termination to decision',
  MoveTerminationToDecisionSuccess = '[Termination] Move termination to decision success',
  MoveTerminationToDecisionFailed = '[Termination] Move termination to decision failed'
}

export class Load implements Action {
  readonly type = TerminationActionType.Load;
  constructor() {}
}

export class LoadSuccess implements Action {
  readonly type = TerminationActionType.LoadSuccess;
  constructor(public payload: TerminationInfo) {}
}

export class LoadFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = TerminationActionType.LoadFailed;
  constructor(public payload: ErrorInfo) {}
}

export class Terminate implements Action {
  readonly type = TerminationActionType.Terminate;
  constructor(public payload: TerminationInfo) {}
}

export class TerminationDraftSuccess implements Action {
  readonly type = TerminationActionType.TerminationDraftSuccess;
  constructor() {}
}

export class TerminationDraftFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = TerminationActionType.TerminationDraftFailed;
  constructor(public payload: ErrorInfo) {}
}

export class MoveTerminationToDecision implements Action {
  readonly type = TerminationActionType.MoveTerminationToDecision;
  constructor() {}
}

export class MoveTerminationToDecisionSuccess implements Action {
  readonly type = TerminationActionType.MoveTerminationToDecisionSuccess;
  constructor() {}
}

export class MoveTerminationToDecisionFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = TerminationActionType.MoveTerminationToDecisionFailed;
  constructor(public payload: ErrorInfo) {}
}

export type TerminationActions =
  | Load
  | LoadSuccess
  | LoadFailed
  | Terminate
  | TerminationDraftSuccess
  | TerminationDraftFailed
  | MoveTerminationToDecision
  | MoveTerminationToDecisionSuccess
  | MoveTerminationToDecisionFailed;
