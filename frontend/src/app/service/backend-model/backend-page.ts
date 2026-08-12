export interface BackendPage<T> {
  content: T[];
  first: boolean;
  last: boolean;
  'number': number;
  numberOfElements: number;
  size: number;
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- backend/frontend JSON payload (dynamically typed API contract)
  sort: any; // always null from backend
  totalElements: number;
  totalPages: number;
}
