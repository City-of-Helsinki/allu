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
        new Date(backendEvent.startTime),
        new Date(backendEvent.endTime),
        backendEvent.timeExceptions,
        backendEvent.attendees,
        backendEvent.entryFee,
        backendEvent.pricing,
        backendEvent.salesActivity,
        backendEvent.ecoCompass,
        backendEvent.foodProviders,
        backendEvent.marketingProviders,
        backendEvent.structureArea,
        backendEvent.structureDescription,
        new Date(backendEvent.structureStartDate),
        new Date(backendEvent.structureEndDate));
    } else {
      return undefined;
    }
  }

  public static mapFrontend(applicationTypeData: ApplicationTypeData): any {
    return applicationTypeData;
  }
}
