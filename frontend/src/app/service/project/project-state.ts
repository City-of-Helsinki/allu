import {Injectable} from '@angular/core';
import {Observable, BehaviorSubject, of, combineLatest} from 'rxjs';
import {Project} from '../../model/project/project';
import {Application} from '../../model/application/application';
import {ProjectService} from './project.service';
import * as fromRoot from '../../feature/allu/reducers';
import {Store} from '@ngrx/store';
import {map, switchMap, tap} from 'rxjs/internal/operators';

@Injectable()
export class ProjectState {
  private _project: Project;
  private childProjects$ = new BehaviorSubject<Array<Project>>([]);
  private parentProjects$ = new BehaviorSubject<Array<Project>>([]);
  private applications$ = new BehaviorSubject<Array<Application>>([]);

  constructor(private projectService: ProjectService,
              private store: Store<fromRoot.State>) {}

  createNew(): Observable<Project> {
    this._project = new Project();
    this.childProjects$.next([]);
    this.parentProjects$.next([]);
    this.applications$.next([]);
    return of(this._project);
  }

  load(id: number): Observable<Project> {
    return this.projectService.getProject(id).pipe(tap(p => this._project = p));
  }

  loadChildProjects(id: number): Observable<Array<Project>> {
    return this.projectService.getChildProjects(id).pipe(
      tap(children => this.childProjects$.next(children))
    );
  }

  loadParentProjects(id: number): Observable<Array<Project>> {
    return this.projectService.getParentProjects(id).pipe(
      tap(parents => this.parentProjects$.next(parents))
    );
  }

  loadRelatedProjects(id: number): Observable<Array<Project>> {
    return combineLatest([
      this.loadParentProjects(id),
      this.loadChildProjects(id)
    ]).pipe(map(projects => [].concat.apply([], projects))); // maps array[array, array] => array
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
    return combineLatest([
      this.parentProjects,
      this.childProjects
    ]).pipe(map(projects => [].concat.apply([], projects)));
  }

  save(project: Project): Observable<Project> {
    return this.projectService.save(project).pipe(
      tap(p => this._project = p)
    );
  }

  updateParentProject(project: Project): Observable<Array<Project>> {
    return this.projectService.updateParent(project.id, this._project.id).pipe(
      switchMap(updated => this.loadRelatedProjects(this.project.id))
    );
  }

  removeParentsFrom(projectIds: Array<number>): Observable<Array<Project>> {
    if (projectIds.length > 0) {
      return this.projectService.removeParent(projectIds).pipe(
        switchMap(response => this.loadRelatedProjects(this.project.id))
      );
    } else {
      return this.relatedProjects;
    }
  }

  districtNames(ids?: Array<number>): Observable<Array<string>> {
    const districtIds = ids || this._project.cityDistricts;

    return this.store.select(fromRoot.getCityDistrictsByIds(districtIds)).pipe(
      map(ds => ds.map(d => d.name))
    );
  }
}
