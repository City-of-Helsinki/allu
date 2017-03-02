"use strict";
(function (AttachmentType) {
    AttachmentType[AttachmentType["ADDED_BY_CUSTOMER"] = 0] = "ADDED_BY_CUSTOMER";
    AttachmentType[AttachmentType["ADDED_BY_HANDLER"] = 1] = "ADDED_BY_HANDLER";
    AttachmentType[AttachmentType["DEFAULT"] = 2] = "DEFAULT";
    AttachmentType[AttachmentType["DEFAULT_IMAGE"] = 3] = "DEFAULT_IMAGE";
    AttachmentType[AttachmentType["DEFAULT_TERMS"] = 4] = "DEFAULT_TERMS"; // Hakemustyyppikohtainen ehtoliite
})(exports.AttachmentType || (exports.AttachmentType = {}));
var AttachmentType = exports.AttachmentType;
exports.commonAttachmentTypes = [
    AttachmentType.ADDED_BY_CUSTOMER,
    AttachmentType.ADDED_BY_HANDLER
];
exports.isCommon = function (type) { return exports.commonAttachmentTypes.indexOf(AttachmentType[type]) >= 0; };
