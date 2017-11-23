import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {Observable} from 'rxjs/Observable';

import {Geocoordinates} from '../model/common/geocoordinates';
import {GeocoordinatesMapper} from './mapper/geocoordinates-mapper';
import {StreetAddress} from '../model/common/street-address';
import {MapUtil} from './map/map.util';
import {UIStateHub} from './ui-state/ui-state-hub';
import {HttpUtil} from './../util/http.util';
import {ErrorInfo} from './ui-state/error-info';
import {ErrorType} from './ui-state/error-type';
import {None, Option, Some} from '../util/option';
import {FixedLocationMapper} from './mapper/fixed-location-mapper';
import {PostalAddress} from '../model/common/postal-address';
import {CityDistrict} from '../model/common/city-district';
import {CityDistrictMapper} from './mapper/city-district-mapper';
import {HttpStatus} from '../util/http-response';
import {FixedLocationArea} from '../model/common/fixed-location-area';

const ADDRESS_URL = '/api/address';
const GEOCODE_URL = '/geocode/helsinki';
const FIXED_LOCATION_URL = '/api/locations/fixed-location-areas';
const CITY_DISTRICT_URL = '/api/locations/city-district';
const SEARCH_URL = '/search';

@Injectable()
export class LocationService {

  constructor(
    private authHttp: AuthHttp,
    private mapService: MapUtil,
    private uiState: UIStateHub) {}

  public geocode(address: string): Observable<Option<Geocoordinates>> {
    const searchUrl = this.geocodeUrl(address);

    return this.authHttp.get(searchUrl)
      .map(response => response.json())
      .map(response => GeocoordinatesMapper.mapBackend(response, this.mapService))
      .map(coordinates => Some(coordinates))
      .catch(err => this.handleGeocodeError(err));
  }

  public getFixedLocations(): Observable<Array<FixedLocationArea>> {
    return this.authHttp.get(FIXED_LOCATION_URL)
      .map(response => response.json())
      .map(json => json.map(ss => FixedLocationMapper.mapBackend(ss)))
      .catch(err => this.uiState.addError(HttpUtil.extractMessage(err)));
  }

  public districts(): Observable<Array<CityDistrict>> {
    return this.authHttp.get(CITY_DISTRICT_URL)
      .map(response => response.json())
      .map(json => json.map(district => CityDistrictMapper.mapBackend(district)))
      .catch(err => this.uiState.addError(HttpUtil.extractMessage(err)));
  }

  public search(searchTerm: string): Observable<Array<PostalAddress>> {
    const searchUrl = ADDRESS_URL + SEARCH_URL + '/' + searchTerm;
    return this.authHttp.get(searchUrl)
      .map(response => response.json())
      .map(json => json.map(address => PostalAddress.fromBackend(address)))
      .catch(err => this.uiState.addError(HttpUtil.extractMessage(err)));
  }

  private geocodeUrl(address: string) {
    const streetAddress = StreetAddress.fromAddressString(address);
    return ADDRESS_URL + GEOCODE_URL
      + '/' + streetAddress.streetName
      + '/' + streetAddress.streetNumber;
  }

  private handleGeocodeError(errorResponse: any): Observable<Option<Geocoordinates>> {
    const httpError = HttpUtil.extractHttpResponse(errorResponse);
    return httpError.status === HttpStatus.NOT_FOUND
      ? Observable.of(None())
      : Observable.throw(new ErrorInfo(ErrorType.GEOLOCATION_SEARCH_FAILED, httpError.message));
  }
}
