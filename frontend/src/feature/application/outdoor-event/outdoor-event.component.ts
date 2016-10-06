import {Component, OnDestroy, OnInit, Input, AfterViewInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';

import {Location} from '../../../model/common/location';
import {Event} from '../../../event/event';
import {EventListener} from '../../../event/event-listener';
import {Application} from '../../../model/application/application';
import {Applicant} from '../../../model/application/applicant';
import {Person} from '../../../model/common/person';
import {Organization} from '../../../model/common/organization';
import {PostalAddress} from '../../../model/common/postal-address';
import {EventService} from '../../../event/event.service';
import {ApplicationSaveEvent} from '../../../event/save/application-save-event';
import {ApplicationAddedAnnounceEvent} from '../../../event/announce/application-added-announce-event';
import {StructureMeta} from '../../../model/application/structure-meta';
import {MetaLoadEvent} from '../../../event/load/meta-load-event';
import {MetaAnnounceEvent} from '../../../event/announce/meta-announce-event';
import {TimeUtil, PICKADATE_PARAMETERS} from '../../../util/time.util';
import {OutdoorEvent} from '../../../model/application/type/outdoor-event';
import {AttachmentService} from '../../../service/attachment-service';
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
import {ApplicationsAnnounceEvent} from '../../../event/announce/applications-announce-event';
import {ApplicationSelectionEvent} from '../../../event/selection/application-selection-event';

declare var Materialize: any;

@Component({
  selector: 'outdoor-event',
  viewProviders: [],
  template: require('./outdoor-event.component.html'),
  styles: [
    require('./outdoor-event.component.scss')
  ]
})
export class OutdoorEventComponent implements EventListener, OnInit, OnDestroy, AfterViewInit {
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
  private pickadateParams = PICKADATE_PARAMETERS;

  private meta: StructureMeta;

  constructor(private eventService: EventService,
              private router: Router,
              private route: ActivatedRoute,
              private attachmentService: AttachmentService,
              private locationState: LocationState) {
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
      this.application.startTime = this.locationState.startDate;
      this.application.endTime = TimeUtil.getEndOfDay(this.locationState.endDate);

      let outdoorEvent = <OutdoorEvent>this.application.event;
      outdoorEvent.eventStartTime = this.application.startTime;
      outdoorEvent.eventEndTime = this.application.endTime;

      this.applicantNameSelection = applicantNameSelection(this.application.applicant.type);
      this.applicantIdSelection = applicantIdSelection(this.application.applicant.type);

      this.eventService.subscribe(this);
      this.eventService.send(this, new MetaLoadEvent('OUTDOOREVENT'));

      UrlUtil.urlPathContains(this.route.parent, 'summary').forEach(summary => {
        this.isSummary = summary;
      });
    });
  }

  ngOnDestroy(): any {
    this.eventService.unsubscribe(this);
  }

  ngAfterViewInit(): void {
    setTimeout(() => Materialize.updateTextFields(), 10);
    this.eventService.send(this, new ApplicationSelectionEvent(this.application));
  }

  public handle(event: Event): void {
    if (event instanceof ApplicationAddedAnnounceEvent) {
      let aaaEvent = <ApplicationAddedAnnounceEvent>event;
      console.log('Successfully added new application', aaaEvent.application);
      this.application = aaaEvent.application;

      let self = this;
      // TODO: use callback method to navigate to summary after attachment upload completes. This should be changed to use the
      // "hub approach" i.e. observable.subscribe(router.navigate...)
      if (this.attachments && this.attachments.length !== 0) {
        this.attachmentService.uploadFiles(
          aaaEvent.application.id, this.attachments,
          () => self.router.navigate(['applications', this.application.id, 'summary']));
      } else {
        this.attachmentService.uploadFiles(
          aaaEvent.application.id, this.attachments, () => { return undefined; });
        self.router.navigate(['applications', this.application.id, 'summary']);
      }
    } else if (event instanceof ApplicationsAnnounceEvent) {
      let aaaEvent = <ApplicationsAnnounceEvent>event;
      console.log('Successfully added new application', aaaEvent.applications[0]);
      this.router.navigate(['applications', this.application.id, 'summary']);
    } else if (event instanceof MetaAnnounceEvent) {
      console.log('Loaded metadata', event);
      let maEvent = <MetaAnnounceEvent>event;
      this.application.metadata = maEvent.structureMeta;
      this.meta = maEvent.structureMeta;
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
  }

  applicantTypeSelection(value: string) {
    this.applicantNameSelection = this.applicantText[value].name;
    this.applicantIdSelection = this.applicantText[value].id;
  }

  eventNatureChange(nature: string) {
    if ('Open' !== nature) {
      this.noPrice = false;
    }
  }

  save(application: Application) {
    // Save application
    console.log('Saving application', application);
    application.metadata = this.meta;
    let saveEvent = new ApplicationSaveEvent(application);
    this.eventService.send(this, saveEvent);
    this.locationState.clear();
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

  private currentAttachments(attachments: AttachmentInfo[]): void {
    this.attachments = attachments;
  }
}
