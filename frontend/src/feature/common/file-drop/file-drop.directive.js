"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
var FileDropDirective = (function () {
    function FileDropDirective(element) {
        this.onFileDrop = new core_1.EventEmitter();
        this.onFileOver = new core_1.EventEmitter();
        this.element = element;
    }
    FileDropDirective.prototype.onDrop = function (event) {
        this.preventDefault(event);
        var files = this.getFiles(event);
        this.onFileDrop.emit(files);
        this.onFileOver.emit(false);
    };
    FileDropDirective.prototype.onDragOver = function (event) {
        this.preventDefault(event);
    };
    FileDropDirective.prototype.onDragEnter = function (event) {
        this.preventDefault(event);
        this.onFileOver.emit(true);
    };
    FileDropDirective.prototype.onDragLeave = function (event) {
        this.preventDefault(event);
        this.onFileOver.emit(false);
    };
    FileDropDirective.prototype.preventDefault = function (event) {
        event.preventDefault();
        event.stopPropagation();
    };
    FileDropDirective.prototype.getFiles = function (event) {
        return event.dataTransfer ? event.dataTransfer.files : [];
    };
    return FileDropDirective;
}());
__decorate([
    core_1.Output()
], FileDropDirective.prototype, "onFileDrop", void 0);
__decorate([
    core_1.Output()
], FileDropDirective.prototype, "onFileOver", void 0);
__decorate([
    core_1.HostListener('drop', ['$event'])
], FileDropDirective.prototype, "onDrop", null);
__decorate([
    core_1.HostListener('dragover', ['$event'])
], FileDropDirective.prototype, "onDragOver", null);
__decorate([
    core_1.HostListener('dragenter', ['$event'])
], FileDropDirective.prototype, "onDragEnter", null);
__decorate([
    core_1.HostListener('dragleave', ['$event'])
], FileDropDirective.prototype, "onDragLeave", null);
FileDropDirective = __decorate([
    core_1.Directive({ selector: '[fileDrop]' })
], FileDropDirective);
exports.FileDropDirective = FileDropDirective;
