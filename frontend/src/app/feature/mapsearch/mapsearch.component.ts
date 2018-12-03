import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {Router} from '@angular/router';
import {ApplicationStore} from '../../service/application/application-store';
import {MapSearchFilter} from '@service/map-search-filter';
import {StoredFilterType} from '@model/user/stored-filter-type';
import {StoredFilterStore} from '@service/stored-filter/stored-filter-store';
import {MapStore} from '@service/map/map-store';
import {Observable} from 'rxjs/internal/Observable';

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

  constructor(private router: Router,
              private applicationStore: ApplicationStore,
              private storedFilterStore: StoredFilterStore,
              private mapStore: MapStore) {
  }

  ngOnInit(): void {
    this.searchFilter$ = this.mapStore.mapSearchFilter;
  }

  showAdvancedSearch() {
    this.sidenavOpen = true;
  }

  hideAdvancedSearch() {
    this.sidenavOpen = false;
  }

  newApplication() {
    this.applicationStore.reset();
    this.router.navigate(['/applications/location']);
  }

  onSearchChange(searchFilter: MapSearchFilter): void {
    this.storedFilterStore.resetCurrent(StoredFilterType.MAP);
    this.mapStore.mapSearchFilterChange(searchFilter);
  }
}
