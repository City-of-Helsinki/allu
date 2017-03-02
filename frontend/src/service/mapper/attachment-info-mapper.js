"use strict";
var attachment_info_1 = require("../../model/application/attachment/attachment-info");
var AttachmentInfoMapper = (function () {
    function AttachmentInfoMapper() {
    }
    AttachmentInfoMapper.mapBackend = function (backendAttachmentInfo) {
        if (!backendAttachmentInfo) {
            return undefined;
        }
        return new attachment_info_1.AttachmentInfo(backendAttachmentInfo.id, backendAttachmentInfo.type, backendAttachmentInfo.name, backendAttachmentInfo.description, backendAttachmentInfo.size, new Date(backendAttachmentInfo.creationTime), backendAttachmentInfo.handlerName, undefined);
    };
    AttachmentInfoMapper.mapFrontend = function (attachmentInfo) {
        return (attachmentInfo) ?
            {
                id: attachmentInfo.id,
                type: attachmentInfo.type,
                name: attachmentInfo.name,
                description: attachmentInfo.description,
                size: attachmentInfo.size,
                creationTime: (attachmentInfo.creationTime) ? attachmentInfo.creationTime.toISOString() : undefined,
                handlerName: attachmentInfo.handlerName
            } : undefined;
    };
    return AttachmentInfoMapper;
}());
exports.AttachmentInfoMapper = AttachmentInfoMapper;
