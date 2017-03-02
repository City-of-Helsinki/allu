"use strict";
var contact_1 = require("./contact");
var location_1 = require("../common/location");
var time_util_1 = require("../../util/time.util");
var option_1 = require("../../util/option");
var application_tag_type_1 = require("./tag/application-tag-type");
var number_util_1 = require("../../util/number.util");
var CENTS = 100;
var Application = (function () {
    function Application(id, applicationId, project, handler, status, type, kind, metadataVersion, name, creationTime, startTime, endTime, applicant, contactList, location, extension, decisionTime, attachmentList, calculatedPrice, priceOverride, priceOverrideReason, applicationTags, comments) {
        this.id = id;
        this.applicationId = applicationId;
        this.project = project;
        this.handler = handler;
        this.status = status;
        this.type = type;
        this.kind = kind;
        this.metadataVersion = metadataVersion;
        this.name = name;
        this.creationTime = creationTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.applicant = applicant;
        this.contactList = contactList;
        this.location = location;
        this.extension = extension;
        this.decisionTime = decisionTime;
        this.attachmentList = attachmentList;
        this.calculatedPrice = calculatedPrice;
        this.priceOverride = priceOverride;
        this.priceOverrideReason = priceOverrideReason;
        this.applicationTags = applicationTags;
        this.comments = comments;
        this.location = location || new location_1.Location();
        this.contactList = contactList || [new contact_1.Contact()];
        this.attachmentList = attachmentList || [];
        this.applicationTags = applicationTags || [];
        this.comments = comments || [];
    }
    Object.defineProperty(Application.prototype, "uiApplicationCreationTime", {
        /*
         * Getters and setters for supporting pickadate editing in UI.
         */
        get: function () {
            return time_util_1.TimeUtil.getUiDateString(this.creationTime);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Application.prototype, "uiStartTime", {
        get: function () {
            return time_util_1.TimeUtil.getUiDateString(this.startTime);
        },
        set: function (dateString) {
            this.startTime = time_util_1.TimeUtil.getDateFromUi(dateString);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Application.prototype, "uiEndTime", {
        get: function () {
            return time_util_1.TimeUtil.getUiDateString(this.endTime);
        },
        set: function (dateString) {
            this.endTime = time_util_1.TimeUtil.getDateFromUi(dateString);
        },
        enumerable: true,
        configurable: true
    });
    Application.prototype.hasGeometry = function () {
        return this.geometryCount() > 0;
    };
    Application.prototype.geometryCount = function () {
        return option_1.Some(this.location)
            .map(function (loc) { return loc.geometry; })
            .map(function (g) { return g.geometries.length; }).orElse(0);
    };
    Application.prototype.hasFixedGeometry = function () {
        return option_1.Some(this.location).map(function (loc) { return loc.fixedLocationIds.length > 0; }).orElse(false);
    };
    Application.prototype.belongsToProject = function (projectId) {
        return option_1.Some(this.project).map(function (p) { return p.id === projectId; }).orElse(false);
    };
    Object.defineProperty(Application.prototype, "calculatedPriceEuro", {
        get: function () {
            return this.toEuros(this.calculatedPrice);
        },
        set: function (priceInEuros) {
            this.calculatedPrice = this.toCents(priceInEuros);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Application.prototype, "priceOverrideEuro", {
        get: function () {
            return this.toEuros(this.priceOverride);
        },
        set: function (overrideInEuros) {
            this.priceOverride = this.toCents(overrideInEuros);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Application.prototype, "waiting", {
        get: function () {
            return this.applicationTags
                .some(function (tag) { return application_tag_type_1.ApplicationTagType[tag.type] === application_tag_type_1.ApplicationTagType.WAITING; });
        },
        enumerable: true,
        configurable: true
    });
    Application.prototype.toEuros = function (priceInCents) {
        return number_util_1.NumberUtil.isDefined(priceInCents) ? priceInCents / CENTS : undefined;
    };
    Application.prototype.toCents = function (priceInEuros) {
        return number_util_1.NumberUtil.isDefined(priceInEuros) ? priceInEuros * CENTS : undefined;
    };
    return Application;
}());
exports.Application = Application;
