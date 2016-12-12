import {Component, OnDestroy, OnInit, AfterViewInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {FormGroup, FormBuilder} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {StructureMeta} from '../../../../model/application/structure-meta';
import {AttachmentInfo} from '../../../../model/application/attachment-info';
import {LocationState} from '../../../../service/application/location-state';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {UrlUtil} from '../../../../util/url.util';
import {MapHub} from '../../../../service/map-hub';
import {ApplicationAttachmentHub} from '../attachment/application-attachment-hub';
import {ApplicantForm} from '../applicant/applicant.form';
import {EventDetailsForm} from './details/event-details.form';
import {EventForm} from './event.form';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {MaterializeUtil} from '../../../../util/materialize.util';
import {Some} from '../../../../util/option';
import {ProjectHub} from '../../../../service/project/project-hub';

@Component({
  selector: 'event',
  viewProviders: [],
  template: require('./event.component.html'),
  styles: []
})
export class EventComponent implements OnInit, OnDestroy, AfterViewInit {
  application: Application;
  applicationForm: FormGroup;
  private isSummary: boolean;
  private attachments: AttachmentInfo[];
  private uploadProgress = 0;
  private submitPending = false;

  private meta: StructureMeta;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private fb: FormBuilder,
              private locationState: LocationState,
              private applicationHub: ApplicationHub,
              private projectHub: ProjectHub,
              private mapHub: MapHub,
              private attachmentHub: ApplicationAttachmentHub) {
  };

  ngOnInit(): any {
    this.route.parent.data
      .map((data: {application: Application}) => data.application)
      .subscribe(application => {
        this.application = application;

        this.applicationHub.loadMetaData('EVENT').subscribe(meta => this.metadataLoaded(meta));

        UrlUtil.urlPathContains(this.route.parent, 'summary').forEach(summary => {
          this.isSummary = summary;
        });

        this.applicationForm = this.fb.group({});
      });
  }

  ngOnDestroy(): any {
  }

  ngAfterViewInit(): void {
    MaterializeUtil.updateTextFields(50);
    this.mapHub.selectApplication(this.application);
  }

  currentAttachments(attachments: AttachmentInfo[]): void {
    this.attachments = attachments;
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

    this.applicationHub.save(application).subscribe(app => {
      this.saveAttachments(app);
      this.locationState.clear();
    });
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.application.metadata = metadata;
    this.meta = metadata;
  }

  private saveAttachments(application: Application) {
    this.attachmentHub.upload(application.id, this.attachments)
      .subscribe(
        progress => { this.uploadProgress = progress; },
        error => {
          console.log('Error', error);
          this.submitPending = false;
        },
        () => this.saved(application));
  }

  private saved(application: Application): void {
    // TODO: move to some common place when refactoring application page
    // We had related project so navigate back to project page
    Some(this.locationState.relatedProject)
      .do(projectId => this.projectHub.addProjectApplication(projectId, application.id).subscribe(project =>
        this.router.navigate(['/projects', project.id])));

    this.router.navigate(['applications', application.id, 'summary']);
  }
}
