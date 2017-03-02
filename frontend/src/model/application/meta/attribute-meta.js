"use strict";
var AttributeMeta = (function () {
    function AttributeMeta(name, uiName, dataType, listType, structureMeta, validationRule) {
        this.name = name;
        this.uiName = uiName;
        this.dataType = dataType;
        this.listType = listType;
        this.structureMeta = structureMeta;
        this.validationRule = validationRule;
    }
    return AttributeMeta;
}());
exports.AttributeMeta = AttributeMeta;
