import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {LocationState} from '../../../service/application/location-state';
import {Location} from '../../../model/common/location';
import * as fromRoot from '../../allu/reducers';
import {Store} from '@ngrx/store';

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

  constructor(private store: Store<fromRoot.State>,
              private locationState: LocationState) {
  }

  ngOnInit(): void {
    this.locations = this.locationState.locations;
  }

  ngOnDestroy(): void {
  }

  districtName(id: number): Observable<string> {
    return this.store.select(fromRoot.getCityDistrictName(id));
  }

  editLocation(index: number): void {
    this.locationState.editLocation(index);
  }

  remove(index: number): void {
    this.locationState.removeLocation(index);
  }
}
