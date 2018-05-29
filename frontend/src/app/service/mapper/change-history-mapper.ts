import {BackendChangeHistoryItem} from '../backend-model/backend-change-history-item';
import {ChangeHistoryItem} from '../../model/history/change-history-item';
import {TimeUtil} from '../../util/time.util';
import {FieldChange} from '../../model/history/field-change';
import {BackendFieldChange} from '../backend-model/backend-field-change';
import {UserMapper} from './user-mapper';

class FieldChangeMapper {
  public static mapBackend(backendFieldChange: BackendFieldChange): FieldChange {
    return new FieldChange(
      backendFieldChange.fieldName,
      backendFieldChange.oldValue,
      backendFieldChange.newValue
    );
  }
}

export class ChangeHistoryMapper {
  public static mapBackend(backendChange: BackendChangeHistoryItem): ChangeHistoryItem {
    return new ChangeHistoryItem(
      UserMapper.mapBackend(backendChange.user),
      backendChange.changeType,
      backendChange.newStatus,
      TimeUtil.dateFromBackend(backendChange.changeTime),
      backendChange.fieldChanges ? backendChange.fieldChanges.map(fc => FieldChangeMapper.mapBackend(fc)) : []
    );
  }
}
