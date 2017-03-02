"use strict";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var application_extension_1 = require("../type/application-extension");
var application_type_1 = require("../type/application-type");
var ShortTermRental = (function (_super) {
    __extends(ShortTermRental, _super);
    function ShortTermRental(description, commercial, largeSalesArea) {
        var _this = _super.call(this, application_type_1.ApplicationType[application_type_1.ApplicationType.SHORT_TERM_RENTAL]) || this;
        _this.description = description;
        _this.commercial = commercial;
        _this.largeSalesArea = largeSalesArea;
        return _this;
    }
    return ShortTermRental;
}(application_extension_1.ApplicationExtension));
exports.ShortTermRental = ShortTermRental;
