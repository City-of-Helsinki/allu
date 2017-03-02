"use strict";
var applicant_1 = require("../../model/application/applicant");
var postal_address_1 = require("../../model/common/postal-address");
var ApplicantMapper = (function () {
    function ApplicantMapper() {
    }
    ApplicantMapper.mapBackend = function (backendApplicant) {
        var postalAddress = undefined;
        if (backendApplicant.postalAddress) {
            postalAddress = new postal_address_1.PostalAddress(backendApplicant.postalAddress.streetAddress, backendApplicant.postalAddress.postalCode, backendApplicant.postalAddress.city);
        }
        return (backendApplicant) ?
            new applicant_1.Applicant(backendApplicant.id, backendApplicant.type, backendApplicant.representative, backendApplicant.name, backendApplicant.registryKey, postalAddress, backendApplicant.email, backendApplicant.phone) : undefined;
    };
    ApplicantMapper.mapFrontend = function (applicant) {
        return (applicant) ?
            {
                id: applicant.id,
                type: applicant.type,
                representative: applicant.representative,
                name: applicant.name,
                registryKey: applicant.registryKey,
                postalAddress: (applicant.postalAddress) ?
                    { streetAddress: applicant.postalAddress.streetAddress,
                        postalCode: applicant.postalAddress.postalCode,
                        city: applicant.postalAddress.city } : undefined,
                email: applicant.email,
                phone: applicant.phone
            } : undefined;
    };
    return ApplicantMapper;
}());
exports.ApplicantMapper = ApplicantMapper;
