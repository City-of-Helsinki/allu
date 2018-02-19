import {MapUtil} from '../src/app/service/map/map.util';
import {DirectGeometryObject, GeometryObject} from 'geojson';

describe('MapService', () => {
  it('should project wgs84 to epsg:3879 correctly', () => {
    const mapService = new MapUtil();
    const x = 25496808.002263;
    const y = 6673112.200334;
    const longitude = 24.9424988;
    const latitude = 60.1708763;
    const epsg3879 = mapService.wgs84ToEpsg3879([longitude, latitude]);
    expect(epsg3879[0]).toBeCloseTo(x, 0);
    expect(epsg3879[1]).toBeCloseTo(y, 0);
  });

  it('should project epsg:3879 to wgs84 correctly', () => {
    const mapService = new MapUtil();
    const x = 25496808.002263;
    const y = 6673112.200334;
    const longitude = 24.9424988;
    const latitude = 60.1708763;
    const wgs84 = mapService.epsg3879ToWgs84([x, y]);
    expect(wgs84[0]).toBeCloseTo(longitude, 0);
    expect(wgs84[1]).toBeCloseTo(latitude, 0);
  });

  it('should map feature collections to geometry collections and vice versa', () => {
    const mapService = new MapUtil();
    const geoJSON = '' +
      '{"type":"GeometryCollection",' +
       '"crs": {"properties":{"name":"EPSG:3879"},"type":"name"},' +
       '"geometries":[{"type":"Polygon",' +
        '"coordinates":[' +
            '[[25496796.034103394,6673116.6363515025],' +
            '[25496768.034103394,6673024.6363515],' +
            '[25496900.034103394,6673024.636351503],' +
            '[25496796.034103394,6673116.6363515025]]]}]}';

    const originalGeometryCollection = JSON.parse(geoJSON);
    const coordinates = originalGeometryCollection.geometries[0].coordinates[0];

    const featureCollection = mapService.geometryCollectionToFeatureCollection(originalGeometryCollection);
    const geometryCollection = mapService.featureCollectionToGeometryCollection(featureCollection);
    const processedGeometries = <DirectGeometryObject>geometryCollection.geometries[0];
    const processedCoordinates = <number[][]>processedGeometries.coordinates[0];

    // compare converted coordinates after rounding them a little bit
    expect(coordinates.length).toBe(processedCoordinates.length);
    for (let i = 0; i < coordinates.length; ++i) {
      expect(Helper.compareCoordinates(coordinates[i], processedCoordinates[i]))
        .toBeTruthy(coordinates[i] + ' does not match ' + processedCoordinates[i]);
    }

    // have to clear coordinates before comparing structures, because coordinate conversion never returns exactly the same values
    processedGeometries.coordinates[0] = [];
    originalGeometryCollection.geometries[0].coordinates[0] = [];
    const roundedOriginalGeoJSON = JSON.stringify(originalGeometryCollection);
    const processedGeoJSON = JSON.stringify(geometryCollection);
    expect(processedGeoJSON).toBe(roundedOriginalGeoJSON);
  });

});

class Helper {
  static compareCoordinates(coordinate1: Array<number>, coordinate2: Array<number>): boolean {
    return Math.trunc(coordinate1[0]) === Math.trunc(coordinate2[0]) && Math.trunc(coordinate1[1]) === Math.trunc(coordinate2[1]);
  }
}

