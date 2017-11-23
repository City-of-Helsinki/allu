export class ApplicationTag {
  constructor()
  constructor(
    type: string
  )
  constructor(
    type: string,
    addedBy: number,
    creationTime: Date
  )
  constructor(
    public type?: string,
    public addedBy?: number,
    public creationTime?: Date
  ) {}
}
