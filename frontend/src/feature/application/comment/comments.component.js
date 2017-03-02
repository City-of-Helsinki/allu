"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
var comment_1 = require("../../../model/application/comment/comment");
var translations_1 = require("../../../util/translations");
var time_util_1 = require("../../../util/time.util");
var notification_service_1 = require("../../../service/notification/notification.service");
var CommentsComponent = (function () {
    function CommentsComponent(applicationState) {
        this.applicationState = applicationState;
        this.comments = [];
    }
    CommentsComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.application = this.applicationState.application;
        this.applicationState.comments
            .map(function (comments) { return comments.sort(function (l, r) { return time_util_1.TimeUtil.compareTo(r.createTime, l.createTime); }); }) // sort latest first
            .subscribe(function (comments) { return _this.comments = comments; }, function (err) { return notification_service_1.NotificationService.error(err); });
    };
    CommentsComponent.prototype.addNew = function () {
        this.comments = [new comment_1.Comment].concat(this.comments);
    };
    CommentsComponent.prototype.save = function (index, comment) {
        var _this = this;
        this.applicationState.saveComment(this.application.id, comment).subscribe(function (c) {
            notification_service_1.NotificationService.message(_this.translateType(c.type) + ' tallennettu');
            _this.comments.splice(index, 1, c);
        }, function (error) { return notification_service_1.NotificationService.errorMessage(_this.translateType(comment.type) + ' tallentaminen epäonnistui'); });
    };
    CommentsComponent.prototype.remove = function (index, comment) {
        var _this = this;
        if (comment.id === undefined) {
            this.comments.splice(index, 1);
        }
        else {
            this.applicationState.removeComment(comment)
                .subscribe(function (status) {
                notification_service_1.NotificationService.message(_this.translateType(comment.type) + ' poistettu');
                _this.comments.splice(index, 1);
            }, function (error) { return notification_service_1.NotificationService.errorMessage(_this.translateType(comment.type) + ' poistaminen epäonnistui'); });
        }
    };
    CommentsComponent.prototype.translateType = function (commentType) {
        return translations_1.findTranslation(['comment.type', commentType]);
    };
    return CommentsComponent;
}());
CommentsComponent = __decorate([
    core_1.Component({
        selector: 'comments',
        template: require('./comments.component.html'),
        styles: []
    })
], CommentsComponent);
exports.CommentsComponent = CommentsComponent;
