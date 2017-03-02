"use strict";
var structure_meta_mapper_1 = require("./structure-meta-mapper");
var attribute_meta_1 = require("../../model/application/meta/attribute-meta");
var AttributeMetaMapper = (function () {
    function AttributeMetaMapper() {
    }
    AttributeMetaMapper.mapBackend = function (backendAttributeMetas) {
        var mappedAttributes = [];
        for (var _i = 0, backendAttributeMetas_1 = backendAttributeMetas; _i < backendAttributeMetas_1.length; _i++) {
            var attribute = backendAttributeMetas_1[_i];
            var structureMeta = undefined;
            if (attribute.structureMeta) {
                structureMeta = structure_meta_mapper_1.StructureMetaMapper.mapBackend(attribute.structureMeta);
            }
            mappedAttributes.push(new attribute_meta_1.AttributeMeta(attribute.name, attribute.uiName, attribute.dataType, attribute.listType, structureMeta, attribute.validationRule));
        }
        return mappedAttributes;
    };
    return AttributeMetaMapper;
}());
exports.AttributeMetaMapper = AttributeMetaMapper;
