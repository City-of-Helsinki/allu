"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
var validatedAlways = [
    'startBeforeEnd'
];
var FieldErrorComponent = (function () {
    function FieldErrorComponent() {
        var _this = this;
        this.shouldValidate = function (formField) { return _this.validateAlways() || _this.fieldChanged(formField); };
        this.fieldChanged = function (formField) { return formField.touched || formField.dirty; };
        this.validateAlways = function () { return validatedAlways.indexOf(_this.hasError) >= 0; };
    }
    FieldErrorComponent.prototype.showError = function () {
        if (this.form) {
            var formField = this.form.get(this.field);
            return this.shouldValidate(formField) && formField.hasError(this.hasError);
        }
        else {
            return false;
        }
    };
    return FieldErrorComponent;
}());
__decorate([
    core_1.Input()
], FieldErrorComponent.prototype, "form", void 0);
__decorate([
    core_1.Input()
], FieldErrorComponent.prototype, "field", void 0);
__decorate([
    core_1.Input()
], FieldErrorComponent.prototype, "hasError", void 0);
FieldErrorComponent = __decorate([
    core_1.Component({
        selector: 'field-error',
        template: '<span *ngIf="showError()"><ng-content></ng-content></span>',
        styles: [require('./field-error.component.scss')]
    })
], FieldErrorComponent);
exports.FieldErrorComponent = FieldErrorComponent;
