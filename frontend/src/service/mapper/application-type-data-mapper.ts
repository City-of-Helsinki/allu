import {OutdoorEvent} from '../../model/application/type/outdoor-event';
import {ApplicationTypeData} from '../../model/application/type/application-type-data';

export class ApplicationTypeDataMapper {

  public static mapBackend(backendEvent: any): ApplicationTypeData {
    if (backendEvent.type === 'OutdoorEvent') {
      return new OutdoorEvent(
        backendEvent.type,
        backendEvent.description,
        backendEvent.url,
        new Date(backendEvent.startTime),
        new Date(backendEvent.endTime),
        backendEvent.timeExceptions,
        backendEvent.attendees,
        backendEvent.entryFee);
    } else {
      return undefined;
    }
  }

  public static mapFrontend(applicationTypeData: ApplicationTypeData): any {
    return applicationTypeData;
  }
}
