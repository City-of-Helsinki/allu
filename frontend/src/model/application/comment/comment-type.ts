export enum CommentType {
  INVOICING,
  RETURN,
  REJECT,
  INTERNAL,
  PROPOSE_APPROVAL,
  PROPOSE_REJECT
}

export const manualComments: Array<CommentType> = [
  CommentType.INVOICING,
  CommentType.INTERNAL
];

export const decisionProposalComments: Array<CommentType> = [
  CommentType.PROPOSE_APPROVAL,
  CommentType.PROPOSE_REJECT
];

export const manualCommentNames = manualComments.map(type => CommentType[type]);
