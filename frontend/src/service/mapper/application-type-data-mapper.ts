import {Event} from '../../model/application/event/event';
import {ApplicationExtension} from '../../model/application/type/application-extension';
import {TimeUtil} from '../../util/time.util';
import {ApplicationType} from '../../model/application/type/application-type';
import {ShortTermRental} from '../../model/application/short-term-rental/short-term-rental';
import {CableReport} from '../../model/application/cable-report/cable-report';
import {ExcavationAnnouncement} from '../../model/application/excavation-announcement/excavation-announcement';
import {Note} from '../../model/application/note/note';
import {TrafficArrangement} from '../../model/application/traffic-arrangement/traffic-arrangement';
import {PlacementContract} from '../../model/application/placement-contract/placement-contract';
import {AreaRental} from '../../model/application/area-rental/area-rental';

export class ApplicationTypeDataMapper {
  public static mapBackend(backendExtension: any): ApplicationExtension {
    let applicationType: string = backendExtension.applicationType;
    switch (ApplicationType[applicationType]) {
      case ApplicationType.EVENT:
        return new Event(
          backendExtension.nature,
          backendExtension.description,
          backendExtension.url,
          backendExtension.applicationType,
          TimeUtil.dateFromBackend(backendExtension.eventStartTime),
          TimeUtil.dateFromBackend(backendExtension.eventEndTime),
          backendExtension.timeExceptions,
          backendExtension.attendees,
          backendExtension.entryFee,
          backendExtension.noPriceReason,
          backendExtension.salesActivity,
          backendExtension.heavyStructure,
          backendExtension.ecoCompass,
          backendExtension.foodSales,
          backendExtension.foodProviders,
          backendExtension.marketingProviders,
          backendExtension.structureArea,
          backendExtension.structureDescription,
          TimeUtil.dateFromBackend(backendExtension.structureStartTime),
          TimeUtil.dateFromBackend(backendExtension.structureEndTime),
          backendExtension.terms);
      case ApplicationType.SHORT_TERM_RENTAL:
        return new ShortTermRental(backendExtension.description, backendExtension.commercial);
      case ApplicationType.CABLE_REPORT:
        return new CableReport(
          backendExtension.specifiers,
          TimeUtil.dateFromBackend(backendExtension.validityTime),
          backendExtension.cableSurveyRequired,
          backendExtension.mapUpdated,
          backendExtension.constructionWork,
          backendExtension.maintenanceWork,
          backendExtension.emergencyWork,
          backendExtension.propertyConnectivity,
          backendExtension.cableReportId,
          backendExtension.workDescription,
          backendExtension.owner,
          backendExtension.contact,
          backendExtension.mapExtractCount,
          backendExtension.infoEntries
        );
      case ApplicationType.EXCAVATION_ANNOUNCEMENT:
        return new ExcavationAnnouncement(
          backendExtension.specifiers,
          backendExtension.contractor,
          backendExtension.responsiblePerson,
          backendExtension.propertyDeveloper,
          backendExtension.propertyDeveloperContact,
          backendExtension.pksCard,
          backendExtension.constructionWork,
          backendExtension.maintenanceWork,
          backendExtension.emergencyWork,
          backendExtension.propertyConnectivity,
          backendExtension.winterTimeOperation,
          backendExtension.summerTimeOperation,
          backendExtension.workFinished,
          backendExtension.unauthorizedWorkStartTime,
          backendExtension.unauthorizedWorkEndTime,
          backendExtension.guaranteeEndTime,
          backendExtension.cableReportId,
          backendExtension.additionalInfo,
          backendExtension.trafficArrangements,
          backendExtension.trafficArrangementImpedimentType,
          backendExtension.terms
        );
      case ApplicationType.NOTE:
        return new Note(backendExtension.reoccurring, backendExtension.description);
      case ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS:
        return new TrafficArrangement(
          backendExtension.specifiers,
          backendExtension.contractor,
          backendExtension.responsiblePerson,
          backendExtension.pksCard,
          backendExtension.workFinished,
          backendExtension.trafficArrangements,
          backendExtension.trafficArrangementImpedimentType,
          backendExtension.additionalInfo,
          backendExtension.terms
        );
      case ApplicationType.PLACEMENT_CONTRACT:
        return new PlacementContract(
          backendExtension.specifiers,
          backendExtension.representative,
          backendExtension.contact,
          backendExtension.diaryNumber,
          backendExtension.additionalInfo,
          backendExtension.generalTerms,
          backendExtension.terms
        );
      case ApplicationType.AREA_RENTAL:
        return new AreaRental(
          backendExtension.contractor,
          backendExtension.responsiblePerson,
          backendExtension.workFinished,
          backendExtension.trafficArrangements,
          backendExtension.trafficArrangementImpedimentType,
          backendExtension.additionalInfo,
          backendExtension.terms
        );
      default:
        throw new Error('No mapping from backend for ' + applicationType);
    }
  }

  public static mapFrontend(applicationTypeData: ApplicationExtension): any {
    return applicationTypeData;
  }
}
