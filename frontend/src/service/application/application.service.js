"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
var application_mapper_1 = require("./../mapper/application-mapper");
var structure_meta_mapper_1 = require("./../mapper/structure-meta-mapper");
var application_location_query_mapper_1 = require("./../mapper/application-location-query-mapper");
var http_util_1 = require("../../util/http.util");
var application_status_1 = require("../../model/application/application-status");
var error_info_1 = require("./../ui-state/error-info");
var error_type_1 = require("../ui-state/error-type");
var query_parameters_mapper_1 = require("../mapper/query-parameters-mapper");
var default_text_1 = require("../../model/application/cable-report/default-text");
var ApplicationService = (function () {
    function ApplicationService(authHttp, uiState) {
        this.authHttp = authHttp;
        this.uiState = uiState;
        this.statusToUrl = new Map();
        this.statusToUrl.set(application_status_1.ApplicationStatus.CANCELLED, '/status/cancelled');
        this.statusToUrl.set(application_status_1.ApplicationStatus.PENDING, '/status/pending');
        this.statusToUrl.set(application_status_1.ApplicationStatus.HANDLING, '/status/handling');
        this.statusToUrl.set(application_status_1.ApplicationStatus.DECISIONMAKING, '/status/decisionmaking');
        this.statusToUrl.set(application_status_1.ApplicationStatus.DECISION, '/status/decision');
        this.statusToUrl.set(application_status_1.ApplicationStatus.REJECTED, '/status/rejected');
        this.statusToUrl.set(application_status_1.ApplicationStatus.RETURNED_TO_PREPARATION, '/status/toPreparation');
        this.statusToUrl.set(application_status_1.ApplicationStatus.FINISHED, '/status/finished');
    }
    ApplicationService.prototype.getApplication = function (id) {
        var _this = this;
        return this.authHttp.get(ApplicationService.APPLICATIONS_URL + '/' + id)
            .map(function (response) { return response.json(); })
            .map(function (app) { return application_mapper_1.ApplicationMapper.mapBackend(app); })
            .catch(function (err) { return _this.uiState.addError(http_util_1.HttpUtil.extractMessage(err)); });
    };
    ApplicationService.prototype.getApplicationsByLocation = function (query) {
        var _this = this;
        var searchUrl = ApplicationService.APPLICATIONS_URL + ApplicationService.SEARCH_LOCATION;
        return this.authHttp.post(searchUrl, JSON.stringify(application_location_query_mapper_1.ApplicationLocationQueryMapper.mapFrontend(query)))
            .map(function (response) { return response.json(); })
            .map(function (json) { return json.map(function (app) { return application_mapper_1.ApplicationMapper.mapBackend(app); }); })
            .catch(function (err) { return _this.uiState.addError(new error_info_1.ErrorInfo(error_type_1.ErrorType.APPLICATION_SEARCH_FAILED, http_util_1.HttpUtil.extractMessage(err))); });
    };
    ApplicationService.prototype.searchApplications = function (searchQuery) {
        var _this = this;
        var searchUrl = ApplicationService.APPLICATIONS_URL + ApplicationService.SEARCH;
        return this.authHttp.post(searchUrl, JSON.stringify(query_parameters_mapper_1.QueryParametersMapper.mapApplicationQueryFrontend(searchQuery)))
            .map(function (response) { return response.json(); })
            .map(function (json) { return json.map(function (app) { return application_mapper_1.ApplicationMapper.mapBackend(app); }); })
            .catch(function (err) { return _this.uiState.addError(new error_info_1.ErrorInfo(error_type_1.ErrorType.APPLICATION_SEARCH_FAILED, http_util_1.HttpUtil.extractMessage(err))); });
    };
    ApplicationService.prototype.saveApplication = function (application) {
        var _this = this;
        if (application.id) {
            var url = ApplicationService.APPLICATIONS_URL + '/' + application.id;
            return this.authHttp.put(url, JSON.stringify(application_mapper_1.ApplicationMapper.mapFrontend(application)))
                .map(function (response) { return application_mapper_1.ApplicationMapper.mapBackend(response.json()); })
                .catch(function (err) { return _this.uiState.addError(new error_info_1.ErrorInfo(error_type_1.ErrorType.APPLICATION_SAVE_FAILED, http_util_1.HttpUtil.extractMessage(err))); });
        }
        else {
            return this.authHttp.post(ApplicationService.APPLICATIONS_URL, JSON.stringify(application_mapper_1.ApplicationMapper.mapFrontend(application)))
                .map(function (response) { return application_mapper_1.ApplicationMapper.mapBackend(response.json()); })
                .catch(function (err) { return _this.uiState.addError(new error_info_1.ErrorInfo(error_type_1.ErrorType.APPLICATION_SAVE_FAILED, http_util_1.HttpUtil.extractMessage(err))); });
        }
    };
    ApplicationService.prototype.loadMetadata = function (applicationType) {
        var _this = this;
        return this.authHttp.get(ApplicationService.METADATA_URL + '/' + applicationType)
            .map(function (response) { return structure_meta_mapper_1.StructureMetaMapper.mapBackend(response.json()); })
            .catch(function (err) { return _this.uiState.addError(http_util_1.HttpUtil.extractMessage(err)); });
    };
    ApplicationService.prototype.applicationStatusChange = function (statusChange) {
        var _this = this;
        var url = ApplicationService.APPLICATIONS_URL + '/' + statusChange.id + this.statusToUrl.get(statusChange.status);
        return this.authHttp.put(url, JSON.stringify(application_mapper_1.ApplicationMapper.mapComment(statusChange.comment)))
            .map(function (response) { return application_mapper_1.ApplicationMapper.mapBackend(response.json()); })
            .catch(function (err) { return _this.uiState.addError(new error_info_1.ErrorInfo(error_type_1.ErrorType.APPLICATION_STATUS_CHANGE_FAILED)); });
    };
    ApplicationService.prototype.applicationHandlerChange = function (handler, applicationIds) {
        var _this = this;
        var url = ApplicationService.APPLICATIONS_URL + '/handler/' + handler;
        return this.authHttp.put(url, JSON.stringify(applicationIds))
            .catch(function (err) { return _this.uiState.addError(new error_info_1.ErrorInfo(error_type_1.ErrorType.APPLICATION_HANDLER_CHANGE_FAILED)); });
    };
    ApplicationService.prototype.applicationHandlerRemove = function (applicationIds) {
        var _this = this;
        var url = ApplicationService.APPLICATIONS_URL + '/handler/remove';
        return this.authHttp.put(url, JSON.stringify(applicationIds))
            .catch(function (err) { return _this.uiState.addError(new error_info_1.ErrorInfo(error_type_1.ErrorType.APPLICATION_HANDLER_CHANGE_FAILED)); });
    };
    ApplicationService.prototype.loadDefaultTexts = function () {
        var _this = this;
        return this.authHttp.get(ApplicationService.DEFAULT_TEXTS_URL)
            .map(function (response) { return response.json(); })
            .map(function (texts) { return texts.map(function (text) { return default_text_1.DefaultText.mapBackend(text); }); })
            .catch(function (err) { return _this.uiState.addError(http_util_1.HttpUtil.extractMessage(err)); });
    };
    ApplicationService.prototype.saveDefaultText = function (text) {
        var _this = this;
        if (text.id) {
            var url = ApplicationService.DEFAULT_TEXTS_URL + '/' + text.id;
            return this.authHttp.put(url, JSON.stringify(default_text_1.DefaultText.mapFrontend(text)))
                .map(function (response) { return default_text_1.DefaultText.mapBackend(response.json()); })
                .catch(function (err) { return _this.uiState.addError(new error_info_1.ErrorInfo(error_type_1.ErrorType.DEFAULT_TEXT_SAVE_FAILED, http_util_1.HttpUtil.extractMessage(err))); });
        }
        else {
            return this.authHttp.post(ApplicationService.DEFAULT_TEXTS_URL, JSON.stringify(default_text_1.DefaultText.mapFrontend(text)))
                .map(function (response) { return default_text_1.DefaultText.mapBackend(response.json()); })
                .catch(function (err) { return _this.uiState.addError(new error_info_1.ErrorInfo(error_type_1.ErrorType.DEFAULT_TEXT_SAVE_FAILED, http_util_1.HttpUtil.extractMessage(err))); });
        }
    };
    ApplicationService.prototype.removeDefaultText = function (id) {
        var _this = this;
        var url = ApplicationService.DEFAULT_TEXTS_URL + '/' + id;
        return this.authHttp.delete(url)
            .map(function (response) { return http_util_1.HttpUtil.extractHttpResponse(response); })
            .catch(function (err) { return _this.uiState.addError(new error_info_1.ErrorInfo(error_type_1.ErrorType.DEFAULT_TEXT_SAVE_FAILED, http_util_1.HttpUtil.extractMessage(err))); });
    };
    return ApplicationService;
}());
ApplicationService.APPLICATIONS_URL = '/api/applications';
ApplicationService.SEARCH = '/search';
ApplicationService.SEARCH_LOCATION = '/search_location';
ApplicationService.METADATA_URL = '/api/meta';
ApplicationService.DEFAULT_TEXTS_URL = ApplicationService.APPLICATIONS_URL + '/cable-info/texts';
ApplicationService = __decorate([
    core_1.Injectable()
], ApplicationService);
exports.ApplicationService = ApplicationService;
