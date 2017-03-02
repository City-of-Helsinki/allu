"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
var http_util_1 = require("../../../util/http.util");
var comment_mapper_1 = require("./comment-mapper");
var translations_1 = require("../../../util/translations");
var COMMENTS_URL = '/api/comments';
var COMMENTS_APP_URL = COMMENTS_URL + '/applications/:appId';
var CommentService = (function () {
    function CommentService(authHttp, errorHandler) {
        this.authHttp = authHttp;
        this.errorHandler = errorHandler;
    }
    CommentService.prototype.getComments = function (applicationId) {
        var _this = this;
        var url = COMMENTS_APP_URL.replace(':appId', String(applicationId));
        return this.authHttp.get(url)
            .map(function (response) { return response.json(); })
            .map(function (comments) { return comments.map(function (comment) { return comment_mapper_1.CommentMapper.mapBackend(comment); }); })
            .catch(function (error) { return _this.errorHandler.handle(error, translations_1.findTranslation('comment.error.fetch')); });
    };
    CommentService.prototype.save = function (applicationId, comment) {
        var _this = this;
        if (comment.id) {
            var url = COMMENTS_URL + '/' + comment.id;
            return this.authHttp.put(url, JSON.stringify(comment_mapper_1.CommentMapper.mapFrontend(comment)))
                .map(function (response) { return comment_mapper_1.CommentMapper.mapBackend(response.json()); })
                .catch(function (error) { return _this.errorHandler.handle(error, translations_1.findTranslation('comment.error.save')); });
        }
        else {
            var url = COMMENTS_APP_URL.replace(':appId', String(applicationId));
            return this.authHttp.post(url, JSON.stringify(comment_mapper_1.CommentMapper.mapFrontend(comment)))
                .map(function (response) { return comment_mapper_1.CommentMapper.mapBackend(response.json()); })
                .catch(function (error) { return _this.errorHandler.handle(error, translations_1.findTranslation('comment.error.save')); });
        }
    };
    CommentService.prototype.remove = function (id) {
        var _this = this;
        var url = COMMENTS_URL + '/' + id;
        return this.authHttp.delete(url)
            .map(function (response) { return http_util_1.HttpUtil.extractHttpResponse(response); })
            .catch(function (error) { return _this.errorHandler.handle(error, translations_1.findTranslation('comment.error.remove')); });
    };
    return CommentService;
}());
CommentService = __decorate([
    core_1.Injectable()
], CommentService);
exports.CommentService = CommentService;
