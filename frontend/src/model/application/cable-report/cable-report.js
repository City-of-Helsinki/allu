"use strict";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var application_extension_1 = require("../type/application-extension");
var application_type_1 = require("../type/application-type");
var CableReport = (function (_super) {
    __extends(CableReport, _super);
    function CableReport(specifiers, cableSurveyRequired, mapUpdated, constructionWork, maintenanceWork, emergencyWork, propertyConnectivity, cableReportId, workDescription, owner, contact, mapExtractCount, infoEntries) {
        var _this = _super.call(this, application_type_1.ApplicationType[application_type_1.ApplicationType.CABLE_REPORT], specifiers) || this;
        _this.specifiers = specifiers;
        _this.cableSurveyRequired = cableSurveyRequired;
        _this.mapUpdated = mapUpdated;
        _this.constructionWork = constructionWork;
        _this.maintenanceWork = maintenanceWork;
        _this.emergencyWork = emergencyWork;
        _this.propertyConnectivity = propertyConnectivity;
        _this.cableReportId = cableReportId;
        _this.workDescription = workDescription;
        _this.owner = owner;
        _this.contact = contact;
        _this.mapExtractCount = mapExtractCount;
        _this.infoEntries = infoEntries;
        _this.infoEntries = infoEntries || [];
        return _this;
    }
    Object.defineProperty(CableReport.prototype, "contactList", {
        get: function () {
            return this.contact ? [this.contact] : undefined;
        },
        enumerable: true,
        configurable: true
    });
    return CableReport;
}(application_extension_1.ApplicationExtension));
exports.CableReport = CableReport;
