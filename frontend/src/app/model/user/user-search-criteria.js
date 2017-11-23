"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var role_type_1 = require("./role-type");
var application_type_1 = require("../application/type/application-type");
var UserSearchCriteria = /** @class */ (function () {
    function UserSearchCriteria(roleType, applicationType, cityDistrictId) {
        this.roleType = roleType;
        this.applicationType = applicationType;
        this.cityDistrictId = cityDistrictId;
    }
    Object.defineProperty(UserSearchCriteria.prototype, "uiRoleType", {
        get: function () {
            return role_type_1.RoleType[this.roleType];
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(UserSearchCriteria.prototype, "uiApplicationType", {
        get: function () {
            return application_type_1.ApplicationType[this.applicationType];
        },
        enumerable: true,
        configurable: true
    });
    return UserSearchCriteria;
}());
exports.UserSearchCriteria = UserSearchCriteria;
