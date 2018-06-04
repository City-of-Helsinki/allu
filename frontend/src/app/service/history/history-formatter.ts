import {Injectable} from '@angular/core';
import {ChangeType} from '../../model/history/change-type';
import {ChangeHistoryItem} from '../../model/history/change-history-item';
import {EntityDescriptor} from '../../model/history/entity-change';
import {toDictionary} from '../../util/object.util';
import {Some} from '../../util/option';

@Injectable()
export class HistoryFormatter {
  public getChangeDescription(change: ChangeHistoryItem): EntityDescriptor {
    switch (ChangeType[change.changeType]) {
      case ChangeType.APPLICATION_ADDED:
      case ChangeType.APPLICATION_REMOVED:
        return this.getApplicationChangeDescription(change);
      default:
        return {
          ref: undefined,
          content: undefined
        };
    }
  }

  public getApplicationChangeDescription(change: ChangeHistoryItem): EntityDescriptor {
    const dict = toDictionary(change.fieldChanges, item => item.fieldName);
    const id = Some(dict['/id']).map(field => field.newValue || field.oldValue).orElse(undefined);
    const applicationId = Some(dict['/applicationId']).map(field => field.newValue || field.oldValue).orElse(undefined);
    const applicationName = Some(dict['/applicationName']).map(field => field.newValue || field.oldValue).orElse(undefined);

    return {
      ref: ['/applications', id, 'summary'],
      content: `${applicationId} - ${applicationName}`
    };
  }
}
