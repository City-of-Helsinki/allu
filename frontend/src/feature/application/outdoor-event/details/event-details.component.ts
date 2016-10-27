import {Component, Input, OnInit, AfterViewInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';

import {StructureMeta} from '../../../../model/application/structure-meta';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {ApplicationTypeData} from '../../../../model/application/type/application-type-data';
import {Location} from '../../../../model/common/location';
import {Application} from '../../../../model/application/application';

@Component({
  selector: 'event-details',
  template: require('./event-details.component.html'),
  styles: []
})
export class EventDetailsComponent implements OnInit, AfterViewInit {
  @Input() eventName: string;
  @Input() event: ApplicationTypeData;
  @Input() location: Location;
  @Input() readonly: boolean;

  applicationId: number;
  meta: StructureMeta;
  billingTypes: Array<any>;
  eventNatures: Array<any>;
  noPriceReasons: Array<any>;

  private _noPrice = false;

  constructor(private applicationHub: ApplicationHub, private route: ActivatedRoute) {
    applicationHub.metaData().subscribe(meta => this.metadataLoaded(meta));
  }

  ngOnInit(): void {
    this.route.parent.data.subscribe((data: {application: Application}) => this.applicationId = data.application.id);
  }

  ngAfterViewInit(): void {
    setTimeout(() => Materialize.updateTextFields(), 10);
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.meta = metadata;
  }
}
