import {OutdoorEvent} from '../../model/application/type/outdoor-event';
import {ApplicationTypeData} from '../../model/application/type/application-type-data';

export class ApplicationTypeDataMapper {

  public static mapBackend(backendEvent: any): ApplicationTypeData {
    if (backendEvent.type === 'OUTDOOREVENT') {
      return new OutdoorEvent(
        backendEvent.nature,
        backendEvent.description,
        backendEvent.url,
        backendEvent.type,
        new Date(backendEvent.eventStartTime),
        new Date(backendEvent.eventEndTime),
        backendEvent.timeExceptions,
        backendEvent.attendees,
        backendEvent.entryFee,
        backendEvent.noPriceReason,
        backendEvent.salesActivity,
        backendEvent.heavyStructure,
        backendEvent.ecoCompass,
        backendEvent.foodSales,
        backendEvent.foodProviders,
        backendEvent.marketingProviders,
        backendEvent.structureArea,
        backendEvent.structureDescription,
        new Date(backendEvent.structureStartTime),
        new Date(backendEvent.structureEndTime));
    } else {
      return undefined;
    }
  }

  public static mapFrontend(applicationTypeData: ApplicationTypeData): any {
    return applicationTypeData;
  }
}
