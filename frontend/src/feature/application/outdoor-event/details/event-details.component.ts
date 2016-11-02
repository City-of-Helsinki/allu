import {Component, Input, OnInit, AfterViewInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {FormGroup, FormBuilder, FormControl, Validators} from '@angular/forms';

import {StructureMeta} from '../../../../model/application/structure-meta';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {ApplicationTypeData} from '../../../../model/application/type/application-type-data';
import {Location} from '../../../../model/common/location';
import {Application} from '../../../../model/application/application';
import {outdoorEventConfig} from '../outdoor-event-config';
import {OutdoorEvent} from '../../../../model/application/type/outdoor-event';
import {EventNature} from '../outdoor-event-config';
import {OutdoorEventDetailsForm} from './outdoor-event-details.form';
import {translations} from '../../../../util/translations';
import moment = require('moment/moment');
import {TimeUtil} from '../../../../util/time.util';
import {Some} from '../../../../util/option';
import {PICKADATE_PARAMETERS} from '../../../../util/time.util';
import {ComplexValidator} from '../../../../util/complex-validator';

@Component({
  selector: 'event-details',
  template: require('./event-details.component.html'),
  styles: []
})
export class EventDetailsComponent implements OnInit, AfterViewInit {
  @Input() eventName: string;
  @Input() applicationForm: FormGroup;
  @Input() location: Location;
  @Input() readonly: boolean;

  eventForm: FormGroup;
  applicationId: number;
  meta: StructureMeta;
  billingTypes: Array<any>;
  eventNatures: Array<any>;
  noPriceReasons: Array<any>;
  translations = translations;
  pickadateParams = PICKADATE_PARAMETERS;

  constructor(private applicationHub: ApplicationHub, private route: ActivatedRoute, private fb: FormBuilder) {
    this.billingTypes = outdoorEventConfig.billingTypes;
    this.eventNatures = outdoorEventConfig.eventNatures;
    this.noPriceReasons = outdoorEventConfig.noPriceReasons;

    applicationHub.metaData().subscribe(meta => this.metadataLoaded(meta));
  }

  ngOnInit(): void {
    this.initForm();

    this.route.parent.data.subscribe((data: {application: Application}) => {
      this.applicationId = data.application.id;
      this.eventForm.patchValue(OutdoorEventDetailsForm.fromOutdoorEvent(data.application.name, <OutdoorEvent>data.application.event));
    });
  }

  ngAfterViewInit(): void {
  }

  eventNatureChange(nature: string): void {
    if (EventNature.PUBLIC_FREE !== EventNature[nature]) {
      this.eventForm.patchValue({noPrice: false});
      this.noPriceChange(true);
    }
  }

  noPriceChange(noPrice: boolean): void {
    if (noPrice) {
      this.eventForm.patchValue({
        salesActivity: false,
        heavyStructure: false,
        noPriceReason: undefined
      });
    }
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.meta = metadata;
  }

  private initForm(): void {
    this.eventForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      nature: ['', Validators.required],
      description: ['', Validators.required],
      url: [''],
      type: ['', Validators.required],
      eventTimes: this.fb.group({
        startTime: ['', Validators.required],
        endTime: ['', Validators.required]
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
      timeExceptions: [''],
      attendees: undefined,
      entryFee: undefined,
      noPrice: [false],
      noPriceReason: [''],
      salesActivity: [false],
      heavyStructure: [false],
      ecoCompass: [false],
      foodSales: [false],
      foodProviders: [''],
      marketingProviders: [''],
      structureArea: undefined,
      structureDescription: [''],
      structureTimes: this.fb.group({
        startTime: [''],
        endTime: ['']
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime'))
    });

    this.applicationForm.addControl('event', this.eventForm);
  }
}
