"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var translations_1 = require("../../util/translations");
var ErrorType;
(function (ErrorType) {
    ErrorType[ErrorType["GEOLOCATION_SEARCH_FAILED"] = 0] = "GEOLOCATION_SEARCH_FAILED";
    ErrorType[ErrorType["APPLICATION_SEARCH_FAILED"] = 1] = "APPLICATION_SEARCH_FAILED";
    ErrorType[ErrorType["APPLICATION_WORKQUEUE_SEARCH_FAILED"] = 2] = "APPLICATION_WORKQUEUE_SEARCH_FAILED";
    ErrorType[ErrorType["APPLICATION_SAVE_FAILED"] = 3] = "APPLICATION_SAVE_FAILED";
    ErrorType[ErrorType["APPLICATION_STATUS_CHANGE_FAILED"] = 4] = "APPLICATION_STATUS_CHANGE_FAILED";
    ErrorType[ErrorType["APPLICATION_HANDLER_CHANGE_FAILED"] = 5] = "APPLICATION_HANDLER_CHANGE_FAILED";
    ErrorType[ErrorType["PDF_GENERATION_FAILED"] = 6] = "PDF_GENERATION_FAILED";
    ErrorType[ErrorType["PROJECT_SAVE_FAILED"] = 7] = "PROJECT_SAVE_FAILED";
    ErrorType[ErrorType["PROJECT_SEARCH_FAILED"] = 8] = "PROJECT_SEARCH_FAILED";
    ErrorType[ErrorType["DEFAULT_TEXT_SAVE_FAILED"] = 9] = "DEFAULT_TEXT_SAVE_FAILED";
})(ErrorType = exports.ErrorType || (exports.ErrorType = {}));
/**
 * Contains all mappings from ErrorType -> messageToReadable
 */
var errorTypeToMessage = createMappings();
/**
 * Given ErrorType is converted to readable form
 */
function message(errorType) {
    return errorTypeToMessage.get(errorType);
}
exports.message = message;
function createMappings() {
    var map = new Map();
    map.set(ErrorType.GEOLOCATION_SEARCH_FAILED, translations_1.translations.geolocation.error.searchFailed);
    map.set(ErrorType.APPLICATION_SEARCH_FAILED, translations_1.translations.application.error.searchFailed);
    map.set(ErrorType.APPLICATION_WORKQUEUE_SEARCH_FAILED, translations_1.translations.application.error.searchFailed);
    map.set(ErrorType.APPLICATION_SAVE_FAILED, translations_1.translations.application.error.saveFailed);
    map.set(ErrorType.APPLICATION_STATUS_CHANGE_FAILED, translations_1.translations.application.error.statusChangeFailed);
    map.set(ErrorType.APPLICATION_HANDLER_CHANGE_FAILED, translations_1.translations.application.error.handlerChangeFailed);
    map.set(ErrorType.PDF_GENERATION_FAILED, translations_1.translations.decision.error.generatePdf);
    map.set(ErrorType.PROJECT_SAVE_FAILED, translations_1.translations.project.error.saveFailed);
    map.set(ErrorType.PROJECT_SEARCH_FAILED, translations_1.translations.project.error.searchFailed);
    map.set(ErrorType.DEFAULT_TEXT_SAVE_FAILED, translations_1.translations.defaultText.error.saveFailed);
    return map;
}
