import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {Observable} from 'rxjs/Observable';
import {Application} from '../../model/application/application';
import {ApplicationMapper} from './../mapper/application-mapper';
import {StructureMetaMapper} from './../mapper/structure-meta-mapper';
import {StructureMeta} from '../../model/application/meta/structure-meta';
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
import {QueryParametersMapper} from '../mapper/query/query-parameters-mapper';
import {PageMapper} from '../common/page-mapper';
import {ApplicationIdentifier} from '../../model/application/application-identifier';
import {Sort} from '../../model/common/sort';
import {PageRequest} from '../../model/common/page-request';
import {Page} from '../../model/common/page';

const APPLICATIONS_URL = '/api/applications';
const STATUS_URL = '/api/applications/:appId/status/:statusPart';
const TAGS_URL = '/api/applications/:appId/tags';
const ATTACHMENTS_URL = '/api/applications/:appId/attachments';
const SEARCH = '/search';
const METADATA_URL = '/api/meta';
const WORK_QUEUE_URL = '/api/workqueue';

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

  /**
   * Fetches single application
   */
  public get(id: number): Observable<Application> {
    return this.authHttp.get(APPLICATIONS_URL + '/' + id)
      .map(response => response.json())
      .map(app => ApplicationMapper.mapBackend(app))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.fetch')));
  }

  /**
   * Fetches applications based on given search query
   */
  public pagedSearch(searchQuery: ApplicationSearchQuery, sort?: Sort, pageRequest?: PageRequest): Observable<Page<Application>> {
    const searchUrl = APPLICATIONS_URL + SEARCH;

    return this.authHttp.post(
      searchUrl,
      JSON.stringify(ApplicationQueryParametersMapper.mapFrontend(searchQuery)),
      QueryParametersMapper.pageRequestToQueryParameters(pageRequest, sort))
      .map(response => PageMapper.mapBackend(response.json(), ApplicationMapper.mapBackend))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.searchFailed')));
  }

  public byProject(projectId: number, sort?: Sort, pageRequest?: PageRequest): Observable<Page<Application>> {
    const search = new ApplicationSearchQuery();
    search.projectId = projectId;
    return this.pagedSearch(search, sort, pageRequest);
  }

  /**
   * Helper search function to return only content without page
   */
  public search(searchQuery: ApplicationSearchQuery, sort?: Sort, pageRequest?: PageRequest): Observable<Array<Application>> {
    return this.pagedSearch(searchQuery, sort, pageRequest)
      .map(page => page.content);
  }

  /**
   * Free text search for applications
   */
  public freeTextSearch(term: string): Observable<Application[]> {
    const searchQuery = new ApplicationSearchQuery();
    searchQuery.freeText = term;
    return this.search(searchQuery);
  }

  /**
   * Saves given application (new / update) and returns saved application
   */
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

  /**
   * Deletes given application (only NOTE-types can be deleted)
   */
  public remove(id: number): Observable<HttpResponse> {
    const url = APPLICATIONS_URL + '/note/' + id;
    return this.authHttp.delete(url)
      .map(response => HttpUtil.extractHttpResponse(response))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.removeFailed')));
  }

  /**
   * Loads metadata for given application type
   */
  public loadMetadata(applicationType: string): Observable<StructureMeta> {
    return this.authHttp.get(METADATA_URL + '/' + applicationType)
      .map(response => StructureMetaMapper.mapBackend(response.json()))
      .catch(error => this.errorHandler.handle(error, 'Loading metadata failed'));
  }

  /**
   * Changes applications status according to statusChange.
   * Returns updated application.
   */
  public changeStatus(appId: number, status: ApplicationStatus, changeInfo?: StatusChangeInfo): Observable<Application> {
    const url = STATUS_URL
      .replace(':appId', String(appId))
      .replace(':statusPart', this.statusToUrl.get(status));

    return this.authHttp.put(url, JSON.stringify(StatusChangeInfoMapper.mapFrontEnd(changeInfo)))
      .map(response => ApplicationMapper.mapBackend(response.json()))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.statusChangeFailed')));
  }

  /**
   * Changes owner of given applications. Does not return anything. Use Observable's subscribe complete.
   */
  public changeOwner(owner: number, applicationIds: Array<number>): Observable<any> {
    const url = APPLICATIONS_URL + '/owner/' + owner;
    return this.authHttp.put(url, JSON.stringify(applicationIds))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.ownerChangeFailed')));
  }

  /**
   * Removes owner of given applications. Does not return anything. Use Observable's subscribe complete.
   */
  public removeOwner(applicationIds: Array<number>): Observable<any> {
    const url = APPLICATIONS_URL + '/owner/remove';
    return this.authHttp.put(url, JSON.stringify(applicationIds))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.ownerChangeFailed')));
  }

  /**
   * Saves tags for application specified by id
   */
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

  replace(id: number): Observable<Application> {
    const url = `${APPLICATIONS_URL}/${id}/replace`;
    return this.authHttp.post(url, undefined)
      .map(response => ApplicationMapper.mapBackend(response.json()))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.replaceFailed')));
  }

  getReplacementHistory(id: number): Observable<Array<ApplicationIdentifier>> {
    const url = `${APPLICATIONS_URL}/${id}/replacementHistory`;
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(json => json.map(identifier => new ApplicationIdentifier(identifier.id, identifier.applicationId)))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.replacementHistory')));
  }
}

