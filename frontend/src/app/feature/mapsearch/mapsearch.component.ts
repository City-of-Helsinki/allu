import {AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewEncapsulation} from '@angular/core';
import {MapSearchFilter} from '@service/map-search-filter';
import {StoredFilterType} from '@model/user/stored-filter-type';
import {StoredFilterStore} from '@service/stored-filter/stored-filter-store';
import {MapStore} from '@service/map/map-store';
import {Observable} from 'rxjs/internal/Observable';
import {MapLayer} from '@service/map/map-layer';
import {select, Store} from '@ngrx/store';
import * as fromMapLayers from '@feature/map/reducers';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import {ActivatedRoute} from '@angular/router';
import {Load} from '@feature/application/actions/application-actions';
import {filter, switchMap, take} from 'rxjs/operators';
import {numberFromQueryParams} from '@util/url.util';

@Component({
  selector: 'mapsearch',
  viewProviders: [],
  templateUrl: './mapsearch.component.html',
  styleUrls: [
    './mapsearch.component.scss'
  ],
  encapsulation: ViewEncapsulation.None
})

export class MapSearchComponent implements OnInit, AfterViewInit, OnDestroy {
  sidenavOpen = false;
  searchFilter$: Observable<MapSearchFilter>;
  selectedLayers$: Observable<MapLayer[]>;
  availableLayers$: Observable<MapLayer[]>;
  displayMap = false;

  constructor(private storedFilterStore: StoredFilterStore,
              private mapStore: MapStore,
              private store: Store<fromRoot.State>,
              private route: ActivatedRoute,
              private cdr: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.searchFilter$ = this.mapStore.mapSearchFilter;
    this.availableLayers$ = this.store.pipe(select(fromMapLayers.getAllLayers));
    this.selectedLayers$ = this.store.pipe(select(fromMapLayers.getSelectedLayers));
    
    // fixes the map not always being displayed
    setTimeout(() => {
      this.displayMap = true;
      this.cdr.detectChanges();
    });
  }

  ngAfterViewInit(): void {
    numberFromQueryParams(this.route, 'applicationId')
      .pipe(take(1))
      .subscribe(id => this.focusOnApplication(id));
  }

  ngOnDestroy(): void {
    this.mapStore.selectedApplicationChange(undefined);
  }

  showAdvancedSearch() {
    this.sidenavOpen = true;
  }

  hideAdvancedSearch() {
    this.sidenavOpen = false;
  }

  onSearchChange(searchFilter: MapSearchFilter): void {
    this.storedFilterStore.resetCurrent(StoredFilterType.MAP);
    this.mapStore.mapSearchFilterChange(searchFilter);
  }

  private focusOnApplication(id: number): void {
    this.store.dispatch(new Load(id));

    this.store.pipe(
      select(fromApplication.getApplicationLoaded),
      filter(loaded => loaded),
      switchMap(() => this.store.pipe(select(fromApplication.getCurrentApplication))),
      take(1)
    ).subscribe(app => this.mapStore.selectedApplicationChange(app));
  }
}
