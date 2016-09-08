import {Injectable, OnInit} from '@angular/core';

import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable}     from 'rxjs/Observable';
import '../rxjs-extensions.ts';

import {Application} from '../model/application/application';
import {SearchbarFilter} from '../event/search/searchbar-filter';
import {ApplicationLocationQuery} from '../model/search/ApplicationLocationQuery';
import {ApplicationStatusChange} from '../model/application/application-status-change';
import {Subject} from 'rxjs/Subject';

export type ApplicationSearch = ApplicationLocationQuery | number;

@Injectable()
export class ApplicationHub {
  private applications$: BehaviorSubject<Array<Application>> = new BehaviorSubject([]);
  private applicationChange$: Subject<Application> = new Subject();
  private applicationStatusChange$: Subject<ApplicationStatusChange> = new Subject();
  private applicationSearch$: Subject<ApplicationSearch> = new Subject();

  private searchBar$: Subject<SearchbarFilter> = new Subject();
  private mapView$: Subject<GeoJSON.GeometryObject> = new Subject();

  constructor() {
    // Waits until searchBar and mapView observables produce value and combines them (latest)
    // as a query which is added to applicationSearch subject
    Observable.combineLatest(
      this.searchBar(),
      this.mapView(),
      this.toApplicationLocationQuery)
      .subscribe(query => this.addApplicationSearch(query));
  }

  /**
   * Observable which contains results of application queries.
   * List applications, fetching single application, etc.
   */
  public applications = () => this.applications$.asObservable();

  /**
   * Adds given array of applications as next value in applications subject.
   * Used by services which request data from backend.
   */
  public addApplications = (applications: Array<Application>) => this.applications$.next(applications);

  /**
   * Used to receive application changes (create & update)
   */
  public applicationChange = () => this.applicationChange$.asObservable();
  /**
   * Used to add application change events (create & update)
   */
  public addApplicationChange = (application: Application) => this.applicationChange$.next(application);

  /**
   * Used to receive application status changes
   */
  public applicationStatusChange = () => this.applicationStatusChange$.asObservable();
  /**
   * Used to add application status changes
   */
  public addApplicationStatusChange = (statusChange: ApplicationStatusChange) => this.applicationStatusChange$.next(statusChange);

  /**
   * Observable where all application searches are added (single, all, filtered).
   */
  public applicationSearch = () => this.applicationSearch$.asObservable();

  /**
   * Adds given search as next value in searchapplicationSearch subject.
   * Used to search set of applications
   */
  public addApplicationSearch = (search) => this.applicationSearch$.next(search);

  /**
   * Adds given SearchBarFilter as next value in searchBar subject.
   * Used to notify changes in searchBar
   */
  public addSearchFilter = (filter: SearchbarFilter) => this.searchBar$.next(filter);

  /**
   * Adds given geometry object to mapView subject.
   * Used to notify changes in maps view area.
   */
  public addMapView = (geometry: GeoJSON.GeometryObject) => this.mapView$.next(geometry);

  private searchBar = () => this.searchBar$.asObservable();
  private mapView = () => this.mapView$.asObservable();


  private toApplicationLocationQuery(searchBar: SearchbarFilter, mapView: GeoJSON.GeometryObject) {
    return new ApplicationLocationQuery(
      searchBar.startDate,
      searchBar.endDate,
      mapView);
  }
}
