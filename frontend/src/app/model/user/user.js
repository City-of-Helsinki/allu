"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var time_util_1 = require("../../util/time.util");
var User = /** @class */ (function () {
    function User(id, userName, realName, emailAddress, title, isActive, lastLogin, allowedApplicationTypes, assignedRoles, cityDistrictIds) {
        if (allowedApplicationTypes === void 0) { allowedApplicationTypes = []; }
        if (assignedRoles === void 0) { assignedRoles = []; }
        if (cityDistrictIds === void 0) { cityDistrictIds = []; }
        this.id = id;
        this.userName = userName;
        this.realName = realName;
        this.emailAddress = emailAddress;
        this.title = title;
        this.isActive = isActive;
        this.lastLogin = lastLogin;
        this.allowedApplicationTypes = allowedApplicationTypes;
        this.assignedRoles = assignedRoles;
        this.cityDistrictIds = cityDistrictIds;
    }
    ;
    User.prototype.hasRole = function (role) {
        return this.assignedRoles.indexOf(role) >= 0;
    };
    Object.defineProperty(User.prototype, "isAdmin", {
        get: function () {
            return this.hasRole('ROLE_ADMIN');
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(User.prototype, "roles", {
        get: function () {
            return this.assignedRoles;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(User.prototype, "uiLastLogin", {
        get: function () {
            return time_util_1.TimeUtil.getUiDateTimeString(this.lastLogin);
        },
        enumerable: true,
        configurable: true
    });
    return User;
}());
exports.User = User;
