export interface BackendPage<T> {
  content: T[];
  first: boolean;
  last: boolean;
  'number': number;
  numberOfElements: number;
  size: number;
  sort: any; // always null from backend
  totalElements: number;
  totalPages: number;
}
