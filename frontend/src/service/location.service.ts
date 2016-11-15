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
import {HttpUtil} from './../util/http.util.ts';
import {HttpResponse} from '../util/http.util.ts';
import {HTTP_NOT_FOUND} from '../util/http-status-codes';
import {ErrorInfo} from './ui-state/error-info';
import {ErrorType} from './ui-state/error-type';
import {None} from '../util/option';
import {Option} from '../util/option';
import {Some} from '../util/option';
import {FixedLocationMapper} from './mapper/fixed-location-mapper';
import {FixedLocation} from '../model/common/fixed-location';
import {HttpStatus} from '../util/http.util';

@Injectable()
export class LocationService {

  static ADDRESS_URL = '/api/address';
  static GEOCODE_URL = '/geocode/helsinki';
  static FIXED_LOCATION_URL = '/api/locations/fixed-location';

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

  public getFixedLocations(): Observable<Array<FixedLocation>> {
    return this.authHttp.get(LocationService.FIXED_LOCATION_URL)
      .map(response => response.json())
      .map(json => json.map(ss => FixedLocationMapper.mapBackend(ss)))
      .catch(err => this.uiState.addError(HttpUtil.extractMessage(err)));
  }

  private geocodeUrl(address: string) {
    let streetAddress = StreetAddress.fromAddressString(address);
    return LocationService.ADDRESS_URL + LocationService.GEOCODE_URL
      + '/' + streetAddress.streetName
      + '/' + streetAddress.streetNumber;
  }

  private handleGeocodeError(errorResponse: any): Observable<Option<Geocoordinates>> {
    let httpError = HttpUtil.extractHttpResponse(errorResponse);
    return httpError.status === HttpStatus.NOT_FOUND
      ? Observable.of(None())
      : Observable.throw(new ErrorInfo(ErrorType.GEOLOCATION_SEARCH_FAILED, httpError.message));
  }
}
