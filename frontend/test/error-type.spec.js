"use strict";
exports.__esModule = true;
require("./../src/service/ui-state/error-type.ts");
var error_type_1 = require("../src/service/ui-state/error-type");
var translations_1 = require("../src/util/translations");
var enum_util_1 = require("../src/util/enum.util");
describe('ErrorType', function () {
    it('should return expected message', function () {
        return expect(error_type_1.message(error_type_1.ErrorType.APPLICATION_SEARCH_FAILED)).toBe(translations_1.translations.application.error.searchFailed);
    });
    it('should have messages for all Error types', function () {
        enum_util_1.EnumUtil.enumValues(error_type_1.ErrorType).forEach(function (type) {
            expect(error_type_1.message(error_type_1.ErrorType[type])).toBeDefined('Missing message for type ' + type);
        });
    });
});
