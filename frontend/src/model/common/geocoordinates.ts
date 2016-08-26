export class Geocoordinates {
  constructor(
    public longitude: number,
    public latitude: number) {}

  public static fromArray(coordinates: Array<number>): Geocoordinates {
    return new Geocoordinates(coordinates[0], coordinates[1]);
  }

  public toArray(): Array<number> {
    return [this.longitude, this.latitude];
  }
}
