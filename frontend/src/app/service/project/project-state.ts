import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Project} from '../../model/project/project';
import {Application} from '../../model/application/application';
import {ProjectHub} from './project-hub';
import {MapHub} from '../map/map-hub';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

@Injectable()
export class ProjectState {
  private _project: Project;
  private childProjects$ = new BehaviorSubject<Array<Project>>([]);
  private parentProjects$ = new BehaviorSubject<Array<Project>>([]);
  private applications$ = new BehaviorSubject<Array<Application>>([]);

  constructor(private projectHub: ProjectHub,
              private mapHub: MapHub) {}

  createNew(): Observable<Project> {
    this._project = new Project();
    this.childProjects$.next([]);
    this.parentProjects$.next([]);
    this.applications$.next([]);
    return Observable.of(this._project);
  }

  load(id: number): Observable<Project> {
    return this.projectHub.getProject(id)
      .do(p => this._project = p);
  }

  loadChildProjects(id: number): Observable<Array<Project>> {
    return this.projectHub.getChildProjects(id)
      .do(children => this.childProjects$.next(children));
  }

  loadParentProjects(id: number): Observable<Array<Project>> {
    return this.projectHub.getParentProjects(id)
      .do(parents => this.parentProjects$.next(parents));
  }

  loadRelatedProjects(id: number): Observable<Array<Project>> {
    return Observable.combineLatest(
      this.loadParentProjects(id),
      this.loadChildProjects(id)
    ).map(projects => [].concat.apply([], projects)); // maps array[array, array] => array
  }

  loadApplications(id: number): Observable<Array<Application>> {
    return this.projectHub.getProjectApplications(id)
      .do(applications => this.applications$.next(applications));
  }

  get project(): Project {
    return this._project;
  }

  set project(value: Project) {
    this._project = value;
  }

  get childProjects(): Observable<Array<Project>> {
    return this.childProjects$.asObservable();
  }

  get parentProjects(): Observable<Array<Project>> {
    return this.parentProjects$.asObservable();
  }

  get applications(): Observable<Array<Application>> {
    return this.applications$.asObservable();
  }

  get relatedProjects(): Observable<Array<Project>> {
    return Observable.combineLatest(
      this.parentProjects,
      this.childProjects
    ).map(projects => [].concat.apply([], projects));
  }

  save(project: Project): Observable<Project> {
    return this.projectHub.save(project)
      .do(p => this._project = p);
  }

  updateApplications(appIds: Array<number>): Observable<Project> {
    return this.projectHub.updateProjectApplications(this._project.id, appIds)
      .do(p => {
        this._project = p;
        this.loadApplications(p.id).subscribe(apps => {});
      });
  }

  updateParentProject(project: Project): Observable<Array<Project>> {
    return this.projectHub.updateParent(project.id, this._project.id)
      .switchMap(updated => this.loadRelatedProjects(this.project.id));
  }

  removeParentsFrom(projectIds: Array<number>): Observable<Array<Project>> {
    if (projectIds.length > 0) {
      return this.projectHub.removeParent(projectIds)
        .switchMap(response => this.loadRelatedProjects(this.project.id));
    } else {
      return this.relatedProjects;
    }
  }

  districtNames(ids?: Array<number>): Observable<Array<string>> {
    const districtIds = ids || this._project.cityDistricts;

    return this.mapHub.districtsById(districtIds)
      .map(ds => ds.map(d => d.name));
  }
}
