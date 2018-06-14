import {Event} from '../../../../model/application/event/event';
import {TimePeriod} from '../time-period';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {Application} from '../../../../model/application/application';
import {ApplicationForm} from '../application-form';
import {ComplexValidator} from '../../../../util/complex-validator';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {TimeUtil} from '../../../../util/time.util';
import {NumberUtil} from '../../../../util/number.util';

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
      this.structureTimes(application, event),
      NumberUtil.toEuros(application.calculatedPrice),
      event.terms);
  }

  static toEvent(form: EventForm, type: ApplicationType): Event {
    const event = new Event();
    event.nature = form.nature;
    event.description = form.description;
    event.url = form.url;
    event.applicationType = ApplicationType[type];
    event.eventStartTime = TimeUtil.toStartDate(form.eventTimes.startTime);
    event.eventEndTime = TimeUtil.toEndDate(form.eventTimes.endTime);
    event.timeExceptions = form.timeExceptions;
    event.attendees = form.attendees;
    event.ecoCompass = form.ecoCompass;
    event.foodSales = form.foodSales;
    event.foodProviders = form.foodProviders;
    event.marketingProviders = form.marketingProviders;
    event.structureArea = form.structureArea;
    event.structureDescription = form.structureDescription;
    event.terms = form.terms;
    return event;
  }

  static eventForm(fb: FormBuilder): { [key: string]: any; } {
    return {
      name: ['', [Validators.required, Validators.minLength(2)]],
      description: [''],
      url: [''],
      eventTimes: fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, Validators.required]
      }, {validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') }),
      timeExceptions: [''],
      attendees: [0, ComplexValidator.greaterThanOrEqual(0)],
      entryFee: [0, ComplexValidator.greaterThanOrEqual(0)],
      notBillable: [false],
      notBillableReason: [''],
      salesActivity: [false],
      heavyStructure: [false],
      ecoCompass: [false],
      foodSales: [false],
      foodProviders: [''],
      marketingProviders: [''],
      calculatedPrice: [0],
      structureArea: [undefined, ComplexValidator.greaterThanOrEqual(0)],
      structureDescription: [''],
      structureTimes: fb.group({
        startTime: [undefined],
        endTime: [undefined]
      }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') })
    };
  }

  static eventDraft(fb: FormBuilder): { [key: string]: any; } {
    const form = EventForm.eventForm(fb);
    form.description = [''];
    return form;
  }

  static outdoorEventForm(fb: FormBuilder): { [key: string]: any; } {
    const form = EventForm.eventForm(fb);
    form.nature = ['', Validators.required];
    return form;
  }

  static outdoorEventDraft(fb: FormBuilder): { [key: string]: any; } {
    const form = EventForm.outdoorEventForm(fb);
    form.description = [''];
    form.nature = [''];
    return form;
  }

  private static structureTimes(application: Application, event: Event): TimePeriod {
    const tp = new TimePeriod();
    tp.startTime = TimeUtil.equals(application.startTime, event.eventStartTime) ? undefined : application.startTime;
    tp.endTime = TimeUtil.equals(application.endTime, event.eventEndTime) ? undefined : application.endTime;
    return tp;
  }
}
