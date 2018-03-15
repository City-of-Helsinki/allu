import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {Observable} from 'rxjs/Observable';

import {Project} from '../../model/project/project';
import {ProjectMapper} from '../mapper/project-mapper';
import {HttpUtil} from '../../util/http.util';
import {ApplicationMapper} from '../mapper/application-mapper';
import {Application} from '../../model/application/application';
import {ProjectSearchQuery} from '../../model/project/project-search-query';
import {HttpResponse, HttpStatus} from '../../util/http-response';
import {ProjectQueryParametersMapper} from '../mapper/query/project-query-parameters-mapper';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';
import {QueryParametersMapper} from '../mapper/query/query-parameters-mapper';
import {PageMapper} from '../common/page-mapper';
import {PageRequest} from '../../model/common/page-request';
import {Page} from '../../model/common/page';
import {Sort} from '../../model/common/sort';

@Injectable()
export class ProjectService {

  static PROJECT_URL = '/api/projects';
  static SEARCH = '/search';
  static CHILDREN = 'children';
  static PARENTS = 'parents';

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {}

  /**
   * Fetches projects based on given search query
   */
  public pagedSearch(searchQuery: ProjectSearchQuery, sort?: Sort, pageRequest?: PageRequest): Observable<Page<Project>> {
    const searchUrl = ProjectService.PROJECT_URL + ProjectService.SEARCH;

    return this.authHttp.post(
      searchUrl,
      JSON.stringify(ProjectQueryParametersMapper.mapFrontend(searchQuery)),
      QueryParametersMapper.pageRequestToQueryParameters(pageRequest, sort))
      .map(response => PageMapper.mapBackend(response.json(), ProjectMapper.mapBackend))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.searchFailed')));
  }

  public searchProjects(searchQuery: ProjectSearchQuery): Observable<Array<Project>> {
    const searchUrl = ProjectService.PROJECT_URL + ProjectService.SEARCH;

    return this.authHttp.post(
      searchUrl,
      JSON.stringify(ProjectQueryParametersMapper.mapFrontend(searchQuery)),
      QueryParametersMapper.mapSortToSearchServiceQuery(searchQuery.sort))
      .map(response => PageMapper.mapBackend(response.json(), ProjectMapper.mapBackend))
      .map(page => page.content)
      .catch(err => this.errorHandler.handle(err, findTranslation('project.error.searchFailed')));
  }

  public getProject(id: number): Observable<Project> {
    return this.authHttp.get(ProjectService.PROJECT_URL + '/' + id)
      .map(response => response.json())
      .map(project => ProjectMapper.mapBackend(project))
      .catch(err => this.errorHandler.handle(err, findTranslation('project.error.fetchFailed')));
  }

  public save(project: Project): Observable<Project> {
    if (project.id) {
      const url = ProjectService.PROJECT_URL + '/' + project.id;

      return this.authHttp.put(url,
        JSON.stringify(ProjectMapper.mapFrontend(project)))
        .map(response => ProjectMapper.mapBackend(response.json()))
        .catch(err => this.errorHandler.handle(err, findTranslation('project.error.saveFailed')));
    } else {
      return this.authHttp.post(ProjectService.PROJECT_URL,
        JSON.stringify(ProjectMapper.mapFrontend(project)))
        .map(response => ProjectMapper.mapBackend(response.json()))
        .catch(err => this.errorHandler.handle(err, findTranslation('project.error.saveFailed')));
    }
  }

  public remove(id: number): Observable<HttpResponse> {
    return Observable.of(new HttpResponse(HttpStatus.OK, 'Project removed ' + id));
  }

  public updateProjectApplications(id: number, applicationIds: Array<number>): Observable<Project> {
    const url = ProjectService.PROJECT_URL + '/' + id + '/applications';
    return this.authHttp.put(url, JSON.stringify(applicationIds))
      .map(response => ProjectMapper.mapBackend(response.json()))
      .catch(err => this.errorHandler.handle(err, findTranslation('project.error.saveFailed')));
  }

  public addProjectApplication(id: number, applicationId: number): Observable<Project> {
    return this.getProjectApplications(id)
      .map(applications => applications.map(app => app.id))
      .map(appIds => appIds.concat(applicationId))
      .switchMap(appIds => this.updateProjectApplications(id, appIds));
  }

  public getProjectApplications(id: number): Observable<Array<Application>> {
    const url = ProjectService.PROJECT_URL + '/' + id + '/applications';
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(json => json.map(app => ApplicationMapper.mapBackend(app)))
      .catch(err => this.errorHandler.handle(err, findTranslation('project.error.applicationFetchFailed')));
  }

  public getChildProjects(id: number): Observable<Array<Project>> {
    const url = [ProjectService.PROJECT_URL, id, ProjectService.CHILDREN].join('/');
    return this.getProjects(url);
  }

  public getParentProjects(id: number): Observable<Array<Project>> {
    const url = [ProjectService.PROJECT_URL, id, ProjectService.PARENTS].join('/');
    return this.getProjects(url);
  }

  public updateParent(id: number, parentId: number): Observable<Project> {
    const url = [ProjectService.PROJECT_URL, id, 'parentProject', parentId].join('/');
    return this.authHttp.put(url, '')
      .map(response => response.json())
      .map(project => ProjectMapper.mapBackend(project))
      .catch(err => this.errorHandler.handle(err, findTranslation('project.error.updateParentFailed')));
  }

  public removeParent(ids: Array<number>): Observable<HttpResponse> {
    const url = [ProjectService.PROJECT_URL, 'parent', 'remove'].join('/');
    return this.authHttp.put(url, JSON.stringify(ids))
      .map(response => HttpUtil.extractHttpResponse(response))
      .catch(err => this.errorHandler.handle(err, findTranslation('project.error.removeParentFailed')));
  }

  private getProjects(url: string): Observable<Array<Project>> {
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(json => json.map(project => ProjectMapper.mapBackend(project)))
      .catch(err => this.errorHandler.handle(err, findTranslation('project.error.searchFailed')));
  }
}
