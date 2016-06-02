import {Injectable, Injector, ReflectiveInjector} from '@angular/core';
import {EventService} from '../../event/event.service';
import {Event} from '../../event/event';
import {EventListener} from '../../event/event-listener';
import {ApplicationsLoadEvent} from '../../event/load/applications-load-event';
import {LoadApplicationsTask} from '../../task/application/load-applications-task';
import {ApplicationSaveEvent} from '../../event/save/application-save-event';
import {SaveApplicationTask} from '../../task/application/save-application-task';
import {Task} from './task';

@Injectable()
export class TaskManagerService implements EventListener {

  private eventToTaskMap: Map<string, { new (...args: any[]): Task; }>;
  private reflectiveInjector: ReflectiveInjector;

  constructor(private eventService: EventService, private injector: Injector) {
    console.log('Task Manager created');
    this.eventService.subscribe(this);
    this.eventToTaskMap = TaskManagerService.getEventTaskMapping();
    let taskProviders = Array.from(this.eventToTaskMap.values());
    this.reflectiveInjector = ReflectiveInjector.resolveAndCreate(taskProviders, this.injector);
  }

  // TODO: the event to task mapping could be moved outside this class
  private static getEventTaskMapping(): Map<string, { new (...args: any[]): Task; }> {
    let eventToTaskMap = new Map<string, { new (...args: any[]): Task; }>();
    eventToTaskMap.set(TaskManagerService.extractName(ApplicationSaveEvent.toString()), SaveApplicationTask);
    eventToTaskMap.set(TaskManagerService.extractName(ApplicationsLoadEvent.toString()), LoadApplicationsTask);
    return eventToTaskMap;
  }

  private static getEventName(event: Event): string {
    return this.extractName(event.constructor.toString());
  }

  private static extractName(str: string): string {
    return str.match(/\w+/g)[1];
  }

  public handle(event: Event): void {
    let task: new () => Task = this.eventToTaskMap.get(TaskManagerService.getEventName(event));
    if (task) {
      let taskObj: Task = this.reflectiveInjector.get(task);
      taskObj.execute(this, this.eventService, event);
    }
  }
}
