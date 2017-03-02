"use strict";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var application_extension_1 = require("../type/application-extension");
var time_util_ts_1 = require("../../../util/time.util.ts");
var Event = (function (_super) {
    __extends(Event, _super);
    function Event(nature, description, url, applicationType, eventStartTime, eventEndTime, timeExceptions, attendees, entryFee, noPriceReason, salesActivity, heavyStructure, ecoCompass, foodSales, foodProviders, marketingProviders, structureArea, structureDescription, structureStartTime, structureEndTime, terms) {
        var _this = _super.call(this, applicationType, [], terms) || this;
        _this.nature = nature;
        _this.description = description;
        _this.url = url;
        _this.applicationType = applicationType;
        _this.eventStartTime = eventStartTime;
        _this.eventEndTime = eventEndTime;
        _this.timeExceptions = timeExceptions;
        _this.attendees = attendees;
        _this.entryFee = entryFee;
        _this.noPriceReason = noPriceReason;
        _this.salesActivity = salesActivity;
        _this.heavyStructure = heavyStructure;
        _this.ecoCompass = ecoCompass;
        _this.foodSales = foodSales;
        _this.foodProviders = foodProviders;
        _this.marketingProviders = marketingProviders;
        _this.structureArea = structureArea;
        _this.structureDescription = structureDescription;
        _this.structureStartTime = structureStartTime;
        _this.structureEndTime = structureEndTime;
        _this.terms = terms;
        return _this;
    }
    Object.defineProperty(Event.prototype, "uiStartTime", {
        /*
         * Getters and setters for supporting pickadate editing in UI.
         */
        get: function () {
            return time_util_ts_1.TimeUtil.getUiDateString(this.eventStartTime);
        },
        set: function (dateString) {
            this.eventStartTime = time_util_ts_1.TimeUtil.getDateFromUi(dateString);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Event.prototype, "uiEndTime", {
        get: function () {
            return time_util_ts_1.TimeUtil.getUiDateString(this.eventEndTime);
        },
        set: function (dateString) {
            this.eventEndTime = time_util_ts_1.TimeUtil.getDateFromUi(dateString);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Event.prototype, "uiStructureStartTime", {
        get: function () {
            return time_util_ts_1.TimeUtil.getUiDateString(this.structureStartTime);
        },
        set: function (dateString) {
            this.structureStartTime = time_util_ts_1.TimeUtil.getDateFromUi(dateString);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Event.prototype, "uiStructureEndTime", {
        get: function () {
            return time_util_ts_1.TimeUtil.getUiDateString(this.structureEndTime);
        },
        set: function (dateString) {
            this.structureEndTime = time_util_ts_1.TimeUtil.getDateFromUi(dateString);
        },
        enumerable: true,
        configurable: true
    });
    return Event;
}(application_extension_1.ApplicationExtension));
exports.Event = Event;
