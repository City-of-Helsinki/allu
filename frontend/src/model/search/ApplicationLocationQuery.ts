export class ApplicationLocationQuery {
  constructor(
    public startDate: Date,
    public endDate: Date,
    public geometry: GeoJSON.GeometryObject
  ) {}
}
