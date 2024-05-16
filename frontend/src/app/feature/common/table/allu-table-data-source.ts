import {MatLegacyTableDataSource as MatTableDataSource} from '@angular/material/legacy-table';

export interface SortOptions {
  caseInsensitiveFields: string[];
}

const defaultOptions: SortOptions = {
  caseInsensitiveFields: []
};

export class AlluTableDataSource<T> extends MatTableDataSource<T> {
  private caseInsensitiveFields: { [key: string]: boolean };

  constructor(initialData?: T[], sortOptions: SortOptions = defaultOptions) {
    super(initialData);

    this.caseInsensitiveFields = sortOptions.caseInsensitiveFields.reduce((fields, cur) => {
      fields[cur] = true;
      return fields;
    }, {});

    this.sortingDataAccessor = (data: any, sortHeaderId: string): string => {
      if (typeof data[sortHeaderId] === 'string' && this.caseInsensitiveFields[sortHeaderId]) {
        return data[sortHeaderId].toLocaleLowerCase();
      }
      return data[sortHeaderId];
    };
  }
}
