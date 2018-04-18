export class Page<T> {
  constructor(
    public content: T[] = [],
    public first?: boolean,
    public last?: boolean,
    public pageNumber?: number,
    public numberOfElements?: number,
    public size?: number,
    public sort?: any, // always null from backend
    public totalElements?: number,
    public totalPages?: number
  ) {}
}

