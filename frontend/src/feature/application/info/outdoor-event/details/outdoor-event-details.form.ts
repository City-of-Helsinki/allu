import {OutdoorEvent} from '../../../../../model/application/outdoor-event/outdoor-event';
import {TimePeriod} from '../../time-period';
import {ApplicationCategoryType} from '../../../type/application-category';

export class OutdoorEventDetailsForm {
  constructor(public name?: string,
              public nature?: string,
              public description?: string,
              public url?: string,
              public type?: string,
              public eventTimes?: TimePeriod,
              public timeExceptions?: string,
              public attendees?: number,
              public entryFee?: number,
              public noPrice?: boolean,
              public noPriceReason?: string,
              public salesActivity?: boolean,
              public heavyStructure?: boolean,
              public ecoCompass?: boolean,
              public foodSales?: boolean,
              public foodProviders?: string,
              public marketingProviders?: string,
              public structureArea?: number,
              public structureDescription?: string,
              public structureTimes?: TimePeriod) {
    eventTimes = new TimePeriod();
    structureTimes = new TimePeriod();
  }

  static fromOutdoorEvent(name: string, event: OutdoorEvent): OutdoorEventDetailsForm {
    return new OutdoorEventDetailsForm(
      name,
      event.nature,
      event.description,
      event.url,
      event.type,
      new TimePeriod(event.uiStartTime, event.uiEndTime),
      event.timeExceptions,
      event.attendees,
      event.entryFee,
      !!event.noPriceReason,
      event.noPriceReason,
      event.salesActivity,
      event.heavyStructure,
      event.ecoCompass,
      event.foodSales,
      event.foodProviders,
      event.marketingProviders,
      event.structureArea,
      event.structureDescription,
      new TimePeriod(event.uiStructureStartTime, event.uiStructureEndTime));
  }

  static toOutdoorEvent(form: OutdoorEventDetailsForm): OutdoorEvent {
    let event = new OutdoorEvent();
    event.nature = form.nature;
    event.description = form.description;
    event.url = form.url;
    event.type = form.type;
    event.applicationCategory = ApplicationCategoryType[ApplicationCategoryType.EVENT];
    event.uiStartTime = form.eventTimes.startTime;
    event.uiEndTime = form.eventTimes.endTime;
    event.timeExceptions = form.timeExceptions;
    event.attendees = form.attendees;
    event.entryFee = form.entryFee;
    event.noPriceReason = form.noPriceReason;
    event.salesActivity = form.salesActivity;
    event.heavyStructure = form.heavyStructure;
    event.ecoCompass = form.ecoCompass;
    event.foodSales = form.foodSales;
    event.foodProviders = form.foodProviders;
    event.marketingProviders = form.marketingProviders;
    event.structureArea = form.structureArea;
    event.structureDescription = form.structureDescription;
    event.uiStructureStartTime = form.structureTimes.startTime;
    event.uiStructureEndTime = form.structureTimes.endTime;
    return event;
  }
}
