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
import {OrdererId} from '../../model/application/cable-report/orderer-id';
import {Application} from '../../model/application/application';
import {Some} from '../../util/option';
import {CustomerWithContacts} from '../../model/customer/customer-with-contacts';
import {ArrayUtil} from '../../util/array-util';

export class ApplicationExtensionMapper {
  public static mapBackend(backendExtension: any): ApplicationExtension {
    const applicationType: string = backendExtension.applicationType;
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
          backendExtension.ecoCompass,
          backendExtension.foodSales,
          backendExtension.foodProviders,
          backendExtension.marketingProviders,
          backendExtension.structureArea,
          backendExtension.structureDescription,
          backendExtension.terms);
      case ApplicationType.SHORT_TERM_RENTAL:
        return new ShortTermRental(
          backendExtension.description,
          backendExtension.commercial,
          backendExtension.billableSalesArea,
          backendExtension.terms);
      case ApplicationType.CABLE_REPORT:
        return new CableReport(
          TimeUtil.dateFromBackend(backendExtension.validityTime),
          backendExtension.cableSurveyRequired,
          backendExtension.mapUpdated,
          backendExtension.constructionWork,
          backendExtension.maintenanceWork,
          backendExtension.emergencyWork,
          backendExtension.propertyConnectivity,
          backendExtension.cableReportId,
          backendExtension.workDescription,
          backendExtension.mapExtractCount,
          backendExtension.infoEntries,
          OrdererId.ofId(backendExtension.orderer)
        );
      case ApplicationType.EXCAVATION_ANNOUNCEMENT:
        return new ExcavationAnnouncement(
          backendExtension.pksCard,
          backendExtension.constructionWork,
          backendExtension.maintenanceWork,
          backendExtension.emergencyWork,
          backendExtension.propertyConnectivity,
          TimeUtil.dateFromBackend(backendExtension.winterTimeOperation),
          TimeUtil.dateFromBackend(backendExtension.workFinished),
          TimeUtil.dateFromBackend(backendExtension.unauthorizedWorkStartTime),
          TimeUtil.dateFromBackend(backendExtension.unauthorizedWorkEndTime),
          TimeUtil.dateFromBackend(backendExtension.guaranteeEndTime),
          TimeUtil.dateFromBackend(backendExtension.customerStartTime),
          TimeUtil.dateFromBackend(backendExtension.customerEndTime),
          TimeUtil.dateFromBackend(backendExtension.customerWinterTimeOperation),
          TimeUtil.dateFromBackend(backendExtension.customerWorkFinished),
          backendExtension.cableReportId,
          backendExtension.workPurpose,
          backendExtension.trafficArrangements,
          backendExtension.trafficArrangementImpedimentType,
          backendExtension.terms,
          backendExtension.operationalConditionReported,
          backendExtension.workFinishedReported
        );
      case ApplicationType.NOTE:
        return new Note(backendExtension.description);
      case ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS:
        return new TrafficArrangement(
          backendExtension.trafficArrangements,
          backendExtension.trafficArrangementImpedimentType,
          backendExtension.workPurpose,
          backendExtension.terms
        );
      case ApplicationType.PLACEMENT_CONTRACT:
        return new PlacementContract(
          backendExtension.propertyIdentificationNumber,
          backendExtension.additionalInfo,
          backendExtension.contractText,
          backendExtension.terms,
          backendExtension.terminationDate,
          backendExtension.rationale
        );
      case ApplicationType.AREA_RENTAL:
        return new AreaRental(
          backendExtension.pksCard,
          TimeUtil.dateFromBackend(backendExtension.workFinished),
          backendExtension.trafficArrangements,
          backendExtension.trafficArrangementImpedimentType,
          backendExtension.additionalInfo,
          backendExtension.terms
        );
      default:
        throw new Error('No mapping from backend for ' + applicationType);
    }
  }

  public static mapFrontend(application: Application): any {
      const applicationType: string = application.type;
      switch (ApplicationType[applicationType]) {
          case ApplicationType.EVENT:
            return this.mapFrontendEvent(<Event> application.extension);
          case ApplicationType.SHORT_TERM_RENTAL:
            return this.mapFrontendShortTermRental(<ShortTermRental>  application.extension);
          case ApplicationType.CABLE_REPORT:
            return this.mapFrontendCableReport(application);
          case ApplicationType.EXCAVATION_ANNOUNCEMENT:
            return this.mapFrontendExcavationAnnouncement(<ExcavationAnnouncement>  application.extension);
          case ApplicationType.NOTE:
            return this.mapFrontendNote(<Note>  application.extension);
          case ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS:
            return this.mapFrontendTrafficArrangement(<TrafficArrangement>  application.extension);
          case ApplicationType.PLACEMENT_CONTRACT:
            return this.mapFrontendPlacementContract(<PlacementContract>  application.extension);
          case ApplicationType.AREA_RENTAL:
            return this.mapFrontendAreaRental(<AreaRental>  application.extension);
          default:
              throw new Error('No mapping from backend for ' + applicationType);
      }
  }

  private static mapFrontendEvent(event: Event): any {
    return {
        applicationType: event.applicationType,
        nature: event.nature,
        description: event.description,
        url: event.url,
        eventStartTime: TimeUtil.dateToBackend(event.eventStartTime),
        eventEndTime: TimeUtil.dateToBackend(event.eventEndTime),
        timeExceptions: event.timeExceptions,
        attendees: event.attendees,
        entryFee: event.entryFee,
        ecoCompass: event.ecoCompass,
        foodSales: event.foodSales,
        foodProviders: event.foodProviders,
        marketingProviders: event.marketingProviders,
        structureArea: event.structureArea,
        structureDescription: event.structureDescription,
        terms: event.terms
    };
  }

  private static mapFrontendShortTermRental(rental: ShortTermRental): any {
    return {
      applicationType: rental.applicationType,
      description: rental.description,
      commercial: rental.commercial,
      billableSalesArea: rental.billableSalesArea,
      terms: rental.terms
    };
  }

  private static mapFrontendCableReport(application: Application): any {
    const cableReport = <CableReport> application.extension;
    const ordererId = Some(cableReport.ordererId.id)
      .orElse(this.getOrdererId(cableReport.ordererId, application.customersWithContacts));
    return {
      applicationType: cableReport.applicationType,
      validityTime: TimeUtil.dateToBackend(cableReport.validityTime),
      cableSurveyRequired: cableReport.cableSurveyRequired,
      mapUpdated: cableReport.mapUpdated,
      constructionWork: cableReport.constructionWork,
      maintenanceWork: cableReport.maintenanceWork,
      emergencyWork: cableReport.emergencyWork,
      propertyConnectivity: cableReport.propertyConnectivity,
      cableReportId: cableReport.cableReportId,
      workDescription: cableReport.workDescription,
      mapExtractCount: cableReport.mapExtractCount,
      infoEntries: cableReport.infoEntries,
      orderer: ordererId
    };
  }

  private static getOrdererId(ordererId: OrdererId, customersWithContacts: Array<CustomerWithContacts>): number {
    return Some(ArrayUtil.first(customersWithContacts.filter(cwc => cwc.uiRoleType === ordererId.customerRoleType)))
      .map(cwc => cwc.contacts[ordererId.index])
      .map(orderer => orderer.id)
      .orElse(undefined);
  }

  private static mapFrontendExcavationAnnouncement(excavation: ExcavationAnnouncement): any {
    return {
      applicationType: excavation.applicationType,
      pksCard: excavation.pksCard,
      constructionWork: excavation.constructionWork,
      maintenanceWork: excavation.maintenanceWork,
      emergencyWork: excavation.emergencyWork,
      propertyConnectivity: excavation.propertyConnectivity,
      winterTimeOperation: TimeUtil.dateToBackend(excavation.winterTimeOperation),
      workFinished: TimeUtil.dateToBackend(excavation.workFinished),
      unauthorizedWorkStartTime: TimeUtil.dateToBackend(excavation.unauthorizedWorkStartTime),
      unauthorizedWorkEndTime: TimeUtil.dateToBackend(excavation.unauthorizedWorkEndTime),
      guaranteeEndTime: TimeUtil.dateToBackend(excavation.guaranteeEndTime),
      customerStartTime: TimeUtil.dateToBackend(excavation.customerStartTime),
      customerEndTime: TimeUtil.dateToBackend(excavation.customerEndTime),
      customerWinterTimeOperation: TimeUtil.dateToBackend(excavation.customerWinterTimeOperation),
      customerWorkFinished: TimeUtil.dateToBackend(excavation.customerWorkFinished),
      cableReportId: excavation.cableReportId,
      workPurpose: excavation.workPurpose,
      trafficArrangements: excavation.trafficArrangements,
      trafficArrangementImpedimentType: excavation.trafficArrangementImpedimentType,
      terms: excavation.terms,
      operationalConditionReported: excavation.operationalConditionReported,
      workFinishedReported: excavation.workFinishedReported
    };
  }

  private static mapFrontendNote(note: Note): any {
    return {
      applicationType: note.applicationType,
      description: note.description
    };
  }

  private static mapFrontendTrafficArrangement(trafficArrangement: TrafficArrangement): any {
    return {
      applicationType: trafficArrangement.applicationType,
      trafficArrangements: trafficArrangement.trafficArrangements,
      trafficArrangementImpedimentType: trafficArrangement.trafficArrangementImpedimentType,
      workPurpose: trafficArrangement.workPurpose,
      terms: trafficArrangement.terms
    };
  }

  private static mapFrontendPlacementContract(placementContract: PlacementContract): any {
    return {
      applicationType: placementContract.applicationType,
      propertyIdentificationNumber: placementContract.propertyIdentificationNumber,
      additionalInfo: placementContract.additionalInfo,
      contractText: placementContract.contractText,
      terms: placementContract.terms,
      terminationDate: placementContract.terminationDate,
      rationale: placementContract.rationale
    };
  }

  private static mapFrontendAreaRental(areaRental: AreaRental): any {
    return {
      applicationType: areaRental.applicationType,
      pksCard: areaRental.pksCard,
      workFinished: TimeUtil.dateToBackend(areaRental.workFinished),
      trafficArrangements: areaRental.trafficArrangements,
      trafficArrangementImpedimentType: areaRental.trafficArrangementImpedimentType,
      additionalInfo: areaRental.additionalInfo,
      terms: areaRental.terms
    };
  }
}
