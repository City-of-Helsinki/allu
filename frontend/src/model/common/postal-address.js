"use strict";
var PostalAddress = (function () {
    function PostalAddress(streetAddress, postalCode, city) {
        this.streetAddress = streetAddress;
        this.postalCode = postalCode;
        this.city = city;
    }
    ;
    PostalAddress.fromBackend = function (backendPostalAddress) {
        return new PostalAddress(backendPostalAddress.streetAddress, backendPostalAddress.postalCode, backendPostalAddress.city);
    };
    PostalAddress.prototype.toBackend = function () {
        return {
            streetAddress: this.streetAddress,
            postalCode: this.postalCode,
            city: this.city
        };
    };
    Object.defineProperty(PostalAddress.prototype, "uiStreetAddress", {
        get: function () {
            return this.streetAddress ? this.streetAddress.replace(/\b0+/g, '') : undefined;
        },
        enumerable: true,
        configurable: true
    });
    return PostalAddress;
}());
exports.PostalAddress = PostalAddress;
