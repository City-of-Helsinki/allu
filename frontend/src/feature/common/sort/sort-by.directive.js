"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
var sort_1 = require("../../../model/common/sort");
var SortByDirective = (function () {
    function SortByDirective(renderer, viewContainerRef) {
        this.renderer = renderer;
        this.viewContainerRef = viewContainerRef;
        this.onSortChange = new core_1.EventEmitter();
        this.tableHeaderEl = viewContainerRef.element.nativeElement;
        this.tableHeaderEl.classList.add('clickable');
        // Add icon
        this.iconEl = this.renderer.createElement(this.tableHeaderEl, 'i');
        this.iconEl.className = 'material-icons icon-middle';
        this.iconEl.innerHTML = '';
    }
    SortByDirective.prototype.ngOnInit = function () {
        this.localSort = this.localSort || new sort_1.Sort(undefined, undefined);
        this.tableHeaderEl.appendChild(this.iconEl);
    };
    SortByDirective.prototype.onClick = function (event) {
        this.changeSort();
    };
    Object.defineProperty(SortByDirective.prototype, "currentSort", {
        set: function (sort) {
            this.localSort = sort;
            this.updateIcon();
        },
        enumerable: true,
        configurable: true
    });
    SortByDirective.prototype.changeSort = function () {
        var unsorted = this.localSort.field !== this.sortBy || this.localSort.direction === undefined;
        if (unsorted) {
            this.localSort = new sort_1.Sort(this.sortBy, sort_1.Direction.DESC);
        }
        else if (this.localSort.direction === sort_1.Direction.DESC) {
            this.localSort = new sort_1.Sort(this.localSort.field, sort_1.Direction.ASC);
        }
        else {
            this.localSort = new sort_1.Sort(this.sortBy, undefined);
        }
        this.onSortChange.emit(this.localSort);
        this.updateIcon();
    };
    SortByDirective.prototype.updateIcon = function () {
        this.iconEl.innerHTML = this.iconForField();
    };
    SortByDirective.prototype.iconForField = function () {
        return this.sortBy === this.localSort.field ? this.localSort.icon() : '';
    };
    return SortByDirective;
}());
__decorate([
    core_1.Input()
], SortByDirective.prototype, "sortBy", void 0);
__decorate([
    core_1.Output()
], SortByDirective.prototype, "onSortChange", void 0);
__decorate([
    core_1.HostListener('click', ['$event'])
], SortByDirective.prototype, "onClick", null);
__decorate([
    core_1.Input()
], SortByDirective.prototype, "currentSort", null);
SortByDirective = __decorate([
    core_1.Directive({
        selector: 'th[sortBy]'
    })
], SortByDirective);
exports.SortByDirective = SortByDirective;
