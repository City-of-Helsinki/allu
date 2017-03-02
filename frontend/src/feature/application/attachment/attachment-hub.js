"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
require("../../../rxjs-extensions.ts");
var attachment_type_1 = require("../../../model/application/attachment/attachment-type");
var AttachmentHub = (function () {
    function AttachmentHub(attachmentService) {
        var _this = this;
        this.attachmentService = attachmentService;
        /**
         * Uploads given attachments and adds them to given application
         */
        this.upload = function (applicationId, attachments) {
            return _this.attachmentService.uploadFiles(applicationId, attachments);
        };
        /**
         * Removes given attachment
         */
        this.remove = function (applicationId, attachmentId) { return _this.attachmentService.remove(applicationId, attachmentId); };
        /**
         * Retrieves attachment in downloadable format
         * and converts to file with given filename
         */
        this.download = function (attachmentId, name) { return _this.attachmentService.download(attachmentId, name); };
        /**
         * Fetches single default attachment info
         */
        this.defaultAttachmentInfo = function (id) { return _this.attachmentService.getDefaultAttachmentInfo(id); };
        /**
         * Fetches all default attachment infos
         */
        this.defaultAttachmentInfos = function () { return _this.attachmentService.getDefaultAttachmentInfos(); };
        /**
         * Fetches default attachment infos which are for given application- and attachment type
         */
        this.defaultAttachmentInfosBy = function (applicationType, attachmentType) {
            return _this.attachmentService.getDefaultAttachmentInfosByType(applicationType)
                .map(function (attachments) { return attachments.filter(function (attachment) { return attachment_type_1.AttachmentType[attachment.type] === attachmentType; }); });
        };
        /**
         * Saves given default attachment
         */
        this.saveDefaultAttachments = function (attachment) { return _this.attachmentService.saveDefaultAttachments(attachment); };
        /**
         * Removes given default attachment by id
         */
        this.removeDefaultAttachment = function (id) { return _this.attachmentService.removeDefaultAttachment(id); };
    }
    return AttachmentHub;
}());
AttachmentHub = __decorate([
    core_1.Injectable()
], AttachmentHub);
exports.AttachmentHub = AttachmentHub;
