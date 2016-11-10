import {OutdoorEvent} from '../../model/application/outdoor-event/outdoor-event';
import {ApplicationTypeData} from '../../model/application/type/application-type-data';
import {TimeUtil} from '../../util/time.util';
import {ApplicationType} from '../../model/application/type/application-type';
import {ApplicationCategory} from '../../feature/application/type/application-category';
import {shortTermRental} from '../../feature/application/type/application-category';
import {ShortTermRental} from '../../model/application/short-term-rental/short-term-rental';

export class ApplicationTypeDataMapper {

  public static mapBackend(backendEvent: any): ApplicationTypeData {
    let type: string = backendEvent.type;

    if (type === ApplicationType[ApplicationType.OUTDOOREVENT]) {
      return new OutdoorEvent(
        backendEvent.nature,
        backendEvent.description,
        backendEvent.url,
        backendEvent.type,
        backendEvent.applicationCategory,
        TimeUtil.dateFromBackend(backendEvent.eventStartTime),
        TimeUtil.dateFromBackend(backendEvent.eventEndTime),
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
        backendEvent.calculatedPricing,
        backendEvent.structureArea,
        backendEvent.structureDescription,
        TimeUtil.dateFromBackend(backendEvent.structureStartTime),
        TimeUtil.dateFromBackend(backendEvent.structureEndTime));
    } else if (shortTermRental.containsType(ApplicationType[type])) {
      return new ShortTermRental(type, backendEvent.description, backendEvent.commercial);
    } else {
      return undefined;
    }
  }

  public static mapFrontend(applicationTypeData: ApplicationTypeData): any {
    return applicationTypeData;
  }
}
