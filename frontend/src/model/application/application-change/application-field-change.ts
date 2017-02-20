export class ApplicationFieldChange {
  constructor()
  constructor(
    fieldName: string,
    oldValue: string,
    newValue: string
  )
  constructor(
    public fieldName?: string,
    public oldValue?: string,
    public newValue?: string
  ) {}
}
