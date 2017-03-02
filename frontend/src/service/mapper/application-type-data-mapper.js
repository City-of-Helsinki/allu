"use strict";
var event_1 = require("../../model/application/event/event");
var time_util_1 = require("../../util/time.util");
var application_type_1 = require("../../model/application/type/application-type");
var short_term_rental_1 = require("../../model/application/short-term-rental/short-term-rental");
var cable_report_1 = require("../../model/application/cable-report/cable-report");
var excavation_announcement_1 = require("../../model/application/excavation-announcement/excavation-announcement");
var note_1 = require("../../model/application/note/note");
var traffic_arrangement_1 = require("../../model/application/traffic-arrangement/traffic-arrangement");
var placement_contract_1 = require("../../model/application/placement-contract/placement-contract");
var ApplicationTypeDataMapper = (function () {
    function ApplicationTypeDataMapper() {
    }
    ApplicationTypeDataMapper.mapBackend = function (backendExtension) {
        var applicationType = backendExtension.applicationType;
        switch (application_type_1.ApplicationType[applicationType]) {
            case application_type_1.ApplicationType.EVENT:
                return new event_1.Event(backendExtension.nature, backendExtension.description, backendExtension.url, backendExtension.applicationType, time_util_1.TimeUtil.dateFromBackend(backendExtension.eventStartTime), time_util_1.TimeUtil.dateFromBackend(backendExtension.eventEndTime), backendExtension.timeExceptions, backendExtension.attendees, backendExtension.entryFee, backendExtension.noPriceReason, backendExtension.salesActivity, backendExtension.heavyStructure, backendExtension.ecoCompass, backendExtension.foodSales, backendExtension.foodProviders, backendExtension.marketingProviders, backendExtension.structureArea, backendExtension.structureDescription, time_util_1.TimeUtil.dateFromBackend(backendExtension.structureStartTime), time_util_1.TimeUtil.dateFromBackend(backendExtension.structureEndTime), backendExtension.terms);
            case application_type_1.ApplicationType.SHORT_TERM_RENTAL:
                return new short_term_rental_1.ShortTermRental(backendExtension.description, backendExtension.commercial);
            case application_type_1.ApplicationType.CABLE_REPORT:
                return new cable_report_1.CableReport(backendExtension.specifiers, backendExtension.cableSurveyRequired, backendExtension.mapUpdated, backendExtension.constructionWork, backendExtension.maintenanceWork, backendExtension.emergencyWork, backendExtension.propertyConnectivity, backendExtension.cableReportId, backendExtension.workDescription, backendExtension.owner, backendExtension.contact, backendExtension.mapExtractCount, backendExtension.infoEntries);
            case application_type_1.ApplicationType.EXCAVATION_ANNOUNCEMENT:
                return new excavation_announcement_1.ExcavationAnnouncement(backendExtension.specifiers, backendExtension.contractor, backendExtension.responsiblePerson, backendExtension.propertyDeveloper, backendExtension.propertyDeveloperContact, backendExtension.pksCard, backendExtension.constructionWork, backendExtension.maintenanceWork, backendExtension.emergencyWork, backendExtension.propertyConnectivity, backendExtension.winterTimeOperation, backendExtension.summerTimeOperation, backendExtension.workFinished, backendExtension.unauthorizedWorkStartTime, backendExtension.unauthorizedWorkEndTime, backendExtension.guaranteeEndTime, backendExtension.cableReportId, backendExtension.additionalInfo, backendExtension.trafficArrangements, backendExtension.terms);
            case application_type_1.ApplicationType.NOTE:
                return new note_1.Note(backendExtension.reoccurring, backendExtension.description);
            case application_type_1.ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS:
                return new traffic_arrangement_1.TrafficArrangement(backendExtension.specifiers, backendExtension.contractor, backendExtension.responsiblePerson, backendExtension.pksCard, backendExtension.workFinished, backendExtension.trafficArrangements, backendExtension.additionalInfo, backendExtension.terms);
            case application_type_1.ApplicationType.PLACEMENT_CONTRACT:
                return new placement_contract_1.PlacementContract(backendExtension.specifiers, backendExtension.representative, backendExtension.contact, backendExtension.diaryNumber, backendExtension.additionalInfo, backendExtension.generalTerms, backendExtension.terms);
            default:
                throw new Error('No mapping from backend for ' + applicationType);
        }
    };
    ApplicationTypeDataMapper.mapFrontend = function (applicationTypeData) {
        return applicationTypeData;
    };
    return ApplicationTypeDataMapper;
}());
exports.ApplicationTypeDataMapper = ApplicationTypeDataMapper;
