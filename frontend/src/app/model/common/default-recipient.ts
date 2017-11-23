export class DefaultRecipient {
  constructor(
    public id?: number,
    public email?: string,
    public applicationType?: string
  ) {}

  public static ofType(type: string) {
    return new DefaultRecipient(undefined, undefined, type);
  }
}
