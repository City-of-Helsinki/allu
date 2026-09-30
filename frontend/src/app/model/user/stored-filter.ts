import {StoredFilterType} from './stored-filter-type';

export class StoredFilter {
  constructor(
    public id?: number,
    public type?: StoredFilterType,
    public name?: string,
    public defaultFilter?: boolean,
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- backend/frontend JSON payload (dynamically typed API contract)
    public filter?: any,
    public userId?: number) {
  }

  get typeName(): string {
    return StoredFilterType[this.type];
  }
}
