import {Injectable, OnInit} from '@angular/core';

import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable}     from 'rxjs/Observable';
import '../../rxjs-extensions.ts';

import {Application} from '../../model/application/application';
import {SearchbarFilter} from '../../event/search/searchbar-filter';
import {ApplicationLocationQuery} from '../../model/search/ApplicationLocationQuery';
import {ApplicationStatusChange} from '../../model/application/application-status-change';
import {Subject} from 'rxjs/Subject';

export type ApplicationSearch = ApplicationLocationQuery | number;

@Injectable()
export class ApplicationHub {
  private applications$ = new BehaviorSubject<Array<Application>>([]);
  private applicationChange$ = new Subject<Application>();
  private applicationStatusChange$ = new Subject<ApplicationStatusChange>();
  private applicationSearch$ = new Subject<ApplicationSearch>();

  private searchBar$ = new Subject<SearchbarFilter>();
  private mapView$ = new Subject<GeoJSON.GeometryObject>();

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
   * Used for notifying about application changes / listings
   */
  public applications = () => this.applications$.asObservable();
  public addApplications = (applications: Array<Application>) => this.applications$.next(applications);

  /**
   * Used for notifying application changes (create & update)
   */
  public applicationChange = () => this.applicationChange$.asObservable();
  public addApplicationChange = (application: Application) => this.applicationChange$.next(application);

  /**
   * Used for receiving application status changes
   */
  public applicationStatusChange = () => this.applicationStatusChange$.asObservable();
  public addApplicationStatusChange = (statusChange: ApplicationStatusChange) => this.applicationStatusChange$.next(statusChange);

  /**
   * Used for searching applications (single, all, filtered).
   */
  public applicationSearch = () => this.applicationSearch$.asObservable();
  public addApplicationSearch = (search) => this.applicationSearch$.next(search);

  /**
   * Used to notify changes in address search bar
   */
  public addSearchFilter = (filter: SearchbarFilter) => this.searchBar$.next(filter);

  /**
   * Used to notify changes in map's currently visible area
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
