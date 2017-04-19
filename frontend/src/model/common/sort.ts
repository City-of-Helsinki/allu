export enum Direction {
  ASC,
  DESC
}

export class Sort {
  constructor(public field?: string, public direction?: Direction) {
    this.field = field;
    this.direction = direction;
  }

  public byDirection<T>(original: Array<T>, sorted: Array<T>): Array<T> {
    switch (this.direction) {
      case Direction.ASC:
        return sorted.reverse();
      case Direction.DESC:
        return sorted;
      default:
        return original;
    }
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

  public sortFn():  (a, b) => number {
    let sort = (left, right) => {
      if (left[this.field] > right[this.field]) {
        return 1;
      }
      if (left[this.field] < right[this.field]) {
        return -1;
      }
      // a must be equal to b
      return 0;
    };
    return sort;
  }
}
