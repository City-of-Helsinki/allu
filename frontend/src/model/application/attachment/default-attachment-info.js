"use strict";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var attachment_info_1 = require("./attachment-info");
var time_util_1 = require("../../../util/time.util");
var DefaultAttachmentInfo = (function (_super) {
    __extends(DefaultAttachmentInfo, _super);
    function DefaultAttachmentInfo(id, type, name, description, size, creationTime, handlerName, file, defaultAttachmentId, applicationTypes, fixedLocationId) {
        var _this = _super.call(this, id, type, name, description, size, creationTime, handlerName, file) || this;
        _this.id = id;
        _this.type = type;
        _this.name = name;
        _this.description = description;
        _this.size = size;
        _this.creationTime = creationTime;
        _this.handlerName = handlerName;
        _this.file = file;
        _this.defaultAttachmentId = defaultAttachmentId;
        _this.applicationTypes = applicationTypes;
        _this.fixedLocationId = fixedLocationId;
        return _this;
    }
    ;
    DefaultAttachmentInfo.fromForm = function (form) {
        return new DefaultAttachmentInfo(form.id, form.type, form.name, form.description, form.size, time_util_1.TimeUtil.getDateFromUi(form.creationTime), form.handlerName, form.file, form.defaultAttachmentId, form.applicationTypes, form.fixedLocationId);
    };
    DefaultAttachmentInfo.toForm = function (attachmentInfo) {
        return {
            id: attachmentInfo.id,
            type: attachmentInfo.type,
            name: attachmentInfo.name,
            description: attachmentInfo.description,
            creationTime: attachmentInfo.uiCreationTime,
            handlerName: attachmentInfo.handlerName,
            file: attachmentInfo.file,
            defaultAttachmentId: attachmentInfo.defaultAttachmentId,
            applicationTypes: attachmentInfo.applicationTypes,
            fixedLocationId: attachmentInfo.fixedLocationId
        };
    };
    return DefaultAttachmentInfo;
}(attachment_info_1.AttachmentInfo));
exports.DefaultAttachmentInfo = DefaultAttachmentInfo;
