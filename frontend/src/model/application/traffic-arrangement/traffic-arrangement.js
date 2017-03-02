"use strict";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var application_extension_1 = require("../type/application-extension");
var application_type_1 = require("../type/application-type");
var time_util_1 = require("../../../util/time.util");
var TrafficArrangement = (function (_super) {
    __extends(TrafficArrangement, _super);
    function TrafficArrangement(specifiers, contractor, responsiblePerson, pksCard, workFinished, trafficArrangements, additionalInfo, terms) {
        var _this = _super.call(this, application_type_1.ApplicationType[application_type_1.ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS], specifiers, terms) || this;
        _this.specifiers = specifiers;
        _this.contractor = contractor;
        _this.responsiblePerson = responsiblePerson;
        _this.pksCard = pksCard;
        _this.workFinished = workFinished;
        _this.trafficArrangements = trafficArrangements;
        _this.additionalInfo = additionalInfo;
        _this.terms = terms;
        return _this;
    }
    Object.defineProperty(TrafficArrangement.prototype, "uiWorkFinished", {
        get: function () {
            return time_util_1.TimeUtil.getUiDateString(this.workFinished);
        },
        set: function (dateString) {
            this.workFinished = time_util_1.TimeUtil.getDateFromUi(dateString);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(TrafficArrangement.prototype, "responsiblePersonList", {
        get: function () {
            return this.responsiblePerson ? [this.responsiblePerson] : undefined;
        },
        enumerable: true,
        configurable: true
    });
    return TrafficArrangement;
}(application_extension_1.ApplicationExtension));
exports.TrafficArrangement = TrafficArrangement;
