"use strict";
var translations_1 = require("../../util/translations");
var application_status_1 = require("./application-status");
function translateStatus(status) {
    return translations_1.translations.application.status[application_status_1.ApplicationStatus[status]];
}
exports.translateStatus = translateStatus;
var ApplicationStatusChange = (function () {
    function ApplicationStatusChange(id, status, comment) {
        this.id = id;
        this.status = status;
        this.comment = comment;
    }
    ApplicationStatusChange.of = function (id, status) {
        return new ApplicationStatusChange(id, status, undefined);
    };
    ApplicationStatusChange.withComment = function (id, status, comment) {
        return new ApplicationStatusChange(id, status, comment);
    };
    return ApplicationStatusChange;
}());
exports.ApplicationStatusChange = ApplicationStatusChange;
