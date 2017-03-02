"use strict";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var application_extension_1 = require("../type/application-extension");
var application_type_1 = require("../type/application-type");
var time_util_1 = require("../../../util/time.util");
var ExcavationAnnouncement = (function (_super) {
    __extends(ExcavationAnnouncement, _super);
    function ExcavationAnnouncement(specifiers, contractor, responsiblePerson, propertyDeveloper, propertyDeveloperContact, pksCard, constructionWork, maintenanceWork, emergencyWork, propertyConnectivity, winterTimeOperation, summerTimeOperation, workFinished, unauthorizedWorkStartTime, unauthorizedWorkEndTime, guaranteeEndTime, cableReportId, additionalInfo, trafficArrangements, terms) {
        var _this = _super.call(this, application_type_1.ApplicationType[application_type_1.ApplicationType.EXCAVATION_ANNOUNCEMENT], specifiers, terms) || this;
        _this.specifiers = specifiers;
        _this.contractor = contractor;
        _this.responsiblePerson = responsiblePerson;
        _this.propertyDeveloper = propertyDeveloper;
        _this.propertyDeveloperContact = propertyDeveloperContact;
        _this.pksCard = pksCard;
        _this.constructionWork = constructionWork;
        _this.maintenanceWork = maintenanceWork;
        _this.emergencyWork = emergencyWork;
        _this.propertyConnectivity = propertyConnectivity;
        _this.winterTimeOperation = winterTimeOperation;
        _this.summerTimeOperation = summerTimeOperation;
        _this.workFinished = workFinished;
        _this.unauthorizedWorkStartTime = unauthorizedWorkStartTime;
        _this.unauthorizedWorkEndTime = unauthorizedWorkEndTime;
        _this.guaranteeEndTime = guaranteeEndTime;
        _this.cableReportId = cableReportId;
        _this.additionalInfo = additionalInfo;
        _this.trafficArrangements = trafficArrangements;
        _this.terms = terms;
        return _this;
    }
    Object.defineProperty(ExcavationAnnouncement.prototype, "uiWinterTimeOperation", {
        get: function () {
            return time_util_1.TimeUtil.getUiDateString(this.winterTimeOperation);
        },
        set: function (dateString) {
            this.winterTimeOperation = time_util_1.TimeUtil.getDateFromUi(dateString);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ExcavationAnnouncement.prototype, "uiSummerTimeOperation", {
        get: function () {
            return time_util_1.TimeUtil.getUiDateString(this.summerTimeOperation);
        },
        set: function (dateString) {
            this.summerTimeOperation = time_util_1.TimeUtil.getDateFromUi(dateString);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ExcavationAnnouncement.prototype, "uiWorkFinished", {
        get: function () {
            return time_util_1.TimeUtil.getUiDateString(this.workFinished);
        },
        set: function (dateString) {
            this.workFinished = time_util_1.TimeUtil.getDateFromUi(dateString);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ExcavationAnnouncement.prototype, "uiUnauthorizedWorkStartTime", {
        get: function () {
            return time_util_1.TimeUtil.getUiDateString(this.unauthorizedWorkStartTime);
        },
        set: function (dateString) {
            this.unauthorizedWorkStartTime = time_util_1.TimeUtil.getDateFromUi(dateString);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ExcavationAnnouncement.prototype, "uiUnauthorizedWorkEndTime", {
        get: function () {
            return time_util_1.TimeUtil.getUiDateString(this.unauthorizedWorkEndTime);
        },
        set: function (dateString) {
            this.unauthorizedWorkEndTime = time_util_1.TimeUtil.getDateFromUi(dateString);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ExcavationAnnouncement.prototype, "uiGuaranteeEndTime", {
        get: function () {
            return time_util_1.TimeUtil.getUiDateString(this.guaranteeEndTime);
        },
        set: function (dateString) {
            this.guaranteeEndTime = time_util_1.TimeUtil.getDateFromUi(dateString);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ExcavationAnnouncement.prototype, "responsiblePersonList", {
        get: function () {
            return this.responsiblePerson ? [this.responsiblePerson] : undefined;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ExcavationAnnouncement.prototype, "propertyDeveloperContactList", {
        get: function () {
            return this.propertyDeveloperContact ? [this.propertyDeveloperContact] : undefined;
        },
        enumerable: true,
        configurable: true
    });
    return ExcavationAnnouncement;
}(application_extension_1.ApplicationExtension));
exports.ExcavationAnnouncement = ExcavationAnnouncement;
