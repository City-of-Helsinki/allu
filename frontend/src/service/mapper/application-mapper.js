"use strict";
var application_1 = require("../../model/application/application");
var project_mapper_1 = require("./project-mapper");
var applicant_mapper_1 = require("./applicant-mapper");
var contact_mapper_1 = require("./contact-mapper");
var location_mapper_1 = require("./location-mapper");
var application_type_data_mapper_1 = require("./application-type-data-mapper");
var attachment_info_mapper_1 = require("./attachment-info-mapper");
var user_mapper_1 = require("./user-mapper");
var time_util_1 = require("../../util/time.util");
var option_1 = require("../../util/option");
var application_tag_mapper_1 = require("./application-tag-mapper");
var comment_mapper_1 = require("../application/comment/comment-mapper");
var ApplicationMapper = (function () {
    function ApplicationMapper() {
    }
    ApplicationMapper.mapBackend = function (backendApplication) {
        return new application_1.Application(backendApplication.id, backendApplication.applicationId, project_mapper_1.ProjectMapper.mapBackend(backendApplication.project), user_mapper_1.UserMapper.mapBackend(backendApplication.handler), backendApplication.status, backendApplication.type, backendApplication.kind, backendApplication.metadataVersion, backendApplication.name, time_util_1.TimeUtil.dateFromBackend(backendApplication.creationTime), time_util_1.TimeUtil.dateFromBackend(backendApplication.startTime), time_util_1.TimeUtil.dateFromBackend(backendApplication.endTime), applicant_mapper_1.ApplicantMapper.mapBackend(backendApplication.applicant), (backendApplication.contactList) ? backendApplication.contactList.map(function (contact) { return contact_mapper_1.ContactMapper.mapBackend(contact); }) : undefined, location_mapper_1.LocationMapper.mapBackend(backendApplication.locations[0]), application_type_data_mapper_1.ApplicationTypeDataMapper.mapBackend(backendApplication.extension), time_util_1.TimeUtil.dateFromBackend(backendApplication.decisionTime), (backendApplication.attachmentList) ? backendApplication.attachmentList.map(function (attachment) { return attachment_info_mapper_1.AttachmentInfoMapper.mapBackend(attachment); }) : undefined, backendApplication.calculatedPrice, backendApplication.priceOverride, backendApplication.priceOverrideReason, application_tag_mapper_1.ApplicationTagMapper.mapBackendList(backendApplication.applicationTags), comment_mapper_1.CommentMapper.mapBackendList(backendApplication.comments));
    };
    ApplicationMapper.mapFrontend = function (application) {
        return {
            id: application.id,
            applicationId: application.applicationId,
            project: project_mapper_1.ProjectMapper.mapFrontend(application.project),
            handler: user_mapper_1.UserMapper.mapFrontend(application.handler),
            status: application.status,
            type: application.type,
            kind: application.kind,
            metadataVersion: application.metadataVersion,
            name: application.name,
            creationTime: (application.creationTime) ? application.creationTime.toISOString() : undefined,
            startTime: application.startTime.toISOString(),
            endTime: application.endTime.toISOString(),
            applicant: applicant_mapper_1.ApplicantMapper.mapFrontend(application.applicant),
            contactList: (application.contactList) ? application.contactList.map(function (contact) { return contact_mapper_1.ContactMapper.mapFrontend(contact); }) : undefined,
            locations: (application.location) ? [location_mapper_1.LocationMapper.mapFrontend(application.location)] : undefined,
            extension: application_type_data_mapper_1.ApplicationTypeDataMapper.mapFrontend(application.extension),
            decisionTime: option_1.Some(application.decisionTime).map(function (decisionTime) { return decisionTime.toISOString(); }).orElse(undefined),
            attachmentList: undefined,
            calculatedPrice: application.calculatedPrice,
            priceOverride: application.priceOverride,
            priceOverrideReason: application.priceOverrideReason,
            applicationTags: application_tag_mapper_1.ApplicationTagMapper.mapFrontendList(application.applicationTags)
        };
    };
    ApplicationMapper.mapComment = function (comment) {
        return {
            comment: comment
        };
    };
    return ApplicationMapper;
}());
exports.ApplicationMapper = ApplicationMapper;
