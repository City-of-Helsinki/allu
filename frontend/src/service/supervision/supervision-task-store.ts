import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';
import {SupervisionTask} from '../../model/application/supervision/supervision-task';
import {HttpResponse} from '../../util/http-response';
import {SupervisionTaskService} from './supervision-task.service';

@Injectable()
export class SupervisionTaskStore {
  private tasks$ = new BehaviorSubject<Array<SupervisionTask>>([]);

  constructor(private supervisionTaskService: SupervisionTaskService) {
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
    return this.supervisionTaskService.save(task)
      .do(saved => this.loadTasks(applicationId));
  }

  removeTask(applicationId: number, taskId: number): Observable<HttpResponse> {
    return this.supervisionTaskService.remove(taskId)
      .do(response => this.loadTasks(applicationId));
  }
}
