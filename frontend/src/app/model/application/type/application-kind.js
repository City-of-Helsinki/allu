"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var application_specifier_1 = require("./application-specifier");
var array_util_1 = require("../../../util/array-util");
var ApplicationKind;
(function (ApplicationKind) {
    ApplicationKind[ApplicationKind["PROMOTION"] = 0] = "PROMOTION";
    ApplicationKind[ApplicationKind["OUTDOOREVENT"] = 1] = "OUTDOOREVENT";
    ApplicationKind[ApplicationKind["BRIDGE_BANNER"] = 2] = "BRIDGE_BANNER";
    ApplicationKind[ApplicationKind["BENJI"] = 3] = "BENJI";
    ApplicationKind[ApplicationKind["PROMOTION_OR_SALES"] = 4] = "PROMOTION_OR_SALES";
    ApplicationKind[ApplicationKind["URBAN_FARMING"] = 5] = "URBAN_FARMING";
    ApplicationKind[ApplicationKind["KESKUSKATU_SALES"] = 6] = "KESKUSKATU_SALES";
    ApplicationKind[ApplicationKind["SUMMER_THEATER"] = 7] = "SUMMER_THEATER";
    ApplicationKind[ApplicationKind["DOG_TRAINING_FIELD"] = 8] = "DOG_TRAINING_FIELD";
    ApplicationKind[ApplicationKind["DOG_TRAINING_EVENT"] = 9] = "DOG_TRAINING_EVENT";
    ApplicationKind[ApplicationKind["SMALL_ART_AND_CULTURE"] = 10] = "SMALL_ART_AND_CULTURE";
    ApplicationKind[ApplicationKind["SEASON_SALE"] = 11] = "SEASON_SALE";
    ApplicationKind[ApplicationKind["CIRCUS"] = 12] = "CIRCUS";
    ApplicationKind[ApplicationKind["ART"] = 13] = "ART";
    ApplicationKind[ApplicationKind["STORAGE_AREA"] = 14] = "STORAGE_AREA";
    ApplicationKind[ApplicationKind["STREET_AND_GREEN"] = 15] = "STREET_AND_GREEN";
    ApplicationKind[ApplicationKind["WATER_AND_SEWAGE"] = 16] = "WATER_AND_SEWAGE";
    ApplicationKind[ApplicationKind["ELECTRICITY"] = 17] = "ELECTRICITY";
    ApplicationKind[ApplicationKind["DATA_TRANSFER"] = 18] = "DATA_TRANSFER";
    ApplicationKind[ApplicationKind["HEATING_COOLING"] = 19] = "HEATING_COOLING";
    ApplicationKind[ApplicationKind["CONSTRUCTION"] = 20] = "CONSTRUCTION";
    ApplicationKind[ApplicationKind["YARD"] = 21] = "YARD";
    ApplicationKind[ApplicationKind["GEOLOGICAL_SURVEY"] = 22] = "GEOLOGICAL_SURVEY";
    ApplicationKind[ApplicationKind["PROPERTY_RENOVATION"] = 23] = "PROPERTY_RENOVATION";
    ApplicationKind[ApplicationKind["CONTAINER_BARRACK"] = 24] = "CONTAINER_BARRACK";
    ApplicationKind[ApplicationKind["PHOTO_SHOOTING"] = 25] = "PHOTO_SHOOTING";
    ApplicationKind[ApplicationKind["SNOW_WORK"] = 26] = "SNOW_WORK";
    ApplicationKind[ApplicationKind["RELOCATION"] = 27] = "RELOCATION";
    ApplicationKind[ApplicationKind["LIFTING"] = 28] = "LIFTING";
    ApplicationKind[ApplicationKind["NEW_BUILDING_CONSTRUCTION"] = 29] = "NEW_BUILDING_CONSTRUCTION";
    ApplicationKind[ApplicationKind["ROLL_OFF"] = 30] = "ROLL_OFF";
    ApplicationKind[ApplicationKind["CHRISTMAS_TREE_SALES_AREA"] = 31] = "CHRISTMAS_TREE_SALES_AREA";
    ApplicationKind[ApplicationKind["CITY_CYCLING_AREA"] = 32] = "CITY_CYCLING_AREA";
    ApplicationKind[ApplicationKind["AGILE_KIOSK_AREA"] = 33] = "AGILE_KIOSK_AREA";
    ApplicationKind[ApplicationKind["STATEMENT"] = 34] = "STATEMENT";
    ApplicationKind[ApplicationKind["SNOW_HEAP_AREA"] = 35] = "SNOW_HEAP_AREA";
    ApplicationKind[ApplicationKind["SNOW_GATHER_AREA"] = 36] = "SNOW_GATHER_AREA";
    ApplicationKind[ApplicationKind["OTHER_SUBVISION_OF_STATE_AREA"] = 37] = "OTHER_SUBVISION_OF_STATE_AREA";
    ApplicationKind[ApplicationKind["MILITARY_EXCERCISE"] = 38] = "MILITARY_EXCERCISE";
    ApplicationKind[ApplicationKind["WINTER_PARKING"] = 39] = "WINTER_PARKING";
    ApplicationKind[ApplicationKind["REPAVING"] = 40] = "REPAVING";
    ApplicationKind[ApplicationKind["ELECTION_ADD_STAND"] = 41] = "ELECTION_ADD_STAND";
    ApplicationKind[ApplicationKind["PUBLIC_EVENT"] = 42] = "PUBLIC_EVENT";
    ApplicationKind[ApplicationKind["OTHER"] = 43] = "OTHER";
})(ApplicationKind = exports.ApplicationKind || (exports.ApplicationKind = {}));
var ApplicationKindEntry = /** @class */ (function () {
    function ApplicationKindEntry(kind, specifiers) {
        var _this = this;
        this.kind = kind;
        this.specifiers = specifiers;
        this.specifiers = specifiers || [];
        this._specifierNamesSortedByTranslation = this.uiSpecifiers
            .sort(array_util_1.ArrayUtil.naturalSortTranslated(['application.specifier'], function (specifier) { return specifier; }))
            .map(function (s) { return new application_specifier_1.SpecifierEntry(s, _this.uiKind); });
    }
    Object.defineProperty(ApplicationKindEntry.prototype, "uiKind", {
        get: function () {
            return ApplicationKind[this.kind];
        },
        set: function (kind) {
            this.kind = ApplicationKind[kind];
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ApplicationKindEntry.prototype, "uiSpecifiers", {
        get: function () {
            return this.specifiers.map(function (s) { return application_specifier_1.ApplicationSpecifier[s]; });
        },
        set: function (specifiers) {
            this.specifiers = specifiers.map(function (s) { return application_specifier_1.ApplicationSpecifier[s]; });
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ApplicationKindEntry.prototype, "specifierEntriesSortedByTranslation", {
        get: function () {
            return this._specifierNamesSortedByTranslation;
        },
        enumerable: true,
        configurable: true
    });
    ApplicationKindEntry.prototype.contains = function (specifier) {
        return this.specifiers.some(function (s) { return s === application_specifier_1.ApplicationSpecifier[specifier]; });
    };
    ApplicationKindEntry.prototype.hasSpecifiers = function () {
        return this.specifiers.length > 0;
    };
    return ApplicationKindEntry;
}());
exports.ApplicationKindEntry = ApplicationKindEntry;
function drawingAllowedForKind(kind) {
    return ![ApplicationKind.BRIDGE_BANNER, ApplicationKind.DOG_TRAINING_EVENT, ApplicationKind.DOG_TRAINING_FIELD]
        .some(function (k) { return k === kind; });
}
exports.drawingAllowedForKind = drawingAllowedForKind;
