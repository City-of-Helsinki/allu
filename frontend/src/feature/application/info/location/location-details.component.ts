import {Component, Input, OnInit, AfterViewInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {StructureMeta} from '../../../../model/application/structure-meta';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {MapHub} from '../../../../service/map-hub';
import {FixedLocation} from '../../../../model/common/fixed-location';
import {Location} from '../../../../model/common/location';

@Component({
  selector: 'location-details',
  viewProviders: [],
  template: require('./location-details.component.html'),
  styles: []
})
export class LocationDetailsComponent implements OnInit, AfterViewInit {
  @Input() applicationId: number;
  @Input() location: Location;
  @Input() readonly: boolean;

  meta: StructureMeta;
  fixedLocation: FixedLocation;

  constructor(private applicationHub: ApplicationHub, private mapHub: MapHub) {
    applicationHub.metaData().subscribe(meta => this.metadataLoaded(meta));
  }

  ngOnInit(): void {
    this.mapHub.fixedLocation(this.location.fixedLocationId).subscribe(flOpt => {
      this.fixedLocation = flOpt.orElse(new FixedLocation());
    });
  }

  ngAfterViewInit(): void {
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.meta = metadata;
  }
}
