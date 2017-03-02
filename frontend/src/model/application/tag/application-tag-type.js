"use strict";
(function (ApplicationTagType) {
    ApplicationTagType[ApplicationTagType["ADDITIONAL_INFORMATION_REQUESTED"] = 0] = "ADDITIONAL_INFORMATION_REQUESTED";
    ApplicationTagType[ApplicationTagType["STATEMENT_REQUESTED"] = 1] = "STATEMENT_REQUESTED";
    ApplicationTagType[ApplicationTagType["DEPOSIT_REQUESTED"] = 2] = "DEPOSIT_REQUESTED";
    ApplicationTagType[ApplicationTagType["DEPOSIT_PAID"] = 3] = "DEPOSIT_PAID";
    ApplicationTagType[ApplicationTagType["PRELIMINARY_INSPECTION_REQUESTED"] = 4] = "PRELIMINARY_INSPECTION_REQUESTED";
    ApplicationTagType[ApplicationTagType["PRELIMINARY_INSPECTION_DONE"] = 5] = "PRELIMINARY_INSPECTION_DONE";
    ApplicationTagType[ApplicationTagType["FINAL_INSPECTION_AGREED"] = 6] = "FINAL_INSPECTION_AGREED";
    ApplicationTagType[ApplicationTagType["FINAL_INSPECTION_DONE"] = 7] = "FINAL_INSPECTION_DONE";
    ApplicationTagType[ApplicationTagType["WAITING"] = 8] = "WAITING";
    ApplicationTagType[ApplicationTagType["COMPENSATION_CLARIFICATION"] = 9] = "COMPENSATION_CLARIFICATION";
    ApplicationTagType[ApplicationTagType["PAYMENT_BASIS_CORRECTION"] = 10] = "PAYMENT_BASIS_CORRECTION"; // maksuperusteet korjattava
})(exports.ApplicationTagType || (exports.ApplicationTagType = {}));
var ApplicationTagType = exports.ApplicationTagType;
