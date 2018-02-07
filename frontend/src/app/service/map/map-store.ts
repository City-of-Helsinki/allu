import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Geocoordinates} from '../../model/common/geocoordinates';

import {Application} from '../../model/application/application';
import {Option} from '../../util/option';
import {ApplicationService} from '../application/application.service';
import {ApplicationLocationQuery} from '../../model/search/ApplicationLocationQuery';
import {LocationService} from '../location.service';
import {Location} from '../../model/common/location';
import {NotificationService} from '../notification/notification.service';
import {defaultFilter, MapSearchFilter} from '../map-search-filter';
import {ObjectUtil} from '../../util/object.util';
import {Observable} from 'rxjs/Observable';
import {PostalAddress} from '../../model/common/postal-address';

export interface MapState {
  coordinates: Option<Geocoordinates>;
  coordinateSearch: string;
  mapSearchFilter: MapSearchFilter;
  matchingAddresses: PostalAddress[];
  selectedApplication: Application;
  visibleApplications: Array<Application>;
  editedLocation: Location;
  locationsToDraw: Array<Location>;
  shape: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>;
  selectedSections: Array<number>;
  drawingAllowed: boolean;
}

const initialState: MapState = {
  coordinates: undefined,
  coordinateSearch: undefined,
  mapSearchFilter: defaultFilter,
  matchingAddresses: [],
  selectedApplication: undefined,
  visibleApplications: [],
  editedLocation: undefined,
  locationsToDraw: [],
  shape: undefined,
  selectedSections: [],
  drawingAllowed: true
};

@Injectable()
export class MapStore {
  private store = new BehaviorSubject<MapState>(initialState);

  constructor(private applicationService: ApplicationService, private locationService: LocationService) {
    this.searchFilter
      .debounceTime(300)
      .filter(filter => !!filter.geometry)
      .map(filter => this.toApplicationLocationQuery(filter))
      .switchMap(query => this.applicationService.getByLocation(query))
      .subscribe(applications => this.applicationsChange(applications));

    // When search changes fetches new coordinates and adds them to coordinates observable
    this.coordinateSearch
      .filter(search => !!search)
      .debounceTime(300)
      .distinctUntilChanged()
      .switchMap(term => this.locationService.geocode(term))
      .subscribe(
        coordinates => this.coordinateChange(coordinates),
        err => NotificationService.error(err)
      );
  }

  get snapshot(): MapState {
    return ObjectUtil.clone(this.store.getValue());
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

  get searchFilter(): Observable<MapSearchFilter> {
    return this.store.map(state => state.mapSearchFilter).distinctUntilChanged();
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

  get matchingAddresses(): Observable<PostalAddress[]> {
    return this.store.map(state => state.matchingAddresses).distinctUntilChanged();
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

  searchFilterChange(filter: MapSearchFilter): void {
    const current = this.snapshot.mapSearchFilter;
    const next = {...current, ...filter};
    this.store.next({...this.store.getValue(), mapSearchFilter: next});
  }

  mapViewChange(geometry: GeoJSON.GeometryObject): void {
    const current = this.snapshot.mapSearchFilter;
    const next = {...current, geometry: geometry};
    this.store.next({...this.store.getValue(), mapSearchFilter: next});
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
      .subscribe(result => this.store.next({...this.store.getValue(), matchingAddresses: result}));
  }

  private toApplicationLocationQuery(filter: MapSearchFilter) {
    return new ApplicationLocationQuery(
      filter.startDate,
      filter.endDate,
      filter.statusTypes,
      filter.geometry);
  }
}
