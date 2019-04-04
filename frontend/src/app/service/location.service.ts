import {Observable, of} from 'rxjs';
import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';

import {Geocoordinates} from '@model/common/geocoordinates';
import {GeocoordinatesMapper} from './mapper/geocoordinates-mapper';
import {StreetAddress} from '@model/common/street-address';
import {MapUtil} from './map/map.util';
import {None, Option, Some} from '@util/option';
import {FixedLocationMapper} from './mapper/fixed-location-mapper';
import {PostalAddress} from '@model/common/postal-address';
import {CityDistrict} from '@model/common/city-district';
import {BackendCityDistrict, CityDistrictMapper} from './mapper/city-district-mapper';
import {FixedLocationArea} from '@model/common/fixed-location-area';
import {ErrorHandler} from './error/error-handler.service';
import {findTranslation} from '@util/translations';
import {BackendGeocoordinates} from './backend-model/backend-geocoordinates';
import {BackendFixedLocationArea} from './backend-model/backend-fixed-location-area';
import {BackendPostalAddress} from './backend-model/backend-postal-address';
import {HttpStatus} from '@util/http-status';
import {catchError, map} from 'rxjs/internal/operators';

const ADDRESS_URL = '/api/address';
const GEOCODE_URL = 'geocode/helsinki';
const FIXED_LOCATION_URL = '/api/locations/fixed-location-areas';
const CITY_DISTRICT_URL = '/api/locations/city-district';
const SEARCH_URL = '/search';

@Injectable()
export class LocationService {

  constructor(
    private http: HttpClient,
    private mapService: MapUtil,
    private errorHandler: ErrorHandler) {}

  public geocode(address: string, ): Observable<Option<Geocoordinates>> {
    return this.http.get<BackendGeocoordinates>(this.geocodeUrl(address)).pipe(
      map(response => GeocoordinatesMapper.mapBackend(response, this.mapService)),
      map(coordinates => Some(coordinates)),
      catchError(err => this.handleGeocodeError(err))
    );
  }

  public getFixedLocations(): Observable<Array<FixedLocationArea>> {
    return this.http.get<BackendFixedLocationArea[]>(FIXED_LOCATION_URL).pipe(
      map(json => json.map(ss => FixedLocationMapper.mapBackend(ss))),
      catchError(err => this.errorHandler.handle(err, findTranslation('location.error.fetchFixedLocations')))
    );
  }

  public districts(): Observable<Array<CityDistrict>> {
    return this.http.get<BackendCityDistrict[]>(CITY_DISTRICT_URL).pipe(
      map(districts => districts.map(district => CityDistrictMapper.mapBackend(district))),
      catchError(err => this.errorHandler.handle(err, findTranslation('location.error.fetchCityDistricts')))
    );
  }

  public search(searchTerm: string): Observable<Array<PostalAddress>> {
    const searchUrl = ADDRESS_URL + SEARCH_URL + '/' + searchTerm;
    return this.http.get<BackendPostalAddress[]>(searchUrl).pipe(
      map(addressses => addressses.map(address => PostalAddress.fromBackend(address))),
      catchError(err => this.errorHandler.handle(err, findTranslation('location.error.addressSearch')))
    );
  }

  private geocodeUrl(address: string) {
    const streetAddress = StreetAddress.fromAddressString(address);
    let urlParts: string[] = [ADDRESS_URL, GEOCODE_URL];
    urlParts = Some(streetAddress.streetName).map(name => urlParts.concat(name)).orElseGet(() => urlParts);
    urlParts = Some(streetAddress.streetNumber).map(streetNum => urlParts.concat(streetNum.toLocaleString())).orElseGet(() => urlParts);
    urlParts = Some(streetAddress.streetLetter).map(streetLetter => urlParts.concat(streetLetter)).orElseGet(() => urlParts);
    return urlParts.join('/');
  }

  private handleGeocodeError(errorResponse: HttpErrorResponse): Observable<Option<Geocoordinates>> {
    return errorResponse.status === HttpStatus.NOT_FOUND
      ? of(None())
      : this.errorHandler.handle(errorResponse, findTranslation('geolocation.error.searchFailed'));
  }
}
