import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Application} from '../../model/application/application';
import {ApplicationMapper} from './../mapper/application-mapper';
import {ApplicationStatus} from '../../model/application/application-status';
import {ApplicationSearchQuery, fromApplicationIdAndName} from '../../model/search/ApplicationSearchQuery';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';
import {ApplicationQueryParametersMapper} from '../mapper/query/application-query-parameters-mapper';
import {ApplicationTag} from '../../model/application/tag/application-tag';
import {ApplicationTagType} from '@model/application/tag/application-tag-type';
import {ApplicationTagMapper, BackendApplicationTag} from '../mapper/application-tag-mapper';
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
import {BackendApplication, SearchResultApplication} from '../backend-model/backend-application';
import {BackendPage} from '../backend-model/backend-page';
import {BackendAttachmentInfo} from '../backend-model/backend-attachment-info';
import {catchError, map} from 'rxjs/internal/operators';
import {DistributionEntry} from '@model/common/distribution-entry';
import {BackendDistributionEntry} from '@service/backend-model/backend-distribution-entry';
import {DistributionMapper} from '@service/mapper/distribution-mapper';

const APPLICATIONS_URL = '/api/applications';
const STATUS_URL = '/api/applications/:appId/status/:statusPart';
const RETURN_TO_EDITING_URL = '/api/applications/:appId/status/returnToEditing';
const TAGS_URL = '/api/applications/:appId/tags';
const ATTACHMENTS_URL = '/api/applications/:appId/attachments';
const SEARCH = '/search';

@Injectable()
export class ApplicationService {
  private statusToUrl = new Map<ApplicationStatus, string>();

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
    this.statusToUrl.set(ApplicationStatus.CANCELLED, 'cancelled');
    this.statusToUrl.set(ApplicationStatus.PENDING, 'pending');
    this.statusToUrl.set(ApplicationStatus.WAITING_INFORMATION, 'waiting_information');
    this.statusToUrl.set(ApplicationStatus.HANDLING, 'handling');
    this.statusToUrl.set(ApplicationStatus.DECISIONMAKING, 'decisionmaking');
    this.statusToUrl.set(ApplicationStatus.DECISION, 'decision');
    this.statusToUrl.set(ApplicationStatus.REJECTED, 'rejected');
    this.statusToUrl.set(ApplicationStatus.RETURNED_TO_PREPARATION, 'toPreparation');
    this.statusToUrl.set(ApplicationStatus.OPERATIONAL_CONDITION, 'operational_condition');
    this.statusToUrl.set(ApplicationStatus.FINISHED, 'finished');
    this.statusToUrl.set(ApplicationStatus.TERMINATED, 'terminated');
  }

  /**
   * Fetches single application
   */
  public get(id: number): Observable<Application> {
    return this.http.get<BackendApplication>(APPLICATIONS_URL + '/' + id).pipe(
      map(app => ApplicationMapper.mapBackend(app)),
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.fetch')))
    );
  }

  public byIds(ids: number[]): Observable<Application[]> {
    const idsParam = ids.join(',');
    const url = `${APPLICATIONS_URL}?ids=${idsParam}`;
    return this.http.get<BackendApplication[]>(url).pipe(
      map(apps => ApplicationMapper.mapBackendList(apps)),
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.fetch')))
    );
  }

  /**
   * Fetches applications based on given search query
   */
  public pagedSearch(searchQuery: ApplicationSearchQuery, sort?: Sort,
      pageRequest?: PageRequest, matchAny?: boolean): Observable<Page<Application>> {
    const searchUrl = APPLICATIONS_URL + SEARCH;

    return this.http.post<BackendPage<SearchResultApplication>>(
      searchUrl,
      JSON.stringify(ApplicationQueryParametersMapper.mapFrontend(searchQuery)),
      {params: QueryParametersMapper.mapPageRequest(pageRequest, sort, matchAny)}).pipe(
      map(page => PageMapper.mapBackend(page, ApplicationMapper.mapSearchResult)),
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.searchFailed')))
    );
  }

  /**
   * Helper search function to return only content without page
   */
  public search(searchQuery: ApplicationSearchQuery, sort?: Sort,
      pageRequest?: PageRequest, matchAny?: boolean): Observable<Array<Application>> {
    return this.pagedSearch(searchQuery, sort, pageRequest, matchAny).pipe(
      map(page => page.content)
    );
  }

  public nameOrApplicationIdSearch(term: string): Observable<Page<Application>> {
    const searchQuery = fromApplicationIdAndName(term, term);
    return this.pagedSearch(searchQuery, undefined, undefined, true);
  }

  /**
   * Saves given application (new / update) and returns saved application
   */
  public save(application: Application): Observable<Application> {
    if (application.id) {
      const url = APPLICATIONS_URL + '/' + application.id;

      return this.http.put<BackendApplication>(url,
        JSON.stringify(ApplicationMapper.mapFrontend(application))).pipe(
        map(saved => ApplicationMapper.mapBackend(saved)),
        catchError(error => this.errorHandler.handle(error, findTranslation('application.error.saveFailed')))
      );
    } else {
      return this.http.post<BackendApplication>(APPLICATIONS_URL,
        JSON.stringify(ApplicationMapper.mapFrontend(application))).pipe(
        map(saved => ApplicationMapper.mapBackend(saved)),
        catchError(error => this.errorHandler.handle(error, findTranslation('application.error.saveFailed')))
      );
    }
  }

  /**
   * Deletes given application (only NOTE-types can be deleted)
   */
  public remove(id: number): Observable<{}> {
    const url = APPLICATIONS_URL + '/note/' + id;
    return this.http.delete(url).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.removeFailed')))
    );
  }

  /**
   * Returns application back to editing state. New state is based on current state.
   */
  public returnToEditing(appId: number, changeInfo?: StatusChangeInfo): Observable<Application> {
    const url = RETURN_TO_EDITING_URL.replace(':appId', String(appId));
    return this.http.put<BackendApplication>(url, JSON.stringify(StatusChangeInfoMapper.mapFrontEnd(changeInfo))).pipe(
      map(app => ApplicationMapper.mapBackend(app)),
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.statusChangeFailed')))
    );
  }

  /**
   * Changes applications status according to statusChange.
   * Returns updated application.
   */
  public changeStatus(appId: number, status: ApplicationStatus, changeInfo?: StatusChangeInfo): Observable<Application> {
    const url = STATUS_URL
      .replace(':appId', String(appId))
      .replace(':statusPart', this.statusToUrl.get(status));

    return this.http.put<BackendApplication>(url, JSON.stringify(StatusChangeInfoMapper.mapFrontEnd(changeInfo))).pipe(
      map(app => ApplicationMapper.mapBackend(app)),
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.statusChangeFailed')))
    );
  }

  /**
   * Changes owner of given applications. Does not return anything. Use Observable's subscribe complete.
   */
  public changeOwner(owner: number, applicationIds: Array<number>): Observable<{}> {
    const url = APPLICATIONS_URL + '/owner/' + owner;
    return this.http.put(url, JSON.stringify(applicationIds)).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.ownerChangeFailed')))
    );
  }

  /**
   * Removes owner of given applications. Does not return anything. Use Observable's subscribe complete.
   */
  public removeOwner(applicationIds: Array<number>): Observable<{}> {
    const url = APPLICATIONS_URL + '/owner/remove';
    return this.http.put(url, JSON.stringify(applicationIds)).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.ownerChangeFailed')))
    );
  }

  /**
   * Saves tags for application specified by id
   */
  public saveTags(appId: number, tags: Array<ApplicationTag>): Observable<Array<ApplicationTag>> {
    const url = TAGS_URL.replace(':appId', String(appId));
    return this.http.put<BackendApplicationTag[]>(url, JSON.stringify(ApplicationTagMapper.mapFrontendList(tags))).pipe(
      map(saved => ApplicationTagMapper.mapBackendList(saved)),
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.tagUpdateFailed')))
    );
  }

  public saveTag(appId: number, tag: ApplicationTag): Observable<ApplicationTag> {
    const url = `${APPLICATIONS_URL}/${appId}/tags`;
    return this.http.post<BackendApplicationTag>(url, JSON.stringify(ApplicationTagMapper.mapFrontend(tag))).pipe(
      map(saved => ApplicationTagMapper.mapBackend(saved)),
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.tagSaveFailed')))
    );
  }

  public removeTag(appId: number, tagType: ApplicationTagType): Observable<{}> {
    const url = `${APPLICATIONS_URL}/${appId}/tags/${tagType}`;
    return this.http.delete(url).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.tagRemoveFailed')))
    );
  }

  public getTags(appId: number): Observable<ApplicationTag[]> {
    const url = `${APPLICATIONS_URL}/${appId}/tags`;
    return this.http.get<BackendApplicationTag[]>(url).pipe(
      map(tags => ApplicationTagMapper.mapBackendList(tags)),
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.tagFetchFailed')))
    );
  }

  getAttachments(applicationId: number): Observable<Array<AttachmentInfo>> {
    const url = ATTACHMENTS_URL.replace(':appId', String(applicationId));
    return this.http.get<BackendAttachmentInfo[]>(url).pipe(
      map(infos => infos.map(info => AttachmentInfoMapper.mapBackend(info)))
    );
  }

  replace(id: number): Observable<Application> {
    const url = `${APPLICATIONS_URL}/${id}/replace`;
    return this.http.post<BackendApplication>(url, undefined).pipe(
      map(app => ApplicationMapper.mapBackend(app)),
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.replaceFailed')))
    );
  }

  getReplacementHistory(id: number): Observable<Array<ApplicationIdentifier>> {
    const url = `${APPLICATIONS_URL}/${id}/replacementHistory`;
    return this.http.get<ApplicationIdentifier>(url).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.replacementHistory')))
    );
  }

  removeClientApplicationData(id: number): Observable<Application> {
    const url = `${APPLICATIONS_URL}/${id}/clientapplicationdata`;
    return this.http.delete<BackendApplication>(url).pipe(
      map(app => ApplicationMapper.mapBackend(app)),
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.confirmClientApplicationData')))
    );
  }

  removeOwnerNotification(id: number): Observable<{}> {
    const url = `${APPLICATIONS_URL}/${id}/ownernotification`;
    return this.http.delete<void>(url).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.removeOwnerNotification')))
    );
  }

  updateDistribution(id: number, distribution: DistributionEntry[] = []): Observable<DistributionEntry[]> {
    const url = `${APPLICATIONS_URL}/${id}/distribution`;
    return this.http.put<BackendDistributionEntry[]>(url, JSON.stringify(DistributionMapper.mapFrontendList(distribution))).pipe(
      map(updatedDistribution => DistributionMapper.mapBackendList(updatedDistribution)),
      catchError(error => this.errorHandler.handle(error, findTranslation('application.distribution.error.save')))
    );
  }

  getDistribution(id: number): Observable<DistributionEntry[]> {
    const url = `${APPLICATIONS_URL}/${id}/distribution`;
    return this.http.get<BackendDistributionEntry[]>(url).pipe(
      map(distribution => DistributionMapper.mapBackendList(distribution)),
      catchError(error => this.errorHandler.handle(error, findTranslation('application.distribution.error.fetch')))
    );
  }
}

