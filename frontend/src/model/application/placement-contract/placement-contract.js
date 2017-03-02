"use strict";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var application_type_1 = require("../type/application-type");
var application_extension_1 = require("../type/application-extension");
var PlacementContract = (function (_super) {
    __extends(PlacementContract, _super);
    function PlacementContract(specifiers, representative, contact, diaryNumber, additionalInfo, generalTerms, terms) {
        var _this = _super.call(this, application_type_1.ApplicationType[application_type_1.ApplicationType.PLACEMENT_CONTRACT], specifiers, terms) || this;
        _this.specifiers = specifiers;
        _this.representative = representative;
        _this.contact = contact;
        _this.diaryNumber = diaryNumber;
        _this.additionalInfo = additionalInfo;
        _this.generalTerms = generalTerms;
        _this.terms = terms;
        return _this;
    }
    Object.defineProperty(PlacementContract.prototype, "representativeContactList", {
        get: function () {
            return this.contact ? [this.contact] : undefined;
        },
        enumerable: true,
        configurable: true
    });
    return PlacementContract;
}(application_extension_1.ApplicationExtension));
exports.PlacementContract = PlacementContract;
