"use strict";
(function (ApplicationStatus) {
    ApplicationStatus[ApplicationStatus["PRE_RESERVED"] = 0] = "PRE_RESERVED";
    ApplicationStatus[ApplicationStatus["PENDING"] = 1] = "PENDING";
    ApplicationStatus[ApplicationStatus["HANDLING"] = 2] = "HANDLING";
    ApplicationStatus[ApplicationStatus["RETURNED_TO_PREPARATION"] = 3] = "RETURNED_TO_PREPARATION";
    ApplicationStatus[ApplicationStatus["DECISIONMAKING"] = 4] = "DECISIONMAKING";
    ApplicationStatus[ApplicationStatus["DECISION"] = 5] = "DECISION";
    ApplicationStatus[ApplicationStatus["REJECTED"] = 6] = "REJECTED";
    ApplicationStatus[ApplicationStatus["FINISHED"] = 7] = "FINISHED";
    ApplicationStatus[ApplicationStatus["CANCELLED"] = 8] = "CANCELLED";
})(exports.ApplicationStatus || (exports.ApplicationStatus = {}));
var ApplicationStatus = exports.ApplicationStatus;
