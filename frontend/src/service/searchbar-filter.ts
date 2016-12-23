export class SearchbarFilter {
  constructor()
  constructor(search: string,
              startDate?: Date,
              endDate?: Date)
  constructor(public search?: string,
              public startDate?: Date,
              public endDate?: Date) {}
}
