import {SupervisionTaskType} from '../../model/application/supervision/supervision-task-type';
import {BackendSupervisionWorkItem, SupervisionWorkItem} from '../../model/application/supervision/supervision-work-item';
import {ApplicationStatus} from '../../model/application/application-status';
import {UserMapper} from '../mapper/user-mapper';
import {TimeUtil} from '../../util/time.util';
import {PostalAddress} from '../../model/common/postal-address';
import {Some} from '../../util/option';
import {
  BackendSupervisionTaskSearchCriteria,
  SupervisionTaskSearchCriteria
} from '../../model/application/supervision/supervision-task-search-criteria';
import {ApplicationType} from '../../model/application/type/application-type';

export class SupervisionSearchMapper {
  static mapWorkItem(backendWorkItem: BackendSupervisionWorkItem): SupervisionWorkItem {
    const workItem = new SupervisionWorkItem();
    workItem.id = backendWorkItem.id;
    workItem.applicationId = backendWorkItem.applicationId;
    workItem.type = SupervisionTaskType[backendWorkItem.type];
    workItem.applicationId = backendWorkItem.applicationId;
    workItem.applicationIdText = backendWorkItem.applicationIdText;
    workItem.applicationStatus = ApplicationStatus[backendWorkItem.applicationStatus];
    workItem.creator = UserMapper.mapBackend(backendWorkItem.creator);
    workItem.plannedFinishingTime = TimeUtil.dateFromBackend(backendWorkItem.plannedFinishingTime);
    workItem.address = Some(backendWorkItem.address).map(address => PostalAddress.fromBackend(address)).orElse(new PostalAddress());
    workItem.projectName = backendWorkItem.projectName;
    workItem.handler = UserMapper.mapBackend(backendWorkItem.handler);
    return workItem;
  }

  static mapSearchCriteria(searchCriteria: SupervisionTaskSearchCriteria): BackendSupervisionTaskSearchCriteria {
    return (searchCriteria) ?
      {
        taskTypes: Some(searchCriteria.taskTypes).map(types => types.map(type => SupervisionTaskType[type])).orElse([]),
        applicationId: searchCriteria.applicationId,
        after: TimeUtil.dateToBackend(searchCriteria.after),
        before: TimeUtil.dateToBackend(searchCriteria.before),
        applicationTypes: Some(searchCriteria.applicationTypes).map(types => types.map(type => ApplicationType[type])).orElse([]),
        applicationStatus: Some(searchCriteria.applicationStatus).map(status => status.map(s => ApplicationStatus[s])).orElse([]),
        handlerId: searchCriteria.handlerId
      } : undefined;
  }
}

