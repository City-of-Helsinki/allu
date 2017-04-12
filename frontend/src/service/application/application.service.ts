import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {Observable} from 'rxjs/Observable';
import {Application} from '../../model/application/application';
import {ApplicationMapper} from './../mapper/application-mapper';
import {StructureMetaMapper} from './../mapper/structure-meta-mapper';
import {StructureMeta} from '../../model/application/meta/structure-meta';
import {ApplicationLocationQuery} from '../../model/search/ApplicationLocationQuery';
import {ApplicationLocationQueryMapper} from './../mapper/application-location-query-mapper';
import {ApplicationStatusChange} from '../../model/application/application-status-change';
import {ApplicationStatus} from '../../model/application/application-status';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';
import {ApplicationQueryParametersMapper} from '../mapper/query/application-query-parameters-mapper';


@Injectable()
export class ApplicationService {
  static APPLICATIONS_URL = '/api/applications';
  static SEARCH = '/search';
  static SEARCH_LOCATION = '/search_location';
  static METADATA_URL = '/api/meta';

  private statusToUrl = new Map<ApplicationStatus, string>();

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {
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
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.fetch')));
  }

  public getApplicationsByLocation(query: ApplicationLocationQuery): Observable<Array<Application>> {
    let searchUrl = ApplicationService.APPLICATIONS_URL + ApplicationService.SEARCH_LOCATION;

    return this.authHttp.post(
      searchUrl,
      JSON.stringify(ApplicationLocationQueryMapper.mapFrontend(query)))
      .map(response => response.json())
      .map(json => json.map(app => ApplicationMapper.mapBackend(app)))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.searchFailed')));
  }

  public searchApplications(searchQuery: ApplicationSearchQuery): Observable<Array<Application>> {
    let searchUrl = ApplicationService.APPLICATIONS_URL + ApplicationService.SEARCH;

    return this.authHttp.post(
      searchUrl,
      JSON.stringify(ApplicationQueryParametersMapper.mapFrontend(searchQuery)))
      .map(response => response.json())
      .map(json => json.map(app => ApplicationMapper.mapBackend(app)))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.searchFailed')));
  }

  public saveApplication(application: Application): Observable<Application> {
    if (application.id) {
      let url = ApplicationService.APPLICATIONS_URL + '/' + application.id;

      return this.authHttp.put(url,
        JSON.stringify(ApplicationMapper.mapFrontend(application)))
        .map(response => ApplicationMapper.mapBackend(response.json()))
        .catch(error => this.errorHandler.handle(error, findTranslation('application.error.saveFailed')));
    } else {
      return this.authHttp.post(ApplicationService.APPLICATIONS_URL,
        JSON.stringify(ApplicationMapper.mapFrontend(application)))
        .map(response => ApplicationMapper.mapBackend(response.json()))
        .catch(error => this.errorHandler.handle(error, findTranslation('application.error.saveFailed')));
    }
  }

  public loadMetadata(applicationType: string): Observable<StructureMeta> {
    return this.authHttp.get(ApplicationService.METADATA_URL + '/' + applicationType)
      .map(response => StructureMetaMapper.mapBackend(response.json()))
      .catch(error => this.errorHandler.handle(error, 'Loading metadata failed'));
  }

  public applicationStatusChange(statusChange: ApplicationStatusChange): Observable<Application> {
    let url = ApplicationService.APPLICATIONS_URL + '/' + statusChange.id + this.statusToUrl.get(statusChange.status);
    return this.authHttp.put(url, JSON.stringify(ApplicationMapper.mapComment(statusChange.comment)))
      .map(response => ApplicationMapper.mapBackend(response.json()))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.statusChangeFailed')));
  }

  public applicationHandlerChange(handler: number, applicationIds: Array<number>): Observable<any> {
    let url = ApplicationService.APPLICATIONS_URL + '/handler/' + handler;
    return this.authHttp.put(url, JSON.stringify(applicationIds))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.handlerChangeFailed')));
  }

  public applicationHandlerRemove(applicationIds: Array<number>): Observable<any> {
    let url = ApplicationService.APPLICATIONS_URL + '/handler/remove';
    return this.authHttp.put(url, JSON.stringify(applicationIds))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.handlerChangeFailed')));
  }
}

