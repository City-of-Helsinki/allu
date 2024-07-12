import {SupervisionTaskType} from '../../model/application/supervision/supervision-task-type';
import {
  BackendSupervisionWorkItem,
  SupervisionWorkItem
} from '../../model/application/supervision/supervision-work-item';
import {ApplicationStatus} from '../../model/application/application-status';
import {UserMapper} from '../mapper/user-mapper';
import {TimeUtil} from '../../util/time.util';
import {PostalAddress} from '../../model/common/postal-address';
import {Some} from '../../util/option';
import {
  BackendSupervisionTaskSearchCriteria,
  SupervisionTaskSearchCriteria
} from '../../model/application/supervision/supervision-task-search-criteria';

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
    workItem.postalAddress = Some(backendWorkItem.postalAddress)
      .map(address => PostalAddress.fromBackend(address)).orElse(new PostalAddress());
    workItem.address = backendWorkItem.address;
    workItem.projectName = backendWorkItem.projectName;
    workItem.owner = UserMapper.mapBackend(backendWorkItem.owner);
    return workItem;
  }

  static mapSearchCriteria(searchCriteria: SupervisionTaskSearchCriteria): BackendSupervisionTaskSearchCriteria {
    return (searchCriteria) ?
      {
        taskTypes: Some(searchCriteria.taskTypes).orElse([]),
        applicationId: searchCriteria.applicationId,
        after: TimeUtil.dateToBackend(searchCriteria.after),
        before: TimeUtil.dateToBackend(searchCriteria.before),
        applicationTypes: Some(searchCriteria.applicationTypes).orElse([]),
        applicationStatus: Some(searchCriteria.applicationStatus).orElse([]),
        owners: searchCriteria.owners,
        cityDistrictIds: Some(searchCriteria.cityDistrictIds).orElse([])
      } : undefined;
  }
}


