export class Page<T> {
  constructor(
    public content: T[] = [],
    public first?: boolean,
    public last?: boolean,
    public pageNumber?: number,
    public numberOfElements?: number,
    public size?: number,
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- backend/frontend JSON payload (dynamically typed API contract)
    public sort?: any, // always null from backend
    public totalElements?: number,
    public totalPages?: number
  ) {}
}
