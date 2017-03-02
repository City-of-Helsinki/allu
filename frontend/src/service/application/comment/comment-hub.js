"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
require("../../../rxjs-extensions.ts");
var CommentHub = (function () {
    function CommentHub(commentService) {
        var _this = this;
        this.commentService = commentService;
        /**
         * Saves comment to given application
         */
        this.saveComment = function (applicationId, comment) { return _this.commentService.save(applicationId, comment); };
        /**
         * Removes comment with id
         */
        this.removeComment = function (id) { return _this.commentService.remove(id); };
        /**
         * Fetches all comments belonging to given application
         */
        this.getComments = function (applicationId) { return _this.commentService.getComments(applicationId); };
    }
    return CommentHub;
}());
CommentHub = __decorate([
    core_1.Injectable()
], CommentHub);
exports.CommentHub = CommentHub;
