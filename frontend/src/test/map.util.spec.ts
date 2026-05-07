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

  it('should not throw and should skip nested GeometryCollection geometries gracefully', () => {
    const mapService = new MapUtil(new Projection());

    // The unproject() helper assumes .coordinates exists, but GeometryCollection has
    // .geometries instead — causing "TypeError: Cannot read properties of undefined (reading '0')"
    // which aborts the entire Array.map and prevents all applications from being drawn.
    const geoJSON = {
      type: 'GeometryCollection',
      crs: { type: 'name', properties: { name: 'EPSG:3879' } },
      geometries: [
        {
          // Valid polygon — should still be rendered after the fix
          type: 'Polygon',
          coordinates: [
            [
              [25500000, 6700000],
              [25500100, 6700000],
              [25500100, 6700100],
              [25500000, 6700000]
            ]
          ]
        },
        {
          // Nested GeometryCollection — has no .coordinates, only .geometries, which causes
          // "TypeError: Cannot read properties of undefined (reading '0')" in unproject().
          type: 'GeometryCollection',
          geometries: [
            {
              type: 'Polygon',
              coordinates: [
                [
                  [25501000, 6701000],
                  [25501100, 6701000],
                  [25501100, 6701100],
                  [25501000, 6701000]
                ]
              ]
            }
          ]
        }
      ]
    };

    // Before the fix this throws:
    // "TypeError: Cannot read properties of undefined (reading '0')"
    expect(() => mapService.createFeatureCollection(geoJSON as any)).not.toThrow();

    // The valid polygon should still produce a feature — the bad geometry must not
    // abort the entire collection
    const fc = mapService.createFeatureCollection(geoJSON as any);
    expect(fc.features.length).toBeGreaterThanOrEqual(1);
  });
});

class Helper {
  static compareCoordinates(coordinate1: Array<number>, coordinate2: Array<number>): boolean {
    return Math.trunc(coordinate1[0]) === Math.trunc(coordinate2[0]) && Math.trunc(coordinate1[1]) === Math.trunc(coordinate2[1]);
  }
}

