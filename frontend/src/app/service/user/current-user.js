"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = require("@angular/core");
var Observable_1 = require("rxjs/Observable");
var BehaviorSubject_1 = require("rxjs/BehaviorSubject");
var number_util_1 = require("../../util/number.util");
var CurrentUser = /** @class */ (function () {
    function CurrentUser(userService) {
        this.userService = userService;
        this.user$ = new BehaviorSubject_1.BehaviorSubject(undefined);
    }
    CurrentUser.prototype.clearUser = function () {
        this.user$.next(undefined);
    };
    Object.defineProperty(CurrentUser.prototype, "user", {
        get: function () {
            var _this = this;
            if (!this.user$.getValue()) {
                this.userService.getCurrentUser().subscribe(function (user) { return _this.user$.next(user); });
            }
            return this.user$.asObservable()
                .filter(function (u) { return !!u; })
                .first(); // Use first so clients observable automatically completes after logged user is returned
        },
        enumerable: true,
        configurable: true
    });
    CurrentUser.prototype.hasRole = function (roles) {
        return this.user
            .map(function (u) { return u.roles.reduce(function (prev, cur) { return prev || roles.some(function (role) { return role === cur; }); }, false); });
    };
    CurrentUser.prototype.hasApplicationType = function (types) {
        return this.user
            .map(function (u) { return u.allowedApplicationTypes.reduce(function (prev, cur) { return prev || types.some(function (type) { return type === cur; }); }, false); });
    };
    CurrentUser.prototype.isCurrentUser = function (id) {
        if (number_util_1.NumberUtil.isDefined(id)) {
            return this.user.map(function (user) { return user.id === id; });
        }
        else {
            return Observable_1.Observable.of(false);
        }
    };
    CurrentUser = __decorate([
        core_1.Injectable()
    ], CurrentUser);
    return CurrentUser;
}());
exports.CurrentUser = CurrentUser;
