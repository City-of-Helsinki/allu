import {Injectable} from '@angular/core';
import {URLSearchParams} from '@angular/http';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {Observable} from 'rxjs/Observable';

import {Application} from '../../model/application/application';
import {ApplicationMapper} from './../mapper/application-mapper';
import {StructureMetaMapper} from './../mapper/structure-meta-mapper';
import {StructureMeta} from '../../model/application/structure-meta';
import {ApplicationHub} from './application-hub';
import {ApplicationSearch} from './application-hub';
import {ApplicationLocationQuery} from '../../model/search/ApplicationLocationQuery';
import {ApplicationLocationQueryMapper} from './../mapper/application-location-query-mapper';
import {UIStateHub} from './../ui-state/ui-state-hub';
import {HttpUtil, HttpStatus} from '../../util/http.util.ts';
import {ApplicationStatusChange} from '../../model/application/application-status-change';
import {ApplicationStatus} from '../../model/application/application-status';
import {translations} from '../../util/translations';
import {ErrorInfo} from './../ui-state/error-info';
import {ErrorType} from '../ui-state/error-type';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {QueryParametersMapper} from '../mapper/query-parameters-mapper';
import {DefaultText} from '../../model/application/cable-report/default-text';
import {CableInfoType} from '../../model/application/cable-report/cable-info-type';

const defaultTexts = {
  TELECOMMUNICATION: ['TELECOMMUNICATION1', 'TELECOMMUNICATION2', 'TELECOMMUNICATION3'],
  ELECTRICITY: ['ELECTRICITY1', 'ELECTRICITY2', 'ELECTRICITY3'],
  WATER_AND_SEWAGE: ['WATER_AND_SEWAGE1', 'WATER_AND_SEWAGE2'],
  DISTRICT_HEATING_COOLING: ['DISTRICT_HEATING_COOLING1'],
  GAS: ['GAS1', 'GAS2', 'GAS3'],
  UNDERGROUND_STRUCTURE: ['UNDERGROUND_STRUCTURE1'],
  TRAMWAY: ['TRAMWAY1'],
  STREET_HEATING: ['STREET_HEATING1'],
  SEWAGE_PIPE: ['SEWAGE_PIPE1'],
  GEOTHERMAL_WELL: ['GEOTHERMAL_WELL1'],
  GEOTECHNICAL_OBSERVATION_POST: ['GEOTECHNICAL_OBSERVATION_POST1'],
  OTHER: ['OTHER1']
};


@Injectable()
export class ApplicationService {
  static APPLICATIONS_URL = '/api/applications';
  static SEARCH = '/search';
  static SEARCH_LOCATION = '/search_location';
  static METADATA_URL = '/api/meta';
  static DEFAULT_TEXTS_URL = ApplicationService.APPLICATIONS_URL + '/cable-info/texts';

  private statusToUrl = new Map<ApplicationStatus, string>();

  constructor(private authHttp: AuthHttp, private uiState: UIStateHub) {
    this.statusToUrl.set(ApplicationStatus.CANCELLED, '/status/cancelled');
    this.statusToUrl.set(ApplicationStatus.PENDING, '/status/pending');
    this.statusToUrl.set(ApplicationStatus.HANDLING, '/status/handling');
    this.statusToUrl.set(ApplicationStatus.DECISIONMAKING, '/status/decisionmaking');
    this.statusToUrl.set(ApplicationStatus.DECISION, '/status/decision');
    this.statusToUrl.set(ApplicationStatus.REJECTED, '/status/rejected');
    this.statusToUrl.set(ApplicationStatus.RETURNED_TO_PREPARATION, '/status/toPreparation');
    this.statusToUrl.set(ApplicationStatus.FINISHED, '/status/finished');
  }

  public getApplication(id: number): Observable<Application> {
    return this.authHttp.get(ApplicationService.APPLICATIONS_URL + '/' + id)
      .map(response => response.json())
      .map(app => ApplicationMapper.mapBackend(app))
      .catch(err => this.uiState.addError(HttpUtil.extractMessage(err)));
  }

  public getApplicationsByLocation(query: ApplicationLocationQuery): Observable<Array<Application>> {
    let searchUrl = ApplicationService.APPLICATIONS_URL + ApplicationService.SEARCH_LOCATION;

    return this.authHttp.post(
      searchUrl,
      JSON.stringify(ApplicationLocationQueryMapper.mapFrontend(query)))
      .map(response => response.json())
      .map(json => json.map(app => ApplicationMapper.mapBackend(app)))
      .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.APPLICATION_SEARCH_FAILED, HttpUtil.extractMessage(err))));
  }

  public searchApplications(searchQuery: ApplicationSearchQuery): Observable<Array<Application>> {
    let searchUrl = ApplicationService.APPLICATIONS_URL + ApplicationService.SEARCH;

    return this.authHttp.post(
      searchUrl,
      JSON.stringify(QueryParametersMapper.mapApplicationQueryFrontend(searchQuery)))
      .map(response => response.json())
      .map(json => json.map(app => ApplicationMapper.mapBackend(app)))
      .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.APPLICATION_SEARCH_FAILED, HttpUtil.extractMessage(err))));
  }

  public saveApplication(application: Application): Observable<Application> {
    if (application.id) {
      let url = ApplicationService.APPLICATIONS_URL + '/' + application.id;

      return this.authHttp.put(url,
        JSON.stringify(ApplicationMapper.mapFrontend(application)))
        .map(response => ApplicationMapper.mapBackend(response.json()))
        .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.APPLICATION_SAVE_FAILED, HttpUtil.extractMessage(err))));
    } else {
      return this.authHttp.post(ApplicationService.APPLICATIONS_URL,
        JSON.stringify(ApplicationMapper.mapFrontend(application)))
        .map(response => ApplicationMapper.mapBackend(response.json()))
        .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.APPLICATION_SAVE_FAILED, HttpUtil.extractMessage(err))));
    }
  }

  public loadMetadata(applicationType: string): Observable<StructureMeta> {
    return this.authHttp.get(ApplicationService.METADATA_URL + '/' + applicationType)
      .map(response => StructureMetaMapper.mapBackend(response.json()))
      .catch(err => this.uiState.addError(HttpUtil.extractMessage(err)));
  }

  public applicationStatusChange(statusChange: ApplicationStatusChange): Observable<Application> {
    let url = ApplicationService.APPLICATIONS_URL + '/' + statusChange.id + this.statusToUrl.get(statusChange.status);
    return this.authHttp.put(url, JSON.stringify(ApplicationMapper.mapComment(statusChange.comment)))
      .map(response => ApplicationMapper.mapBackend(response.json()))
      .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.APPLICATION_STATUS_CHANGE_FAILED)));
  }

  public applicationHandlerChange(handler: number, applicationIds: Array<number>): Observable<any> {
    let url = ApplicationService.APPLICATIONS_URL + '/handler/' + handler;
    return this.authHttp.put(url, JSON.stringify(applicationIds))
      .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.APPLICATION_HANDLER_CHANGE_FAILED)));
  }

  public applicationHandlerRemove(applicationIds: Array<number>): Observable<any> {
    let url = ApplicationService.APPLICATIONS_URL + '/handler/remove';
    return this.authHttp.put(url, JSON.stringify(applicationIds))
      .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.APPLICATION_HANDLER_CHANGE_FAILED)));
  }

  public loadDefaultTexts(): Observable<Array<DefaultText>> {
    return this.authHttp.get(ApplicationService.DEFAULT_TEXTS_URL)
      .map(response => response.json())
      .map(texts => texts.map(text => DefaultText.mapBackend(text)))
      .catch(err => this.uiState.addError(HttpUtil.extractMessage(err)));
  }

  public saveDefaultText(text: DefaultText): Observable<DefaultText> {
    if (text.id) {
      let url = ApplicationService.DEFAULT_TEXTS_URL + '/' + text.id;
      return this.authHttp.put(url, JSON.stringify(DefaultText.mapFrontend(text)))
        .map(response => DefaultText.mapBackend(response.json()))
        .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.DEFAULT_TEXT_SAVE_FAILED, HttpUtil.extractMessage(err))));
    } else {
      return this.authHttp.post(ApplicationService.DEFAULT_TEXTS_URL, JSON.stringify(DefaultText.mapFrontend(text)))
        .map(response => DefaultText.mapBackend(response.json()))
        .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.DEFAULT_TEXT_SAVE_FAILED, HttpUtil.extractMessage(err))));
    }
  }

  public removeDefaultText(id: number): Observable<HttpStatus> {
    let url = ApplicationService.DEFAULT_TEXTS_URL + '/' + id;
    return this.authHttp.delete(url)
      .map(response => HttpUtil.extractHttpResponse(response))
      .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.DEFAULT_TEXT_SAVE_FAILED, HttpUtil.extractMessage(err))));
  }
}

