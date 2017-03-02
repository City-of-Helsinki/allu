"use strict";
var User = (function () {
    function User(id, userName, realName, emailAddress, title, isActive, allowedApplicationTypes, assignedRoles) {
        this.id = id;
        this.userName = userName;
        this.realName = realName;
        this.emailAddress = emailAddress;
        this.title = title;
        this.isActive = isActive;
        this.allowedApplicationTypes = allowedApplicationTypes;
        this.assignedRoles = assignedRoles;
    }
    ;
    return User;
}());
exports.User = User;
