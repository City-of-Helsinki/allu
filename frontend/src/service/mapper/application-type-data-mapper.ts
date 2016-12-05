import {Event} from '../../model/application/event/event';
import {ApplicationExtension} from '../../model/application/type/application-extension';
import {TimeUtil} from '../../util/time.util';
import {ApplicationType} from '../../model/application/type/application-type';
import {ShortTermRental} from '../../model/application/short-term-rental/short-term-rental';

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
    } else {
      return undefined;
    }
  }

  public static mapFrontend(applicationTypeData: ApplicationExtension): any {
    return applicationTypeData;
  }
}
