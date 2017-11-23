"use strict";
exports.__esModule = true;
var map_util_1 = require("../src/service/map/map.util");
describe('MapService', function () {
    it('should project wgs84 to epsg:3879 correctly', function () {
        var mapService = new map_util_1.MapUtil();
        var x = 25496808.002263;
        var y = 6673112.200334;
        var longitude = 24.9424988;
        var latitude = 60.1708763;
        var epsg3879 = mapService.wgs84ToEpsg3879([longitude, latitude]);
        expect(epsg3879[0]).toBeCloseTo(x, 0);
        expect(epsg3879[1]).toBeCloseTo(y, 0);
    });
    it('should project epsg:3879 to wgs84 correctly', function () {
        var mapService = new map_util_1.MapUtil();
        var x = 25496808.002263;
        var y = 6673112.200334;
        var longitude = 24.9424988;
        var latitude = 60.1708763;
        var wgs84 = mapService.epsg3879ToWgs84([x, y]);
        expect(wgs84[0]).toBeCloseTo(longitude, 0);
        expect(wgs84[1]).toBeCloseTo(latitude, 0);
    });
    it('should map feature collections to geometry collections and vice versa', function () {
        var mapService = new map_util_1.MapUtil();
        var geoJSON = '' +
            '{"type":"GeometryCollection",' +
            '"crs": {"properties":{"name":"EPSG:3879"},"type":"name"},' +
            '"geometries":[{"type":"Polygon",' +
            '"coordinates":[' +
            '[[25496796.034103394,6673116.6363515025],' +
            '[25496768.034103394,6673024.6363515],' +
            '[25496900.034103394,6673024.636351503],' +
            '[25496796.034103394,6673116.6363515025]]]}]}';
        var originalGeometryCollection = JSON.parse(geoJSON);
        var coordinates = originalGeometryCollection.geometries[0].coordinates[0];
        var featureCollection = mapService.geometryCollectionToFeatureCollection(originalGeometryCollection);
        var geometryCollection = mapService.featureCollectionToGeometryCollection(featureCollection);
        var processedGeometries = geometryCollection.geometries[0];
        var processedCoordinates = processedGeometries.coordinates[0];
        // compare converted coordinates after rounding them a little bit
        expect(coordinates.length).toBe(processedCoordinates.length);
        for (var i = 0; i < coordinates.length; ++i) {
            expect(Helper.compareCoordinates(coordinates[i], processedCoordinates[i]))
                .toBeTruthy(coordinates[i] + ' does not match ' + processedCoordinates[i]);
        }
        // have to clear coordinates before comparing structures, because coordinate conversion never returns exactly the same values
        processedGeometries.coordinates[0] = [];
        originalGeometryCollection.geometries[0].coordinates[0] = [];
        var roundedOriginalGeoJSON = JSON.stringify(originalGeometryCollection);
        var processedGeoJSON = JSON.stringify(geometryCollection);
        expect(processedGeoJSON).toBe(roundedOriginalGeoJSON);
    });
});
var Helper = (function () {
    function Helper() {
    }
    Helper.compareCoordinates = function (coordinate1, coordinate2) {
        return Math.trunc(coordinate1[0]) === Math.trunc(coordinate2[0]) && Math.trunc(coordinate1[1]) === Math.trunc(coordinate2[1]);
    };
    return Helper;
}());
