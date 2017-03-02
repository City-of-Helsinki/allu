"use strict";
var application_tag_1 = require("../../model/application/tag/application-tag");
var time_util_1 = require("../../util/time.util");
var option_1 = require("../../util/option");
var ApplicationTagMapper = (function () {
    function ApplicationTagMapper() {
    }
    ApplicationTagMapper.mapBackendList = function (tags) {
        return (tags)
            ? tags.map(function (tag) { return ApplicationTagMapper.mapBackend(tag); })
            : [];
    };
    ApplicationTagMapper.mapBackend = function (tag) {
        return new application_tag_1.ApplicationTag(tag.type, tag.addedBy, time_util_1.TimeUtil.dateFromBackend(tag.creationTime));
    };
    ApplicationTagMapper.mapFrontendList = function (tags) {
        return (tags)
            ? tags.map(function (tag) { return ApplicationTagMapper.mapFrontend(tag); })
            : [];
    };
    ApplicationTagMapper.mapFrontend = function (tag) {
        return {
            type: tag.type,
            addedBy: tag.addedBy,
            creationTime: option_1.Some(tag.creationTime).map(function (creationTime) { return creationTime.toISOString(); }).orElse(undefined)
        };
    };
    return ApplicationTagMapper;
}());
exports.ApplicationTagMapper = ApplicationTagMapper;
