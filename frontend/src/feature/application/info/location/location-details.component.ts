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
  area: string;
  sections: string;

  constructor(private applicationHub: ApplicationHub, private mapHub: MapHub) {
    applicationHub.metaData().subscribe(meta => this.metadataLoaded(meta));
  }

  ngOnInit(): void {
    this.mapHub.fixedLocationsBy(this.location.fixedLocationIds)
      .filter(fixedLocations => fixedLocations.length > 0)
      .subscribe(fixedLocations => {
        this.area = fixedLocations[0].area;
        this.sections = fixedLocations.map(fx => fx.section).join(', ');
      });
  }

  ngAfterViewInit(): void {
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.meta = metadata;
  }
}
