"use strict";
var time_util_1 = require("../../../util/time.util");
var Comment = (function () {
    function Comment(id, type, text, createTime, updateTime, user) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.user = user;
    }
    Comment.prototype.copy = function () {
        return new Comment(this.id, this.type, this.text, this.createTime, this.updateTime, this.user);
    };
    Object.defineProperty(Comment.prototype, "uiCreateTime", {
        get: function () {
            return time_util_1.TimeUtil.getUiDateTimeString(this.createTime);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Comment.prototype, "uiUpdateTime", {
        get: function () {
            return time_util_1.TimeUtil.getUiDateTimeString(this.updateTime);
        },
        enumerable: true,
        configurable: true
    });
    return Comment;
}());
exports.Comment = Comment;
