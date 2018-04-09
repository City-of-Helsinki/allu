import {StoredFilterType} from '../../model/user/stored-filter-type';
import {StoredFilter} from '../../model/user/stored-filter';
import {Some} from '../../util/option';
import {TimeUtil} from '../../util/time.util';
import {ArrayUtil} from '../../util/array-util';
import {ApplicationType} from '../../model/application/type/application-type';

export class StoredFilterMapper {
  static mapBackendList(filters: BackendStoredFilter[]): StoredFilter[] {
    return filters ? filters.map(f => this.mapBackend(f)) : [];
  }

  static mapBackend(filter: BackendStoredFilter): StoredFilter {
    const type = StoredFilterType[filter.type];

    return new StoredFilter(
      filter.id,
      type,
      filter.name,
      filter.defaultFilter,
      Some(filter.filter).map(f => this.mapFilterForType(type, f)).orElse({}),
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

  private static mapFilterForType(type: StoredFilterType, filterString: string): any {
    const filter = JSON.parse(filterString);

    switch (type) {
      case StoredFilterType.MAP:
        return StoredFilterMapper.mapBackendMap(filter);
      case StoredFilterType.WORKQUEUE:
        return StoredFilterMapper.mapBackendWorkqueue(filter);
      default:
        throw new Error('No mapper for filter ' + StoredFilterType[type]);
    }
  }

  private static mapBackendMap(filter: any): any {
    return {
      address: filter.address,
      startDate: TimeUtil.dateFromBackend(filter.startDate),
      endDate: TimeUtil.dateFromBackend(filter.endDate),
      statuses: filter.statuses
    };
  }

  private static mapBackendWorkqueue(filter: any): any {
    const search = filter.search;

    const mappedSearch = {
      ...search,
      startTime: TimeUtil.dateFromBackend(search.startTime),
      endTime: TimeUtil.dateFromBackend(search.endTime),
      type: search.type,
      status: search.status,
      districts: ArrayUtil.map(search.districts, val => Number(val)),
      owner: search.owner
    };

    return {
      search: mappedSearch,
      sort: filter.sort
    };
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
