"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = require("@angular/core");
var translations_1 = require("../../util/translations");
var ReplaySubject_1 = require("rxjs/ReplaySubject");
var CONFIG_URL = '/api/uiconfig';
var ConfigService = /** @class */ (function () {
    function ConfigService(authHttp, errorHandler) {
        this.authHttp = authHttp;
        this.errorHandler = errorHandler;
        this.configuration$ = new ReplaySubject_1.ReplaySubject();
        this.loadConfiguration();
    }
    ConfigService.prototype.getConfiguration = function () {
        return this.configuration$.asObservable().first();
    };
    ConfigService.prototype.loadConfiguration = function () {
        var _this = this;
        this.authHttp.get(CONFIG_URL)
            .map(function (response) { return response.json(); })
            .catch(function (error) { return _this.errorHandler.handle(error, translations_1.findTranslation('config.error.fetch')); })
            .subscribe(function (config) { return _this.configuration$.next(config); });
    };
    ConfigService = __decorate([
        core_1.Injectable()
    ], ConfigService);
    return ConfigService;
}());
exports.ConfigService = ConfigService;
