"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
var BehaviorSubject_1 = require("rxjs/BehaviorSubject");
var Subject_1 = require("rxjs/Subject");
var Observable_1 = require("rxjs/Observable");
var ui_state_ts_1 = require("./ui-state.ts");
require("../../rxjs-extensions.ts");
var angular2_materialize_1 = require("angular2-materialize");
var error_type_1 = require("./error-type");
/**
 * Class for handling UIState changes and notify
 * listening components about them.
 *
 * All changes create a new state into ui-state stream.
 */
var UIStateHub = (function () {
    function UIStateHub() {
        var _this = this;
        this.uiState$ = new BehaviorSubject_1.BehaviorSubject(new ui_state_ts_1.UIState());
        this.displayedMessage$ = new Subject_1.Subject();
        /**
         * Observable which conveys latest state of UI.
         */
        this.uiState = function () { return _this.uiState$.asObservable(); };
        this.displayedMessage$.asObservable()
            .debounceTime(50)
            .distinctUntilChanged()
            .subscribe(function (message) { return angular2_materialize_1.toast(message, 4000); });
    }
    /**
     * For adding a notification message
     */
    UIStateHub.prototype.addMessage = function (message) {
        var currentState = this.uiState$.getValue();
        this.uiState$.next(new ui_state_ts_1.UIState(message, currentState.error));
        return Observable_1.Observable.empty();
    };
    /**
     * For clearing current notification message
     */
    UIStateHub.prototype.clearMessage = function () {
        var currentState = this.uiState$.getValue();
        this.uiState$.next(new ui_state_ts_1.UIState(undefined, currentState.error));
    };
    /**
     * For adding an error message
     */
    UIStateHub.prototype.addError = function (error) {
        var currentState = this.uiState$.getValue();
        this.uiState$.next(new ui_state_ts_1.UIState(currentState.message, error));
        this.displayedMessage$.next(error_type_1.message(error.type));
        return Observable_1.Observable.empty();
    };
    /**
     * For clearing current error message
     */
    UIStateHub.prototype.clearError = function () {
        var currentState = this.uiState$.getValue();
        this.uiState$.next(new ui_state_ts_1.UIState(currentState.message, undefined));
    };
    UIStateHub.prototype.clear = function () {
        this.uiState$.next(new ui_state_ts_1.UIState());
    };
    return UIStateHub;
}());
UIStateHub = __decorate([
    core_1.Injectable()
], UIStateHub);
exports.UIStateHub = UIStateHub;
