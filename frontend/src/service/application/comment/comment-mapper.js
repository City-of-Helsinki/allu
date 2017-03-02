"use strict";
var comment_1 = require("../../../model/application/comment/comment");
var time_util_1 = require("../../../util/time.util");
var user_mapper_1 = require("../../mapper/user-mapper");
var option_1 = require("../../../util/option");
var CommentMapper = (function () {
    function CommentMapper() {
    }
    CommentMapper.mapBackendList = function (comments) {
        return (comments)
            ? comments.map(function (comment) { return CommentMapper.mapBackend(comment); })
            : [];
    };
    CommentMapper.mapBackend = function (backendComment) {
        return new comment_1.Comment(backendComment.id, backendComment.type, backendComment.text, time_util_1.TimeUtil.dateFromBackend(backendComment.createTime), time_util_1.TimeUtil.dateFromBackend(backendComment.updateTime), user_mapper_1.UserMapper.mapBackend(backendComment.user));
    };
    CommentMapper.mapFrontend = function (comment) {
        return (comment) ?
            {
                id: comment.id,
                type: comment.type,
                text: comment.text,
                createTime: option_1.Some(comment.createTime).map(function (createTime) { return createTime.toISOString(); }).orElse(undefined),
                updateTime: option_1.Some(comment.updateTime).map(function (updateTime) { return updateTime.toISOString(); }).orElse(undefined),
                user: user_mapper_1.UserMapper.mapFrontend(comment.user)
            }
            : undefined;
    };
    return CommentMapper;
}());
exports.CommentMapper = CommentMapper;
