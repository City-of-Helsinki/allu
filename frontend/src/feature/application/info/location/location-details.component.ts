import {Component, Input, OnInit, AfterViewInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';

import {MapHub} from '../../../../service/map/map-hub';
import {Application} from '../../../../model/application/application';
import {Location} from '../../../../model/common/location';

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

  constructor(private mapHub: MapHub) {
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
  }

  districtName(id: number): Observable<string> {
    return this.mapHub.districtById(id).map(d => d.name);
  }
}
