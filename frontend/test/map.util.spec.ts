import {MapUtil} from '@service/map/map.util';
import {DirectGeometryObject} from 'geojson';
import {Projection} from '@feature/map/projection';

describe('MapService', () => {
  it('should map feature collections to geometry collections and vice versa', () => {
    const mapService = new MapUtil(new Projection());
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

    const featureCollection = mapService.createFeatureCollection(originalGeometryCollection);
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

