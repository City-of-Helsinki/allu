/**
 * Value for describing dialog close value.
 */
export enum DialogCloseReason {
  OK,
  CANCEL
}

export class DialogCloseValue {
  /**
   * Constructor.
   *
   * @param       reason  The reason why (which button was pressed) dialog was closed.
   * @param       result  Result of the dialog returned to the dialog creator.
   */
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  public constructor(public reason: DialogCloseReason, public result: any) {}
}
