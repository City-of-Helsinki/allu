import {MatSortable, Sort as MatSort} from '@angular/material';

export type SortDirection = 'asc' | 'desc' | '';

export function toggle(current: SortDirection): SortDirection {
  if (current === 'asc') {
    return 'desc';
  } else {
    return 'asc';
  }
}

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

  public icon(): string {
    if (this.direction === 'desc') {
      return 'keyboard_arrow_down';
    } else if (this.direction === 'asc') {
      return 'keyboard_arrow_up';
    } else {
      return '';
    }
  }
}
