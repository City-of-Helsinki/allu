import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {SupervisionTask} from '../../model/application/supervision/supervision-task';
import {SupervisionTaskService} from './supervision-task.service';
import {tap} from 'rxjs/internal/operators';
import {Store} from '@ngrx/store';
import * as fromApplication from '../../feature/application/reducers';
import * as tagActions from '../../feature/application/actions/application-tag-actions';

@Injectable()
export class SupervisionTaskStore {
  private tasks$ = new BehaviorSubject<Array<SupervisionTask>>([]);

  constructor(private supervisionTaskService: SupervisionTaskService,
              private store: Store<fromApplication.State>) {
  }

  get tasks(): Observable<Array<SupervisionTask>> {
    return this.tasks$.asObservable();
  }

  loadTasks(applicationId: number): void {
    this.supervisionTaskService.findTasksByApplicationId(applicationId)
      .subscribe(tasks => this.tasks$.next(tasks));
  }

  saveTask(applicationId: number, task: SupervisionTask): Observable<SupervisionTask> {
    task.applicationId = applicationId;
    return this.supervisionTaskService.save(task).pipe(
      tap(() => this.loadTasks(applicationId)),
      tap(() => this.store.dispatch(new tagActions.Load()))
    );
  }

  removeTask(applicationId: number, taskId: number): Observable<{}> {
    return this.supervisionTaskService.remove(taskId).pipe(
      tap(() => this.loadTasks(applicationId)),
      tap(() => this.store.dispatch(new tagActions.Load()))
    );
  }

  approve(task: SupervisionTask): Observable<SupervisionTask> {
    return this.supervisionTaskService.approve(task).pipe(
      tap(() => this.loadTasks(task.applicationId)),
      tap(() => this.store.dispatch(new tagActions.Load()))
    );
  }

  reject(task: SupervisionTask, newSupervisionDate: Date): Observable<SupervisionTask> {
    return this.supervisionTaskService.reject(task, newSupervisionDate).pipe(
      tap(() => this.loadTasks(task.applicationId)),
      tap(() => this.store.dispatch(new tagActions.Load()))
    );
  }
}
