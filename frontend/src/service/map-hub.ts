import {Injectable} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {Observable}     from 'rxjs/Observable';
import {Geocoordinates} from '../model/common/geocoordinates';
import '../rxjs-extensions.ts';


@Injectable()
export class MapHub {
  private coordinates$: Subject<Geocoordinates> = new Subject<Geocoordinates>();
  private search$: Subject<string> = new Subject<string>();

  constructor() {}

  public coordinates = () => this.coordinates$.asObservable();

  public search = () => this.search$.asObservable()
    .filter(search => !!search)
    .debounceTime(300)
    .distinctUntilChanged();


  public addCoordinates = (coordinates: Geocoordinates) => this.coordinates$.next(coordinates);
  public addSearch = (search: string) => this.search$.next(search);
}
