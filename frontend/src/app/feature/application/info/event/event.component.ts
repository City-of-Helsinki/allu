import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder} from '@angular/forms';
import {Application} from '../../../../model/application/application';
import {EventForm} from './event.form';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {ApplicationStore} from '../../../../service/application/application-store';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';
import {Event} from '../../../../model/application/event/event';
import {ApplicationKind} from '../../../../model/application/type/application-kind';
import {EventNature} from '../../../../model/application/event/event-nature';
import {ProjectHub} from '../../../../service/project/project-hub';
import {TimeUtil} from '../../../../util/time.util';
import {ComplexValidator} from '../../../../util/complex-validator';
import {UnitOfTime} from 'moment';


@Component({
  selector: 'event',
  viewProviders: [],
  templateUrl: './event.component.html',
  styleUrls: []
})
export class EventComponent extends ApplicationInfoBaseComponent implements OnInit {

  constructor(fb: FormBuilder,
              route: ActivatedRoute,
              applicationStore: ApplicationStore,
              router: Router,
              projectHub: ProjectHub) {
    super(fb, route, applicationStore, router, projectHub);
  }

  ngOnInit(): any {
    super.ngOnInit();
  }

  get maxEventStartTime(): Date {
    return this.applicationForm.get('eventTimes.endTime').value;
  }

  get minEventEndTime(): Date {
    return this.applicationForm.get('eventTimes.startTime').value;
  }

  get maxBuildStart(): Date {
    const eventStart =  this.applicationForm.get('eventTimes.startTime').value;
    return TimeUtil.subract(eventStart, 1, 'day');
  }

  get minTeardownEnd(): Date {
    const eventEnd =  this.applicationForm.get('eventTimes.endTime').value;
    return TimeUtil.add(eventEnd, 1, 'day');
  }

  protected initForm() {
    const snapshot = this.applicationStore.snapshot;

    if (snapshot.application.kind === ApplicationKind.OUTDOOREVENT) {
      this.completeFormStructure = EventForm.outdoorEventForm(this.fb);
      this.draftFormStructure = EventForm.outdoorEventDraft(this.fb);
    } else {
      this.completeFormStructure = EventForm.eventForm(this.fb);
      this.draftFormStructure = EventForm.eventDraft(this.fb);
    }

    this.applicationForm = snapshot.draft
      ? this.fb.group(this.draftFormStructure)
      : this.fb.group(this.completeFormStructure);

    this.setStructureTimeValidation();
  }

  protected onApplicationChange(application: Application): void {
    super.onApplicationChange(application);
    const event = this.event(application);
    this.applicationForm.patchValue(EventForm.fromEvent(application, event));
  }

  protected update(form: EventForm): Application {
    const application = super.update(form);
    application.name = form.name;
    application.startTime = TimeUtil.toStartDate(form.structureTimes.startTime || form.eventTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.structureTimes.endTime || form.eventTimes.endTime);
    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;
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

  private setStructureTimeValidation(): void {
    const eventStart = this.applicationForm.get('eventTimes.startTime');
    const eventEnd = this.applicationForm.get('eventTimes.endTime');
    const buildStart = this.applicationForm.get('structureTimes.startTime');
    const teardownEnd = this.applicationForm.get('structureTimes.endTime');

    buildStart.setValidators(ComplexValidator.after(eventStart));
    teardownEnd.setValidators(ComplexValidator.before(eventEnd));
  }
}
