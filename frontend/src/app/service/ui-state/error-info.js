"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/**
 * Class to wrap http errors with messageToReadable
 */
var ErrorInfo = /** @class */ (function () {
    function ErrorInfo(type, message, response) {
        this.type = type;
        this.message = message;
        this.response = response;
    }
    ;
    ErrorInfo.of = function (response, message) {
        return new ErrorInfo(undefined, message, response);
    };
    return ErrorInfo;
}());
exports.ErrorInfo = ErrorInfo;
