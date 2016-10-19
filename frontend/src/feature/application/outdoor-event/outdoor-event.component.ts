import {Component, OnDestroy, OnInit, Input, AfterViewInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';

import {Location} from '../../../model/common/location';
import {Application} from '../../../model/application/application';
import {Applicant} from '../../../model/application/applicant';
import {Person} from '../../../model/common/person';
import {Organization} from '../../../model/common/organization';
import {PostalAddress} from '../../../model/common/postal-address';
import {StructureMeta} from '../../../model/application/structure-meta';
import {TimeUtil, PICKADATE_PARAMETERS} from '../../../util/time.util';
import {OutdoorEvent} from '../../../model/application/type/outdoor-event';
import {AttachmentInfo} from '../../../model/application/attachment-info';
import {LocationState} from '../../../service/application/location-state';
import {outdoorEventConfig} from './outdoor-event-config';
import {DEFAULT_APPLICANT} from './outdoor-event-config';
import {applicantNameSelection} from './outdoor-event-config';
import {applicantIdSelection} from './outdoor-event-config';
import {ApplicationHub} from '../../../service/application/application-hub';
import {UrlUtil} from '../../../util/url.util';
import {Subscription} from 'rxjs/Subscription';
import {MapHub} from '../../../service/map-hub';
import {ApplicationStatus} from '../../../model/application/application-status-change';
import {ApplicationAttachmentHub} from '../attachment/application-attachment-hub';
import {EventNature} from './outdoor-event-config';

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
  private application: Application;
  private isSummary: boolean;
  private _noPrice = false;
  private events: Array<any>;
  private applicantType: any;
  private applicantText: any;
  private applicant: any;
  private applicantNameSelection: string;
  private applicantIdSelection: string;
  private countries: Array<any>;
  private billingTypes: Array<any>;
  private eventNatures: Array<any>;
  private pricingTypes: Array<any>;
  private noPriceReasons: Array<any>;
  private attachments: AttachmentInfo[];
  private uploadProgress = 0;
  private submitted = false;
  private pickadateParams = PICKADATE_PARAMETERS;

  private meta: StructureMeta;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private locationState: LocationState,
              private applicationHub: ApplicationHub,
              private mapHub: MapHub,
              private attachmentHub: ApplicationAttachmentHub) {
    this.events = outdoorEventConfig.events;
    this.applicantType = outdoorEventConfig.applicantType;
    this.applicantText = outdoorEventConfig.applicantText;
    this.countries = outdoorEventConfig.countries;
    this.billingTypes = outdoorEventConfig.billingTypes;
    this.eventNatures = outdoorEventConfig.eventNatures;
    this.noPriceReasons = outdoorEventConfig.noPriceReasons;
  };

  ngOnInit(): any {
    this.route.parent.data.subscribe((data: {application: Application}) => {
      this.application = data.application;
      this.application.location = this.application.location || this.locationState.location;

      // TODO: mismatch here. Date+time should be used in location too.
      this.application.startTime = this.locationState.startDate || this.application.startTime;
      this.application.endTime = TimeUtil.getEndOfDay(this.locationState.endDate);

      let outdoorEvent = <OutdoorEvent>this.application.event;
      outdoorEvent.eventStartTime = this.application.startTime;
      outdoorEvent.eventEndTime = this.application.endTime;

      this.applicantNameSelection = applicantNameSelection(this.application.applicant.type);
      this.applicantIdSelection = applicantIdSelection(this.application.applicant.type);

      this.applicationHub.loadMetaData('OUTDOOREVENT').subscribe(meta => this.metadataLoaded(meta));

      UrlUtil.urlPathContains(this.route.parent, 'summary').forEach(summary => {
        this.isSummary = summary;
      });
    });
  }

  ngOnDestroy(): any {
  }

  ngAfterViewInit(): void {
    setTimeout(() => Materialize.updateTextFields(), 10);
    this.mapHub.selectApplication(this.application);
  }

  applicantTypeSelection(value: string) {
    this.applicantNameSelection = this.applicantText[value].name;
    this.applicantIdSelection = this.applicantText[value].id;
  }

  eventNatureChange(nature: string) {
    if (EventNature.PUBLIC_FREE !== EventNature[nature]) {
      this.noPrice = false;
    }
  }

  save(application: Application) {
    // Save application
    console.log('Saving application', application);
    application.metadata = this.meta;
    this.applicationHub.save(application).subscribe(app => {
      console.log('application saved');
      this.locationState.clear();
      this.saveAttachments(app);
    });
   }

  @Input()
  set noPrice(noPrice: boolean) {
    this._noPrice = noPrice;

    if (!noPrice) {
      let event = <OutdoorEvent>this.application.event;
      event.salesActivity = false;
      event.heavyStructure = false;
    }
  }

  get noPrice() {
    return this._noPrice;
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.application.metadata = metadata;
    this.meta = metadata;
    this.applicantText = {
      'DEFAULT': {
        name: 'Hakijan nimi',
        id: this.meta.getUiName('applicant.businessId')},
      'COMPANY': {
        name: this.meta.getUiName('applicant.companyName'),
        id: this.meta.getUiName('applicant.businessId')},
      'ASSOCIATION': {
        name: this.meta.getUiName('applicant.organizationName'),
        id: this.meta.getUiName('applicant.businessId')},
      'PERSON': {
        name: this.meta.getUiName('applicant.personName'),
        id: this.meta.getUiName('applicant.ssn')}
    };
    this.applicantNameSelection = applicantNameSelection(this.application.applicant.type);
    this.applicantIdSelection = applicantIdSelection(this.application.applicant.type);
  }

  private currentAttachments(attachments: AttachmentInfo[]): void {
    this.attachments = attachments;
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
