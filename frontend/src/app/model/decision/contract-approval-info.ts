import {StatusChangeInfo} from '@model/application/status-change-info';
import {CommentType} from '@model/application/comment/comment-type';

export class ContractApprovalInfo extends StatusChangeInfo {
  constructor(
    public type?: CommentType,
    public comment?: string,
    public owner?: number,
    public frameAgreementExists?: boolean,
    public contractAsAttachment?: boolean
  ) {
    super(type, comment, owner);
  }
}
