import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {ApplicantForm} from '../applicant/applicant.form';
import {EventDetailsForm} from './details/event-details.form';
import {EventForm} from './event.form';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {ApplicationState} from '../../../../service/application/application-state';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';


@Component({
  selector: 'event',
  viewProviders: [],
  template: require('./event.component.html'),
  styles: []
})
export class EventComponent extends ApplicationInfoBaseComponent implements OnInit {

  constructor(private fb: FormBuilder,
              private applicationHub: ApplicationHub,
              route: ActivatedRoute,
              applicationState: ApplicationState) {
    super(route, applicationState);
  };

  ngOnInit(): any {
    super.ngOnInit();
  }

  protected initForm() {
    this.applicationForm = this.fb.group({});
  }

  protected update(form: EventForm): Application {
    let application = this.application;

    application.name = form.event.name;
    application.calculatedPriceEuro = form.event.calculatedPrice;
    application.priceOverrideEuro = form.event.priceOverride;
    application.priceOverrideReason = form.event.priceOverrideReason;
    application.type = ApplicationType[ApplicationType.EVENT];
    application.applicant = ApplicantForm.toApplicant(form.applicant);
    application.extension = EventDetailsForm.toEvent(form.event, ApplicationType.EVENT);
    application.contactList = form.contacts;
    return application;
  }
}
