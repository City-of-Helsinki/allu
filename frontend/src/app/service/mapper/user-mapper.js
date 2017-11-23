"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var user_1 = require("../../model/user/user");
var time_util_1 = require("../../util/time.util");
var UserMapper = /** @class */ (function () {
    function UserMapper() {
    }
    UserMapper.mapBackend = function (backendUser) {
        return (backendUser) ?
            new user_1.User(backendUser.id, backendUser.userName, backendUser.realName, backendUser.emailAddress, backendUser.title, backendUser.active, time_util_1.TimeUtil.dateFromBackend(backendUser.lastLogin), backendUser.allowedApplicationTypes, backendUser.assignedRoles, backendUser.cityDistrictIds) : undefined;
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
                lastLogin: time_util_1.TimeUtil.dateToBackend(user.lastLogin),
                allowedApplicationTypes: user.allowedApplicationTypes,
                assignedRoles: user.assignedRoles,
                cityDistrictIds: user.cityDistrictIds
            } : undefined;
    };
    UserMapper.mapSearchCriteria = function (searchCriteria) {
        return {
            roleType: searchCriteria.uiRoleType,
            applicationType: searchCriteria.uiApplicationType,
            cityDistrictId: searchCriteria.cityDistrictId
        };
    };
    return UserMapper;
}());
exports.UserMapper = UserMapper;
