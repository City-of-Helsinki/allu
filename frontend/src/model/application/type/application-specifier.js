"use strict";
(function (ApplicationSpecifier) {
    // Katu- ja vihertyöt
    ApplicationSpecifier[ApplicationSpecifier["ASPHALT"] = 0] = "ASPHALT";
    ApplicationSpecifier[ApplicationSpecifier["INDUCTION_LOOP"] = 1] = "INDUCTION_LOOP";
    ApplicationSpecifier[ApplicationSpecifier["COVER_STRUCTURE"] = 2] = "COVER_STRUCTURE";
    ApplicationSpecifier[ApplicationSpecifier["STREET_OR_PARK"] = 3] = "STREET_OR_PARK";
    ApplicationSpecifier[ApplicationSpecifier["PAVEMENT"] = 4] = "PAVEMENT";
    ApplicationSpecifier[ApplicationSpecifier["TRAFFIC_LIGHT"] = 5] = "TRAFFIC_LIGHT";
    ApplicationSpecifier[ApplicationSpecifier["COMMERCIAL_DEVICE"] = 6] = "COMMERCIAL_DEVICE";
    ApplicationSpecifier[ApplicationSpecifier["TRAFFIC_STOP"] = 7] = "TRAFFIC_STOP";
    ApplicationSpecifier[ApplicationSpecifier["BRIDGE"] = 8] = "BRIDGE";
    ApplicationSpecifier[ApplicationSpecifier["OUTDOOR_LIGHTING"] = 9] = "OUTDOOR_LIGHTING";
    // Vesi / viemäri
    ApplicationSpecifier[ApplicationSpecifier["STORM_DRAIN"] = 10] = "STORM_DRAIN";
    ApplicationSpecifier[ApplicationSpecifier["WELL"] = 11] = "WELL";
    ApplicationSpecifier[ApplicationSpecifier["UNDERGROUND_DRAIN"] = 12] = "UNDERGROUND_DRAIN";
    ApplicationSpecifier[ApplicationSpecifier["WATER_PIPE"] = 13] = "WATER_PIPE";
    ApplicationSpecifier[ApplicationSpecifier["DRAIN"] = 14] = "DRAIN";
    // Sähkö
    ApplicationSpecifier[ApplicationSpecifier["DISTRIBUTION_CABINET"] = 15] = "DISTRIBUTION_CABINET";
    ApplicationSpecifier[ApplicationSpecifier["ELECTRICITY_CABLE"] = 16] = "ELECTRICITY_CABLE";
    ApplicationSpecifier[ApplicationSpecifier["ELECTRICITY_WELL"] = 17] = "ELECTRICITY_WELL";
    // Tiedonsiirto
    ApplicationSpecifier[ApplicationSpecifier["DISTRIBUTION_CABINET_OR_PILAR"] = 18] = "DISTRIBUTION_CABINET_OR_PILAR";
    ApplicationSpecifier[ApplicationSpecifier["DATA_CABLE"] = 19] = "DATA_CABLE";
    ApplicationSpecifier[ApplicationSpecifier["DATA_WELL"] = 20] = "DATA_WELL";
    // Lämmitys/viilennys
    ApplicationSpecifier[ApplicationSpecifier["STREET_HEATING"] = 21] = "STREET_HEATING";
    ApplicationSpecifier[ApplicationSpecifier["DISTRICT_HEATING"] = 22] = "DISTRICT_HEATING";
    ApplicationSpecifier[ApplicationSpecifier["DISTRICT_COOLING"] = 23] = "DISTRICT_COOLING";
    // Rakennus
    ApplicationSpecifier[ApplicationSpecifier["GROUND_ROCK_ANCHOR"] = 24] = "GROUND_ROCK_ANCHOR";
    ApplicationSpecifier[ApplicationSpecifier["UNDERGROUND_STRUCTURE"] = 25] = "UNDERGROUND_STRUCTURE";
    ApplicationSpecifier[ApplicationSpecifier["UNDERGROUND_SPACE"] = 26] = "UNDERGROUND_SPACE";
    ApplicationSpecifier[ApplicationSpecifier["BASE_STRUCTURES"] = 27] = "BASE_STRUCTURES";
    ApplicationSpecifier[ApplicationSpecifier["DRILL_PILE"] = 28] = "DRILL_PILE";
    ApplicationSpecifier[ApplicationSpecifier["CONSTRUCTION_EQUIPMENT"] = 29] = "CONSTRUCTION_EQUIPMENT";
    ApplicationSpecifier[ApplicationSpecifier["CONSTRUCTION_PART"] = 30] = "CONSTRUCTION_PART";
    ApplicationSpecifier[ApplicationSpecifier["GROUND_FROST_INSULATION"] = 31] = "GROUND_FROST_INSULATION";
    ApplicationSpecifier[ApplicationSpecifier["SMOKE_HATCH_OR_PIPE"] = 32] = "SMOKE_HATCH_OR_PIPE";
    ApplicationSpecifier[ApplicationSpecifier["STOP_OR_TRANSITION_SLAB"] = 33] = "STOP_OR_TRANSITION_SLAB";
    ApplicationSpecifier[ApplicationSpecifier["SUPPORTING_WALL_OR_PILE"] = 34] = "SUPPORTING_WALL_OR_PILE";
    // Piha
    ApplicationSpecifier[ApplicationSpecifier["FENCE_OR_WALL"] = 35] = "FENCE_OR_WALL";
    ApplicationSpecifier[ApplicationSpecifier["DRIVEWAY"] = 36] = "DRIVEWAY";
    ApplicationSpecifier[ApplicationSpecifier["STAIRS_RAMP"] = 37] = "STAIRS_RAMP";
    ApplicationSpecifier[ApplicationSpecifier["SUPPORTING_WALL_OR_BANK"] = 38] = "SUPPORTING_WALL_OR_BANK";
    // Pohjatutkimus
    ApplicationSpecifier[ApplicationSpecifier["DRILLING"] = 39] = "DRILLING";
    ApplicationSpecifier[ApplicationSpecifier["TEST_HOLE"] = 40] = "TEST_HOLE";
    ApplicationSpecifier[ApplicationSpecifier["GROUND_WATER_PIPE"] = 41] = "GROUND_WATER_PIPE";
    // Muu
    ApplicationSpecifier[ApplicationSpecifier["ABSORBING_SEWAGE_SYSTEM"] = 42] = "ABSORBING_SEWAGE_SYSTEM";
    ApplicationSpecifier[ApplicationSpecifier["GAS_PIPE"] = 43] = "GAS_PIPE";
    ApplicationSpecifier[ApplicationSpecifier["OTHER"] = 44] = "OTHER"; // Muu
})(exports.ApplicationSpecifier || (exports.ApplicationSpecifier = {}));
var ApplicationSpecifier = exports.ApplicationSpecifier;
