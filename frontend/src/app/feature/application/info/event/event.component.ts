import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';
import {Application} from '../../../../model/application/application';
import {EventForm} from './event.form';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {ApplicationStore} from '../../../../service/application/application-store';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {Event} from '../../../../model/application/event/event';
import {ApplicationKind} from '../../../../model/application/type/application-kind';
import {EventNature} from '../../../../model/application/event/event-nature';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ProjectHub} from '../../../../service/project/project-hub';
import {TimeUtil} from '../../../../util/time.util';


@Component({
  selector: 'event',
  viewProviders: [],
  templateUrl: './event.component.html',
  styleUrls: []
})
export class EventComponent extends ApplicationInfoBaseComponent implements OnInit {

  constructor(
    fb: FormBuilder,
    route: ActivatedRoute,
    applicationStore: ApplicationStore,
    router: Router,
    projectHub: ProjectHub) {
    super(fb, route, applicationStore, router, projectHub);
  }

  ngOnInit(): any {
    super.ngOnInit();
  }

  protected initForm() {
    this.applicationForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      nature: [''],
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
      structureArea: [undefined, ComplexValidator.greaterThanOrEqual(0)],
      structureDescription: [''],
      structureTimes: this.fb.group({
        startTime: [undefined],
        endTime: [undefined]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime'))
    });
  }


  protected onApplicationChange(application: Application): void {
    super.onApplicationChange(application);

    const natureElement = this.applicationForm.get('nature')
    if (application.kind === 'OUTDOOREVENT') {
      natureElement.setValidators([Validators.required])
    } else {
      natureElement.clearValidators();
    }

    const event = this.event(application);
    this.applicationForm.patchValue(EventForm.fromEvent(application, event));
  }

  protected update(form: EventForm): Application {
    const application = super.update(form);
    application.name = form.name;
    application.startTime = TimeUtil.toStartDate(form.structureTimes.startTime || form.eventTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.structureTimes.endTime || form.eventTimes.endTime);
    application.type = ApplicationType[ApplicationType.EVENT];
    application.extension = EventForm.toEvent(form, ApplicationType.EVENT);
    return application;
  }

  private event(application: Application): Event {
    const event = <Event>application.extension || new Event();
    event.eventStartTime = TimeUtil.toStartDate(event.eventStartTime || application.startTime);
    event.eventEndTime = TimeUtil.toEndDate(event.eventEndTime || application.endTime);
    event.applicationType = ApplicationType[ApplicationType.EVENT];

    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    if (application.kind === ApplicationKind[ApplicationKind.PROMOTION]) {
      event.nature = EventNature[EventNature.PROMOTION];
    }

    return event;
  }
}
