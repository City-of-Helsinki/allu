"use strict";
var StructureMeta = (function () {
    function StructureMeta(applicationType, version, attributes) {
        this.applicationType = applicationType;
        this.version = version;
        this.attributes = attributes;
    }
    StructureMeta.prototype.uiName = function (path) {
        try {
            return this.getPath(this, path).uiName;
        }
        catch (error) {
            return path;
        }
    };
    StructureMeta.prototype.dataType = function (path) {
        try {
            return this.getPath(this, path).dataType;
        }
        catch (error) {
            return undefined;
        }
    };
    StructureMeta.prototype.getPath = function (structureMeta, path) {
        var pathComponents = path.split('\.');
        var currentAttributeName = pathComponents[0];
        var attributeMeta = structureMeta.attributes.filter(function (attr) { return attr.name === currentAttributeName; })[0];
        if (attributeMeta && attributeMeta.structureMeta && pathComponents.length > 1) {
            attributeMeta = this.getPath(attributeMeta.structureMeta, pathComponents.slice(1).join('.'));
        }
        else if (!attributeMeta || pathComponents.length > 1) {
            console.error('Attribute not found for path ' + path);
            throw new Error('Attribute not found for path ' + path);
        }
        return attributeMeta;
    };
    return StructureMeta;
}());
exports.StructureMeta = StructureMeta;
