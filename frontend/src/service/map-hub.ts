import {Injectable} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {Observable}     from 'rxjs/Observable';
import {Geocoordinates} from '../model/common/geocoordinates';
import '../rxjs-extensions.ts';
import {Application} from '../model/application/application';


@Injectable()
export class MapHub {
  private coordinates$: Subject<Geocoordinates> = new Subject<Geocoordinates>();
  private search$: Subject<string> = new Subject<string>();
  private applicationSelection$: Subject<Application> = new Subject();

  constructor() {}

  /**
   * Used to notify new geocoordinates are available
   */
  public coordinates = () => this.coordinates$.asObservable();

  /**
   * Used for adding notification about new coordinates
   */
  public addCoordinates = (coordinates: Geocoordinates) => this.coordinates$.next(coordinates);

  /**
   * Used to notify about new address search terms
   */
  public search = () => this.search$.asObservable()
    .filter(search => !!search)
    .debounceTime(300)
    .distinctUntilChanged();

  /**
   * Used for adding new search terms
   */
  public addSearch = (search: string) => this.search$.next(search);

  /**
   * Used to notify that new application (for centering and zooming) has been selected
   */
  public applicationSelection = () => this.applicationSelection$.asObservable();

  /**
   * Used for selecting application
   */
  public addApplicationSelection = (application: Application) => this.applicationSelection$.next(application);
}
