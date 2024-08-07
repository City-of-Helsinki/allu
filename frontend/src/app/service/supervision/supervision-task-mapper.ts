import {SupervisionTask} from '../../model/application/supervision/supervision-task';
import {TimeUtil} from '../../util/time.util';
import {UserMapper} from '../mapper/user-mapper';
import {Some} from '../../util/option';
import {BackendSupervisionTask} from '../../model/application/supervision/backend-supervision-task';
import {SupervisionTaskType} from '../../model/application/supervision/supervision-task-type';
import {SupervisionTaskStatusType} from '../../model/application/supervision/supervision-task-status-type';
import {LocationMapper} from '@app/service/mapper/location-mapper';
import {Location} from '@model/common/location';

export class SupervisionTaskMapper {
  static mapBackendList(tasks: Array<BackendSupervisionTask>): Array<SupervisionTask> {
    return (tasks)
      ? tasks.map(task => SupervisionTaskMapper.mapBackend(task))
      : [];
  }

  static mapBackend(task: BackendSupervisionTask): SupervisionTask {
    return new SupervisionTask(
      task.id,
      task.applicationId,
      task.type,
      Some(task.creator).map(creator => UserMapper.mapBackend(creator)).orElse(undefined),
      Some(task.owner).map(owner => UserMapper.mapBackend(owner)).orElse(undefined),
      TimeUtil.dateFromBackend(task.creationTime),
      TimeUtil.dateFromBackend(task.plannedFinishingTime),
      TimeUtil.dateFromBackend(task.actualFinishingTime),
      task.status,
      task.description,
      task.result,
      task.locationId,
      LocationMapper.mapBackendSupervisionTaskLocations(task.supervisedLocations)
    );
  }

  static mapFrontend(task: SupervisionTask): any {
    return (task) ?
      {
        id: task.id,
        applicationId: task.applicationId,
        type: task.type,
        creator: Some(task.creator).map(creator => UserMapper.mapFrontend(creator)).orElse(undefined),
        owner: Some(task.owner).map(owner => UserMapper.mapFrontend(owner)).orElse(undefined),
        creationTime: TimeUtil.dateToBackend(task.creationTime),
        plannedFinishingTime: TimeUtil.dateToBackend(task.plannedFinishingTime),
        actualFinishingTime: TimeUtil.dateToBackend(task.actualFinishingTime),
        status: task.status,
        description: task.description,
        result: task.result,
        locationId: task.locationId
      }
      : undefined;
  }
}

