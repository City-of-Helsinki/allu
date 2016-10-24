import {Injectable} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';
import {Geocoordinates} from '../model/common/geocoordinates';
import '../rxjs-extensions.ts';

import {Application} from '../model/application/application';
import {Option} from '../util/option';
import {ApplicationService} from './application/application.service';
import {ApplicationLocationQuery} from '../model/search/ApplicationLocationQuery';
import {SearchbarFilter} from './searchbar-filter';
import {LocationService} from './location.service';
import {UIStateHub} from './ui-state/ui-state-hub';
import {SquareSection} from '../model/common/square-section';
import {Some} from '../util/option';


@Injectable()
export class MapHub {
  private coordinates$ = new Subject<Option<Geocoordinates>>();
  private search$ = new Subject<string>();
  private applicationSelection$ = new Subject<Application>();
  private applications$ = new Subject<Array<Application>>();
  private searchBar$ = new Subject<SearchbarFilter>();
  private mapView$ = new Subject<GeoJSON.GeometryObject>();
  private shape$ = new Subject<GeoJSON.FeatureCollection<GeoJSON.GeometryObject>>();
  private squareSections$ = new BehaviorSubject<Array<SquareSection>>([]);

  constructor(private applicationService: ApplicationService,
              private locationService: LocationService,
              private uiState: UIStateHub) {
    // Waits until searchBar and mapView observables produce value and combines them (latest)
    // as a query which is added to applicationSearch subject
    Observable.combineLatest(
      this.searchBar$.asObservable(),
      this.mapView$.asObservable(),
      this.toApplicationLocationQuery)
      .subscribe(query => this.applicationService.getApplicationsByLocation(query)
        .subscribe(applications => this.applications$.next(applications)));

    // When search changes fetches new coordinates and adds them to coordinates observable
    this.search()
      .switchMap(term => this.locationService.geocode(term))
      .subscribe(
        coordinates => this.coordinates$.next(coordinates),
        err => this.uiState.addError(err)
      );

    this.locationService.getSquaresAndSections()
      .subscribe(squaresAndSections => this.squareSections$.next(squaresAndSections));
  }

  /**
   * Used to notify new geocoordinates are available
   */
  public coordinates = () => this.coordinates$.asObservable();

  /**
   * Used for notifying about visible applications in map
   */
  public applications = () => this.applications$.asObservable();

  /**
   * Used for adding new search terms
   */
  public addSearch = (search: string) => this.search$.next(search);

  /**
   * Used to notify that new application (for centering and zooming) has been selected
   */
  public selectApplication = (application: Application) => this.applicationSelection$.next(application);
  public applicationSelection = () => this.applicationSelection$.asObservable();

  /**
   * Used to notify changes in address search bar
   */
  public addSearchFilter = (filter: SearchbarFilter) => this.searchBar$.next(filter);

  /**
   * Used to notify changes in map's currently visible area
   */
  public addMapView = (geometry: GeoJSON.GeometryObject) => this.mapView$.next(geometry);

  /**
   * Used to notify that new shape has been added to map
   */
  public addShape = (shape: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>) => this.shape$.next(shape);
  public shape = () => this.shape$.asObservable();

  /**
   * Used for fetching all available squares / sections
   */
  public squaresAndSections = () => this.squareSections$.asObservable();

  /**
   * Used for fetching single square / section
   */
  public squareAndSection = (id: number) => this.squaresAndSections()
    .map(entries => Some(entries.find(ss => ss.id === id)));

  /**
   * Used to notify about new address search terms
   */
  private search = () => this.search$.asObservable()
    .filter(search => !!search)
    .debounceTime(300)
    .distinctUntilChanged();

  private toApplicationLocationQuery(searchBar: SearchbarFilter, mapView: GeoJSON.GeometryObject) {
    return new ApplicationLocationQuery(
      searchBar.startDate,
      searchBar.endDate,
      mapView);
  }
}
