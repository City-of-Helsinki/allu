import {StoredFilterType} from '../../model/user/stored-filter-type';
import {StoredFilter} from '../../model/user/stored-filter';
import {Some} from '../../util/option';

export class StoredFilterMapper {
  static mapBackendList(filters: BackendStoredFilter[]): StoredFilter[] {
    return filters ? filters.map(f => this.mapBackend(f)) : [];
  }

  static mapBackend(filter: BackendStoredFilter): StoredFilter {
    return new StoredFilter(
      filter.id,
      StoredFilterType[filter.type],
      filter.name,
      filter.defaultFilter,
      Some(filter.filter).map(f => JSON.parse(f)).orElse({}),
      filter.userId
    );
  }

  static mapFrontend(filter: StoredFilter): BackendStoredFilter {
    return (filter) ? {
      id: filter.id,
      type: StoredFilterType[filter.type],
      name: filter.name,
      defaultFilter: filter.defaultFilter,
      filter: Some(filter.filter).map(f => JSON.stringify(f)).orElse('{}'),
      userId: filter.userId
    } : undefined;
  }
}

export interface BackendStoredFilter {
  id: number;
  type: string;
  name: string;
  defaultFilter: boolean;
  filter: string;
  userId: number;
}
