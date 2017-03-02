"use strict";
var project_1 = require("../../model/project/project");
var option_1 = require("../../util/option");
var ProjectMapper = (function () {
    function ProjectMapper() {
    }
    ProjectMapper.mapBackend = function (backendProject) {
        return (backendProject) ?
            new project_1.Project(backendProject.id, backendProject.name, option_1.Some(backendProject.startTime).map(function (start) { return new Date(start); }).orElse(undefined), option_1.Some(backendProject.endTime).map(function (end) { return new Date(end); }).orElse(undefined), backendProject.cityDistricts, backendProject.ownerName, backendProject.contactName, backendProject.email, backendProject.phone, backendProject.customerReference, backendProject.additionalInfo, backendProject.parentId) : undefined;
    };
    ProjectMapper.mapFrontend = function (project) {
        return (project) ? {
            id: project.id,
            name: project.name,
            startTime: (project.startTime) ? project.startTime.toISOString() : undefined,
            endTime: (project.endTime) ? project.endTime.toISOString() : undefined,
            cityDistricts: project.cityDistricts,
            ownerName: project.ownerName,
            contactName: project.contactName,
            email: project.email,
            phone: project.phone,
            customerReference: project.customerReference,
            additionalInfo: project.additionalInfo,
            parentId: project.parentId
        } : undefined;
    };
    return ProjectMapper;
}());
exports.ProjectMapper = ProjectMapper;
