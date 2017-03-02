"use strict";
var postal_address_1 = require("../common/postal-address");
var Applicant = (function () {
    function Applicant(id, type, representative, name, registryKey, postalAddress, email, phone) {
        this.id = id;
        this.type = type;
        this.representative = representative;
        this.name = name;
        this.registryKey = registryKey;
        this.postalAddress = postalAddress;
        this.email = email;
        this.phone = phone;
        this.postalAddress = postalAddress || new postal_address_1.PostalAddress();
    }
    return Applicant;
}());
exports.Applicant = Applicant;
