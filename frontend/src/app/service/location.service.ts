import {Observable, of} from 'rxjs';
import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';

import {Geocoordinates} from '@model/common/geocoordinates';
import {GeocoordinatesMapper} from './mapper/geocoordinates-mapper';
import {StreetAddress} from '@feature/map/street-address';
import {None, Option, Some} from '@util/option';
import {PostalAddress} from '@model/common/postal-address';
import {CityDistrict} from '@model/common/city-district';
import {BackendCityDistrict, CityDistrictMapper} from './mapper/city-district-mapper';
import {ErrorHandler} from './error/error-handler.service';
import {findTranslation} from '@util/translations';
import {BackendGeocoordinates} from './backend-model/backend-geocoordinates';
import {BackendPostalAddress} from './backend-model/backend-postal-address';
import {HttpStatus} from '@util/http-status';
import {catchError, map} from 'rxjs/internal/operators';
import {FixedLocation} from '@model/common/fixed-location';
import {BackendFixedLocation, FixedLocationMapper} from '@service/mapper/fixed-location-mapper';
import {FixedLocationArea} from '@model/common/fixed-location-area';
import {FeatureCollection, GeometryObject} from 'geojson';
import {Projection} from '@feature/map/projection';

const ADDRESS_URL = '/api/address';
const GEOCODE_URL = 'geocode/helsinki';
const FIXED_LOCATION_URL = '/api/locations/fixed-location';
const FIXED_LOCATION_AREA_URL = '/api/locations/fixed-location-areas';
const CITY_DISTRICT_URL = '/api/locations/city-district';
const SEARCH_URL = '/search';
const USER_AREAS_URL = '/api/wfs/user-areas';

@Injectable()
export class LocationService {

  constructor(
    private http: HttpClient,
    private projection: Projection,
    private errorHandler: ErrorHandler) {}

  public geocode(address: string): Observable<Option<Geocoordinates>> {
    return this.http.get<BackendGeocoordinates>(this.geocodeUrl(address)).pipe(
      map(response => GeocoordinatesMapper.mapBackend(response, this.projection)),
      map(coordinates => Some(coordinates)),
      catchError(err => this.handleGeocodeError(err))
    );
  }

  public getFixedLocations(): Observable<FixedLocation[]> {
    return this.http.get<BackendFixedLocation[]>(FIXED_LOCATION_URL).pipe(
      map(response => FixedLocationMapper.mapBackendArray(response)),
      catchError(err => this.errorHandler.handle(err, findTranslation('location.error.fetchFixedLocations')))
    );
  }

  public getFixedLocationAreas(): Observable<FixedLocationArea[]> {
    return this.http.get<FixedLocationArea[]>(FIXED_LOCATION_AREA_URL).pipe(
      catchError(err => this.errorHandler.handle(err, findTranslation('location.error.fetchFixedLocationAreas')))
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

  public getUserAreas(): Observable<FeatureCollection<GeometryObject>> {
    return this.http.get<FeatureCollection<GeometryObject>>(USER_AREAS_URL).pipe(
      catchError(err => this.errorHandler.handle(err, findTranslation('location.error.userAreas')))
    );
  }

  private geocodeUrl(address: string) {
    const streetAddress = StreetAddress.fromAddressString(address);
    let urlParts: string[] = [ADDRESS_URL, GEOCODE_URL];
    urlParts = Some(streetAddress.streetName).map(name => urlParts.concat(name)).orElseGet(() => urlParts);
    urlParts = Some(streetAddress.specifier).map(specifier => urlParts.concat(specifier)).orElseGet(() => urlParts);
    return urlParts.join('/');
  }

  private handleGeocodeError(errorResponse: HttpErrorResponse): Observable<Option<Geocoordinates>> {
    return errorResponse.status === HttpStatus.NOT_FOUND
      ? of(None())
      : this.errorHandler.handle(errorResponse, findTranslation('geolocation.error.searchFailed'));
  }
}
