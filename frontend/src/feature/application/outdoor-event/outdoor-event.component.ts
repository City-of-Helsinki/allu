import {Component, OnDestroy, OnInit, Input, AfterViewInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {FormGroup, FormBuilder, FormControl, Validators} from '@angular/forms';

import {Location} from '../../../model/common/location';
import {Application} from '../../../model/application/application';
import {StructureMeta} from '../../../model/application/structure-meta';
import {TimeUtil, PICKADATE_PARAMETERS} from '../../../util/time.util';
import {OutdoorEvent} from '../../../model/application/type/outdoor-event';
import {AttachmentInfo} from '../../../model/application/attachment-info';
import {LocationState} from '../../../service/application/location-state';
import {ApplicationHub} from '../../../service/application/application-hub';
import {UrlUtil} from '../../../util/url.util';
import {Subscription} from 'rxjs/Subscription';
import {MapHub} from '../../../service/map-hub';
import {ApplicationStatus} from '../../../model/application/application-status-change';
import {ApplicationAttachmentHub} from '../attachment/application-attachment-hub';
import {ApplicantForm} from '../applicant/applicant.form';
import {OutdoorEventDetailsForm} from './details/outdoor-event-details.form';
import {OutdoorEventForm} from './outdoor-event.form';
import {EnumUtil} from '../../../util/enum.util';
import {ApplicationType} from '../../../model/application/type/application-type';

declare var Materialize: any;

@Component({
  selector: 'outdoor-event',
  viewProviders: [],
  template: require('./outdoor-event.component.html'),
  styles: [
    require('./outdoor-event.component.scss')
  ]
})
export class OutdoorEventComponent implements OnInit, OnDestroy, AfterViewInit {
  application: Application;
  applicationForm: FormGroup;
  private isSummary: boolean;
  private events = EnumUtil.enumValues(ApplicationType);
  private attachments: AttachmentInfo[];
  private uploadProgress = 0;
  private submitted = false;

  private meta: StructureMeta;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private fb: FormBuilder,
              private locationState: LocationState,
              private applicationHub: ApplicationHub,
              private mapHub: MapHub,
              private attachmentHub: ApplicationAttachmentHub) {
  };

  ngOnInit(): any {
    this.route.parent.data
      .map((data: {application: Application}) => data.application)
      .subscribe(application => {
        this.application = application;
        this.application.location = this.application.location || this.locationState.location;

        // TODO: mismatch here. Date+time should be used in location too.
        let defaultDate = new Date();
        this.application.startTime = this.locationState.startDate || this.application.startTime || defaultDate;
        this.application.endTime = TimeUtil.getEndOfDay(this.locationState.endDate || this.application.endTime || defaultDate);

        this.applicationHub.loadMetaData('OUTDOOREVENT').subscribe(meta => this.metadataLoaded(meta));

        UrlUtil.urlPathContains(this.route.parent, 'summary').forEach(summary => {
          this.isSummary = summary;
        });

        this.applicationForm = this.fb.group({});
      });
  }

  ngOnDestroy(): any {
  }

  ngAfterViewInit(): void {
    setTimeout(() => Materialize.updateTextFields(), 50);
    this.mapHub.selectApplication(this.application);
  }

  currentAttachments(attachments: AttachmentInfo[]): void {
    this.attachments = attachments;
  }

  onSubmit(form: OutdoorEventForm) {
    let application = this.application;
    application.metadata = this.meta;

    application.name = form.event.name;
    application.type = 'OUTDOOREVENT';
    application.applicant = ApplicantForm.toApplicant(form.applicant);
    application.event = OutdoorEventDetailsForm.toOutdoorEvent(form.event);
    application.contactList = form.contacts;

    this.applicationHub.save(application).subscribe(app => {
      console.log('application saved');
      this.locationState.clear();
      this.saveAttachments(app);
    });
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.application.metadata = metadata;
    this.meta = metadata;
  }

  private saveAttachments(application: Application) {
    this.submitted = true;
    this.attachmentHub.upload(application.id, this.attachments)
      .subscribe(
        progress => { this.uploadProgress = progress; },
        error => {
          console.log('Error', error);
          this.submitted = false;
        },
        () => this.router.navigate(['applications', application.id, 'summary'])
      );
  }
}
