import {BackendApplicationChange} from '../backend-model/backend-application-change';
import {ApplicationChange} from '../../model/application/application-change/application-change';
import {TimeUtil} from '../../util/time.util';
import {ApplicationFieldChange} from '../../model/application/application-change/application-field-change';
import {BackendApplicationFieldChange} from '../backend-model/backend-application-field-change';
import {UserMapper} from './user-mapper';

class ApplicationFieldChangeMapper {
  public static mapBackend(backendFieldChange: BackendApplicationFieldChange): ApplicationFieldChange {
    return new ApplicationFieldChange(
      backendFieldChange.fieldName,
      backendFieldChange.oldValue,
      backendFieldChange.newValue
    );
  }
}

export class ApplicationChangeMapper {
  public static mapBackend(backendChange: BackendApplicationChange): ApplicationChange {
    return new ApplicationChange(
      UserMapper.mapBackend(backendChange.user),
      backendChange.changeType,
      backendChange.newStatus,
      TimeUtil.dateFromBackend(backendChange.changeTime),
      backendChange.fieldChanges ? backendChange.fieldChanges.map(fc => ApplicationFieldChangeMapper.mapBackend(fc)) : []
    );
  }
}
