"use strict";
var contact_1 = require("../../model/application/contact");
var ContactMapper = (function () {
    function ContactMapper() {
    }
    ContactMapper.mapBackend = function (backendContact) {
        return (backendContact) ? new contact_1.Contact(backendContact.id, backendContact.applicantId, backendContact.name, backendContact.streetAddress, backendContact.postalCode, backendContact.city, backendContact.email, backendContact.phone) : undefined;
    };
    ContactMapper.mapFrontend = function (contact) {
        return (contact) ?
            {
                id: contact.id,
                applicantId: contact.applicantId,
                name: contact.name,
                streetAddress: contact.streetAddress,
                postalCode: contact.postalCode,
                city: contact.city,
                email: contact.email,
                phone: contact.phone
            } : undefined;
    };
    return ContactMapper;
}());
exports.ContactMapper = ContactMapper;
