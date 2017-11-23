import {MapUtil} from '../src/app/service/map/map.util';
import {DirectGeometryObject} from 'geojson';

describe('MapService', () => {
  it('should project wgs84 to epsg:3879 correctly', () => {
    let mapService = new MapUtil();
    let x = 25496808.002263;
    let y = 6673112.200334;
    let longitude = 24.9424988;
    let latitude = 60.1708763;
    let epsg3879 = mapService.wgs84ToEpsg3879([longitude, latitude]);
    expect(epsg3879[0]).toBeCloseTo(x, 0);
    expect(epsg3879[1]).toBeCloseTo(y, 0);
  });

  it('should project epsg:3879 to wgs84 correctly', () => {
    let mapService = new MapUtil();
    let x = 25496808.002263;
    let y = 6673112.200334;
    let longitude = 24.9424988;
    let latitude = 60.1708763;
    let wgs84 = mapService.epsg3879ToWgs84([x, y]);
    expect(wgs84[0]).toBeCloseTo(longitude, 0);
    expect(wgs84[1]).toBeCloseTo(latitude, 0);
  });

  it('should map feature collections to geometry collections and vice versa', () => {
    let mapService = new MapUtil();
    let geoJSON = '' +
      '{"type":"GeometryCollection",' +
       '"crs": {"properties":{"name":"EPSG:3879"},"type":"name"},' +
       '"geometries":[{"type":"Polygon",' +
        '"coordinates":[' +
            '[[25496796.034103394,6673116.6363515025],' +
            '[25496768.034103394,6673024.6363515],' +
            '[25496900.034103394,6673024.636351503],' +
            '[25496796.034103394,6673116.6363515025]]]}]}';

    let originalGeometryCollection = JSON.parse(geoJSON);
    let coordinates = originalGeometryCollection.geometries[0].coordinates[0];

    let featureCollection = mapService.geometryCollectionToFeatureCollection(originalGeometryCollection);
    let geometryCollection = mapService.featureCollectionToGeometryCollection(featureCollection);
    let processedGeometries = <DirectGeometryObject>geometryCollection.geometries[0];
    let processedCoordinates = <number[][]>processedGeometries.coordinates[0];

    // compare converted coordinates after rounding them a little bit
    expect(coordinates.length).toBe(processedCoordinates.length);
    for (let i = 0; i < coordinates.length; ++i) {
      expect(Helper.compareCoordinates(coordinates[i], processedCoordinates[i]))
        .toBeTruthy(coordinates[i] + ' does not match ' + processedCoordinates[i]);
    }

    // have to clear coordinates before comparing structures, because coordinate conversion never returns exactly the same values
    processedGeometries.coordinates[0] = [];
    originalGeometryCollection.geometries[0].coordinates[0] = [];
    let roundedOriginalGeoJSON = JSON.stringify(originalGeometryCollection);
    let processedGeoJSON = JSON.stringify(geometryCollection);
    expect(processedGeoJSON).toBe(roundedOriginalGeoJSON);
  });

});

class Helper {
  static compareCoordinates(coordinate1: Array<number>, coordinate2: Array<number>): boolean {
    return Math.trunc(coordinate1[0]) === Math.trunc(coordinate2[0]) && Math.trunc(coordinate1[1]) === Math.trunc(coordinate2[1]);
  }
}

