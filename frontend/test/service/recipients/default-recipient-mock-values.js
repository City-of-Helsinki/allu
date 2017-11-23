"use strict";
exports.__esModule = true;
var default_recipient_1 = require("../../../src/model/common/default-recipient");
var application_type_1 = require("../../../src/model/application/type/application-type");
exports.RECIPIENT_ONE = new default_recipient_1.DefaultRecipient(1, 'first@test.fi', application_type_1.ApplicationType[application_type_1.ApplicationType.EVENT]);
exports.RECIPIENT_TWO = new default_recipient_1.DefaultRecipient(2, 'second@test.fi', application_type_1.ApplicationType[application_type_1.ApplicationType.NOTE]);
exports.RECIPIENT_NEW = new default_recipient_1.DefaultRecipient(undefined, 'new@test.fi', application_type_1.ApplicationType[application_type_1.ApplicationType.AREA_RENTAL]);
exports.RECIPIENTS_ALL = [exports.RECIPIENT_ONE, exports.RECIPIENT_TWO];
