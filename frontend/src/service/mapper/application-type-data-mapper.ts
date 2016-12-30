import {Event} from '../../model/application/event/event';
import {ApplicationExtension} from '../../model/application/type/application-extension';
import {TimeUtil} from '../../util/time.util';
import {ApplicationType} from '../../model/application/type/application-type';
import {ShortTermRental} from '../../model/application/short-term-rental/short-term-rental';
import {CableReport} from '../../model/application/cable-report/cable-report';
import {ExcavationAnnouncement} from '../../model/application/excavation-announcement/excavation-announcement';

export class ApplicationTypeDataMapper {

  public static mapBackend(backendExtension: any): ApplicationExtension {
    if (backendExtension.applicationType === ApplicationType[ApplicationType.EVENT]) {
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
        TimeUtil.dateFromBackend(backendExtension.structureEndTime));
    } else if (backendExtension.applicationType === ApplicationType[ApplicationType.SHORT_TERM_RENTAL]) {
      return new ShortTermRental(backendExtension.description, backendExtension.commercial);
    } else if (backendExtension.applicationType === ApplicationType[ApplicationType.CABLE_REPORT]) {
      return new CableReport(
        backendExtension.cableSurveyRequired,
        backendExtension.cableReportId,
        backendExtension.workDescription,
        backendExtension.owner,
        backendExtension.contact,
        backendExtension.mapExtractCount,
        backendExtension.infoEntries
      );
    } else if (backendExtension.applicationType === ApplicationType[ApplicationType.EXCAVATION_ANNOUNCEMENT]) {
      return new ExcavationAnnouncement(
        backendExtension.contractor,
        backendExtension.responsiblePerson,
        backendExtension.pksCard,
        backendExtension.constructionWork,
        backendExtension.maintenanceWork,
        backendExtension.emergencyWork,
        backendExtension.plotConnectivity,
        backendExtension.propertyConnectivity,
        backendExtension.winterTimeOperation,
        backendExtension.summerTimeOperation,
        backendExtension.workFinished,
        backendExtension.unauthorizedWorkStartTime,
        backendExtension.unauthorizedWorkEndTime,
        backendExtension.guaranteeEndTime,
        backendExtension.cableReportId,
        backendExtension.additionalInfo,
        backendExtension.trafficArrangements
      );
    } else {
      return undefined;
    }
  }

  public static mapFrontend(applicationTypeData: ApplicationExtension): any {
    return applicationTypeData;
  }
}
