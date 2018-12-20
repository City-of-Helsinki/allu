import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Router} from '@angular/router';
import {ApplicationStore} from '@service/application/application-store';
import {MapSearchFilter} from '@service/map-search-filter';
import {StoredFilterType} from '@model/user/stored-filter-type';
import {StoredFilterStore} from '@service/stored-filter/stored-filter-store';
import {MapStore} from '@service/map/map-store';
import {Observable} from 'rxjs/internal/Observable';
import {MapLayer} from '@service/map/map-layer';
import {select, Store} from '@ngrx/store';
import * as fromMapLayers from '@feature/map/reducers';
import {ResetLayers} from '@feature/map/actions/map-layer-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

@Component({
  selector: 'mapsearch',
  viewProviders: [],
  templateUrl: './mapsearch.component.html',
  styleUrls: [
    './mapsearch.component.scss'
  ],
  encapsulation: ViewEncapsulation.None
})

export class MapSearchComponent implements OnInit {
  sidenavOpen = false;
  searchFilter$: Observable<MapSearchFilter>;
  selectedLayers$: Observable<MapLayer[]>;
  availableLayers$: Observable<MapLayer[]>;

  constructor(private router: Router,
              private applicationStore: ApplicationStore,
              private storedFilterStore: StoredFilterStore,
              private mapStore: MapStore,
              private store: Store<fromMapLayers.State>) {
  }

  ngOnInit(): void {
    this.searchFilter$ = this.mapStore.mapSearchFilter;
    this.availableLayers$ = this.store.pipe(select(fromMapLayers.getAllLayers));
    this.selectedLayers$ = this.store.pipe(select(fromMapLayers.getSelectedLayers));
  }

  showAdvancedSearch() {
    this.sidenavOpen = true;
  }

  hideAdvancedSearch() {
    this.sidenavOpen = false;
  }

  newApplication() {
    this.applicationStore.reset();
    this.store.dispatch(new ResetLayers(ActionTargetType.Location));
    this.router.navigate(['/applications/location']);
  }

  onSearchChange(searchFilter: MapSearchFilter): void {
    this.storedFilterStore.resetCurrent(StoredFilterType.MAP);
    this.mapStore.mapSearchFilterChange(searchFilter);
  }
}
