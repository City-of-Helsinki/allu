"use strict";
var postal_address_1 = require("./postal-address");
var Location = (function () {
    function Location(id, locationKey, locationVersion, startTime, endTime, geometry, area, areaOverride, postalAddress, fixedLocationIds, cityDistrictId, cityDistrictIdOverride, paymentTariff, paymentTariffOverride, underpass, info) {
        this.id = id;
        this.locationKey = locationKey;
        this.locationVersion = locationVersion;
        this.startTime = startTime;
        this.endTime = endTime;
        this.geometry = geometry;
        this.area = area;
        this.areaOverride = areaOverride;
        this.postalAddress = postalAddress;
        this.fixedLocationIds = fixedLocationIds;
        this.cityDistrictId = cityDistrictId;
        this.cityDistrictIdOverride = cityDistrictIdOverride;
        this.paymentTariff = paymentTariff;
        this.paymentTariffOverride = paymentTariffOverride;
        this.underpass = underpass;
        this.info = info;
        this.postalAddress = postalAddress || new postal_address_1.PostalAddress();
        this.fixedLocationIds = fixedLocationIds || [];
        this.underpass = underpass || false;
    }
    ;
    Object.defineProperty(Location.prototype, "uiArea", {
        get: function () {
            return this.area ? Math.ceil(this.area) : undefined;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Location.prototype, "effectiveCityDistrictId", {
        get: function () {
            return Number.isInteger(this.cityDistrictIdOverride) ? this.cityDistrictIdOverride : this.cityDistrictId;
        },
        enumerable: true,
        configurable: true
    });
    return Location;
}());
exports.Location = Location;
