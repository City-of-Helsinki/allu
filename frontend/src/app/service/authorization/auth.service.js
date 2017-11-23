"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = require("@angular/core");
var http_1 = require("@angular/http");
var angular2_jwt_1 = require("angular2-jwt/angular2-jwt");
var LOGIN_URL = '/api/auth/login';
var OAUTH_URL = '/api/oauth2/';
var AuthService = /** @class */ (function () {
    function AuthService(http, currentUser) {
        this.http = http;
        this.currentUser = currentUser;
        this.jwtHelper = new angular2_jwt_1.JwtHelper();
        this.contentHeaders = new http_1.Headers();
        this.contentHeaders.append('Accept', 'application/json');
        this.contentHeaders.append('Content-Type', 'application/json');
    }
    AuthService.prototype.authenticated = function () {
        var jwt = localStorage.getItem('jwt');
        return !!jwt && !this.jwtHelper.isTokenExpired(jwt);
    };
    AuthService.prototype.login = function (username) {
        var _this = this;
        var body = JSON.stringify({ 'userName': username });
        return this.http.post(LOGIN_URL, body, { headers: this.contentHeaders })
            .switchMap(function (response) {
            _this.storeJwt(response.text());
            return _this.currentUser.user;
        });
    };
    AuthService.prototype.loginOAuth = function (code) {
        var _this = this;
        var searchParams = new http_1.URLSearchParams();
        searchParams.append('code', code);
        return this.http.get(OAUTH_URL, { headers: this.contentHeaders, search: searchParams })
            .switchMap(function (response) {
            _this.storeJwt(response.text());
            return _this.currentUser.user;
        });
    };
    AuthService.prototype.logout = function () {
        localStorage.removeItem('jwt');
        this.currentUser.clearUser();
    };
    Object.defineProperty(AuthService.prototype, "token", {
        get: function () {
            return localStorage.getItem('jwt');
        },
        enumerable: true,
        configurable: true
    });
    AuthService.prototype.storeJwt = function (token) {
        localStorage.setItem('jwt', token);
    };
    AuthService = __decorate([
        core_1.Injectable()
    ], AuthService);
    return AuthService;
}());
exports.AuthService = AuthService;
