import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Event} from '../../../../../model/application/event/event';
import {EventDetailsForm} from './event-details.form';
import {translations} from '../../../../../util/translations';
import {ComplexValidator} from '../../../../../util/complex-validator';
import {EnumUtil} from '../../../../../util/enum.util';
import {BillingType} from '../../../../../model/application/billing-type';
import {EventNature} from '../../../../../model/application/event/event-nature';
import {ApplicationType} from '../../../../../model/application/type/application-type';
import {ApplicationState} from '../../../../../service/application/application-state';
import {Application} from '../../../../../model/application/application';
import {ApplicationKind} from '../../../../../model/application/type/application-kind';

@Component({
  selector: 'event-details',
  template: require('./event-details.component.html'),
  styles: []
})
export class EventDetailsComponent implements OnInit {
  @Input() applicationForm: FormGroup;
  @Input() application: Application;
  @Input() readonly: boolean;

  eventForm: FormGroup;
  applicationId: number;
  billingTypes = EnumUtil.enumValues(BillingType);
  translations = translations;

  constructor(private applicationState: ApplicationState, private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.initForm();
    let application = this.applicationState.application;

    this.applicationId = application.id;

    let event = this.event(application);
    this.eventForm.patchValue(EventDetailsForm.fromEvent(application, event));

    if (this.readonly) {
      this.eventForm.disable();
    }
  }

  private initForm(): void {
    this.eventForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      nature: ['', Validators.required],
      description: ['', Validators.required],
      url: [''],
      eventTimes: this.fb.group({
        startTime: [undefined, Validators.required],
        endTime: [undefined, Validators.required]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
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
      priceOverride: [undefined, ComplexValidator.greaterThanOrEqual(0)],
      priceOverrideReason: [''],
      structureArea: [undefined, ComplexValidator.greaterThanOrEqual(0)],
      structureDescription: [''],
      structureTimes: this.fb.group({
        startTime: [undefined],
        endTime: [undefined]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime'))
    });

    this.applicationForm.addControl('event', this.eventForm);
  }

  private event(application: Application): Event {
    let event = <Event>application.extension || new Event();
    event.eventStartTime = event.eventStartTime || application.startTime;
    event.eventEndTime = event.eventEndTime || application.endTime;
    event.applicationType = ApplicationType[ApplicationType.EVENT];

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    if (application.kind === ApplicationKind[ApplicationKind.PROMOTION]) {
      event.nature = EventNature[EventNature.PROMOTION];
    }

    return event;
  }
}
