"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
var Observable_1 = require("rxjs/Observable");
require("../../rxjs-extensions.ts");
var project_mapper_1 = require("../mapper/project-mapper");
var http_util_1 = require("../../util/http.util");
var error_info_1 = require("../ui-state/error-info");
var error_type_1 = require("../ui-state/error-type");
var application_mapper_1 = require("../mapper/application-mapper");
var query_parameters_mapper_1 = require("../mapper/query-parameters-mapper");
var http_response_1 = require("../../util/http-response");
var ProjectService = (function () {
    function ProjectService(authHttp, uiState) {
        this.authHttp = authHttp;
        this.uiState = uiState;
    }
    ProjectService.prototype.searchProjects = function (searchQuery) {
        var _this = this;
        var searchUrl = ProjectService.PROJECT_URL + ProjectService.SEARCH;
        return this.authHttp.post(searchUrl, JSON.stringify(query_parameters_mapper_1.QueryParametersMapper.mapProjectQueryFrontend(searchQuery)))
            .map(function (response) { return response.json(); })
            .map(function (json) { return json.map(function (project) { return project_mapper_1.ProjectMapper.mapBackend(project); }); })
            .catch(function (err) { return _this.uiState.addError(new error_info_1.ErrorInfo(error_type_1.ErrorType.PROJECT_SEARCH_FAILED, http_util_1.HttpUtil.extractMessage(err))); });
    };
    ProjectService.prototype.getProject = function (id) {
        var _this = this;
        return this.authHttp.get(ProjectService.PROJECT_URL + '/' + id)
            .map(function (response) { return response.json(); })
            .map(function (project) { return project_mapper_1.ProjectMapper.mapBackend(project); })
            .catch(function (err) { return _this.uiState.addError(http_util_1.HttpUtil.extractMessage(err)); });
    };
    ProjectService.prototype.save = function (project) {
        var _this = this;
        if (project.id) {
            var url = ProjectService.PROJECT_URL + '/' + project.id;
            return this.authHttp.put(url, JSON.stringify(project_mapper_1.ProjectMapper.mapFrontend(project)))
                .map(function (response) { return project_mapper_1.ProjectMapper.mapBackend(response.json()); })
                .catch(function (err) { return _this.uiState.addError(new error_info_1.ErrorInfo(error_type_1.ErrorType.PROJECT_SAVE_FAILED, http_util_1.HttpUtil.extractMessage(err))); });
        }
        else {
            return this.authHttp.post(ProjectService.PROJECT_URL, JSON.stringify(project_mapper_1.ProjectMapper.mapFrontend(project)))
                .map(function (response) { return project_mapper_1.ProjectMapper.mapBackend(response.json()); })
                .catch(function (err) { return _this.uiState.addError(new error_info_1.ErrorInfo(error_type_1.ErrorType.PROJECT_SAVE_FAILED, http_util_1.HttpUtil.extractMessage(err))); });
        }
    };
    ProjectService.prototype.remove = function (id) {
        return Observable_1.Observable.of(new http_response_1.HttpResponse(http_response_1.HttpStatus.OK, 'Project removed ' + id));
    };
    ProjectService.prototype.updateProjectApplications = function (id, applicationIds) {
        var _this = this;
        var url = ProjectService.PROJECT_URL + '/' + id + '/applications';
        return this.authHttp.put(url, JSON.stringify(applicationIds))
            .map(function (response) { return project_mapper_1.ProjectMapper.mapBackend(response.json()); })
            .catch(function (err) { return _this.uiState.addError(new error_info_1.ErrorInfo(error_type_1.ErrorType.PROJECT_SAVE_FAILED, http_util_1.HttpUtil.extractMessage(err))); });
    };
    ProjectService.prototype.addProjectApplication = function (id, applicationId) {
        var _this = this;
        return this.getProjectApplications(id)
            .map(function (applications) { return applications.map(function (app) { return app.id; }); })
            .map(function (appIds) { return appIds.concat(applicationId); })
            .switchMap(function (appIds) { return _this.updateProjectApplications(id, appIds); });
    };
    ProjectService.prototype.getProjectApplications = function (id) {
        var _this = this;
        var url = ProjectService.PROJECT_URL + '/' + id + '/applications';
        return this.authHttp.get(url)
            .map(function (response) { return response.json(); })
            .map(function (json) { return json.map(function (app) { return application_mapper_1.ApplicationMapper.mapBackend(app); }); })
            .catch(function (err) { return _this.uiState.addError(http_util_1.HttpUtil.extractMessage(err)); });
    };
    ProjectService.prototype.getChildProjects = function (id) {
        var url = [ProjectService.PROJECT_URL, id, ProjectService.CHILDREN].join('/');
        return this.getProjects(url);
    };
    ProjectService.prototype.getParentProjects = function (id) {
        var url = [ProjectService.PROJECT_URL, id, ProjectService.PARENTS].join('/');
        return this.getProjects(url);
    };
    ProjectService.prototype.updateParent = function (id, parentId) {
        var _this = this;
        var url = [ProjectService.PROJECT_URL, id, 'parentProject', parentId].join('/');
        return this.authHttp.put(url, '')
            .map(function (response) { return response.json(); })
            .map(function (project) { return project_mapper_1.ProjectMapper.mapBackend(project); })
            .catch(function (err) { return _this.uiState.addError(http_util_1.HttpUtil.extractMessage(err)); });
    };
    ProjectService.prototype.removeParent = function (ids) {
        var _this = this;
        var url = [ProjectService.PROJECT_URL, 'parent', 'remove'].join('/');
        return this.authHttp.put(url, JSON.stringify(ids))
            .map(function (response) { return http_util_1.HttpUtil.extractHttpResponse(response); })
            .catch(function (err) { return _this.uiState.addError(http_util_1.HttpUtil.extractMessage(err)); });
    };
    ProjectService.prototype.getProjects = function (url) {
        var _this = this;
        return this.authHttp.get(url)
            .map(function (response) { return response.json(); })
            .map(function (json) { return json.map(function (project) { return project_mapper_1.ProjectMapper.mapBackend(project); }); })
            .catch(function (err) { return _this.uiState.addError(new error_info_1.ErrorInfo(error_type_1.ErrorType.PROJECT_SEARCH_FAILED, http_util_1.HttpUtil.extractMessage(err))); });
    };
    return ProjectService;
}());
ProjectService.PROJECT_URL = '/api/projects';
ProjectService.SEARCH = '/search';
ProjectService.CHILDREN = 'children';
ProjectService.PARENTS = 'parents';
ProjectService = __decorate([
    core_1.Injectable()
], ProjectService);
exports.ProjectService = ProjectService;
