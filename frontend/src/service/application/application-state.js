"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
var Observable_1 = require("rxjs/Observable");
var application_1 = require("../../model/application/application");
var option_1 = require("../../util/option");
var Subject_1 = require("rxjs/Subject");
var BehaviorSubject_1 = require("rxjs/BehaviorSubject");
var http_response_1 = require("../../util/http-response");
var ApplicationState = (function () {
    function ApplicationState(router, applicationHub, projectHub, attachmentHub, commentHub) {
        this.router = router;
        this.applicationHub = applicationHub;
        this.projectHub = projectHub;
        this.attachmentHub = attachmentHub;
        this.commentHub = commentHub;
        this.application$ = new BehaviorSubject_1.BehaviorSubject(new application_1.Application());
        this._pendingAttachments = [];
        this.attachments$ = new BehaviorSubject_1.BehaviorSubject([]);
        this.comments$ = new BehaviorSubject_1.BehaviorSubject([]);
        this.tabChange$ = new Subject_1.Subject();
    }
    Object.defineProperty(ApplicationState.prototype, "application", {
        get: function () {
            return this.application$.getValue();
        },
        set: function (value) {
            this.application$.next(value);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ApplicationState.prototype, "applicationChanges", {
        get: function () {
            return this.application$.asObservable();
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ApplicationState.prototype, "tags", {
        get: function () {
            return this.application.applicationTags;
        },
        set: function (tags) {
            var app = this.application;
            app.applicationTags = tags;
            this.application = app;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ApplicationState.prototype, "attachments", {
        get: function () {
            return this.attachments$.asObservable();
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ApplicationState.prototype, "pendingAttachments", {
        get: function () {
            return this._pendingAttachments;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ApplicationState.prototype, "comments", {
        get: function () {
            return this.comments$.asObservable();
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ApplicationState.prototype, "tabChange", {
        get: function () {
            return this.tabChange$.asObservable();
        },
        enumerable: true,
        configurable: true
    });
    ApplicationState.prototype.addAttachment = function (attachment) {
        this._pendingAttachments.push(attachment);
    };
    ApplicationState.prototype.saveAttachment = function (applicationId, attachment) {
        return this.saveAttachments(applicationId, [attachment])
            .filter(function (attachments) { return attachments.length > 0; })
            .map(function (attachments) { return attachments[0]; });
    };
    ApplicationState.prototype.saveAttachments = function (applicationId, attachments) {
        var _this = this;
        var result = new Subject_1.Subject();
        this.attachmentHub.upload(applicationId, attachments)
            .subscribe(function (items) { return result.next(items); }, function (error) { return result.error(error); }, function () { return result.complete(); });
        return result.do(function (saved) { return _this.loadAttachments(applicationId).subscribe(); });
    };
    ApplicationState.prototype.removeAttachment = function (attachmentId, index) {
        var _this = this;
        if (attachmentId) {
            return this.attachmentHub.remove(this.application.id, attachmentId)
                .do(function (response) { return _this.loadAttachments(_this.application.id).subscribe(); });
        }
        else {
            option_1.Some(index).do(function (i) { return _this._pendingAttachments.splice(i, 1); });
            return Observable_1.Observable.of(new http_response_1.HttpResponse(http_response_1.HttpStatus.ACCEPTED));
        }
    };
    ApplicationState.prototype.loadAttachments = function (id) {
        var _this = this;
        return this.applicationHub.getApplication(id)
            .map(function (app) { return app.attachmentList; })
            .do(function (attachments) { return _this.attachments$.next(attachments); });
    };
    ApplicationState.prototype.saveComment = function (applicationId, comment) {
        var _this = this;
        return this.commentHub.saveComment(applicationId, comment)
            .do(function (c) { return _this.loadComments(_this.application.id).subscribe(); });
    };
    ApplicationState.prototype.removeComment = function (comment) {
        var _this = this;
        return this.commentHub.removeComment(comment.id)
            .do(function (c) { return _this.loadComments(_this.application.id).subscribe(); });
    };
    ApplicationState.prototype.loadComments = function (id) {
        var _this = this;
        return this.commentHub.getComments(id)
            .do(function (comments) { return _this.comments$.next(comments); });
    };
    ApplicationState.prototype.load = function (id) {
        var _this = this;
        return this.applicationHub.getApplication(id)
            .do(function (app) {
            _this.attachments$.next(app.attachmentList);
            _this.application = app;
        });
    };
    ApplicationState.prototype.notifyTabChange = function (tab) {
        this.tabChange$.next(tab);
    };
    ApplicationState.prototype.save = function (application) {
        var _this = this;
        return this.applicationHub.save(application)
            .switchMap(function (app) { return _this.savePendingAttachments(app); });
    };
    ApplicationState.prototype.savePendingAttachments = function (application) {
        var _this = this;
        var result = new Subject_1.Subject();
        this.saveAttachments(application.id, this._pendingAttachments)
            .subscribe(function (items) { }, function (error) { return result.error(error); }, function () {
            _this.saved(application).subscribe(function (app) { return result.next(app); });
            result.complete();
            _this._pendingAttachments = [];
        });
        return result;
    };
    ApplicationState.prototype.saved = function (application) {
        var _this = this;
        console.log('Application saved');
        this.application = application;
        // We had related project so navigate back to project page
        option_1.Some(this.relatedProject)
            .do(function (projectId) { return _this.projectHub.addProjectApplication(projectId, application.id).subscribe(function (project) {
            return _this.router.navigate(['/projects', project.id]);
        }); });
        this.router.navigate(['applications', application.id, 'summary']);
        return Observable_1.Observable.of(application);
    };
    return ApplicationState;
}());
ApplicationState = __decorate([
    core_1.Injectable()
], ApplicationState);
exports.ApplicationState = ApplicationState;
