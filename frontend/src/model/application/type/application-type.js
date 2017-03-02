"use strict";
var application_kind_1 = require("./application-kind");
var application_specifier_1 = require("./application-specifier");
var array_util_1 = require("../../../util/array-util");
var option_1 = require("../../../util/option");
(function (ApplicationType) {
    ApplicationType[ApplicationType["EXCAVATION_ANNOUNCEMENT"] = 0] = "EXCAVATION_ANNOUNCEMENT";
    ApplicationType[ApplicationType["AREA_RENTAL"] = 1] = "AREA_RENTAL";
    ApplicationType[ApplicationType["TEMPORARY_TRAFFIC_ARRANGEMENTS"] = 2] = "TEMPORARY_TRAFFIC_ARRANGEMENTS";
    ApplicationType[ApplicationType["CABLE_REPORT"] = 3] = "CABLE_REPORT";
    ApplicationType[ApplicationType["PLACEMENT_CONTRACT"] = 4] = "PLACEMENT_CONTRACT";
    ApplicationType[ApplicationType["EVENT"] = 5] = "EVENT";
    ApplicationType[ApplicationType["SHORT_TERM_RENTAL"] = 6] = "SHORT_TERM_RENTAL";
    ApplicationType[ApplicationType["NOTE"] = 7] = "NOTE"; // Muistiinpano
})(exports.ApplicationType || (exports.ApplicationType = {}));
var ApplicationType = exports.ApplicationType;
var ApplicationTypeStructure = (function () {
    function ApplicationTypeStructure(type, kinds) {
        this.type = type;
        this.kinds = kinds;
    }
    ApplicationTypeStructure.prototype.containsKind = function (kind) {
        return this.kinds.map(function (k) { return k.kind; }).indexOf(kind) >= 0;
    };
    ApplicationTypeStructure.prototype.structureByKind = function (kind) {
        return this.kinds.find(function (k) { return k.kind === kind; });
    };
    Object.defineProperty(ApplicationTypeStructure.prototype, "typeName", {
        get: function () {
            return ApplicationType[this.type];
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ApplicationTypeStructure.prototype, "applicationKindNames", {
        get: function () {
            return this.kinds.map(function (k) { return application_kind_1.ApplicationKind[k.kind]; });
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ApplicationTypeStructure.prototype, "applicationKindNamesSortedByTranslation", {
        get: function () {
            return this.applicationKindNames.sort(array_util_1.ArrayUtil.naturalSortTranslated(['application.kind'], function (kind) { return kind; }));
        },
        enumerable: true,
        configurable: true
    });
    return ApplicationTypeStructure;
}());
exports.ApplicationTypeStructure = ApplicationTypeStructure;
var commonApplicationKinds = [
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.STREET_AND_GREEN, [
        application_specifier_1.ApplicationSpecifier.ASPHALT,
        application_specifier_1.ApplicationSpecifier.INDUCTION_LOOP,
        application_specifier_1.ApplicationSpecifier.COVER_STRUCTURE,
        application_specifier_1.ApplicationSpecifier.STREET_OR_PARK,
        application_specifier_1.ApplicationSpecifier.PAVEMENT,
        application_specifier_1.ApplicationSpecifier.TRAFFIC_LIGHT,
        application_specifier_1.ApplicationSpecifier.COMMERCIAL_DEVICE,
        application_specifier_1.ApplicationSpecifier.TRAFFIC_STOP,
        application_specifier_1.ApplicationSpecifier.BRIDGE,
        application_specifier_1.ApplicationSpecifier.OUTDOOR_LIGHTING
    ]),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.WATER_AND_SEWAGE, [
        application_specifier_1.ApplicationSpecifier.STORM_DRAIN,
        application_specifier_1.ApplicationSpecifier.WELL,
        application_specifier_1.ApplicationSpecifier.UNDERGROUND_DRAIN,
        application_specifier_1.ApplicationSpecifier.WATER_PIPE,
        application_specifier_1.ApplicationSpecifier.DRAIN
    ]),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.ELECTRICITY, [
        application_specifier_1.ApplicationSpecifier.DISTRIBUTION_CABINET,
        application_specifier_1.ApplicationSpecifier.ELECTRICITY_CABLE,
        application_specifier_1.ApplicationSpecifier.ELECTRICITY_WELL
    ]),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.DATA_TRANSFER, [
        application_specifier_1.ApplicationSpecifier.DISTRIBUTION_CABINET_OR_PILAR,
        application_specifier_1.ApplicationSpecifier.DATA_CABLE,
        application_specifier_1.ApplicationSpecifier.DATA_WELL
    ]),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.HEATING_COOLING, [
        application_specifier_1.ApplicationSpecifier.STREET_HEATING,
        application_specifier_1.ApplicationSpecifier.DISTRICT_HEATING,
        application_specifier_1.ApplicationSpecifier.DISTRICT_COOLING
    ]),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.CONSTRUCTION, [
        application_specifier_1.ApplicationSpecifier.GROUND_ROCK_ANCHOR,
        application_specifier_1.ApplicationSpecifier.UNDERGROUND_STRUCTURE,
        application_specifier_1.ApplicationSpecifier.UNDERGROUND_SPACE,
        application_specifier_1.ApplicationSpecifier.BASE_STRUCTURES,
        application_specifier_1.ApplicationSpecifier.DRILL_PILE,
        application_specifier_1.ApplicationSpecifier.CONSTRUCTION_EQUIPMENT,
        application_specifier_1.ApplicationSpecifier.CONSTRUCTION_PART,
        application_specifier_1.ApplicationSpecifier.GROUND_FROST_INSULATION,
        application_specifier_1.ApplicationSpecifier.SMOKE_HATCH_OR_PIPE,
        application_specifier_1.ApplicationSpecifier.STOP_OR_TRANSITION_SLAB,
        application_specifier_1.ApplicationSpecifier.SUPPORTING_WALL_OR_PILE
    ]),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.YARD, [
        application_specifier_1.ApplicationSpecifier.FENCE_OR_WALL,
        application_specifier_1.ApplicationSpecifier.DRIVEWAY,
        application_specifier_1.ApplicationSpecifier.STAIRS_RAMP,
        application_specifier_1.ApplicationSpecifier.SUPPORTING_WALL_OR_BANK
    ]),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.GEOLOGICAL_SURVEY, [
        application_specifier_1.ApplicationSpecifier.DRILLING,
        application_specifier_1.ApplicationSpecifier.TEST_HOLE,
        application_specifier_1.ApplicationSpecifier.GROUND_WATER_PIPE
    ]),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.OTHER_CABLE_REPORT, [
        application_specifier_1.ApplicationSpecifier.ABSORBING_SEWAGE_SYSTEM,
        application_specifier_1.ApplicationSpecifier.GAS_PIPE,
        application_specifier_1.ApplicationSpecifier.OTHER
    ])
];
exports.excavationAnnouncement = new ApplicationTypeStructure(ApplicationType.EXCAVATION_ANNOUNCEMENT, commonApplicationKinds);
exports.areaRental = new ApplicationTypeStructure(ApplicationType.AREA_RENTAL, []);
exports.temporaryTrafficArrangements = new ApplicationTypeStructure(ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS, [
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.PUBLIC_EVENT),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.OTHER_TEMPORARY_TRAFFIC_ARRANGEMENT)
]);
exports.cableReport = new ApplicationTypeStructure(ApplicationType.CABLE_REPORT, commonApplicationKinds);
exports.placementContract = new ApplicationTypeStructure(ApplicationType.PLACEMENT_CONTRACT, commonApplicationKinds);
exports.event = new ApplicationTypeStructure(ApplicationType.EVENT, [
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.PROMOTION),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.OUTDOOREVENT)
]);
exports.shortTermRental = new ApplicationTypeStructure(ApplicationType.SHORT_TERM_RENTAL, [
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.BRIDGE_BANNER),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.BENJI),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.PROMOTION_OR_SALES),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.URBAN_FARMING),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.MAIN_STREET_SALES),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.SUMMER_THEATER),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.DOG_TRAINING_FIELD),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.DOG_TRAINING_EVENT),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.CARGO_CONTAINER),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.SMALL_ART_AND_CULTURE),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.SEASON_SALE),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.CIRCUS),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.ART),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.STORAGE_AREA),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.OTHER_SHORT_TERM_RENTAL)
]);
exports.note = new ApplicationTypeStructure(ApplicationType.NOTE, [
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.CHRISTMAS_TREE_SALES_AREA),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.CITY_CYCLING_AREA),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.AGILE_KIOSK_AREA),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.STATEMENT),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.SNOW_HEAP_AREA),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.SNOW_GATHER_AREA),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.OTHER_SUBVISION_OF_STATE_AREA),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.MILITARY_EXCERCISE),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.WINTER_PARKING),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.REPAVING),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.ELECTION_ADD_STAND),
    new application_kind_1.ApplicationKindStructure(application_kind_1.ApplicationKind.NOTE_OTHER)
]);
exports.applicationTypeStructures = [
    exports.excavationAnnouncement,
    exports.areaRental,
    exports.temporaryTrafficArrangements,
    exports.cableReport,
    exports.placementContract,
    exports.event,
    exports.shortTermRental,
    exports.note
];
function typeStructureByType(type) {
    var appType = ApplicationType[type];
    return option_1.Some(exports.applicationTypeStructures.find(function (ts) { return ts.type === appType; }));
}
exports.typeStructureByType = typeStructureByType;
function kindStructureByTypeAndKind(type, kind) {
    var kindType = application_kind_1.ApplicationKind[kind];
    return typeStructureByType(type)
        .map(function (ts) { return ts.structureByKind(kindType); });
}
exports.kindStructureByTypeAndKind = kindStructureByTypeAndKind;
