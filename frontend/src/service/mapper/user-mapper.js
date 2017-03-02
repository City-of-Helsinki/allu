"use strict";
var user_1 = require("../../model/common/user");
var UserMapper = (function () {
    function UserMapper() {
    }
    UserMapper.mapBackend = function (backendUser) {
        return (backendUser) ?
            new user_1.User(backendUser.id, backendUser.userName, backendUser.realName, backendUser.emailAddress, backendUser.title, backendUser.active, backendUser.allowedApplicationTypes, backendUser.assignedRoles) : undefined;
    };
    UserMapper.mapFrontend = function (user) {
        return (user) ?
            {
                id: user.id,
                userName: user.userName,
                realName: user.realName,
                emailAddress: user.emailAddress,
                title: user.title,
                active: user.isActive,
                allowedApplicationTypes: user.allowedApplicationTypes,
                assignedRoles: user.assignedRoles
            } : undefined;
    };
    return UserMapper;
}());
exports.UserMapper = UserMapper;
