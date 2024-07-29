import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

import {Project} from '../../model/project/project';
import {ProjectMapper} from '../mapper/project-mapper';
import {ApplicationMapper} from '../mapper/application-mapper';
import {Application} from '../../model/application/application';
import {ProjectSearchQuery} from '../../model/project/project-search-query';
import {ProjectQueryParametersMapper} from '../mapper/query/project-query-parameters-mapper';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';
import {QueryParametersMapper} from '../mapper/query/query-parameters-mapper';
import {PageMapper} from '../common/page-mapper';
import {PageRequest} from '../../model/common/page-request';
import {Page} from '../../model/common/page';
import {Sort} from '../../model/common/sort';
import {BackendPage} from '../backend-model/backend-page';
import {BackendProject} from '../backend-model/backend-project';
import {BackendApplication} from '../backend-model/backend-application';
import {catchError, map} from 'rxjs/internal/operators';

@Injectable()
export class ProjectService {

  static PROJECT_URL = '/api/projects';
  static SEARCH = '/search';
  static CHILDREN = 'children';
  static PARENTS = 'parents';

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {}

  /**
   * Fetches projects based on given search query
   */
  public pagedSearch(searchQuery: ProjectSearchQuery, sort?: Sort, pageRequest?: PageRequest): Observable<Page<Project>> {
    const searchUrl = ProjectService.PROJECT_URL + ProjectService.SEARCH;

    return this.http.post<BackendPage<BackendProject>>(
      searchUrl,
      JSON.stringify(ProjectQueryParametersMapper.mapFrontend(searchQuery)),
      {params: QueryParametersMapper.mapPageRequest(pageRequest, sort)}).pipe(
      map(page => PageMapper.mapBackend(page, ProjectMapper.mapBackend)),
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.searchFailed')))
    );
  }

  public getProject(id: number): Observable<Project> {
    return this.http.get<BackendProject>(ProjectService.PROJECT_URL + '/' + id).pipe(
      map(project => ProjectMapper.mapBackend(project)),
      catchError(err => this.errorHandler.handle(err, findTranslation('project.error.fetchFailed')))
    );
  }

  public save(project: Project): Observable<Project> {
    if (project.id) {
      const url = ProjectService.PROJECT_URL + '/' + project.id;

      return this.http.put<BackendProject>(url,
        JSON.stringify(ProjectMapper.mapFrontend(project))).pipe(
        map(saved => ProjectMapper.mapBackend(saved)),
        catchError(err => this.errorHandler.handle(err, findTranslation('project.error.saveFailed')))
      );
    } else {
      return this.http.post<BackendProject>(ProjectService.PROJECT_URL,
        JSON.stringify(ProjectMapper.mapFrontend(project))).pipe(
        map(saved => ProjectMapper.mapBackend(saved)),
        catchError(err => this.errorHandler.handle(err, findTranslation('project.error.saveFailed')))
      );
    }
  }

  public delete(id: number): Observable<object> {
    const url = ProjectService.PROJECT_URL + '/' + id;
    return this.http.delete(url).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('project.error.removeFailed')))
    );
  }

  public addProjectApplications(id: number, applicationIds: number[]): Observable<Application[]> {
    const url = `${ProjectService.PROJECT_URL}/${id}/applications`;
    return this.http.put<BackendApplication[]>(url, JSON.stringify(applicationIds)).pipe(
      map(applications => applications.map(app => ApplicationMapper.mapBackend(app))),
      catchError(err => this.errorHandler.handle(err, findTranslation('project.error.applicationAddFailed')))
    );
  }

  public addProjectApplication(id: number, applicationId: number): Observable<Application[]> {
    return this.addProjectApplications(id, [applicationId]);
  }

  public removeApplication(appId: number): Observable<object> {
    const url = `${ProjectService.PROJECT_URL}/applications/${appId}`;
    return this.http.delete(url).pipe(
      catchError(err => this.errorHandler.handle(err, findTranslation('project.error.applicationRemoveFailed')))
    );
  }

  public getProjectApplications(id: number, sort?: Sort, pageRequest?: PageRequest): Observable<Array<Application>> {
    const url = ProjectService.PROJECT_URL + '/' + id + '/applications';
    return this.http.get<BackendApplication[]>(url,
      {params: QueryParametersMapper.mapPageRequest(pageRequest, sort)}).pipe(
      map(applications => applications.map(app => ApplicationMapper.mapBackend(app))),
      catchError(err => this.errorHandler.handle(err, findTranslation('project.error.applicationFetchFailed')))
    );
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
    return this.http.put<BackendProject>(url, '').pipe(
      map(project => ProjectMapper.mapBackend(project)),
      catchError(err => this.errorHandler.handle(err, findTranslation('project.error.updateParentFailed')))
    );
  }

  public removeParent(ids: Array<number>): Observable<object> {
    const url = [ProjectService.PROJECT_URL, 'parent', 'remove'].join('/');
    return this.http.put(url, JSON.stringify(ids)).pipe(
      catchError(err => this.errorHandler.handle(err, findTranslation('project.error.removeParentFailed')))
    );
  }

  public getNextProjectNumber(): Observable<number> {
    const url = [ProjectService.PROJECT_URL, 'nextProjectNumber'].join('/');
    return this.http.post<number>(url, null).pipe(
      catchError(err => this.errorHandler.handle(err, findTranslation('project.error.nextProjectNumberFailed')))
    );
  }

  private getProjects(url: string): Observable<Array<Project>> {
    return this.http.get<BackendProject[]>(url).pipe(
      map(projects => projects.map(project => ProjectMapper.mapBackend(project))),
      catchError(err => this.errorHandler.handle(err, findTranslation('project.error.searchFailed')))
    );
  }
}
