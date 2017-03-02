"use strict";
var structure_meta_1 = require("../../model/application/meta/structure-meta");
var attribute_meta_mapper_1 = require("./attribute-meta-mapper");
var StructureMetaMapper = (function () {
    function StructureMetaMapper() {
    }
    StructureMetaMapper.mapBackend = function (backendStructureMeta) {
        return new structure_meta_1.StructureMeta(backendStructureMeta.applicationType, backendStructureMeta.version, attribute_meta_mapper_1.AttributeMetaMapper.mapBackend(backendStructureMeta.attributes));
    };
    StructureMetaMapper.mapFrontend = function (frontendStructureMeta) {
        return {
            applicationType: frontendStructureMeta.applicationType,
            version: frontendStructureMeta.version,
            attributes: [] // attributes are not mapped at the moment, because backend does not need them. To be done later, if needed
        };
    };
    return StructureMetaMapper;
}());
exports.StructureMetaMapper = StructureMetaMapper;
