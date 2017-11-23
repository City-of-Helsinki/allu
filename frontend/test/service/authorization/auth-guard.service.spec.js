"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var auth_guard_service_1 = require("../../../src/service/authorization/auth-guard.service");
var auth_service_1 = require("../../../src/service/authorization/auth.service");
var config_service_1 = require("../../../src/service/config/config.service");
var testing_1 = require("@angular/core/testing");
var Observable_1 = require("rxjs/Observable");
var router_1 = require("@angular/router");
var createSpyObj = jasmine.createSpyObj;
var AuthServiceMock = /** @class */ (function () {
    function AuthServiceMock() {
    }
    AuthServiceMock.prototype.authenticated = function () {
        return true;
    };
    AuthServiceMock.prototype.loginOAuth = function (code) {
        return Observable_1.Observable.empty();
    };
    return AuthServiceMock;
}());
var ConfigServiceMock = /** @class */ (function () {
    function ConfigServiceMock() {
    }
    ConfigServiceMock.prototype.getConfiguration = function () {
        return Observable_1.Observable.empty();
    };
    return ConfigServiceMock;
}());
var routerStateSnapshot = createSpyObj('RouterStateSnapshot', []);
describe('AuthGuard', function () {
    var authGuard;
    var authService;
    var configService;
    beforeEach(function () {
        var tb = testing_1.TestBed.configureTestingModule({
            imports: [],
            providers: [
                { provide: auth_service_1.AuthService, useClass: AuthServiceMock },
                { provide: config_service_1.ConfigService, useClass: ConfigServiceMock },
                auth_guard_service_1.AuthGuard
            ]
        });
        authGuard = tb.get(auth_guard_service_1.AuthGuard);
        // authService = tb.get(AuthService) as AuthServiceMock;
        // configService = tb.get(ConfigService) as ConfigServiceMock;
    });
    it('allow route activation when already authenticated', function () {
        var authenticated = spyOn(authService, 'authenticated').and.returnValue(Observable_1.Observable.of(true));
        authGuard.canActivate(new router_1.ActivatedRouteSnapshot(), routerStateSnapshot)
            .subscribe(function (canActivate) { return expect(canActivate).toEqual(true); });
    });
});
