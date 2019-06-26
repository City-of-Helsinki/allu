import {Action} from '@ngrx/store';
import {ActionWithPayload} from '@feature/common/action-with-payload';
import {ErrorInfo} from '@service/error/error-info';
import {TerminationInfo} from '@feature/decision/termination/termination-info';
import {TerminationDocument} from '@feature/decision/termination/TerminationDocument';

export enum TerminationActionType {
  LoadInfo = '[Termination] Load termination info',
  LoadInfoSuccess = '[Termination] Load termination info success',
  LoadInfoFailed = '[Termination] Load termination info failed',
  LoadDocument = '[Termination] Load termination document',
  LoadDocumentSuccess = '[Termination] Load termination document success',
  LoadDocumentFailed = '[Termination] Load termination document failed',
  Terminate = '[Termination] Terminate termination',
  TerminationDraftSuccess = '[Termination] Termination draft success',
  TerminationDraftFailed = '[Termination] Termination draft failed',
  MoveTerminationToDecision = '[Termination] Move termination to decision',
  MoveTerminationToDecisionSuccess = '[Termination] Move termination to decision success',
  MoveTerminationToDecisionFailed = '[Termination] Move termination to decision failed'
}

export class LoadInfo implements Action {
  readonly type = TerminationActionType.LoadInfo;
  constructor() {}
}

export class LoadInfoSuccess implements Action {
  readonly type = TerminationActionType.LoadInfoSuccess;
  constructor(public payload: TerminationInfo) {}
}

export class LoadInfoFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = TerminationActionType.LoadInfoFailed;
  constructor(public payload: ErrorInfo) {}
}

export class LoadDocument implements Action {
  readonly type = TerminationActionType.LoadDocument;
  constructor() {}
}

export class LoadDocumentSuccess implements Action {
  readonly type = TerminationActionType.LoadDocumentSuccess;
  constructor(public payload: TerminationDocument) {}
}

export class LoadDocumentFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = TerminationActionType.LoadDocumentFailed;
  constructor(public payload: ErrorInfo) {}
}

export class Terminate implements Action {
  readonly type = TerminationActionType.Terminate;
  constructor(public payload: TerminationInfo) {}
}

export class TerminationDraftSuccess implements ActionWithPayload<TerminationInfo> {
  readonly type = TerminationActionType.TerminationDraftSuccess;
  constructor(public payload: TerminationInfo) {}
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
  | LoadInfo
  | LoadInfoSuccess
  | LoadInfoFailed
  | LoadDocument
  | LoadDocumentSuccess
  | LoadDocumentFailed
  | Terminate
  | TerminationDraftSuccess
  | TerminationDraftFailed
  | MoveTerminationToDecision
  | MoveTerminationToDecisionSuccess
  | MoveTerminationToDecisionFailed;
