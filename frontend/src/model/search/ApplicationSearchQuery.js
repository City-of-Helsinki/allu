"use strict";
var time_util_1 = require("../../util/time.util");
var option_1 = require("../../util/option");
var ApplicationSearchQuery = (function () {
    function ApplicationSearchQuery() {
    }
    Object.defineProperty(ApplicationSearchQuery.prototype, "uiStartTime", {
        get: function () {
            return time_util_1.TimeUtil.getUiDateString(this.startTime);
        },
        set: function (dateString) {
            this.startTime = time_util_1.TimeUtil.getDateFromUi(dateString);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ApplicationSearchQuery.prototype, "uiEndTime", {
        get: function () {
            return time_util_1.TimeUtil.getUiDateString(this.endTime);
        },
        set: function (dateString) {
            this.endTime = time_util_1.TimeUtil.getDateFromUi(dateString);
        },
        enumerable: true,
        configurable: true
    });
    ApplicationSearchQuery.from = function (queryForm, sort) {
        var query = new ApplicationSearchQuery();
        query.applicationId = queryForm.applicationId;
        query.type = queryForm.type;
        query.status = queryForm.status;
        query.districts = option_1.Some(queryForm.districts)
            .map(function (ds) { return ds
            .map(function (d) { return d.toString(); }); })
            .orElse([]);
        query.handler = queryForm.handler;
        query.address = queryForm.address;
        query.applicant = queryForm.applicant;
        query.contact = queryForm.contact;
        query.freeText = queryForm.freeText;
        query.startTime = time_util_1.TimeUtil.getDateFromUi(queryForm.startTime);
        query.endTime = time_util_1.TimeUtil.getDateFromUi(queryForm.endTime);
        query.tags = queryForm.tags;
        query.sort = sort;
        return query;
    };
    ApplicationSearchQuery.forApplicationId = function (id) {
        var query = new ApplicationSearchQuery();
        query.applicationId = id;
        return query;
    };
    ApplicationSearchQuery.forIdAndTypes = function (id, types) {
        var query = new ApplicationSearchQuery();
        query.applicationId = id;
        query.type = types;
        return query;
    };
    ApplicationSearchQuery.prototype.copy = function () {
        var query = new ApplicationSearchQuery();
        query.applicationId = this.applicationId;
        query.type = this.type;
        query.status = this.status;
        query.districts = this.districts;
        query.handler = this.handler;
        query.address = this.address;
        query.applicant = this.applicant;
        query.contact = this.contact;
        query.freeText = this.freeText;
        query.startTime = this.startTime;
        query.endTime = this.endTime;
        query.tags = this.tags;
        return query;
    };
    ApplicationSearchQuery.prototype.withSort = function (sort) {
        var newQuery = this.copy();
        newQuery.sort = sort;
        return newQuery;
    };
    return ApplicationSearchQuery;
}());
exports.ApplicationSearchQuery = ApplicationSearchQuery;
