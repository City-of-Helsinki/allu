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
import {ApplicationSearchQuery} from '@model/search/ApplicationSearchQuery';
import {BackendQueryParameter, BackendQueryParameters} from '@service/backend-model/backend-query-parameters';
import {QueryParametersMapper} from '@service/mapper/query/query-parameters-mapper';

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

  static mapSearchCriteria(searchCriteria: SupervisionTaskSearchCriteria): BackendQueryParameters {
    return searchCriteria ?
      {
        queryParameters: SupervisionSearchMapper.mapSupervisionParameters(searchCriteria)
      } : undefined;
  }

  private static mapSupervisionParameters(query: SupervisionTaskSearchCriteria): Array<BackendQueryParameter> {
    const queryParameters: Array<BackendQueryParameter> = [];
    QueryParametersMapper.mapArrayParameter(queryParameters, 'type',  query.taskTypes);
    QueryParametersMapper.mapParameter(queryParameters, 'applicationIdText',  query.applicationId);
    QueryParametersMapper.mapDateParameter(queryParameters, 'plannedFinishingTime',  query.after, query.before, true);
    QueryParametersMapper.mapArrayParameter(queryParameters, 'applicationType',  query.applicationTypes);
    QueryParametersMapper.mapArrayParameter(queryParameters, 'applicationStatus',  query.applicationStatus);
    const owners = query.owners || [];
    QueryParametersMapper.mapArrayParameter(queryParameters, 'owner.id',  owners.map(id => id.toString()));
    const cityDistrictIds = query.cityDistrictIds || [];
    QueryParametersMapper.mapArrayParameter(queryParameters, 'cityDistrictId',  cityDistrictIds.map(id => id.toString()));
    return queryParameters;
  }
}

