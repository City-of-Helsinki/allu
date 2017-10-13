export class ContentRow<T extends {id?: number}> {
  constructor(public content: T, public selected?: boolean) {
    this.selected = !!selected;
  }

  get id(): number {
    return this.content.id;
  }
}
