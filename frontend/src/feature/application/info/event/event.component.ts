import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router, NavigationStart} from '@angular/router';
import {FormGroup, FormBuilder} from '@angular/forms';
import {Subscription} from 'rxjs/Subscription';

import {Application} from '../../../../model/application/application';
import {StructureMeta} from '../../../../model/application/structure-meta';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {UrlUtil} from '../../../../util/url.util';
import {ApplicantForm} from '../applicant/applicant.form';
import {EventDetailsForm} from './details/event-details.form';
import {EventForm} from './event.form';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {ApplicationState} from '../../../../service/application/application-state';


@Component({
  selector: 'event',
  viewProviders: [],
  template: require('./event.component.html'),
  styles: []
})
export class EventComponent implements OnInit, OnDestroy {
  application: Application;
  applicationForm: FormGroup;
  private readonly: boolean;
  private submitPending = false;
  private routeEvents: Subscription;

  private meta: StructureMeta;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private fb: FormBuilder,
              private applicationHub: ApplicationHub,
              private applicationState: ApplicationState) {
  };

  ngOnInit(): any {
    this.applicationForm = this.fb.group({});
    this.application = this.applicationState.application;
    this.applicationHub.loadMetaData('EVENT').subscribe(meta => this.metadataLoaded(meta));

    UrlUtil.urlPathContains(this.route.parent, 'summary')
      .filter(contains => contains)
      .forEach(summary => {
        this.readonly = summary;
        this.applicationForm.disable();
      });

    this.routeEvents = this.router.events
      .filter(event => event instanceof NavigationStart)
      .subscribe(navStart => {
        if (!this.readonly) {
          this.applicationState.application = this.update(this.applicationForm.value);
        }
      });
  }

  ngOnDestroy(): any {
    this.routeEvents.unsubscribe();
  }

  onSubmit(form: EventForm) {
    this.submitPending = true;
    let application = this.update(form);

    this.applicationState.save(application)
      .subscribe(app => this.submitPending = false, err => this.submitPending = false);
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.application.metadata = metadata;
    this.meta = metadata;
  }

  private update(form: EventForm): Application {
    let application = this.application;
    application.metadata = this.meta;

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
