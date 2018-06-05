import {Injectable} from '@angular/core';
import {ChangeType} from '../../model/history/change-type';
import {ChangeHistoryItem} from '../../model/history/change-history-item';
import {toEntityChange} from '../../model/history/entity-change';

// Description containing either old and new values or single value
export interface ChangeDescription {
  old?: ChangeItemDescription;
  new?: ChangeItemDescription;
  single?: ChangeItemDescription;
}

export interface ChangeItemDescription {
  ref: string | any[];
  content;
}

@Injectable()
export class HistoryFormatter {
  public getChangeDescription(change: ChangeHistoryItem): ChangeDescription {
    switch (ChangeType[change.changeType]) {
      case ChangeType.APPLICATION_ADDED:
      case ChangeType.APPLICATION_REMOVED:
        return this.getApplicationChangeDescription(change);
      case ChangeType.CUSTOMER_CHANGED:
        return this.getCustomerChangeDescription(change);
      default:
        return undefined;
    }
  }

  public getApplicationChangeDescription(change: ChangeHistoryItem): ChangeDescription {
    const entityChange = toEntityChange(change.fieldChanges);
    const entity = ChangeType.APPLICATION_ADDED === ChangeType[change.changeType]
      ? entityChange.newEntity
      : entityChange.oldEntity;

    const id = entity['/id'];
    const applicationId = entity['/applicationId'];
    const applicationName = entity['/applicationName'];

    return {
      single: {
        ref: ['/applications', id, 'summary'],
        content: `${applicationId} - ${applicationName}`
      }
    };
  }

  public getCustomerChangeDescription(change: ChangeHistoryItem): ChangeDescription {
    const entityChange = toEntityChange(change.fieldChanges);
    return {
      old: {
        ref: ['/customers', entityChange.oldEntity['/id']],
        content: entityChange.oldEntity['/customerName']
      },
      new: {
        ref: ['/customers', entityChange.newEntity['/id']],
        content: entityChange.newEntity['/customerName']
      }
    };
  }
}
