"use strict";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
var Subject_1 = require("rxjs/Subject");
var ng2_file_upload_1 = require("ng2-file-upload");
var http_util_1 = require("../util/http.util");
var http_1 = require("@angular/http");
var attachment_info_mapper_1 = require("./mapper/attachment-info-mapper");
var default_attachment_info_mapper_1 = require("./mapper/default-attachment-info-mapper");
var application_type_1 = require("../model/application/type/application-type");
var uploadUrl = '/api/applications/appId/attachments';
var downloadUrl = '/api/applications/attachments/:attachmentId/data';
var defaultAttachmentUrl = '/api/admin/attachments';
var AttachmentService = (function () {
    function AttachmentService(authHttp) {
        this.authHttp = authHttp;
    }
    AttachmentService.prototype.uploadFiles = function (applicationId, attachments) {
        var url = uploadUrl.replace('appId', String(applicationId));
        return this.upload(url, attachments);
    };
    AttachmentService.prototype.remove = function (applicationId, attachmentId) {
        var url = uploadUrl.replace('appId', String(applicationId)) + '/' + attachmentId;
        return this.authHttp.delete(url)
            .map(function (response) { return http_util_1.HttpUtil.extractHttpResponse(response); });
    };
    AttachmentService.prototype.download = function (attachmentId, name) {
        var url = downloadUrl.replace(':attachmentId', String(attachmentId));
        var options = { responseType: http_1.ResponseContentType.Blob };
        return this.authHttp.get(url, options)
            .map(function (response) { return new File([response.blob()], name); });
    };
    AttachmentService.prototype.getDefaultAttachmentInfo = function (id) {
        var url = defaultAttachmentUrl + '/' + id;
        return this.authHttp.get(url)
            .map(function (response) { return response.json(); })
            .map(function (info) { return default_attachment_info_mapper_1.DefaultAttachmentInfoMapper.mapBackend(info); });
    };
    AttachmentService.prototype.getDefaultAttachmentInfos = function () {
        return this.authHttp.get(defaultAttachmentUrl)
            .map(function (response) { return response.json(); })
            .map(function (infos) { return infos.map(function (info) { return default_attachment_info_mapper_1.DefaultAttachmentInfoMapper.mapBackend(info); }); });
    };
    AttachmentService.prototype.getDefaultAttachmentInfosByType = function (appType) {
        var url = defaultAttachmentUrl + '/applicationType/' + application_type_1.ApplicationType[appType];
        return this.authHttp.get(url)
            .map(function (response) { return response.json(); })
            .map(function (infos) { return infos.map(function (info) { return default_attachment_info_mapper_1.DefaultAttachmentInfoMapper.mapBackend(info); }); });
    };
    AttachmentService.prototype.saveDefaultAttachments = function (attachment) {
        if (attachment.id) {
            return this.updateDefaultAttachmentInfo(attachment);
        }
        else {
            return this.upload(defaultAttachmentUrl, [attachment]).map(function (results) { return results[0]; });
        }
    };
    AttachmentService.prototype.updateDefaultAttachmentInfo = function (attachment) {
        var url = defaultAttachmentUrl + '/' + attachment.id;
        return this.authHttp.put(url, attachment)
            .map(function (response) { return response.json(); })
            .map(function (info) { return default_attachment_info_mapper_1.DefaultAttachmentInfoMapper.mapBackend(info); });
    };
    AttachmentService.prototype.removeDefaultAttachment = function (id) {
        var url = defaultAttachmentUrl + '/' + id;
        return this.authHttp.delete(url)
            .map(function (response) { return http_util_1.HttpUtil.extractHttpResponse(response); });
    };
    AttachmentService.prototype.upload = function (url, attachments) {
        var uploadSubject = new Subject_1.Subject();
        if (attachments && attachments.length !== 0) {
            var uploader = new ExtendedFileUploader({
                url: url,
                authToken: 'Bearer ' + localStorage.getItem('jwt')
            }, attachments);
            var files = attachments.map(function (a) { return a.file; });
            uploader.onSuccessItem = function (item, response, status, headers) {
                var items = JSON.parse(response);
                var infos = items.map(function (i) { return attachment_info_mapper_1.AttachmentInfoMapper.mapBackend(i); });
                uploadSubject.next(infos);
            };
            uploader.onErrorItem = function (item, response, status, headers) { return uploadSubject.error(response); };
            uploader.onCompleteAll = function () { return uploadSubject.complete(); };
            uploader.addToQueue(files);
            uploader.uploadAll();
        }
        else {
            uploadSubject.complete();
        }
        return uploadSubject.asObservable();
    };
    return AttachmentService;
}());
AttachmentService = __decorate([
    core_1.Injectable()
], AttachmentService);
exports.AttachmentService = AttachmentService;
var ExtendedFileUploader = (function (_super) {
    __extends(ExtendedFileUploader, _super);
    function ExtendedFileUploader(options, meta) {
        var _this = _super.call(this, options) || this;
        _this.meta = meta;
        return _this;
    }
    ExtendedFileUploader.prototype.onBuildItemForm = function (fileItem, form) {
        var metaForItem = this.meta.shift();
        var json = JSON.stringify([metaForItem]);
        var blob = new Blob([json], { type: 'application/json' });
        form.append('meta', blob);
        _super.prototype.onBuildItemForm.call(this, fileItem, form);
    };
    return ExtendedFileUploader;
}(ng2_file_upload_1.FileUploader));
exports.ExtendedFileUploader = ExtendedFileUploader;
