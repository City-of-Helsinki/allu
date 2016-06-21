import {OutdoorEvent} from '../../model/application/type/outdoor-event';
import {ApplicationTypeData} from '../../model/application/type/application-type-data';

export class ApplicationTypeDataMapper {

  public static mapBackend(backendEvent: any): ApplicationTypeData {
    if (backendEvent.type === 'OutdoorEvent') {
      return new OutdoorEvent(
        backendEvent.nature,
        backendEvent.description,
        backendEvent.url,
        backendEvent.type,
        new Date(backendEvent.startTime),
        new Date(backendEvent.endTime),
        backendEvent.audience);
    } else {
      return undefined;
    }
  }

  public static mapFrontend(applicationTypeData: ApplicationTypeData): any {
    return applicationTypeData;
  }
}
