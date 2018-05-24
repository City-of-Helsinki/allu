import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {SupervisionTask} from '../../model/application/supervision/supervision-task';
import {SupervisionTaskService} from './supervision-task.service';
import {ApplicationStore} from '../application/application-store';
import {tap} from 'rxjs/internal/operators';

@Injectable()
export class SupervisionTaskStore {
  private tasks$ = new BehaviorSubject<Array<SupervisionTask>>([]);

  constructor(private supervisionTaskService: SupervisionTaskService,
              private applicationStore: ApplicationStore) {
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
      tap(() => this.applicationStore.load(applicationId).subscribe())
    );
  }

  removeTask(applicationId: number, taskId: number): Observable<{}> {
    return this.supervisionTaskService.remove(taskId).pipe(
      tap(() => this.loadTasks(applicationId)),
      tap(() => this.applicationStore.load(applicationId).subscribe())
    );
  }

  approve(task: SupervisionTask): Observable<SupervisionTask> {
    return this.supervisionTaskService.approve(task).pipe(
      tap(() => this.loadTasks(task.applicationId)),
      tap(() => this.applicationStore.load(task.applicationId).subscribe())
    );
  }

  reject(task: SupervisionTask, newSupervisionDate: Date): Observable<SupervisionTask> {
    return this.supervisionTaskService.reject(task, newSupervisionDate).pipe(
      tap(() => this.loadTasks(task.applicationId)),
      tap(() => this.applicationStore.load(task.applicationId).subscribe())
    );
  }
}
