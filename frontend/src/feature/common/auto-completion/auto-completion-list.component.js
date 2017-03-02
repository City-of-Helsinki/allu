"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
var array_util_1 = require("../../../util/array-util");
var AutoCompletionListComponent = (function () {
    function AutoCompletionListComponent(renderer, viewContainerRef) {
        this.renderer = renderer;
        this.viewContainerRef = viewContainerRef;
        this.onSelection = new core_1.EventEmitter();
        this.sortedEntries = [];
        this.renderer.setElementClass(viewContainerRef.element.nativeElement, 'auto-completion-list', true);
    }
    AutoCompletionListComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.sortBy = this.sortBy || this.sortByField(this.nameField);
        this.entries
            .subscribe(function (entries) { return _this.sortedEntries = entries.sort(_this.sortBy); });
    };
    AutoCompletionListComponent.prototype.select = function (selection) {
        this.onSelection.emit(selection);
    };
    AutoCompletionListComponent.prototype.sortByField = function (fieldName) {
        return array_util_1.ArrayUtil.naturalSort(function (item) { return item[fieldName]; });
    };
    return AutoCompletionListComponent;
}());
__decorate([
    core_1.Input()
], AutoCompletionListComponent.prototype, "entries", void 0);
__decorate([
    core_1.Input()
], AutoCompletionListComponent.prototype, "idField", void 0);
__decorate([
    core_1.Input()
], AutoCompletionListComponent.prototype, "nameField", void 0);
__decorate([
    core_1.Input()
], AutoCompletionListComponent.prototype, "sortBy", void 0);
__decorate([
    core_1.Output()
], AutoCompletionListComponent.prototype, "onSelection", void 0);
AutoCompletionListComponent = __decorate([
    core_1.Component({
        selector: 'auto-completion-list',
        template: require('./auto-completion-list.component.html'),
        styles: [
            require('./auto-completion-list.component.scss')
        ]
    })
], AutoCompletionListComponent);
exports.AutoCompletionListComponent = AutoCompletionListComponent;
