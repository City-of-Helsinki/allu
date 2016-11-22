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

  public updateProjectApplications(id: number, applicationIds: Array<number>): Observable<HttpResponse> {
    let url = ProjectService.PROJECT_URL + '/' + id + '/applications';
    return this.authHttp.put(url, JSON.stringify(applicationIds))
      .map(response => new HttpResponse(HttpStatus.OK, 'Applications added to project ' + id))
      .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.PROJECT_SAVE_FAILED, HttpUtil.extractMessage(err))));
  }

  public getProjectApplications(id: number): Observable<Array<Application>> {
    let url = ProjectService.PROJECT_URL + '/' + id + '/applications';
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(json => json.map(app => ApplicationMapper.mapBackend(app)))
      .catch(err => this.uiState.addError(HttpUtil.extractMessage(err)));
  }
}
