"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
require("../../rxjs-extensions.ts");
var ProjectHub = (function () {
    function ProjectHub(projectService) {
        var _this = this;
        this.projectService = projectService;
        /**
         * Fetch single project with given id
         */
        this.getProject = function (id) { return _this.projectService.getProject(id); };
        /**
         * Search projects with given search query
         */
        this.searchProjects = function (search) { return _this.projectService.searchProjects(search); };
        /**
         * Saves given project (create / update)
         */
        this.save = function (project) { return _this.projectService.save(project); };
        /**
         * Remove project with given id
         */
        this.remove = function (id) { return _this.projectService.remove(id); };
        /**
         * Sets projects applications as given list of applications (empty array of id's removes all applications from project)
         */
        this.updateProjectApplications = function (id, applicationIds) {
            return _this.projectService.updateProjectApplications(id, applicationIds);
        };
        /**
         * Adds single application to project
         */
        this.addProjectApplication = function (id, applicationId) {
            return _this.projectService.addProjectApplication(id, applicationId);
        };
        /**
         * Fetches projects applications
         */
        this.getProjectApplications = function (id) { return _this.projectService.getProjectApplications(id); };
        /**
         * Fetches childprojects of given project
         */
        this.getChildProjects = function (id) { return _this.projectService.getChildProjects(id); };
        /**
         * Fetches all parents (and grandparents and ...) of given project
         */
        this.getParentProjects = function (id) { return _this.projectService.getParentProjects(id); };
        /**
         * Removes parent from given projects
         */
        this.updateParent = function (id, parentId) { return _this.projectService.updateParent(id, parentId); };
        /**
         * Removes parent from given projects
         */
        this.removeParent = function (ids) { return _this.projectService.removeParent(ids); };
    }
    return ProjectHub;
}());
ProjectHub = __decorate([
    core_1.Injectable()
], ProjectHub);
exports.ProjectHub = ProjectHub;
