"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var RoleType;
(function (RoleType) {
    /**
     * Allowed to create applications.
     */
    RoleType[RoleType["ROLE_CREATE_APPLICATION"] = 0] = "ROLE_CREATE_APPLICATION";
    /**
     * Allowed to process applications.
     */
    RoleType[RoleType["ROLE_PROCESS_APPLICATION"] = 1] = "ROLE_PROCESS_APPLICATION";
    /**
     * Allowed to make decisions.
     */
    RoleType[RoleType["ROLE_DECISION"] = 2] = "ROLE_DECISION";
    /**
     * Allowed to supervise.
     */
    RoleType[RoleType["ROLE_SUPERVISE"] = 3] = "ROLE_SUPERVISE";
    /**
     * Allowed to check invoices.
     */
    RoleType[RoleType["ROLE_INVOICING"] = 4] = "ROLE_INVOICING";
    /**
     * Allowed to login and view applications
     */
    RoleType[RoleType["ROLE_VIEW"] = 5] = "ROLE_VIEW";
    /**
     * Allowed to change system configuration.
     */
    RoleType[RoleType["ROLE_ADMIN"] = 6] = "ROLE_ADMIN";
})(RoleType = exports.RoleType || (exports.RoleType = {}));
