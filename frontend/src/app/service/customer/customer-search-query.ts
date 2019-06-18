import {CustomerType} from '@model/customer/customer-type';
import {PageRequest} from '@model/common/page-request';
import {Sort} from '@model/common/sort';

export interface CustomerSearchQuery {
  name?: string;
  registryKey?: string;
  type?: string;
  active?: boolean;
  invoicingOnly?: boolean;
  matchAny?: boolean;
}

export interface CustomerSearchByType {
  type: CustomerType;
  searchQuery: CustomerSearchQuery;
  active?: boolean;
  sort?: Sort;
  pageRequest?: PageRequest;
  matchAny?: boolean;
}

export const NAME_SEARCH_MIN_CHARS = 2;
export const REGISTRY_KEY_SEARCH_MIN_CHARS = 4;

export const CustomerNameSearchMinChars = (term: string) => !!term && term.length >= NAME_SEARCH_MIN_CHARS;
export const CustomerRegistryKeySearchMinChars = (term: string) => !!term && term.length >= REGISTRY_KEY_SEARCH_MIN_CHARS;
