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

export function remove<T extends WithId>(currentPage: Page<T>, id: number): Page<T> {
  const page = {...currentPage};
  page.content = page.content.filter(item => item.id !== id);
  page.size -= 1;
  page.numberOfElements -= 1;
  page.totalElements -= 1;
  return page;
}

export function add<T>(currentPage: Page<T>, items: T[]): Page<T> {
  const page = {...currentPage};
  page.content = page.content.concat(items);
  const itemCount = items.length;
  page.size += itemCount;
  page.numberOfElements += itemCount;
  page.totalElements += itemCount;
  return page;
}

interface WithId {
  id?: number;
}

