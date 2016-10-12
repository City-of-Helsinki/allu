export enum Direction {
  ASC,
  DESC
}

export class Sort {
  constructor(public field: string, public direction: Direction) {
    this.field = field;
    this.direction = direction;
  }

  public icon(): string {
    if (this.direction === Direction.DESC) {
      return 'keyboard_arrow_down';
    } else if (this.direction === Direction.ASC) {
      return 'keyboard_arrow_up';
    } else {
      return '';
    }
  }
}
