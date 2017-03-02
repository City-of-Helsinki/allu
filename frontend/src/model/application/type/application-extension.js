"use strict";
var ApplicationExtension = (function () {
    function ApplicationExtension(applicationType, specifiers, terms) {
        this.applicationType = applicationType;
        this.specifiers = specifiers;
        this.terms = terms;
        this.specifiers = this.specifiers || [];
    }
    return ApplicationExtension;
}());
exports.ApplicationExtension = ApplicationExtension;
