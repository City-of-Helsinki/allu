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
  sort?: Sort;
  pageRequest?: PageRequest;
  matchAny?: boolean;
}

export const CustomerSearchMinChars = (term: string) => !!term && term.length >= 2;
