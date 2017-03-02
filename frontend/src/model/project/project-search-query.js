"use strict";
var time_util_1 = require("../../util/time.util");
var ProjectSearchQuery = (function () {
    function ProjectSearchQuery() {
    }
    Object.defineProperty(ProjectSearchQuery.prototype, "uiStartTime", {
        get: function () {
            return time_util_1.TimeUtil.getUiDateString(this.startTime);
        },
        set: function (dateString) {
            this.startTime = time_util_1.TimeUtil.getDateFromUi(dateString);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ProjectSearchQuery.prototype, "uiEndTime", {
        get: function () {
            return time_util_1.TimeUtil.getUiDateString(this.endTime);
        },
        set: function (dateString) {
            this.endTime = time_util_1.TimeUtil.getDateFromUi(dateString);
        },
        enumerable: true,
        configurable: true
    });
    ProjectSearchQuery.fromForm = function (form, sort) {
        var query = new ProjectSearchQuery();
        query.id = form.id;
        query.uiStartTime = form.startTime;
        query.uiEndTime = form.endTime;
        query.ownerName = form.ownerName;
        query.onlyActive = form.onlyActive;
        query.districts = form.districts;
        query.creator = form.creator;
        query.sort = sort;
        return query;
    };
    ProjectSearchQuery.fromProjectId = function (id) {
        var query = new ProjectSearchQuery();
        query.id = id;
        return query;
    };
    return ProjectSearchQuery;
}());
exports.ProjectSearchQuery = ProjectSearchQuery;
