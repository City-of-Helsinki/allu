import {Component, OnInit} from '@angular/core';
import {Application} from '@model/application/application';
import {eventDraft, EventForm, eventForm, fromEvent, outdoorEventDraft, outdoorEventForm, toEvent} from './event.form';
import {ApplicationType} from '@model/application/type/application-type';
import {ApplicationInfoBaseComponent} from '@feature/application/info/application-info-base.component';
import {Event} from '@model/application/event/event';
import {ApplicationKind} from '@model/application/type/application-kind';
import {EventNature} from '@model/application/event/event-nature';
import {TimeUtil} from '@util/time.util';
import {ComplexValidator} from '@util/complex-validator';
import {UntypedFormGroup} from '@angular/forms';

@Component({
  selector: 'event',
  viewProviders: [],
  templateUrl: './event.component.html',
  styleUrls: []
})
export class EventComponent extends ApplicationInfoBaseComponent implements OnInit {

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

  onStructureTimeRequiredChanged(): void {
    this.applicationForm.patchValue({structureTimes: {startTime: undefined, endTime: undefined}});
  }

  protected initForm() {
    super.initForm();
    this.setStructureTimeValidation();
  }


  protected createExtensionForm(): UntypedFormGroup {
    const snapshot = this.applicationStore.snapshot;
    this.initFormStructures(snapshot.application);
    return this.fb.group(this.getExtensionFormStructure(snapshot.draft));
  }

  private initFormStructures(application: Application) {
    if (application.kind === ApplicationKind.OUTDOOREVENT) {
      this.completeFormStructure = outdoorEventForm(this.fb);
      this.draftFormStructure = outdoorEventDraft(this.fb);
    } else {
      this.completeFormStructure = eventForm(this.fb);
      this.draftFormStructure = eventDraft(this.fb);
    }
  }

  private getExtensionFormStructure(draft: boolean): ({ [key: string]: any; }) {
    return draft
      ? this.draftFormStructure
      : this.completeFormStructure;
  }

  protected onApplicationChange(application: Application): void {
    super.onApplicationChange(application);
    const event = this.event(application);
    this.applicationForm.patchValue(fromEvent(application, event));
  }

  protected update(form: EventForm): Application {
    const application = super.update(form);
    application.startTime = TimeUtil.toStartDate(form.structureTimes.startTime || form.eventTimes.startTime);
    application.endTime = TimeUtil.toEndDate(form.structureTimes.endTime || form.eventTimes.endTime);
    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;
    application.type = ApplicationType.EVENT;
    application.extension = toEvent(form, ApplicationType.EVENT);
    return application;
  }

  private event(application: Application): Event {
    const event = <Event>application.extension || new Event();
    event.eventStartTime = TimeUtil.toStartDate(event.eventStartTime || application.startTime);
    event.eventEndTime = TimeUtil.toEndDate(event.eventEndTime || application.endTime);
    event.applicationType = ApplicationType[ApplicationType.EVENT];
    application.singleLocation.startTime = application.startTime;
    application.singleLocation.endTime = application.endTime;

    if (application.kind === ApplicationKind.PROMOTION) {
      event.nature = EventNature[EventNature.PROMOTION];
    } else if (application.kind === ApplicationKind.BIG_EVENT) {
      event.nature = EventNature[EventNature.BIG_EVENT];
    }
    return event;
  }

  private setStructureTimeValidation(): void {
    const eventStart = this.applicationForm.get('eventTimes.startTime');
    const eventEnd = this.applicationForm.get('eventTimes.endTime');
    const buildStart = this.applicationForm.get('structureTimes.startTime');
    const teardownEnd = this.applicationForm.get('structureTimes.endTime');

    buildStart.setValidators(ComplexValidator.before(eventStart));
    teardownEnd.setValidators(ComplexValidator.after(eventEnd));
  }
}
