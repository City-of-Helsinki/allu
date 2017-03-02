"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
var FileSelectDirective = (function () {
    function FileSelectDirective(element) {
        this.attachmentsSelected = new core_1.EventEmitter();
        this.element = element;
    }
    FileSelectDirective.prototype.onChange = function () {
        var files = this.element.nativeElement.files;
        var fileArray = [];
        for (var i = 0; i < files.length; ++i) {
            fileArray.push(files[i]);
        }
        this.attachmentsSelected.emit(fileArray);
    };
    return FileSelectDirective;
}());
__decorate([
    core_1.Output()
], FileSelectDirective.prototype, "attachmentsSelected", void 0);
__decorate([
    core_1.HostListener('change')
], FileSelectDirective.prototype, "onChange", null);
FileSelectDirective = __decorate([
    core_1.Directive({ selector: '[file-select]' })
], FileSelectDirective);
exports.FileSelectDirective = FileSelectDirective;
