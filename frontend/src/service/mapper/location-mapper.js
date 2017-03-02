"use strict";
var location_1 = require("../../model/common/location");
var postal_address_1 = require("../../model/common/postal-address");
var LocationMapper = (function () {
    function LocationMapper() {
    }
    LocationMapper.mapBackend = function (backendLocation) {
        return (backendLocation) ?
            new location_1.Location(backendLocation.id, backendLocation.locationKey, backendLocation.locationVersion, backendLocation.startTime, backendLocation.endTime, backendLocation.geometry, backendLocation.area, backendLocation.areaOverride, postal_address_1.PostalAddress.fromBackend(backendLocation.postalAddress), backendLocation.fixedLocationIds, backendLocation.cityDistrictId, backendLocation.cityDistrictIdOverride, backendLocation.paymentTariff, backendLocation.paymentTariffOverride, backendLocation.underpass, backendLocation.info) : undefined;
    };
    LocationMapper.mapFrontend = function (location) {
        return (location) ?
            {
                id: location.id,
                locationKey: location.locationKey,
                locationVersion: location.locationVersion,
                startTime: location.startTime,
                endTime: location.endTime,
                geometry: location.geometry,
                area: location.area,
                areaOverride: location.areaOverride,
                postalAddress: location.postalAddress.toBackend(),
                fixedLocationIds: location.fixedLocationIds,
                cityDistrictId: location.cityDistrictId,
                cityDistrictIdOverride: location.cityDistrictIdOverride,
                paymentTariff: location.paymentTariff,
                paymentTariffOverride: location.paymentTariffOverride,
                underpass: location.underpass,
                info: location.info
            } : undefined;
    };
    return LocationMapper;
}());
exports.LocationMapper = LocationMapper;
