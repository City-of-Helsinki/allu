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
  ref?: string | any[];
  content: string;
}

export enum ChangeDescriptionType {
  FULL,
  SIMPLE
}

@Injectable()
export class HistoryFormatter {
  public getChangeDescription(change: ChangeHistoryItem, type: ChangeDescriptionType = ChangeDescriptionType.FULL): ChangeDescription {
    switch (ChangeType[change.changeType]) {
      case ChangeType.APPLICATION_ADDED:
      case ChangeType.APPLICATION_REMOVED:
        return this.getApplicationChangeDescription(change, type);
      case ChangeType.CUSTOMER_CHANGED:
        return this.getCustomerChangeDescription(change, type);
      case ChangeType.CONTACT_CHANGED:
        return this.getContactChangeDescription(change, type);
      case ChangeType.STATUS_CHANGED:
        return this.getStatusChangeDescription(change, type);
      default:
        return undefined;
    }
  }

  public getApplicationChangeDescription(change: ChangeHistoryItem, type: ChangeDescriptionType): ChangeDescription {
    const entityChange = toEntityChange(change.fieldChanges);
    const entity = ChangeType.APPLICATION_ADDED === ChangeType[change.changeType]
      ? entityChange.newEntity
      : entityChange.oldEntity;

    const id = entity['/id'] ? entity['/id'] : change.info.id;
    const applicationId = entity['/applicationId'] ? entity['/applicationId'] : change.info.applicationId;
    const applicationName = entity['/applicationName'] ? entity['/applicationName'] : change.info.name;
    const content = this.getApplicationContent(type, applicationId, applicationName);

    return { single: this.createChangeItemDescription(type, content, ['/applications', id, 'summary']) };
  }

  public getCustomerChangeDescription(change: ChangeHistoryItem, type: ChangeDescriptionType): ChangeDescription {
    const entityChange = toEntityChange(change.fieldChanges);
    return {
      old: this.createChangeItemDescription(type, entityChange.oldEntity['/customerName'], ['/customers', entityChange.oldEntity['/id']]),
      new: this.createChangeItemDescription(type, entityChange.newEntity['/customerName'], ['/customers', entityChange.newEntity['/id']])
    };
  }

  private getContactChangeDescription(change: ChangeHistoryItem, type: ChangeDescriptionType) {
    const entityChange = toEntityChange(change.fieldChanges);
    return {
      old: this.createChangeItemDescription(type, entityChange.oldEntity['/name']),
      new: this.createChangeItemDescription(type, entityChange.newEntity['/name'])
    };
  }

  private getStatusChangeDescription(change: ChangeHistoryItem, type: ChangeDescriptionType): ChangeDescription {
    const id = change.info.id;
    const applicationId = change.info.applicationId;
    const applicationName = change.info.name;
    const content = this.getApplicationContent(type, applicationId, applicationName);

    return { single: this.createChangeItemDescription(type, content, ['/applications', id, 'summary']) };
  }

  private createChangeItemDescription(descriptionType: ChangeDescriptionType,
                                      content: string, ref?: string | any[]): ChangeItemDescription {
    return ChangeDescriptionType.FULL === descriptionType
      ? {ref, content}
      : {content};
  }

  private getApplicationContent(descriptionType: ChangeDescriptionType, applicationId: string, name: string): string {
    return ChangeDescriptionType.FULL === descriptionType
      ? `${applicationId} - ${name}`
      : `${applicationId}`;
  }
}
