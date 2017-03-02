"use strict";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var application_extension_1 = require("../type/application-extension");
var application_type_1 = require("../type/application-type");
var Note = (function (_super) {
    __extends(Note, _super);
    function Note(reoccurring, description) {
        var _this = _super.call(this, application_type_1.ApplicationType[application_type_1.ApplicationType.NOTE]) || this;
        _this.reoccurring = reoccurring;
        _this.description = description;
        return _this;
    }
    return Note;
}(application_extension_1.ApplicationExtension));
exports.Note = Note;
