import {Event} from '@model/application/event/event';
import {TimePeriod} from '../time-period';
import {ApplicationType} from '@model/application/type/application-type';
import {Application} from '@model/application/application';
import {ApplicationForm} from '../application-form';
import {ComplexValidator} from '@util/complex-validator';
import {UntypedFormBuilder, Validators} from '@angular/forms';
import {TimeUtil} from '@util/time.util';
import {SurfaceHardness} from '@model/application/event/surface-hardness';

export interface EventForm extends ApplicationForm {
  nature?: string;
  description?: string;
  url?: string;
  eventTimes?: TimePeriod;
  timeExceptions?: string;
  attendees?: number;
  entryFee?: number;
  ecoCompass?: boolean;
  foodSales?: boolean;
  foodProviders?: string;
  marketingProviders?: string;
  structureArea?: number;
  structureDescription?: string;
  structureTimes?: TimePeriod;
  calculatedPrice?: number;
  surfaceHardness?: SurfaceHardness;
  terms?: string;
}

export function fromEvent(application: Application, event: Event): EventForm {
  return {
    name: application.name,
    nature: event.nature,
    description: event.description,
    url: event.url,
    eventTimes: new TimePeriod(event.eventStartTime, event.eventEndTime),
    timeExceptions: event.timeExceptions,
    attendees: event.attendees,
    entryFee: event.entryFee,
    ecoCompass: event.ecoCompass,
    foodSales: event.foodSales,
    foodProviders: event.foodProviders,
    marketingProviders: event.marketingProviders,
    structureArea: event.structureArea,
    structureDescription: event.structureDescription,
    structureTimes: structureTimes(application, event),
    surfaceHardness: event.surfaceHardness,
    terms: event.terms
  };
}

export function toEvent(form: EventForm, type: ApplicationType): Event {
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
  event.surfaceHardness = form.surfaceHardness;
  event.terms = form.terms;
  return event;
}

export function eventForm(fb: UntypedFormBuilder): { [key: string]: any; } {
  return {
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
    }, { validator: ComplexValidator.startBeforeEnd('startTime', 'endTime') }),
    surfaceHardness: [undefined, Validators.required],
    terms: [undefined],
    nature: [undefined]
  };
}

export function eventDraft(fb: UntypedFormBuilder): { [key: string]: any; } {
  const form = eventForm(fb);
  form.description = [''];
  form.surfaceHardness = [undefined];
  return form;
}

export function outdoorEventForm(fb: UntypedFormBuilder): { [key: string]: any; } {
  const form = eventForm(fb);
  form.nature = ['', Validators.required];
  return form;
}

export function outdoorEventDraft(fb: UntypedFormBuilder): { [key: string]: any; } {
  const form = outdoorEventForm(fb);
  form.description = [''];
  form.nature = [''];
  return form;
}

export function structureTimes(application: Application, event: Event): TimePeriod {
  const tp = new TimePeriod();
  tp.startTime = TimeUtil.isSame(application.startTime, event.eventStartTime, 'day') ? undefined : application.startTime;
  tp.endTime = TimeUtil.isSame(application.endTime, event.eventEndTime, 'day') ? undefined : application.endTime;
  return tp;
}
