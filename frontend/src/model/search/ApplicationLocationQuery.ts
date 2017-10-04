export class ApplicationLocationQuery {
  constructor(
    public startDate: Date,
    public endDate: Date,
    public statusTypes: Array<string>,
    public geometry: GeoJSON.GeometryObject
  ) {}
}
