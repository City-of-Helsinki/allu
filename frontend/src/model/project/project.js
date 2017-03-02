"use strict";
var time_util_1 = require("../../util/time.util");
var Project = (function () {
    function Project(id, name, startTime, endTime, cityDistricts, ownerName, contactName, email, phone, customerReference, additionalInfo, parentId) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cityDistricts = cityDistricts;
        this.ownerName = ownerName;
        this.contactName = contactName;
        this.email = email;
        this.phone = phone;
        this.customerReference = customerReference;
        this.additionalInfo = additionalInfo;
        this.parentId = parentId;
        this.active = startTime && endTime && time_util_1.TimeUtil.isBetweenInclusive(new Date(), this.startTime, this.endTime);
        this.cityDistricts = cityDistricts || [];
    }
    Object.defineProperty(Project.prototype, "idWithName", {
        get: function () {
            return this.id + ': ' + this.name;
        },
        enumerable: true,
        configurable: true
    });
    return Project;
}());
exports.Project = Project;
