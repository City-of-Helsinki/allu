import {Event} from '../../../../model/application/event/event';
import {TimePeriod} from '../time-period';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {Application} from '../../../../model/application/application';
import {ApplicationForm} from '../application-form';

export class EventForm implements ApplicationForm {
  constructor(public name?: string,
              public nature?: string,
              public description?: string,
              public url?: string,
              public eventTimes?: TimePeriod,
              public timeExceptions?: string,
              public attendees?: number,
              public entryFee?: number,
              public ecoCompass?: boolean,
              public foodSales?: boolean,
              public foodProviders?: string,
              public marketingProviders?: string,
              public structureArea?: number,
              public structureDescription?: string,
              public structureTimes?: TimePeriod,
              public calculatedPrice?: number,
              public terms?: string
            ) {
    this.eventTimes = eventTimes || new TimePeriod();
    this.structureTimes = structureTimes || new TimePeriod();
  }

  static fromEvent(application: Application, event: Event): EventForm {
    return new EventForm(
      application.name,
      event.nature,
      event.description,
      event.url,
      new TimePeriod(event.eventStartTime, event.eventEndTime),
      event.timeExceptions,
      event.attendees,
      event.entryFee,
      event.ecoCompass,
      event.foodSales,
      event.foodProviders,
      event.marketingProviders,
      event.structureArea,
      event.structureDescription,
      new TimePeriod(event.structureStartTime, event.structureEndTime),
      application.calculatedPriceEuro,
      event.terms);
  }

  static toEvent(form: EventForm, type: ApplicationType): Event {
    const event = new Event();
    event.nature = form.nature;
    event.description = form.description;
    event.url = form.url;
    event.applicationType = ApplicationType[type];
    event.eventStartTime = form.eventTimes.startTime;
    event.eventEndTime = form.eventTimes.endTime;
    event.timeExceptions = form.timeExceptions;
    event.attendees = form.attendees;
    event.ecoCompass = form.ecoCompass;
    event.foodSales = form.foodSales;
    event.foodProviders = form.foodProviders;
    event.marketingProviders = form.marketingProviders;
    event.structureArea = form.structureArea;
    event.structureDescription = form.structureDescription;
    event.structureStartTime = form.structureTimes.startTime;
    event.structureEndTime = form.structureTimes.endTime;
    event.terms = form.terms;
    return event;
  }
}
