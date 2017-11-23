import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder} from '@angular/forms';
import {Application} from '../../../../model/application/application';
import {EventDetailsForm} from './details/event-details.form';
import {EventForm} from './event.form';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {ApplicationState} from '../../../../service/application/application-state';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';


@Component({
  selector: 'event',
  viewProviders: [],
  templateUrl: './event.component.html',
  styleUrls: []
})
export class EventComponent extends ApplicationInfoBaseComponent implements OnInit {

  constructor(fb: FormBuilder, route: ActivatedRoute, applicationState: ApplicationState) {
    super(fb, route, applicationState);
  };

  ngOnInit(): any {
    super.ngOnInit();
  }

  protected initForm() {
    this.applicationForm = this.fb.group({});
  }

  protected update(form: EventForm): Application {
    let application = super.update(form);

    application.name = form.event.name;
    application.startTime = form.event.structureTimes.startTime || form.event.eventTimes.startTime;
    application.endTime = form.event.structureTimes.endTime || form.event.eventTimes.endTime;
    application.calculatedPriceEuro = form.event.calculatedPrice;
    application.type = ApplicationType[ApplicationType.EVENT];
    application.extension = EventDetailsForm.toEvent(form.event, ApplicationType.EVENT);
    return application;
  }
}
