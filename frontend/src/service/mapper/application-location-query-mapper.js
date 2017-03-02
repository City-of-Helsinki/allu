"use strict";
var ApplicationLocationQueryMapper = (function () {
    function ApplicationLocationQueryMapper() {
    }
    ApplicationLocationQueryMapper.mapFrontend = function (query) {
        return query ? {
            after: (query.startDate) ? query.startDate.toISOString() : undefined,
            before: (query.endDate) ? query.endDate.toISOString() : undefined,
            intersectingGeometry: query.geometry
        } :
            undefined;
    };
    return ApplicationLocationQueryMapper;
}());
exports.ApplicationLocationQueryMapper = ApplicationLocationQueryMapper;
