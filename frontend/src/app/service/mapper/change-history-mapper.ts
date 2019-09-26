import {BackendChangeHistoryItem} from '../backend-model/backend-change-history-item';
import {BackendChangeHistoryItemInfo} from '../backend-model/backend-change-history-item-info';
import {ChangeHistoryItem} from '../../model/history/change-history-item';
import {ChangeHistoryItemInfo} from '../../model/history/change-history-item-info';
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
      this.mapBackendInfo(backendChange.info),
      backendChange.changeType,
      backendChange.changeSpecifier,
      backendChange.changeSpecifier2,
      TimeUtil.dateFromBackend(backendChange.changeTime),
      backendChange.fieldChanges ? backendChange.fieldChanges.map(fc => FieldChangeMapper.mapBackend(fc)) : []
    );
  }

  private static mapBackendInfo(backendInfo: BackendChangeHistoryItemInfo): ChangeHistoryItemInfo {
    return new ChangeHistoryItemInfo(
      backendInfo.id,
      backendInfo.name,
      backendInfo.applicationId
    );
  }
}
