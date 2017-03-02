"use strict";
var time_util_1 = require("../../../util/time.util");
var AttachmentInfo = (function () {
    function AttachmentInfo(id, type, name, description, size, creationTime, handlerName, file) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.size = size;
        this.creationTime = creationTime;
        this.handlerName = handlerName;
        this.file = file;
    }
    ;
    Object.defineProperty(AttachmentInfo.prototype, "uiCreationTime", {
        get: function () {
            return time_util_1.TimeUtil.getUiDateTimeString(this.creationTime);
        },
        enumerable: true,
        configurable: true
    });
    AttachmentInfo.fromFile = function (file) {
        var attachment = new AttachmentInfo();
        attachment.name = file.name;
        attachment.file = file;
        return attachment;
    };
    AttachmentInfo.fromForm = function (form) {
        return new AttachmentInfo(form.id, form.type, form.name, form.description, form.size, time_util_1.TimeUtil.getDateFromUi(form.creationTime), form.handlerName, form.file);
    };
    AttachmentInfo.toForm = function (attachmentInfo) {
        return {
            id: attachmentInfo.id,
            type: attachmentInfo.type,
            name: attachmentInfo.name,
            description: attachmentInfo.description,
            creationTime: attachmentInfo.uiCreationTime,
            handlerName: attachmentInfo.handlerName,
            file: attachmentInfo.file
        };
    };
    return AttachmentInfo;
}());
exports.AttachmentInfo = AttachmentInfo;
