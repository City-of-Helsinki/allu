"use strict";
var Contact = (function () {
    function Contact(id, applicantId, name, streetAddress, postalCode, city, email, phone) {
        this.id = id;
        this.applicantId = applicantId;
        this.name = name;
        this.streetAddress = streetAddress;
        this.postalCode = postalCode;
        this.city = city;
        this.email = email;
        this.phone = phone;
    }
    return Contact;
}());
exports.Contact = Contact;
