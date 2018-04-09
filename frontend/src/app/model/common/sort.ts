import {MatSortable, Sort as MatSort} from '@angular/material';

export type SortDirection = 'asc' | 'desc' | '';

export class Sort {
  constructor(public field?: string, public direction?: SortDirection) {
    this.field = field;
    this.direction = direction;
  }

  public static fromMatSort(matSort: MatSort): Sort {
    return new Sort(matSort.active, matSort.direction);
  }

  public static toMatSortable(sort: Sort): MatSortable {
    const direction = sort.direction ? sort.direction : undefined;
    return sort.field && direction
      ? {id: sort.field, start: direction, disableClear: false}
      : {id: undefined, start: undefined, disableClear: false};
  }

  public byDirection<T>(original: Array<T>, sorted: Array<T>): Array<T> {
    switch (this.direction) {
      case 'asc':
        return sorted.reverse();
      case 'desc':
        return sorted;
      default:
        return original;
    }
  }

  public icon(): string {
    if (this.direction === 'desc') {
      return 'keyboard_arrow_down';
    } else if (this.direction === 'asc') {
      return 'keyboard_arrow_up';
    } else {
      return '';
    }
  }

  public sortFn():  (a, b) => number {
    const sort = (left, right) => {
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
