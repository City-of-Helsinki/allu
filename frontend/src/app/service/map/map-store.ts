import {Injectable} from '@angular/core';
import {BehaviorSubject, merge, Observable} from 'rxjs';
import {Application} from '@model/application/application';
import {Some} from '@util/option';
import {LocationService} from '@service/location.service';
import {Location} from '@model/common/location';
import {defaultFilter, MapSearchFilter} from '@service/map-search-filter';
import {PostalAddress} from '@model/common/postal-address';
import {LatLngBounds} from 'leaflet';
import {ObjectUtil} from '@util/object.util';
import {StoredFilter} from '@model/user/stored-filter';
import {StoredFilterType} from '@model/user/stored-filter-type';
import {StoredFilterStore} from '../stored-filter/stored-filter-store';
import {debounceTime, distinctUntilChanged, filter, map} from 'rxjs/internal/operators';
import {ArrayUtil} from '@util/array-util';
import {Store} from '@ngrx/store';
import * as fromMap from '@feature/map/reducers';
import {Search} from '@feature/map/actions/application-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

export type MapRole = 'SEARCH' | 'LOCATION' | 'OTHER';

export interface MapState {
  role: MapRole;
  mapSearchFilter: MapSearchFilter;
  locationSearchFilter: MapSearchFilter;
  matchingAddresses: PostalAddress[];
  selectedApplication: Application;
  editedLocation: Location;
  locationsToDraw: Array<Location>;
  shape: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>;
  fixedLocations: Array<number>;
  drawingAllowed: boolean;
  invalidGeometry: boolean;
  loading: boolean;
}

const initialState: MapState = {
  role: undefined,
  mapSearchFilter: defaultFilter,
  locationSearchFilter: defaultFilter,
  matchingAddresses: [],
  selectedApplication: undefined,
  editedLocation: undefined,
  locationsToDraw: [],
  shape: undefined,
  fixedLocations: [],
  drawingAllowed: true,
  invalidGeometry: false,
  loading: false
};

@Injectable()
export class MapStore {
  private state$ = new BehaviorSubject<MapState>(initialState);

  constructor(private locationService: LocationService,
              private storedFilterStore: StoredFilterStore,
              private store: Store<fromMap.State>) {

    this.mapSearchFilter.subscribe(storedFilter => this.store.dispatch(new Search(ActionTargetType.Home, storedFilter)));
    this.locationSearchFilter.subscribe(storedFilter => this.store.dispatch(new Search(ActionTargetType.Location, storedFilter)));

    this.storedFilterStore.getCurrent(StoredFilterType.MAP)
      .subscribe(sf => this.storedFilterChange(sf));
  }

  reset(): void {
    const next = {
      ...ObjectUtil.clone(initialState),
      mapSearchFilter: this.snapshot.mapSearchFilter
    };
    this.state$.next(next);
  }

  get changes(): Observable<MapState> {
    return this.state$.asObservable().pipe(distinctUntilChanged());
  }

  get snapshot(): MapState {
    return this.state$.getValue();
  }

  get selectedApplication(): Observable<Application> {
    return this.state$.pipe(
      map(state => state.selectedApplication),
      distinctUntilChanged()
    );
  }

  get editedLocation(): Observable<Location> {
    return this.state$.pipe(
      map(state => state.editedLocation),
      distinctUntilChanged()
    );
  }

  get locationsToDraw(): Observable<Location[]> {
    return this.state$.pipe(
      map(state => state.locationsToDraw),
      distinctUntilChanged()
    );
  }

  get mapSearchFilter(): Observable<MapSearchFilter> {
    return this.state$.pipe(
      map(state => state.mapSearchFilter),
      distinctUntilChanged()
    );
  }

  get locationSearchFilter(): Observable<MapSearchFilter> {
    return this.state$.pipe(
      map(state => state.locationSearchFilter),
      distinctUntilChanged()
    );
  }

  get shape(): Observable<GeoJSON.FeatureCollection<GeoJSON.GeometryObject>> {
    return this.state$.pipe(
      map(state => state.shape),
      distinctUntilChanged(),
      filter(shape => !!shape)
    );
  }

  get fixedLocations(): Observable<number[]> {
    return this.state$.pipe(
      map(state => state.fixedLocations),
      distinctUntilChanged(ArrayUtil.numberArrayEqual)
    );
  }

  get drawingAllowed(): Observable<boolean> {
    return this.state$.pipe(
      map(state => state.drawingAllowed),
      distinctUntilChanged()
    );
  }

  get role(): Observable<MapRole> {
    return this.state$.pipe(
      map(state => state.role),
      distinctUntilChanged()
    );
  }

  get invalidGeometry(): Observable<boolean> {
    return this.state$.pipe(
      map(state => state.invalidGeometry),
      distinctUntilChanged()
    );
  }

  get loading(): Observable<boolean> {
    return this.state$.pipe(
      map(state => state.loading),
      distinctUntilChanged()
    );
  }

  selectedApplicationChange(application: Application): void {
    this.state$.next({...this.state$.getValue(), selectedApplication: application});
  }

  editedLocationChange(location: Location): void {
    this.state$.next({...this.state$.getValue(), editedLocation: location});
  }

  locationsToDrawChange(locations: Location[]): void {
    this.state$.next({...this.state$.getValue(), locationsToDraw: locations});
  }

  mapSearchFilterChange(searchFilter: MapSearchFilter): void {
    this.state$.next({
      ...this.state$.getValue(),
      mapSearchFilter: { ...this.snapshot.mapSearchFilter, ...searchFilter }
    });
  }

  locationSearchFilterChange(searchFilter: MapSearchFilter): void {
    this.state$.next({
      ...this.state$.getValue(),
      locationSearchFilter: { ...this.snapshot.locationSearchFilter, ...searchFilter }
    });
  }

  mapViewChange(bounds: LatLngBounds): void {
    const role = this.snapshot.role;
    if ('LOCATION' === role) {
      const locationSearchFilter = {...this.snapshot.locationSearchFilter, geometry: bounds};
      this.state$.next({...this.state$.getValue(), locationSearchFilter });
    } else if ('SEARCH' === role) {
      const mapSearchFilter = {...this.snapshot.mapSearchFilter, geometry: bounds};
      this.state$.next({...this.state$.getValue(), mapSearchFilter });
    }
  }

  shapeChange(shape: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>): void {
    this.state$.next({...this.state$.getValue(), shape});
  }

  selectedFixedLocationsChange(fixedLocationIds: number[]): void {
    this.state$.next({...this.state$.getValue(), fixedLocations: fixedLocationIds});
  }

  drawingAllowedChange(allowed: boolean): void {
    this.state$.next({...this.state$.getValue(), drawingAllowed: allowed});
  }

  storedFilterChange(storedFilter: StoredFilter): void {
    const mapSearchFilter = Some(storedFilter)
      .map(sf => sf.filter)
      .orElseGet(() => defaultFilter);

    this.state$.next({
      ...this.state$.getValue(),
      mapSearchFilter: {...this.snapshot.mapSearchFilter, ...mapSearchFilter }
    });
  }

  roleChange(role: MapRole): void {
    this.state$.next({...this.snapshot, role});
  }

  invalidGeometryChange(invalidGeometry: boolean): void {
    this.state$.next({...this.snapshot, invalidGeometry});
  }
}
