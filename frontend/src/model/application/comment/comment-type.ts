export enum CommentType {
  INVOICING,
  RETURN,
  REJECT,
  INTERNAL
}

const manualComments: Array<CommentType> = [
  CommentType.INVOICING,
  CommentType.INTERNAL
];

export const manualCommentNames = manualComments.map(type => CommentType[type]);
