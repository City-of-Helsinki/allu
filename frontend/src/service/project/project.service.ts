import {Injectable} from '@angular/core';
import {URLSearchParams} from '@angular/http';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {Observable} from 'rxjs/Observable';
import '../../rxjs-extensions.ts';

import {Project} from '../../model/project/project';
import {HttpResponse} from '../../util/http.util';
import {HttpStatus} from '../../util/http.util';
import {ProjectMapper} from '../mapper/project-mapper';
import {UIStateHub} from '../ui-state/ui-state-hub';
import {HttpUtil} from '../../util/http.util';
import {ErrorInfo} from '../ui-state/error-info';
import {ErrorType} from '../ui-state/error-type';
import {ApplicationMapper} from '../mapper/application-mapper';
import {Application} from '../../model/application/application';
import {ProjectSearchQuery} from '../../model/project/project-search-query';
import {QueryParametersMapper} from '../mapper/query-parameters-mapper';

@Injectable()
export class ProjectService {

  static PROJECT_URL = '/api/projects';
  static SEARCH = '/search';
  static CHILDREN = 'children';
  static PARENTS = 'parents';

  constructor(private authHttp: AuthHttp, private uiState: UIStateHub) {}

  public searchProjects(searchQuery: ProjectSearchQuery): Observable<Array<Project>> {
    let searchUrl = ProjectService.PROJECT_URL + ProjectService.SEARCH;

    return this.authHttp.post(
      searchUrl,
      JSON.stringify(QueryParametersMapper.mapProjectQueryFrontend(searchQuery)))
      .map(response => response.json())
      .map(json => json.map(project => ProjectMapper.mapBackend(project)))
      .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.PROJECT_SEARCH_FAILED, HttpUtil.extractMessage(err))));
  }

  public getProject(id: number): Observable<Project> {
    return this.authHttp.get(ProjectService.PROJECT_URL + '/' + id)
      .map(response => response.json())
      .map(project => ProjectMapper.mapBackend(project))
      .catch(err => this.uiState.addError(HttpUtil.extractMessage(err)));
  }

  public save(project: Project): Observable<Project> {
    if (project.id) {
      let url = ProjectService.PROJECT_URL + '/' + project.id;

      return this.authHttp.put(url,
        JSON.stringify(ProjectMapper.mapFrontend(project)))
        .map(response => ProjectMapper.mapBackend(response.json()))
        .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.PROJECT_SAVE_FAILED, HttpUtil.extractMessage(err))));
    } else {
      return this.authHttp.post(ProjectService.PROJECT_URL,
        JSON.stringify(ProjectMapper.mapFrontend(project)))
        .map(response => ProjectMapper.mapBackend(response.json()))
        .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.PROJECT_SAVE_FAILED, HttpUtil.extractMessage(err))));
    }
  }

  public remove(id: number): Observable<HttpResponse> {
    return Observable.of(new HttpResponse(HttpStatus.OK, 'Project removed ' + id));
  }

  public updateProjectApplications(id: number, applicationIds: Array<number>): Observable<Project> {
    let url = ProjectService.PROJECT_URL + '/' + id + '/applications';
    return this.authHttp.put(url, JSON.stringify(applicationIds))
      .map(response => ProjectMapper.mapBackend(response.json()))
      .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.PROJECT_SAVE_FAILED, HttpUtil.extractMessage(err))));
  }

  public addProjectApplication(id: number, applicationId: number): Observable<Project> {
    return this.getProjectApplications(id)
      .map(applications => applications.map(app => app.id))
      .map(appIds => appIds.concat(applicationId))
      .switchMap(appIds => this.updateProjectApplications(id, appIds));
  }

  public getProjectApplications(id: number): Observable<Array<Application>> {
    let url = ProjectService.PROJECT_URL + '/' + id + '/applications';
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(json => json.map(app => ApplicationMapper.mapBackend(app)))
      .catch(err => this.uiState.addError(HttpUtil.extractMessage(err)));
  }

  public getChildProjects(id: number): Observable<Array<Project>> {
    let url = [ProjectService.PROJECT_URL, id, ProjectService.CHILDREN].join('/');
    return this.getProjects(url);
  }

  public getParentProjects(id: number): Observable<Array<Project>> {
    let url = [ProjectService.PROJECT_URL, id, ProjectService.PARENTS].join('/');
    return this.getProjects(url);
  }

  private getProjects(url: string): Observable<Array<Project>> {
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(json => json.map(project => ProjectMapper.mapBackend(project)))
      .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.PROJECT_SEARCH_FAILED, HttpUtil.extractMessage(err))));
  }
}
