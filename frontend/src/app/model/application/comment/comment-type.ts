export enum CommentType {
  INVOICING = 'INVOICING',
  RETURN = 'RETURN',
  REJECT = 'REJECT',
  INTERNAL = 'INTERNAL',
  PROPOSE_APPROVAL = 'PROPOSE_APPROVAL',
  PROPOSE_REJECT = 'PROPOSE_REJECT',
  PROPOSE_TERMINATION = 'PROPOSE_TERMINATION',
  TO_EXTERNAL_SYSTEM = 'TO_EXTERNAL_SYSTEM'
}

export const manualComments: Array<CommentType> = [
  CommentType.INVOICING,
  CommentType.INTERNAL,
  CommentType.TO_EXTERNAL_SYSTEM
];

export const decisionProposalComments: Array<CommentType> = [
  CommentType.PROPOSE_APPROVAL,
  CommentType.PROPOSE_REJECT,
  CommentType.PROPOSE_TERMINATION
];
