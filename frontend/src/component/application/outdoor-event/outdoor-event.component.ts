import {Component, OnDestroy, OnInit, Input} from '@angular/core';
import {FORM_DIRECTIVES} from '@angular/common';
import {Router, ROUTER_DIRECTIVES} from '@angular/router';

import {MD_INPUT_DIRECTIVES} from '@angular2-material/input';
import {MdAnchor, MdButton} from '@angular2-material/button';
import {MD_CARD_DIRECTIVES} from '@angular2-material/card';
import {MdToolbar} from '@angular2-material/toolbar';
import {MdRadioButton} from '@angular2-material/radio';
import {MdRadioDispatcher} from '@angular2-material/radio/radio_dispatcher';
import {MdCheckbox} from '@angular2-material/checkbox';

import {MaterializeDirective} from 'angular2-materialize';

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
import {LoadingComponent} from '../../loading/loading.component';
import {TimeUtil, PICKADATE_PARAMETERS} from '../../../util/time.util';
import {OutdoorEvent} from '../../../model/application/type/outdoor-event';
import {ApplicationAttachmentComponent} from '../attachment/application-attachment.component';
import {AttachmentService} from '../../../service/attachment-service';
import {AttachmentInfo} from '../../../model/application/attachment-info';
import {LocationState} from '../../../service/application/location-state';


@Component({
  selector: 'outdoor-event',
  viewProviders: [],
  moduleId: module.id,
  template: require('./outdoor-event.component.html'),
  styles: [
    require('./outdoor-event.component.scss')
  ],
  directives: [
    ROUTER_DIRECTIVES,
    MaterializeDirective,
    MD_INPUT_DIRECTIVES,
    MD_CARD_DIRECTIVES,
    MdToolbar,
    MdButton,
    MdRadioButton,
    MdCheckbox,
    LoadingComponent,
    ApplicationAttachmentComponent
  ],
  providers: [MdRadioDispatcher]
})

export class OutdoorEventComponent implements EventListener, OnInit, OnDestroy {
  private _noPrice = false;

  private application: Application;
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

  constructor(private eventService: EventService, private router: Router, private attachmentService: AttachmentService,
              private locationState: LocationState) {
    // TODO:remove preFilledApplication
    this.application = Application.prefilledApplication();
    this.application.location = locationState.location;
    // TODO: mismatch here. Date+time should be used in location too.
    this.application.startTime = locationState.startDate;
    this.application.endTime = TimeUtil.getEndOfDay(locationState.endDate);

    let outdoorEvent = <OutdoorEvent>this.application.event;
    outdoorEvent.eventStartTime = this.application.startTime;
    outdoorEvent.eventEndTime = this.application.endTime;

    console.log('outdoor-event.application', this.application);

    this.events = [
      {name: 'Ulkoilmatapahtuma', value: 'OutdoorEvent'},
      {name: 'Muu', value: 'Other'}
    ];
    this.applicantType = [
      {name: 'Yritys', value: 'COMPANY', text: 'Yrityksen nimi'},
      {name: 'Yhdistys', value: 'ASSOCIATION', text: 'Yhdistyksen nimi'},
      {name: 'Yksityishenkilö', value: 'PERSON', text: 'Yksityishenkilön nimi'}
    ];
    this.applicantText = {
      'DEFAULT': {
        name: 'Hakijan nimi',
        id: 'Y-tunnus'},
      'COMPANY': {
        name: 'Yrityksen nimi',
        id: 'Y-tunnus'},
      'ASSOCIATION': {
        name: 'Yhdistyksen nimi',
        id: 'Y-tunnus'},
      'PERSON': {
        name: 'Henkilön nimi',
        id: 'Henkilötunnus'}
    };

    let def = 'DEFAULT';
    this.applicantNameSelection = this.application.applicant.type
      ? this.applicantText[this.application.applicant.type].name : this.applicantText[def].name;
    this.applicantIdSelection = this.application.applicant.type
      ? this.applicantText[this.application.applicant.type].id : this.applicantText[def].id;

    this.applicant = {
      id: this.application.applicant && this.application.applicant.id || undefined,
      type: this.application.applicant && this.application.applicant.type || undefined,
      representative: this.application.applicant && this.application.applicant.representative || undefined,
      name: undefined,
      businessIdOrSsn: undefined,
      streetAddress: undefined,
      postalCode: undefined,
      city: undefined,
      email: undefined,
      phone: undefined
    };

    if (this.application.applicant) {
      if (this.application.applicant.person) {
        this.applicant.name = this.application.applicant.person.name;
        this.applicant.businessIdOrSsn = this.application.applicant.person.ssn;
        this.applicant.streetAddress = this.application.applicant.person.postalAddress.streetAddress;
        this.applicant.postalCode = this.application.applicant.person.postalAddress.postalCode;
        this.applicant.city = this.application.applicant.person.postalAddress.city;
        this.applicant.email = this.application.applicant.person.email;
        this.applicant.phone = this.application.applicant.person.phone;
      }
      if (this.application.applicant.organization) {
        this.applicant.name = this.application.applicant.organization.name;
        this.applicant.businessIdOrSsn = this.application.applicant.organization.businessId;
        this.applicant.streetAddress = this.application.applicant.organization.postalAddress.streetAddress;
        this.applicant.postalCode = this.application.applicant.organization.postalAddress.postalCode;
        this.applicant.city = this.application.applicant.organization.postalAddress.city;
        this.applicant.email = this.application.applicant.organization.email;
        this.applicant.phone = this.application.applicant.organization.phone;
      }
    }

    this.countries = [
      {name: 'Suomi', value: 'Finland'},
      {name: 'Ruotsi', value: 'Sweden'},
      {name: 'Venäjä', value: 'Russia'},
      {name: 'Viro', value: 'Estonia'}
    ];
    this.billingTypes = [
      {name: 'Käteinen', value: 'Cash'},
      {name: 'Lasku', value: 'Invoice'},
      {name: 'Suoravelotus', value: 'BankTransaction'}
    ];

    this.eventNatures = [
      {name: 'Avoin', value: 'Open'},
      {name: 'Maksullinen', value: 'Paid'},
      {name: 'Suljettu', value: 'Closed'}
    ];

    this.noPriceReasons = [
      {name: 'Hyväntekeväisyys- tai kansalaisjärjestö tai oppilaistoksen tapahtuma', value: 'Charity'},
      {name: 'Taide- tai kulttuuritapahtuma', value: 'ArtOrCulture'},
      {name: 'Avoin ja maksuton urheilutapahtuma', value: 'NoFeeSporting'},
      {name: 'Asukas- tai kaupunginosayhdistyksen tapahtuma', value: 'ResidentOrCity'},
      {name: 'Aatteellinen, hengellinen tai yhteiskunnallinen tapahtuma', value: 'Spiritual'},
      {name: 'Kaupunki isäntäjä tai järjestäjäkumppanina', value: 'City'},
      {name: 'Tilataideteos', value: 'Art'},
      {name: 'Nuorisojärjestön tapahtuma', value: 'Youth'},
      {name: 'Yksityishenkilön järjestämä merkkipäiväjuhla tai vastaava', value: 'PrivateFunction'},
      {name: 'Puolustus- tai poliisivoimien tapahtuma', value: 'DefenceOrPolice'}
    ];
  };

  ngOnInit(): any {
    this.eventService.subscribe(this);
    this.eventService.send(this, new MetaLoadEvent('OUTDOOREVENT'));
  }

  ngOnDestroy(): any {
    this.eventService.unsubscribe(this);
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
          aaaEvent.application.id, this.attachments, () => self.router.navigate(['/Summary', {id: this.application.id}]));
      } else {
        this.attachmentService.uploadFiles(
          aaaEvent.application.id, this.attachments, () => { return undefined; });
        self.router.navigate(['/summary', this.application.id]);
      }

    } else if (event instanceof MetaAnnounceEvent) {
      console.log('Loaded metadata', event);
      let maEvent = <MetaAnnounceEvent>event;
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
      let def = 'DEFAULT';
      this.applicantNameSelection = this.application.applicant.type
        ? this.applicantText[this.application.applicant.type].name : this.applicantText[def].name;
      this.applicantIdSelection = this.application.applicant.type
        ? this.applicantText[this.application.applicant.type].id : this.applicantText[def].id;
    }
  }

  applicantTypeSelection(value: string) {
    this.applicantNameSelection = this.applicantText[value].name;
    this.applicantIdSelection = this.applicantText[value].id;
  }

  save(application: Application) {
    let postalAddress = new PostalAddress(this.applicant.streetAddress, this.applicant.postalCode, this.applicant.city);

    if (this.applicant.type === 'PERSON') {
      let person = new Person(
        undefined,
        this.applicant.name,
        this.applicant.businessIdOrSsn,
        postalAddress,
        this.applicant.email,
        this.applicant.phone);
      this.application.applicant = new Applicant(
        this.applicant.id,
        this.applicant.type,
        this.applicant.representative || false,
        person,
        undefined);
    } else {
      let organization = new Organization(
        undefined,
        this.applicant.name,
        this.applicant.businessIdOrSsn,
        postalAddress,
        this.applicant.email,
        this.applicant.phone);
      this.application.applicant = new Applicant(
        this.applicant.id,
        this.applicant.type,
        this.applicant.representative || false,
        undefined,
        organization);
    }

    // TODO: We are not checking the ID's of the person and organization objects.

    // Save application
    console.log('Saving application', application);
    application.metadata = this.meta;
    application.startTime = this.locationState.startDate;
    application.endTime = this.locationState.endDate;
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
