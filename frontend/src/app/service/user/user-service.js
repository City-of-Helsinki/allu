"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = require("@angular/core");
var user_mapper_1 = require("../mapper/user-mapper");
var http_util_1 = require("../../util/http.util");
var role_type_1 = require("../../model/user/role-type");
var translations_1 = require("../../util/translations");
var ACTIVE_USERS_URL = '/api/users/active';
var USERS_URL = '/api/users';
var USER_SEARCH_URL = '/api/users/search';
var USERS_BY_ROLE_URL = '/api/users/role/:roleType';
var USER_BY_USERNAME_URL = '/api/users/userName';
var CURRENT_USER_URL = '/api/users/current';
var UserService = /** @class */ (function () {
    function UserService(authHttp, errorHandler) {
        this.authHttp = authHttp;
        this.errorHandler = errorHandler;
    }
    UserService.prototype.getActiveUsers = function () {
        var _this = this;
        return this.authHttp.get(ACTIVE_USERS_URL)
            .map(function (response) { return response.json(); })
            .map(function (users) { return users.map(function (user) { return user_mapper_1.UserMapper.mapBackend(user); }); })
            .catch(function (err) { return _this.errorHandler.handle(http_util_1.HttpUtil.extractMessage(err)); });
    };
    UserService.prototype.search = function (searchCriteria) {
        var _this = this;
        return this.authHttp.post(USER_SEARCH_URL, user_mapper_1.UserMapper.mapSearchCriteria(searchCriteria))
            .map(function (response) { return response.json(); })
            .map(function (users) { return users.map(function (user) { return user_mapper_1.UserMapper.mapBackend(user); }); })
            .catch(function (error) { return _this.errorHandler.handle(error, translations_1.findTranslation('user.error.search')); });
    };
    UserService.prototype.getByRole = function (role) {
        var _this = this;
        var url = USERS_BY_ROLE_URL.replace(':roleType', role_type_1.RoleType[role]);
        return this.authHttp.get(url)
            .map(function (response) { return response.json(); })
            .map(function (users) { return users.map(function (user) { return user_mapper_1.UserMapper.mapBackend(user); }); })
            .catch(function (err) { return _this.errorHandler.handle(http_util_1.HttpUtil.extractMessage(err)); });
    };
    UserService.prototype.getAllUsers = function () {
        var _this = this;
        return this.authHttp.get(USERS_URL)
            .map(function (response) { return response.json(); })
            .map(function (users) { return users.map(function (user) { return user_mapper_1.UserMapper.mapBackend(user); }); })
            .catch(function (err) { return _this.errorHandler.handle(http_util_1.HttpUtil.extractMessage(err)); });
    };
    UserService.prototype.getUser = function (userName) {
        var _this = this;
        var url = USER_BY_USERNAME_URL + '/' + userName;
        return this.authHttp.get(url)
            .map(function (response) { return user_mapper_1.UserMapper.mapBackend(response.json()); })
            .catch(function (err) { return _this.errorHandler.handle(http_util_1.HttpUtil.extractMessage(err)); });
    };
    UserService.prototype.getCurrentUser = function () {
        var _this = this;
        return this.authHttp.get(CURRENT_USER_URL)
            .map(function (response) { return user_mapper_1.UserMapper.mapBackend(response.json()); })
            .catch(function (err) { return _this.errorHandler.handle(http_util_1.HttpUtil.extractMessage(err)); });
    };
    UserService.prototype.save = function (user) {
        if (user.id) {
            return this.update(user);
        }
        else {
            return this.create(user);
        }
    };
    UserService.prototype.create = function (user) {
        var _this = this;
        return this.authHttp.post(USERS_URL, JSON.stringify(user_mapper_1.UserMapper.mapFrontend(user)))
            .map(function (response) { return user_mapper_1.UserMapper.mapBackend(response.json()); })
            .catch(function (err) { return _this.errorHandler.handle(http_util_1.HttpUtil.extractMessage(err)); });
    };
    UserService.prototype.update = function (user) {
        var _this = this;
        return this.authHttp.put(USERS_URL, JSON.stringify(user_mapper_1.UserMapper.mapFrontend(user)))
            .map(function (response) { return user_mapper_1.UserMapper.mapBackend(response.json()); })
            .catch(function (err) { return _this.errorHandler.handle(http_util_1.HttpUtil.extractMessage(err)); });
    };
    UserService = __decorate([
        core_1.Injectable()
    ], UserService);
    return UserService;
}());
exports.UserService = UserService;
