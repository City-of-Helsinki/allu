import {Component, Input, OnInit, AfterViewInit} from '@angular/core';

import {StructureMeta} from '../../../../model/application/structure-meta';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {MapHub} from '../../../../service/map/map-hub';
import {Application} from '../../../../model/application/application';
import {Location} from '../../../../model/common/location';
import {MaterializeUtil} from '../../../../util/materialize.util';

@Component({
  selector: 'location-details',
  viewProviders: [],
  template: require('./location-details.component.html'),
  styles: []
})
export class LocationDetailsComponent implements OnInit, AfterViewInit {
  @Input() application: Application;
  @Input() readonly: boolean;
  location: Location;

  meta: StructureMeta;
  area: string;
  sections: string;

  constructor(private applicationHub: ApplicationHub, private mapHub: MapHub) {
    applicationHub.metaData().subscribe(meta => this.metadataLoaded(meta));
  }

  ngOnInit(): void {
    this.location = this.application.location;
    this.mapHub.fixedLocationsBy(this.location.fixedLocationIds)
      .filter(fixedLocations => fixedLocations.length > 0)
      .subscribe(fixedLocations => {
        this.area = fixedLocations[0].area;
        this.sections = fixedLocations.map(fx => fx.section).join(', ');
      });
  }

  ngAfterViewInit(): void {
    this.mapHub.selectApplication(this.application);
    MaterializeUtil.updateTextFields(50);
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.meta = metadata;
  }
}
