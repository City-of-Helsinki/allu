import {MatLegacyPaginatorIntl as MatPaginatorIntl} from '@angular/material/legacy-paginator';
import {Injectable} from '@angular/core';
import {findTranslation} from '../../util/translations';

@Injectable()
export class AlluPaginatorIntl extends MatPaginatorIntl {
  constructor() {
    super();
    this.itemsPerPageLabel = findTranslation('common.paginator.itemsPerPage');
    this.nextPageLabel = findTranslation('common.paginator.nextPage');
    this.previousPageLabel = findTranslation('common.paginator.previousPage');
  }

  /**
   * Replace MatPaginators range label implementation
   */
  getRangeLabel = (page: number, pageSize: number, length: number) => {
    const of = findTranslation('common.paginator.of');
    if (length === 0 || pageSize === 0) { return `0 ${of} ${length}`; }

    length = Math.max(length, 0);

    const startIndex = page * pageSize;

    // If the start index exceeds the list length, do not try and fix the end index to the end.
    const endIndex = startIndex < length ?
      Math.min(startIndex + pageSize, length) :
      startIndex + pageSize;

    return `${startIndex + 1} - ${endIndex} ${of} ${length}`;
  }
}
