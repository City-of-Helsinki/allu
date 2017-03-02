"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
var ConfirmDialogComponent = (function () {
    function ConfirmDialogComponent(dialogRef) {
        this.dialogRef = dialogRef;
        this.title = 'Haluatko varmasti suorittaa toiminnon';
        this.description = '';
        this.confirmText = 'Vahvista';
        this.cancelText = 'Peruuta';
    }
    ConfirmDialogComponent.prototype.cancel = function () {
        this.dialogRef.close(false);
    };
    ConfirmDialogComponent.prototype.confirm = function () {
        this.dialogRef.close(true);
    };
    return ConfirmDialogComponent;
}());
__decorate([
    core_1.Input()
], ConfirmDialogComponent.prototype, "title", void 0);
__decorate([
    core_1.Input()
], ConfirmDialogComponent.prototype, "description", void 0);
__decorate([
    core_1.Input()
], ConfirmDialogComponent.prototype, "confirmText", void 0);
__decorate([
    core_1.Input()
], ConfirmDialogComponent.prototype, "cancelText", void 0);
ConfirmDialogComponent = __decorate([
    core_1.Component({
        selector: 'confirm-dialog',
        template: require('./confirm-dialog.component.html'),
        styles: [
            require('./confirm-dialog.component.scss')
        ]
    })
], ConfirmDialogComponent);
exports.ConfirmDialogComponent = ConfirmDialogComponent;
