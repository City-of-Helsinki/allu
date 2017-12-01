import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {Observable} from 'rxjs/Observable';
import {Application} from '../../model/application/application';
import {ApplicationMapper} from './../mapper/application-mapper';
import {StructureMetaMapper} from './../mapper/structure-meta-mapper';
import {StructureMeta} from '../../model/application/meta/structure-meta';
import {ApplicationLocationQuery} from '../../model/search/ApplicationLocationQuery';
import {ApplicationLocationQueryMapper} from './../mapper/application-location-query-mapper';
import {ApplicationStatus} from '../../model/application/application-status';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';
import {ApplicationQueryParametersMapper} from '../mapper/query/application-query-parameters-mapper';
import {HttpResponse} from '../../util/http-response';
import {HttpUtil} from '../../util/http.util';
import {ApplicationTag} from '../../model/application/tag/application-tag';
import {ApplicationTagMapper} from '../mapper/application-tag-mapper';
import {StatusChangeInfo} from '../../model/application/status-change-info';
import {StatusChangeInfoMapper} from '../mapper/status-change-info-mapper';
import {AttachmentInfo} from '../../model/application/attachment/attachment-info';
import {AttachmentInfoMapper} from '../mapper/attachment-info-mapper';

const APPLICATIONS_URL = '/api/applications';
const STATUS_URL = '/api/applications/:appId/status/:statusPart';
const TAGS_URL = '/api/applications/:appId/tags';
const ATTACHMENTS_URL = '/api/applications/:appId/attachments';
const SEARCH = '/search';
const SEARCH_LOCATION = '/search_location';
const METADATA_URL = '/api/meta';

@Injectable()
export class ApplicationService {
  private statusToUrl = new Map<ApplicationStatus, string>();

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {
    this.statusToUrl.set(ApplicationStatus.CANCELLED, 'cancelled');
    this.statusToUrl.set(ApplicationStatus.PENDING, 'pending');
    this.statusToUrl.set(ApplicationStatus.HANDLING, 'handling');
    this.statusToUrl.set(ApplicationStatus.DECISIONMAKING, 'decisionmaking');
    this.statusToUrl.set(ApplicationStatus.DECISION, 'decision');
    this.statusToUrl.set(ApplicationStatus.REJECTED, 'rejected');
    this.statusToUrl.set(ApplicationStatus.RETURNED_TO_PREPARATION, 'toPreparation');
    this.statusToUrl.set(ApplicationStatus.FINISHED, 'finished');
  }

  public get(id: number): Observable<Application> {
    return this.authHttp.get(APPLICATIONS_URL + '/' + id)
      .map(response => response.json())
      .map(app => ApplicationMapper.mapBackend(app))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.fetch')));
  }

  public getByLocation(query: ApplicationLocationQuery): Observable<Array<Application>> {
    if (query.statusTypes && query.statusTypes.length) {
      const searchUrl = APPLICATIONS_URL + SEARCH_LOCATION;

      return this.authHttp.post(
        searchUrl,
        JSON.stringify(ApplicationLocationQueryMapper.mapFrontend(query)))
        .map(response => response.json())
        .map(json => json.map(app => ApplicationMapper.mapBackend(app)))
        .catch(error => this.errorHandler.handle(error, findTranslation('application.error.searchFailed')));
    } else {
      return Observable.of([]);
    }
  }

  public search(searchQuery: ApplicationSearchQuery): Observable<Array<Application>> {
    const searchUrl = APPLICATIONS_URL + SEARCH;

    return this.authHttp.post(
      searchUrl,
      JSON.stringify(ApplicationQueryParametersMapper.mapFrontend(searchQuery)))
      .map(response => response.json())
      .map(json => json.map(app => ApplicationMapper.mapBackend(app)))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.searchFailed')));
  }

  public save(application: Application): Observable<Application> {
    if (application.id) {
      const url = APPLICATIONS_URL + '/' + application.id;

      return this.authHttp.put(url,
        JSON.stringify(ApplicationMapper.mapFrontend(application)))
        .map(response => ApplicationMapper.mapBackend(response.json()))
        .catch(error => this.errorHandler.handle(error, findTranslation('application.error.saveFailed')));
    } else {
      return this.authHttp.post(APPLICATIONS_URL,
        JSON.stringify(ApplicationMapper.mapFrontend(application)))
        .map(response => ApplicationMapper.mapBackend(response.json()))
        .catch(error => this.errorHandler.handle(error, findTranslation('application.error.saveFailed')));
    }
  }

  public remove(id: number): Observable<HttpResponse> {
    const url = APPLICATIONS_URL + '/note/' + id;
    return this.authHttp.delete(url)
      .map(response => HttpUtil.extractHttpResponse(response))
      .catch(error => this.errorHandler.handle(error, findTranslation('comment.error.remove')));
  }

  public loadMetadata(applicationType: string): Observable<StructureMeta> {
    return this.authHttp.get(METADATA_URL + '/' + applicationType)
      .map(response => StructureMetaMapper.mapBackend(response.json()))
      .catch(error => this.errorHandler.handle(error, 'Loading metadata failed'));
  }

  public statusChange(appId: number, status: ApplicationStatus, changeInfo?: StatusChangeInfo): Observable<Application> {
    const url = STATUS_URL
      .replace(':appId', String(appId))
      .replace(':statusPart', this.statusToUrl.get(status));

    return this.authHttp.put(url, JSON.stringify(StatusChangeInfoMapper.mapFrontEnd(changeInfo)))
      .map(response => ApplicationMapper.mapBackend(response.json()))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.statusChangeFailed')));
  }

  public handlerChange(handler: number, applicationIds: Array<number>): Observable<any> {
    const url = APPLICATIONS_URL + '/handler/' + handler;
    return this.authHttp.put(url, JSON.stringify(applicationIds))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.handlerChangeFailed')));
  }

  public handlerRemove(applicationIds: Array<number>): Observable<any> {
    const url = APPLICATIONS_URL + '/handler/remove';
    return this.authHttp.put(url, JSON.stringify(applicationIds))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.handlerChangeFailed')));
  }

  public saveTags(appId: number, tags: Array<ApplicationTag>): Observable<Array<ApplicationTag>> {
    const url = TAGS_URL.replace(':appId', String(appId));
    return this.authHttp.put(url, JSON.stringify(ApplicationTagMapper.mapFrontendList(tags)))
      .map(response => ApplicationTagMapper.mapBackendList(response.json()))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.tagUpdateFailed')));
  }

  getAttachments(applicationId: number): Observable<Array<AttachmentInfo>> {
    const url = ATTACHMENTS_URL.replace(':appId', String(applicationId));
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(infos => infos.map(info => AttachmentInfoMapper.mapBackend(info)));
  }
}

