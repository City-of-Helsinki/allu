import {Component, OnInit, OnDestroy, Input} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {MapHub} from '../../../service/map/map-hub';
import {LocationState} from '../../../service/application/location-state';
import {Location} from '../../../model/common/location';

@Component({
  selector: 'stored-locations',
  viewProviders: [],
  templateUrl: './stored-locations.component.html',
  styleUrls: [
    './stored-locations.component.scss'
  ]
})
export class StoredLocationsComponent implements OnInit, OnDestroy {

  @Input() readonly = false;

  locations: Observable<Array<Location>>;

  constructor(private mapHub: MapHub,
              private locationState: LocationState) {
  }

  ngOnInit(): void {
    this.locations = this.locationState.locations;
  }

  ngOnDestroy(): void {
  }

  districtName(id: number): Observable<string> {
    return this.mapHub.districtById(id).map(d => d.name);
  }

  editLocation(index: number): void {
    this.locationState.editLocation(index);
  }

  remove(index: number): void {
    this.locationState.removeLocation(index);
  }
}
