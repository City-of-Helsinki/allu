"use strict";
var default_attachment_info_1 = require("../../model/application/attachment/default-attachment-info");
var DefaultAttachmentInfoMapper = (function () {
    function DefaultAttachmentInfoMapper() {
    }
    DefaultAttachmentInfoMapper.mapBackend = function (backendAttachmentInfo) {
        if (!backendAttachmentInfo) {
            return undefined;
        }
        return new default_attachment_info_1.DefaultAttachmentInfo(backendAttachmentInfo.id, backendAttachmentInfo.type, backendAttachmentInfo.name, backendAttachmentInfo.description, backendAttachmentInfo.size, new Date(backendAttachmentInfo.creationTime), backendAttachmentInfo.handlerName, undefined, backendAttachmentInfo.defaultAttachmentId, backendAttachmentInfo.applicationTypes, backendAttachmentInfo.fixedLocationId);
    };
    DefaultAttachmentInfoMapper.mapFrontend = function (attachmentInfo) {
        return (attachmentInfo) ?
            {
                id: attachmentInfo.id,
                type: attachmentInfo.type,
                name: attachmentInfo.name,
                description: attachmentInfo.description,
                size: attachmentInfo.size,
                creationTime: (attachmentInfo.creationTime) ? attachmentInfo.creationTime.toISOString() : undefined,
                handlerName: attachmentInfo.handlerName,
                defaultAttachmentId: attachmentInfo.defaultAttachmentId,
                applicationTypes: attachmentInfo.applicationTypes,
                fixedLocationId: attachmentInfo.fixedLocationId
            } : undefined;
    };
    return DefaultAttachmentInfoMapper;
}());
exports.DefaultAttachmentInfoMapper = DefaultAttachmentInfoMapper;
