import {Component, OnDestroy, OnInit, AfterViewInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormGroup, FormBuilder} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {StructureMeta} from '../../../../model/application/structure-meta';
import {AttachmentInfo} from '../../../../model/application/attachment-info';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {UrlUtil} from '../../../../util/url.util';
import {MapHub} from '../../../../service/map-hub';
import {ApplicantForm} from '../applicant/applicant.form';
import {EventDetailsForm} from './details/event-details.form';
import {EventForm} from './event.form';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {MaterializeUtil} from '../../../../util/materialize.util';
import {ApplicationState} from '../../../../service/application/application-state';

@Component({
  selector: 'event',
  viewProviders: [],
  template: require('./event.component.html'),
  styles: []
})
export class EventComponent implements OnInit, OnDestroy, AfterViewInit {
  application: Application;
  applicationForm: FormGroup;
  private readonly: boolean;
  private submitPending = false;

  private meta: StructureMeta;

  constructor(private route: ActivatedRoute,
              private fb: FormBuilder,
              private applicationHub: ApplicationHub,
              private mapHub: MapHub,
              private applicationState: ApplicationState) {
  };

  ngOnInit(): any {
    this.route.parent.data
      .map((data: {application: Application}) => data.application)
      .subscribe(application => {
        this.application = application;

        this.applicationHub.loadMetaData('EVENT').subscribe(meta => this.metadataLoaded(meta));

        UrlUtil.urlPathContains(this.route.parent, 'summary').forEach(summary => {
          this.readonly = summary;
        });

        this.applicationForm = this.fb.group({});

        if (this.readonly) {
          this.applicationForm.disable();
        }
      });
  }

  ngOnDestroy(): any {
  }

  ngAfterViewInit(): void {
    MaterializeUtil.updateTextFields(50);
    this.mapHub.selectApplication(this.application);
  }

  currentAttachments(attachments: AttachmentInfo[]): void {
    this.applicationState.attachments = attachments;
  }

  onSubmit(form: EventForm) {
    this.submitPending = true;
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

    this.applicationState.save(application)
      .subscribe(app => this.submitPending = false, err => this.submitPending = false);
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.application.metadata = metadata;
    this.meta = metadata;
  }
}
