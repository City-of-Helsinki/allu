import {Component, Input, OnInit, AfterViewInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';

import {MapHub} from '../../../../service/map/map-hub';
import {Application} from '../../../../model/application/application';
import {Location} from '../../../../model/common/location';
import {ApplicationKind} from '../../../../model/application/type/application-kind';
import {ArrayUtil} from '../../../../util/array-util';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {LocationState} from '../../../../service/application/location-state';

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

  area: string;
  sections: string;
  multipleLocations: boolean = false;

  constructor(private mapHub: MapHub, private locationState: LocationState) {
  }

  ngOnInit(): void {
    this.location = this.application.firstLocation;
    this.locationState.initLocations(this.application.locations);
    this.multipleLocations = this.application.type === ApplicationType[ApplicationType.AREA_RENTAL];
    // Sections can be selected only from single area so we can
    // get area based on its sections
    this.mapHub.fixedLocationAreasBySectionIds(this.location.fixedLocationIds)
      .filter(areas => areas.length > 0)
      .map(areas => areas[0])
      .subscribe(area => this.area = area.name);

    this.mapHub.fixedLocationSectionsBy(this.location.fixedLocationIds)
      .map(sections => sections.map(s => s.name))
      .map(names => names.sort(ArrayUtil.naturalSort((name: string) => name)))
      .map(names => names.join(', '))
      .subscribe(sectionNames => this.sections = sectionNames);

    this.mapHub.editedLocation().subscribe(loc => this.editLocation(loc));
  }

  ngAfterViewInit(): void {
    this.mapHub.selectApplication(this.application);
  }

  districtName(id: number): Observable<string> {
    return this.mapHub.districtById(id).map(d => d.name);
  }

  private editLocation(loc: Location): void {
    if (!!loc) {
      this.location = loc;
    }
  }
}
