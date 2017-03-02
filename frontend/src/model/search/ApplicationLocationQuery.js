"use strict";
var ApplicationLocationQuery = (function () {
    function ApplicationLocationQuery(startDate, endDate, geometry) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.geometry = geometry;
    }
    return ApplicationLocationQuery;
}());
exports.ApplicationLocationQuery = ApplicationLocationQuery;
