import {CommentType} from '@model/application/comment/comment-type';
import {ContractApprovalInfo} from '@model/decision/contract-approval-info';

export interface BackendContractApprovalInfo {
  type?: CommentType;
  comment?: string;
  owner?: number;
  frameAgreementExists?: boolean;
  contractAsAttachment?: boolean;
}

export class ContractApprovalInfoMapper {
  public static mapFrontEnd(approvalInfo: ContractApprovalInfo): BackendContractApprovalInfo {
    return approvalInfo ? {
      type: approvalInfo.type,
      comment: approvalInfo.comment,
      owner: approvalInfo.owner,
      frameAgreementExists: approvalInfo.frameAgreementExists,
      contractAsAttachment: approvalInfo.contractAsAttachment
    } : undefined;
  }
}

