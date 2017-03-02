"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
var Subject_1 = require("rxjs/Subject");
var auto_completion_list_component_1 = require("./auto-completion-list.component");
var option_1 = require("../../../util/option");
var AutoCompletionDirective = (function () {
    function AutoCompletionDirective(resolver, renderer, viewContainerRef) {
        var _this = this;
        this.resolver = resolver;
        this.renderer = renderer;
        this.viewContainerRef = viewContainerRef;
        // Input for min length of search term which triggers search
        this.minTermLength = 3;
        // Name of the field which is used as id
        this.idField = 'id';
        // Name of the field which is shown in the dropdown
        this.nameField = 'name';
        // Event for notifying search changes
        this.onSearchChange = new core_1.EventEmitter();
        // Event for notifying item was selected
        this.onSelection = new core_1.EventEmitter();
        this.searchTerm = new Subject_1.Subject();
        this.hideDropdown = function (event) {
            option_1.Some(_this.dropdownEl).do(function (el) { return el.style.display = 'none'; });
        };
        this.inputEl = viewContainerRef.element.nativeElement;
    }
    AutoCompletionDirective.prototype.ngOnInit = function () {
        var _this = this;
        this.autocompletion
            .filter(function (results) { return results.length > 0; })
            .subscribe(function (searchResults) { return _this.showDropdown(); });
        this.searchTerm
            .filter(function (term) { return term && term.length >= _this.minTermLength; })
            .debounceTime(300)
            .distinctUntilChanged()
            .subscribe(function (term) { return _this.onSearchChange.emit(term); });
        // disable default autcomplete from parent input
        this.inputEl.autocomplete = 'off';
        this.initDropdown();
    };
    AutoCompletionDirective.prototype.ngOnDestroy = function () {
        this.clickListener();
    };
    AutoCompletionDirective.prototype.onKeyUp = function (event) {
        this.searchTerm.next(event.target.value);
    };
    AutoCompletionDirective.prototype.showDropdown = function () {
        this.dropdownEl = this.listComponentRef.location.nativeElement;
        this.dropdownEl.style.display = 'inline-block';
    };
    AutoCompletionDirective.prototype.initDropdown = function () {
        var _this = this;
        var factory = this.resolver.resolveComponentFactory(auto_completion_list_component_1.AutoCompletionListComponent);
        this.listComponentRef = this.viewContainerRef.createComponent(factory);
        var component = this.listComponentRef.instance;
        component.entries = this.autocompletion;
        component.idField = this.idField;
        component.nameField = this.nameField;
        component.sortBy = this.sortBy;
        component.onSelection.subscribe(function (selection) {
            var name = selection[_this.nameField] || selection;
            _this.inputEl.value = name;
            _this.onSelection.emit(selection);
        });
        // when somewhere else clicked, hide this autocomplete
        this.clickListener = this.renderer.listenGlobal('document', 'click', function (event) { return _this.hideDropdown(event); });
        this.hideDropdown();
    };
    return AutoCompletionDirective;
}());
__decorate([
    core_1.Input()
], AutoCompletionDirective.prototype, "autocompletion", void 0);
__decorate([
    core_1.Input()
], AutoCompletionDirective.prototype, "minTermLength", void 0);
__decorate([
    core_1.Input()
], AutoCompletionDirective.prototype, "idField", void 0);
__decorate([
    core_1.Input()
], AutoCompletionDirective.prototype, "nameField", void 0);
__decorate([
    core_1.Input()
], AutoCompletionDirective.prototype, "sortBy", void 0);
__decorate([
    core_1.Output()
], AutoCompletionDirective.prototype, "onSearchChange", void 0);
__decorate([
    core_1.Output()
], AutoCompletionDirective.prototype, "onSelection", void 0);
__decorate([
    core_1.HostListener('keyup', ['$event'])
], AutoCompletionDirective.prototype, "onKeyUp", null);
AutoCompletionDirective = __decorate([
    core_1.Directive({
        selector: '[autocompletion]'
    })
], AutoCompletionDirective);
exports.AutoCompletionDirective = AutoCompletionDirective;
