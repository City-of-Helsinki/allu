import {ApplicationType} from '@model/application/type/application-type';

const commonFieldBlacklist: string[] = [
  'extension.terms'
];

const excavationAnnouncementFieldBlacklist: string[] = [
  'extension.winterTimeOperation',
  'extension.workFinished',
  'extension.unauthorizedWorkStartTime',
  'extension.unauthorizedWorkEndTime',
  'extension.guaranteeEndTime',
  'extension.customerStartTime',
  'extension.customerEndTime',
  'extension.customerWinterTimeOperation',
  'extension.customerWorkFinished',
  'extension.cableReportId',
  'extension.operationalConditionReported',
  'extension.workFinishedReported',
  'extension.validityReported',
  'extension.compactionAndBearingCapacityMeasurement',
  'extension.qualityAssuranceTest'
];
const temporaryTraffiArrangementFieldBlacklist: string[] = [
  'extension.trafficArrangementImpedimentType'
];

const cableReportFieldBlacklist: string[] = [
  'extension.validityTime',
  'extension.cableReportId',
  'extension.mapExtractCount',
  'extension.infoEntries',
  'extension.ordererId'
];
const placementContractFieldBlacklist: string[] = [
  'extension.contractText',
  'extension.terminationDate',
  'extension.rationale'
];

const eventFieldBlacklist: string[] = [
  'extension.nature',
  'extension.url',
  'extension.applicationType',
  'extension.timeExceptions',
  'extension.attendees',
  'extension.entryFee',
  'extension.ecoCompass',
  'extension.foodSales',
  'extension.foodProviders',
  'extension.marketingProviders',
  'extension.surfaceHardness'
];

const shortTermRentalBlacklist: string[] = [
];

export function blacklistForType(type: ApplicationType): string[] {
  switch (type) {
    case ApplicationType.EXCAVATION_ANNOUNCEMENT:
      return [
        ...commonFieldBlacklist,
        ...excavationAnnouncementFieldBlacklist
      ];

    case ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS:
      return [
        ...commonFieldBlacklist,
        ...temporaryTraffiArrangementFieldBlacklist
      ];

    case ApplicationType.CABLE_REPORT:
      return [
        ...commonFieldBlacklist,
        ...cableReportFieldBlacklist
      ];

    case ApplicationType.PLACEMENT_CONTRACT:
      return [
        ...commonFieldBlacklist,
        ...placementContractFieldBlacklist
      ];

    case ApplicationType.EVENT:
      return [
        ...commonFieldBlacklist,
        ...eventFieldBlacklist
      ];

    case ApplicationType.SHORT_TERM_RENTAL:
      return [
        ...commonFieldBlacklist,
        ...shortTermRentalBlacklist
      ];

    default:
      return [...commonFieldBlacklist];
  }
}
