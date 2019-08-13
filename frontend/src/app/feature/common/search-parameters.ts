import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';

export interface SearchParameters<T> {
  query: T;
  sort?: Sort;
  pageRequest?: PageRequest;
}
