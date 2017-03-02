"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
var Subject_1 = require("rxjs/Subject");
require("../../rxjs-extensions.ts");
var ApplicationHub = (function () {
    function ApplicationHub(applicationService) {
        var _this = this;
        this.applicationService = applicationService;
        this.metaData$ = new Subject_1.Subject();
        /**
         * Fetches single application
         */
        this.getApplication = function (id) { return _this.applicationService.getApplication(id); };
        /**
         * Fetches applications based on given search query
         */
        this.searchApplications = function (searchQuery) { return _this.applicationService.searchApplications(searchQuery); };
        /**
         * Loads metadata for given application type
         */
        this.loadMetaData = function (applicationType) { return _this.applicationService.loadMetadata(applicationType)
            .do(function (meta) { return _this.metaData$.next(meta); }); };
        this.metaData = function () { return _this.metaData$.asObservable(); };
        /**
         * Saves given application (new / update) and returns saved application
         */
        this.save = function (application) { return _this.applicationService.saveApplication(application); };
        /**
         * Changes applications status according to statusChange.
         * Returns updated application.
         */
        this.changeStatus = function (statusChange) { return _this.applicationService.applicationStatusChange(statusChange); };
        /**
         * Changes handler of given applications. Does not return anytyhing. Use Observable's subscribe complete.
         */
        this.changeHandler = function (handler, applicationIds) { return _this.applicationService.applicationHandlerChange(handler, applicationIds); };
        /**
         * Removes handler of given applications. Does not return anytyhing. Use Observable's subscribe complete.
         */
        this.removeHandler = function (applicationIds) { return _this.applicationService.applicationHandlerRemove(applicationIds); };
        this.loadDefaultTexts = function () { return _this.applicationService.loadDefaultTexts(); };
        this.saveDefaultText = function (text) { return _this.applicationService.saveDefaultText(text); };
        this.removeDefaultText = function (id) { return _this.applicationService.removeDefaultText(id); };
    }
    return ApplicationHub;
}());
ApplicationHub = __decorate([
    core_1.Injectable()
], ApplicationHub);
exports.ApplicationHub = ApplicationHub;
