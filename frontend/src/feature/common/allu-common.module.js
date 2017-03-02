"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var core_1 = require("@angular/core");
var common_1 = require("@angular/common");
var material_1 = require("@angular/material");
require("materialize-css");
var angular2_materialize_1 = require("angular2-materialize");
var auto_completion_directive_1 = require("./auto-completion/auto-completion.directive");
var auto_completion_list_component_1 = require("./auto-completion/auto-completion-list.component");
var field_error_component_1 = require("./field-error.component");
var translation_pipe_1 = require("../../pipe/translation.pipe");
var sort_by_directive_1 = require("./sort/sort-by.directive");
var file_drop_directive_1 = require("./file-drop/file-drop.directive");
var confirm_dialog_component_1 = require("./confirm-dialog/confirm-dialog.component");
var comma_separated_pipe_1 = require("../../pipe/comma-separated.pipe");
var file_select_directive_1 = require("../application/attachment/file-select.directive");
var AlluCommonModule = (function () {
    function AlluCommonModule() {
    }
    return AlluCommonModule;
}());
AlluCommonModule = __decorate([
    core_1.NgModule({
        imports: [
            material_1.MdToolbarModule,
            common_1.CommonModule,
            angular2_materialize_1.MaterializeModule
        ],
        declarations: [
            auto_completion_directive_1.AutoCompletionDirective,
            auto_completion_list_component_1.AutoCompletionListComponent,
            sort_by_directive_1.SortByDirective,
            field_error_component_1.FieldErrorComponent,
            translation_pipe_1.TranslationPipe,
            file_select_directive_1.FileSelectDirective,
            file_drop_directive_1.FileDropDirective,
            confirm_dialog_component_1.ConfirmDialogComponent,
            comma_separated_pipe_1.CommaSeparatedPipe
        ],
        exports: [
            common_1.CommonModule,
            material_1.MdToolbarModule,
            material_1.MdTabsModule,
            material_1.MdDialogModule,
            material_1.MdCardModule,
            material_1.MdIconModule,
            material_1.MdButtonModule,
            material_1.MdInputModule,
            material_1.MdSelectModule,
            material_1.MdCheckboxModule,
            angular2_materialize_1.MaterializeModule,
            auto_completion_directive_1.AutoCompletionDirective,
            auto_completion_list_component_1.AutoCompletionListComponent,
            sort_by_directive_1.SortByDirective,
            field_error_component_1.FieldErrorComponent,
            translation_pipe_1.TranslationPipe,
            file_select_directive_1.FileSelectDirective,
            file_drop_directive_1.FileDropDirective,
            confirm_dialog_component_1.ConfirmDialogComponent,
            comma_separated_pipe_1.CommaSeparatedPipe
        ],
        entryComponents: [
            auto_completion_list_component_1.AutoCompletionListComponent,
            confirm_dialog_component_1.ConfirmDialogComponent
        ]
    })
], AlluCommonModule);
exports.AlluCommonModule = AlluCommonModule;
