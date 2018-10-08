import {ApprovalDocument, ApprovalDocumentType} from '@model/decision/approval-document';
import {ActionWithPayload} from '@feature/common/action-with-payload';
import {ErrorInfo} from '@service/error/error-info';

export enum ApprovalDocumentActionType {
  Load = '[ApprovalDocument] Load approval document',
  LoadSuccess = '[ApprovalDocument] Load approval document success',
  LoadFailed = '[ApprovalDocument] Load approval document failed'
}

export class Load {
  readonly type = ApprovalDocumentActionType.Load;

  constructor(readonly documentType: ApprovalDocumentType) {
  }
}

export class LoadSuccess {
  readonly type = ApprovalDocumentActionType.LoadSuccess;
  constructor(readonly documentType: ApprovalDocumentType, public payload: ApprovalDocument) {
    this.documentType = payload.type;
  }
}

export class LoadFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ApprovalDocumentActionType.LoadFailed;
  constructor(readonly documentType: ApprovalDocumentType, public payload: ErrorInfo) {}
}

export type ApprovalDocumentActions =
  | Load
  | LoadSuccess
  | LoadFailed;
