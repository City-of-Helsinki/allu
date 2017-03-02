"use strict";
var application_specifier_1 = require("./application-specifier");
var array_util_1 = require("../../../util/array-util");
(function (ApplicationKind) {
    // EVENTS
    ApplicationKind[ApplicationKind["OUTDOOREVENT"] = 0] = "OUTDOOREVENT";
    ApplicationKind[ApplicationKind["PROMOTION"] = 1] = "PROMOTION";
    // SHORT TERM RENTALS
    ApplicationKind[ApplicationKind["EXCAVATION_ANNOUNCEMENT"] = 2] = "EXCAVATION_ANNOUNCEMENT";
    ApplicationKind[ApplicationKind["AREA_RENTAL"] = 3] = "AREA_RENTAL";
    ApplicationKind[ApplicationKind["TEMPORARY_TRAFFIC_ARRANGEMENTS"] = 4] = "TEMPORARY_TRAFFIC_ARRANGEMENTS";
    ApplicationKind[ApplicationKind["BRIDGE_BANNER"] = 5] = "BRIDGE_BANNER";
    ApplicationKind[ApplicationKind["BENJI"] = 6] = "BENJI";
    ApplicationKind[ApplicationKind["PROMOTION_OR_SALES"] = 7] = "PROMOTION_OR_SALES";
    ApplicationKind[ApplicationKind["URBAN_FARMING"] = 8] = "URBAN_FARMING";
    ApplicationKind[ApplicationKind["MAIN_STREET_SALES"] = 9] = "MAIN_STREET_SALES";
    ApplicationKind[ApplicationKind["SUMMER_THEATER"] = 10] = "SUMMER_THEATER";
    ApplicationKind[ApplicationKind["DOG_TRAINING_FIELD"] = 11] = "DOG_TRAINING_FIELD";
    ApplicationKind[ApplicationKind["DOG_TRAINING_EVENT"] = 12] = "DOG_TRAINING_EVENT";
    ApplicationKind[ApplicationKind["CARGO_CONTAINER"] = 13] = "CARGO_CONTAINER";
    ApplicationKind[ApplicationKind["SMALL_ART_AND_CULTURE"] = 14] = "SMALL_ART_AND_CULTURE";
    ApplicationKind[ApplicationKind["SEASON_SALE"] = 15] = "SEASON_SALE";
    ApplicationKind[ApplicationKind["CIRCUS"] = 16] = "CIRCUS";
    ApplicationKind[ApplicationKind["ART"] = 17] = "ART";
    ApplicationKind[ApplicationKind["STORAGE_AREA"] = 18] = "STORAGE_AREA";
    ApplicationKind[ApplicationKind["OTHER_SHORT_TERM_RENTAL"] = 19] = "OTHER_SHORT_TERM_RENTAL";
    // CABLE REPORTS
    ApplicationKind[ApplicationKind["STREET_AND_GREEN"] = 20] = "STREET_AND_GREEN";
    ApplicationKind[ApplicationKind["WATER_AND_SEWAGE"] = 21] = "WATER_AND_SEWAGE";
    ApplicationKind[ApplicationKind["ELECTRICITY"] = 22] = "ELECTRICITY";
    ApplicationKind[ApplicationKind["DATA_TRANSFER"] = 23] = "DATA_TRANSFER";
    ApplicationKind[ApplicationKind["HEATING_COOLING"] = 24] = "HEATING_COOLING";
    ApplicationKind[ApplicationKind["CONSTRUCTION"] = 25] = "CONSTRUCTION";
    ApplicationKind[ApplicationKind["YARD"] = 26] = "YARD";
    ApplicationKind[ApplicationKind["GEOLOGICAL_SURVEY"] = 27] = "GEOLOGICAL_SURVEY";
    ApplicationKind[ApplicationKind["OTHER_CABLE_REPORT"] = 28] = "OTHER_CABLE_REPORT";
    // NOTES
    ApplicationKind[ApplicationKind["CHRISTMAS_TREE_SALES_AREA"] = 29] = "CHRISTMAS_TREE_SALES_AREA";
    ApplicationKind[ApplicationKind["CITY_CYCLING_AREA"] = 30] = "CITY_CYCLING_AREA";
    ApplicationKind[ApplicationKind["AGILE_KIOSK_AREA"] = 31] = "AGILE_KIOSK_AREA";
    ApplicationKind[ApplicationKind["STATEMENT"] = 32] = "STATEMENT";
    ApplicationKind[ApplicationKind["SNOW_HEAP_AREA"] = 33] = "SNOW_HEAP_AREA";
    ApplicationKind[ApplicationKind["SNOW_GATHER_AREA"] = 34] = "SNOW_GATHER_AREA";
    ApplicationKind[ApplicationKind["OTHER_SUBVISION_OF_STATE_AREA"] = 35] = "OTHER_SUBVISION_OF_STATE_AREA";
    ApplicationKind[ApplicationKind["MILITARY_EXCERCISE"] = 36] = "MILITARY_EXCERCISE";
    ApplicationKind[ApplicationKind["WINTER_PARKING"] = 37] = "WINTER_PARKING";
    ApplicationKind[ApplicationKind["REPAVING"] = 38] = "REPAVING";
    ApplicationKind[ApplicationKind["ELECTION_ADD_STAND"] = 39] = "ELECTION_ADD_STAND";
    ApplicationKind[ApplicationKind["NOTE_OTHER"] = 40] = "NOTE_OTHER";
    // TEMPORARY TRAFFIC ARRANGEMENTS
    ApplicationKind[ApplicationKind["PUBLIC_EVENT"] = 41] = "PUBLIC_EVENT";
    ApplicationKind[ApplicationKind["OTHER_TEMPORARY_TRAFFIC_ARRANGEMENT"] = 42] = "OTHER_TEMPORARY_TRAFFIC_ARRANGEMENT"; // Muu
})(exports.ApplicationKind || (exports.ApplicationKind = {}));
var ApplicationKind = exports.ApplicationKind;
var ApplicationKindStructure = (function () {
    function ApplicationKindStructure(kind, specifiers) {
        this.kind = kind;
        this.specifiers = specifiers;
        this.specifiers = specifiers || [];
    }
    Object.defineProperty(ApplicationKindStructure.prototype, "applicationSpecifierNames", {
        get: function () {
            return this.specifiers.map(function (s) { return application_specifier_1.ApplicationSpecifier[s]; });
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ApplicationKindStructure.prototype, "applicationSpecifierNamesSortedByTranslation", {
        get: function () {
            return this.applicationSpecifierNames
                .sort(array_util_1.ArrayUtil.naturalSortTranslated(['application.specifier'], function (specifier) { return specifier; }));
        },
        enumerable: true,
        configurable: true
    });
    return ApplicationKindStructure;
}());
exports.ApplicationKindStructure = ApplicationKindStructure;
function drawingAllowedForKind(kind) {
    return ![ApplicationKind.BRIDGE_BANNER, ApplicationKind.DOG_TRAINING_EVENT, ApplicationKind.DOG_TRAINING_FIELD]
        .some(function (k) { return k === kind; });
}
exports.drawingAllowedForKind = drawingAllowedForKind;
