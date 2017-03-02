"use strict";
var ApplicationTag = (function () {
    function ApplicationTag(type, addedBy, creationTime) {
        this.type = type;
        this.addedBy = addedBy;
        this.creationTime = creationTime;
    }
    return ApplicationTag;
}());
exports.ApplicationTag = ApplicationTag;
