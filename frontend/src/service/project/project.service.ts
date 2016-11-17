import {Injectable} from '@angular/core';
import {URLSearchParams} from '@angular/http';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {Observable} from 'rxjs/Observable';
import '../../rxjs-extensions.ts';

import {Project} from '../../model/project/project';
import {HttpResponse} from '../../util/http.util';
import {HttpStatus} from '../../util/http.util';

@Injectable()
export class ProjectService {

  public searchProjects(search: any): Observable<Array<Project>> {
    return Observable.of([]);
  }

  public getProject(id: number): Observable<Project> {
    let project = new Project();
    project.id = id;
    return Observable.of(project);
  }

  public save(project: Project): Observable<Project> {
    if (project.id) {
      console.log('Update project', project);
    } else {
      console.log('Create project', project);
    }
    return Observable.of(project);
  }

  public remove(id: number): Observable<HttpResponse> {
    return Observable.of(new HttpResponse(HttpStatus.OK, 'Project removed ' + id));
  }

  public projectApplications(id: number, applicationIds: Array<number>): Observable<HttpResponse> {
    return Observable.of(new HttpResponse(HttpStatus.OK, 'Applications added'));
  }
}
