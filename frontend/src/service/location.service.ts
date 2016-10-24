import {Injectable} from '@angular/core';
import {Http, Response, URLSearchParams} from '@angular/http';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {Observable}     from 'rxjs/Observable';

import {Location} from './location.class';
import {Geocoordinates} from '../model/common/geocoordinates';
import {GeocoordinatesMapper} from './mapper/geocoordinates-mapper';
import {StreetAddress} from '../model/common/street-address';
import {MapUtil} from './map.util.ts';
import {MapHub} from './map-hub';
import '../rxjs-extensions.ts';
import {UIStateHub} from './ui-state/ui-state-hub';
import {ErrorUtil} from './../util/error.util.ts';
import {HttpError} from '../util/error.util';
import {HTTP_NOT_FOUND} from '../util/http-status-codes';
import {ErrorInfo} from './ui-state/error-info';
import {ErrorType} from './ui-state/error-type';
import {None} from '../util/option';
import {Option} from '../util/option';
import {Some} from '../util/option';
import {SquareSectionMapper} from './mapper/square-section-mapper';
import {SquareSection} from '../model/common/square-section';

@Injectable()
export class LocationService {

  static ADDRESS_URL = '/api/address';
  static GEOCODE_URL = '/geocode/helsinki';
  static SQUARE_SECTION_URL = '/api/locations/square-section';

  constructor(
    private authHttp: AuthHttp,
    private mapService: MapUtil,
    private uiState: UIStateHub) {}

  public geocode(address: string): Observable<Option<Geocoordinates>> {
    let searchUrl = this.geocodeUrl(address);

    return this.authHttp.get(searchUrl)
      .map(response => response.json())
      .map(response => GeocoordinatesMapper.mapBackend(response, this.mapService))
      .map(coordinates => Some(coordinates))
      .catch(err => this.handleGeocodeError(err));
  }

  public getSquaresAndSections(): Observable<Array<SquareSection>> {
    return this.authHttp.get(LocationService.SQUARE_SECTION_URL)
      .map(response => response.json())
      .map(json => json.map(ss => SquareSectionMapper.mapBackend(ss)))
      .catch(err => this.uiState.addError(ErrorUtil.extractMessage(err)));
  }

  private geocodeUrl(address: string) {
    let streetAddress = StreetAddress.fromAddressString(address);
    return LocationService.ADDRESS_URL + LocationService.GEOCODE_URL
      + '/' + streetAddress.streetName
      + '/' + streetAddress.streetNumber;
  }

  private handleGeocodeError(errorResponse: any): Observable<Option<Geocoordinates>> {
    let httpError = ErrorUtil.extractHttpError(errorResponse);
    return httpError.status === HTTP_NOT_FOUND
      ? Observable.of(None())
      : Observable.throw(new ErrorInfo(ErrorType.GEOLOCATION_SEARCH_FAILED, httpError.message));
  }
}
