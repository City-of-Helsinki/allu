import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Geocoordinates} from '../../model/common/geocoordinates';

import {Application} from '../../model/application/application';
import {Option, Some} from '../../util/option';
import {LocationService} from '../location.service';
import {Location} from '../../model/common/location';
import {NotificationService} from '../notification/notification.service';
import {defaultFilter, MapSearchFilter} from '../map-search-filter';
import {Observable} from 'rxjs/Observable';
import {PostalAddress} from '../../model/common/postal-address';
import {MapDataService} from './map-data-service';
import {LatLngBounds} from 'leaflet';
import {ObjectUtil} from '../../util/object.util';
import {StoredFilter} from '../../model/user/stored-filter';
import {StoredFilterType} from '../../model/user/stored-filter-type';
import {StoredFilterStore} from '../stored-filter/stored-filter-store';

export type MapRole = 'SEARCH' | 'LOCATION';

export interface MapState {
  role: MapRole;
  coordinates: Option<Geocoordinates>;
  coordinateSearch: string;
  mapSearchFilter: MapSearchFilter;
  locationSearchFilter: MapSearchFilter;
  matchingAddresses: PostalAddress[];
  selectedApplication: Application;
  visibleApplications: Array<Application>;
  editedLocation: Location;
  locationsToDraw: Array<Location>;
  shape: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>;
  selectedSections: Array<number>;
  drawingAllowed: boolean;
  hasFixedGeometry: boolean;
}

const initialState: MapState = {
  role: undefined,
  coordinates: undefined,
  coordinateSearch: undefined,
  mapSearchFilter: defaultFilter,
  locationSearchFilter: defaultFilter,
  matchingAddresses: [],
  selectedApplication: undefined,
  visibleApplications: [],
  editedLocation: undefined,
  locationsToDraw: [],
  shape: undefined,
  selectedSections: [],
  drawingAllowed: true,
  hasFixedGeometry: false
};

@Injectable()
export class MapStore {
  private store = new BehaviorSubject<MapState>(initialState);

  constructor(private mapDataService: MapDataService,
              private locationService: LocationService,
              private storedFilterStore: StoredFilterStore,
              private notification: NotificationService) {

    // Search when either of filters change
    Observable.merge(this.mapSearchFilter, this.locationSearchFilter)
      .debounceTime(300)
      .filter(filter => !!filter.geometry)
      .subscribe(filter => this.fetchMapDataByFilter(filter));

    // When search changes fetches new coordinates and adds them to coordinates observable
    this.coordinateSearch
      .filter(search => !!search)
      .debounceTime(300)
      .distinctUntilChanged()
      .subscribe(term => this.fetchCoordinates(term));

    this.storedFilterStore.getCurrent(StoredFilterType.MAP)
      .subscribe(sf => this.storedFilterChange(sf));
  }

  reset(): void {
    const next = {
      ...ObjectUtil.clone(initialState),
      mapSearchFilter: this.snapshot.mapSearchFilter
    };
    this.store.next(next);
  }

  get changes(): Observable<MapState> {
    return this.store.asObservable()
      .distinctUntilChanged();
  }

  get snapshot(): MapState {
    return this.store.getValue();
  }

  get applications(): Observable<Array<Application>> {
    return this.store.map(state => state.visibleApplications)
      .distinctUntilChanged();
  }

  get coordinates(): Observable<Option<Geocoordinates>> {
    return this.store.map(state => state.coordinates)
      .distinctUntilChanged()
      .filter(c => !!c);
  }

  get coordinateSearch(): Observable<string> {
    return this.store.map(state => state.coordinateSearch).distinctUntilChanged();
  }

  get selectedApplication(): Observable<Application> {
    return this.store.map(state => state.selectedApplication).distinctUntilChanged();
  }

  get editedLocation(): Observable<Location> {
    return this.store.map(state => state.editedLocation).distinctUntilChanged()
      .filter(shape => !!shape);
  }

  get locationsToDraw(): Observable<Location[]> {
    return this.store.map(state => state.locationsToDraw).distinctUntilChanged();
  }

  get mapSearchFilter(): Observable<MapSearchFilter> {
    return this.store.map(state => state.mapSearchFilter).distinctUntilChanged();
  }

  get locationSearchFilter(): Observable<MapSearchFilter> {
    return this.store.map(state => state.locationSearchFilter).distinctUntilChanged();
  }

  get shape(): Observable<GeoJSON.FeatureCollection<GeoJSON.GeometryObject>> {
    return this.store.map(state => state.shape).distinctUntilChanged()
      .filter(shape => !!shape);
  }

  get selectedSections(): Observable<number[]> {
    return this.store.map(state => state.selectedSections).distinctUntilChanged();
  }

  get drawingAllowed(): Observable<boolean> {
    return this.store.map(state => state.drawingAllowed).distinctUntilChanged();
  }

  get hasFixedGeometry(): Observable<boolean> {
    return this.changes.map(state => state.hasFixedGeometry).distinctUntilChanged();
  }

  get matchingAddresses(): Observable<PostalAddress[]> {
    return this.store.map(state => state.matchingAddresses).distinctUntilChanged();
  }

  get role(): Observable<MapRole> {
    return this.store.map(state => state.role).distinctUntilChanged();
  }

  coordinateSearchChange(term: string): void {
    this.store.next({...this.store.getValue(), coordinateSearch: term});
  }

  coordinateChange(coordinates: Option<Geocoordinates>): void {
    this.store.next({...this.store.getValue(), coordinates});
  }

  selectedApplicationChange(application: Application): void {
    this.store.next({...this.store.getValue(), selectedApplication: application});
  }

  applicationsChange(applications: Application[]): void {
    this.store.next({...this.store.getValue(), visibleApplications: applications});
  }

  editedLocationChange(location: Location): void {
    this.store.next({...this.store.getValue(), editedLocation: location});
  }

  locationsToDrawChange(locations: Location[]): void {
    this.store.next({...this.store.getValue(), locationsToDraw: locations});
  }

  mapSearchFilterChange(filter: MapSearchFilter): void {
    this.store.next({
      ...this.store.getValue(),
      mapSearchFilter: { ...this.snapshot.mapSearchFilter, ...filter }
    });
  }

  locationSearchFilterChange(filter: MapSearchFilter): void {
    this.store.next({
      ...this.store.getValue(),
      locationSearchFilter: { ...this.snapshot.locationSearchFilter, ...filter }
    });
  }

  mapViewChange(bounds: LatLngBounds): void {
    const role = this.snapshot.role;
    if ('LOCATION' === role) {
      const locationSearchFilter = {...this.snapshot.locationSearchFilter, geometry: bounds};
      this.store.next({...this.store.getValue(), locationSearchFilter });
    } else {
      const mapSearchFilter = {...this.snapshot.mapSearchFilter, geometry: bounds};
      this.store.next({...this.store.getValue(), mapSearchFilter });
    }
  }

  shapeChange(shape: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>): void {
    this.store.next({...this.store.getValue(), shape});
  }

  selectedSectionsChange(sectionIds: Array<number>): void {
    this.store.next({...this.store.getValue(), selectedSections: sectionIds});
  }

  drawingAllowedChange(allowed: boolean): void {
    this.store.next({...this.store.getValue(), drawingAllowed: allowed});
  }

  addressSearchChange(searchTerm: string): void {
    this.locationService.search(searchTerm)
      .subscribe(
        result => this.store.next({...this.store.getValue(), matchingAddresses: result}),
        err => this.store.next({...this.store.getValue(), matchingAddresses: []}));
  }

  hasFixedGeometryChange(hasFixedGeometry: boolean): void {
    this.store.next({...this.store.getValue(), hasFixedGeometry});
  }

  storedFilterChange(storedFilter: StoredFilter): void {
    const mapSearchFilter = Some(storedFilter)
      .map(sf => sf.filter)
      .orElseGet(() => defaultFilter);

    this.store.next({
      ...this.store.getValue(),
      mapSearchFilter: {...this.snapshot.mapSearchFilter, ...mapSearchFilter }
    });
  }

  roleChange(role: MapRole): void {
    this.store.next({...this.snapshot, role});
  }

  private fetchMapDataByFilter(filter: MapSearchFilter): void {
    this.mapDataService.applicationsByLocation(filter)
      .subscribe(applications => this.applicationsChange(applications));
  }

  private fetchCoordinates(term: string) {
    this.locationService.geocode(term)
      .subscribe(
        coordinates => this.coordinateChange(coordinates),
        err => this.notification.errorInfo(err)
      );
  }
}
