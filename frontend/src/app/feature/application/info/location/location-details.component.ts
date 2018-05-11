import {AfterViewInit, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';

import {MapStore} from '../../../../service/map/map-store';
import {Application} from '../../../../model/application/application';
import {Location} from '../../../../model/common/location';
import {ArrayUtil} from '../../../../util/array-util';
import {ApplicationType} from '../../../../model/application/type/application-type';
import {LocationState} from '../../../../service/application/location-state';
import {FixedLocationService} from '../../../../service/map/fixed-location.service';
import {applicationCanBeEdited} from '../../../../model/application/application-status';
import {MODIFY_ROLES, RoleType} from '../../../../model/user/role-type';
import * as fromRoot from '../../../allu/reducers';
import {Store} from '@ngrx/store';

@Component({
  selector: 'location-details',
  viewProviders: [],
  templateUrl: './location-details.component.html',
  styleUrls: []
})
export class LocationDetailsComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input() application: Application;
  @Input() readonly: boolean;

  MODIFY_ROLES = MODIFY_ROLES.map(role => RoleType[role]);

  location: Location;
  area: string;
  sections: string;
  multipleLocations = false;
  canBeEdited = true;

  constructor(private mapStore: MapStore,
              private locationState: LocationState,
              private fixedLocationService: FixedLocationService,
              private store: Store<fromRoot.State>) {
  }

  ngOnInit(): void {
    this.location = this.application.firstLocation;
    this.canBeEdited = applicationCanBeEdited(this.application.statusEnum);
    this.locationState.initLocations(this.application.locations);
    this.multipleLocations = this.application.type === ApplicationType[ApplicationType.AREA_RENTAL];
    // Sections can be selected only from single area so we can
    // get area based on its sections
    this.fixedLocationService.areaBySectionIds(this.location.fixedLocationIds)
      .subscribe(area => this.area = area.name);

    this.fixedLocationService.sectionsByIds(this.location.fixedLocationIds)
      .map(sections => sections.map(s => s.name))
      .map(names => names.sort(ArrayUtil.naturalSort((name: string) => name)))
      .map(names => names.join(', '))
      .subscribe(sectionNames => this.sections = sectionNames);

    this.mapStore.editedLocation.subscribe(loc => this.editLocation(loc));
  }

  ngAfterViewInit(): void {
    this.mapStore.selectedApplicationChange(this.application);
  }

  ngOnDestroy(): void {
    this.mapStore.reset();
  }

  districtName(id: number): Observable<string> {
    return this.store.select(fromRoot.getCityDistrictName(id));
  }

  private editLocation(loc: Location): void {
    if (!!loc) {
      this.location = loc;
    }
  }
}
