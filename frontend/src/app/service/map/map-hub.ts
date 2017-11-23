import {Injectable} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Geocoordinates} from '../../model/common/geocoordinates';

import {Application} from '../../model/application/application';
import {Option} from '../../util/option';
import {ApplicationService} from '../application/application.service';
import {ApplicationLocationQuery} from '../../model/search/ApplicationLocationQuery';
import {LocationService} from '../location.service';
import {CityDistrict} from '../../model/common/city-district';
import {ArrayUtil} from '../../util/array-util';
import {FixedLocationArea} from '../../model/common/fixed-location-area';
import {FixedLocationSection} from '../../model/common/fixed-location-section';
import {Location} from '../../model/common/location';
import {NotificationService} from '../notification/notification.service';
import {defaultFilter, MapSearchFilter} from '../map-search-filter';


@Injectable()
export class MapHub {
  private coordinates$ = new Subject<Option<Geocoordinates>>();
  private addressSearch$ = new Subject<string>();
  private mapSearchFilter$ = new BehaviorSubject<MapSearchFilter>(defaultFilter);
  private applicationSelection$ = new Subject<Application>();
  private applications$ = new Subject<Array<Application>>();
  private editedLocation$ = new BehaviorSubject<Location>(undefined);
  private locationsToDraw$ = new BehaviorSubject<Array<Location>>([]);
  private shape$ = new Subject<GeoJSON.FeatureCollection<GeoJSON.GeometryObject>>();
  private fixedLocations$ = new BehaviorSubject<Array<FixedLocationArea>>([]);
  private selectedFixedLocations$ = new Subject<Array<FixedLocationSection>>();
  private cityDistricts$ = new BehaviorSubject<Array<CityDistrict>>([]);
  private drawingAllowed$ = new BehaviorSubject<boolean>(true);

  constructor(private applicationService: ApplicationService, private locationService: LocationService) {
    this.mapSearchFilter$.asObservable()
      .debounceTime(300)
      .filter(filter => !!filter.geometry)
      .map(filter => this.toApplicationLocationQuery(filter))
      .switchMap(query => this.applicationService.getByLocation(query))
      .subscribe(applications => this.applications$.next(applications));

    // When search changes fetches new coordinates and adds them to coordinates observable
    this.addressSearch$.asObservable()
      .filter(search => !!search)
      .debounceTime(300)
      .distinctUntilChanged()
      .switchMap(term => this.locationService.geocode(term))
      .subscribe(
        coordinates => this.coordinates$.next(coordinates),
        err => NotificationService.error(err)
      );

    this.locationService.getFixedLocations().subscribe(fls => this.fixedLocations$.next(fls));

    this.locationService.districts().subscribe(ds =>  {
      let districts = ds
        .filter(d => d.districtId !== 0) // Ignore 0 Aluemeri
        .sort(ArrayUtil.naturalSort((district: CityDistrict) => district.name));
      this.cityDistricts$.next(districts);
    });
  }

  /**
   * Used for notifying about visible applications in map
   */
  public applications = () => this.applications$.asObservable();

  /**
   * Used for adding new search terms
   */
  public coordinateSearch = (search: string) => this.addressSearch$.next(search);

  /**
   * Used to notify new geocoordinates are available
   */
  public coordinates = () => this.coordinates$.asObservable();

  /**
   * Used to notify that new application (for centering and zooming) has been selected
   */
  public selectApplication = (application: Application) => this.applicationSelection$.next(application);
  public applicationSelection = () => this.applicationSelection$.asObservable();

  /**
   * Used to notify that new location is selected (used when handling multiple locations per application)
   */
  public editLocation = (location: Location) => this.editedLocation$.next(location);
  public editedLocation = () => this.editedLocation$.asObservable();

  /**
   * Used to notify that specific locations should be drawn onto map (used when handling multiple locations per application)
   */
  public drawLocations = (locations: Array<Location>) => this.locationsToDraw$.next(locations);
  public locationsToDraw = () => this.locationsToDraw$.asObservable();

  /**
   * Used to notify changes in address search bar
   */
  public addSearchFilter = (filter: MapSearchFilter) => {
    const current = this.mapSearchFilter$.getValue();
    this.mapSearchFilter$.next({...current, ...filter});
  }

  public searchFilter = () => this.mapSearchFilter$.asObservable();

  /**
   * Used to notify changes in map's currently visible area
   */
  public addMapView = (geometry: GeoJSON.GeometryObject) => {
    const current = this.mapSearchFilter$.getValue();
    this.mapSearchFilter$.next({...current, geometry: geometry});
  }

  /**
   * Used to notify that new shape has been added to map
   */
  public addShape = (shape: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>) => this.shape$.next(shape);
  public shape = () => this.shape$.asObservable();

  /**
   * Used for fetching all available areas / sections
   */
  public fixedLocationAreas = () => this.fixedLocations$.asObservable();

  public fixedLocationAreasByIds = (ids: Array<number>) => this.fixedLocationAreas()
    .map(areas => areas.filter(a => ids.indexOf(a.id) >= 0));

  /**
   * Used for fetching fixed location area by id
   */
  public fixedLocationAreaById = (id: number) => this.fixedLocationAreas()
    .map(areas => areas.find(a => a.id === id))
    .filter(area => !!area);

  /**
   * Fetches areas containing given sections
   */
  public fixedLocationAreaBySectionIds = (ids: Array<number>) => this.fixedLocationAreas()
    .map(areas => areas.filter(a => a.hasSectionIds(ids)))
    .filter(areas => areas.length > 0)
    .map(areas => areas[0]);

  /**
   * Fetches all available fixed location sections
   */
  public fixedLocationSections = () => this.fixedLocationAreas()
    .map(areas => areas
      .map(area => area.sections)
      .reduce((acc, cur) => acc.concat(cur), []));

  /**
   * Used for fetching sections by given ids
   */
  public fixedLocationSectionsBy = (ids: Array<number>) => this.fixedLocationSections()
    .map(fxs => fxs.filter(fx => ids.indexOf(fx.id) >= 0));

  /**
   * Adds fixed locations sections with given ids as selected
   */
  public selectFixedLocationSections = (sectionIds: Array<number>) => this.fixedLocationSectionsBy(sectionIds)
    .subscribe(fxs => this.selectedFixedLocations$.next(fxs));

  /**
   * Observable to provide selected fixed locations
   */
  public selectedFixedLocationSections = () => this.selectedFixedLocations$.asObservable();

  /**
   * Used for fetching all available city districts
   */
  public districts = () => this.cityDistricts$.asObservable();

  /**
   * Used for fetching city district by id
   */
  public districtById = (id: number) => this.districts()
    .map(ds => ds.find(d => d.id === id))
    .filter(d => !!d);

  /**
   * Used for fetching multiple city district by ids
   */
  public districtsById = (ids: Array<number>) => this.districts().map(ds => ds.filter(d => ids.indexOf(d.id) >= 0));

  /**
   * Finds addresses matching with partial search term
   */
  public findMatchingAddresses = (searchTerm: string) => this.locationService.search(searchTerm);

  /**
   * Observable to notify map if drawing should be allowed
   */
  public drawingAllowed = () => this.drawingAllowed$;
  public setDrawingAllowed = (allowed: boolean) => this.drawingAllowed().next(allowed);


  private toApplicationLocationQuery(filter: MapSearchFilter) {
    return new ApplicationLocationQuery(
      filter.startDate,
      filter.endDate,
      filter.statusTypes,
      filter.geometry);
  }
}
