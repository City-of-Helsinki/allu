import {StringUtil} from '../../util/string.util';
import {NumberUtil} from '../../util/number.util';
import {ArrayUtil} from '../../util/array-util';

const DISTRICT_ID_CHANGE = ['cityDistrictId', '/cityDistricts'];
const CUSTOMER_CHANGE = '/customer/';
const CONTACT_CHANGE = '/contacts/';
const CUSTOMER_URL = '/customers/:id';

export enum FieldChangeType {
  CUSTOMER,
  CONTACT,
  DISTRICT_ID,
  OTHER
}

export enum FieldChangeOperationType {
  ADD,
  REMOVE,
  CHANGE
}

export class FieldChange {
  private readonly _id: number;
  private _fieldChangeType: FieldChangeType;
  private _fieldChangeOperationType: FieldChangeOperationType;
  private _link: string;

  constructor(
    public fieldName?: string,
    public oldValue?: string,
    public newValue?: string,
    public uiFieldName?: string
  ) {
    const parts = fieldName.split('/').filter(part => !StringUtil.isEmpty(part));
    this._id = this.getId(ArrayUtil.first(parts));
    this.fieldName = this.getFieldName(ArrayUtil.rest(parts));
    this.initFieldChangeType(fieldName);
    this.initFieldChangeOperationType(oldValue, newValue);
    this.initLink();
  }

  get id() {
    return this._id;
  }

  get fieldChangeType() {
    return this._fieldChangeType;
  }

  get uiFieldChangeType() {
    return FieldChangeType[this._fieldChangeType];
  }

  get fieldChangeOperationType() {
    return this._fieldChangeOperationType;
  }

  get uiFieldChangeOperationType() {
    return FieldChangeOperationType[this._fieldChangeOperationType];
  }

  get definedValue() {
    return !StringUtil.isEmpty(this.oldValue) ? this.oldValue : this.newValue;
  }

  get link() {
    return this._link;
  }

  private getId(idField: string): number {
    return NumberUtil.isNumeric(idField)
      ? +idField
      : undefined;
  }

  private getFieldName(parts: string[]): string {
    if (NumberUtil.isNumeric(this._id) && parts.length) {
      return '/' + parts.join('/');
    } else {
      return this.fieldName;
    }
  }

  private initFieldChangeType(fieldName: string) {
    if (DISTRICT_ID_CHANGE.some(partId => fieldName.indexOf(partId) >= 0)) {
      this._fieldChangeType = FieldChangeType.DISTRICT_ID;
    } else if (fieldName.indexOf(CUSTOMER_CHANGE) >= 0) {
      this._fieldChangeType = FieldChangeType.CUSTOMER;
    } else if (fieldName.indexOf(CONTACT_CHANGE) >= 0) {
      this._fieldChangeType = FieldChangeType.CONTACT;
    } else {
      this._fieldChangeType = FieldChangeType.OTHER;
    }
  }

  private initFieldChangeOperationType(oldVal: string, newVal: string) {
    if (StringUtil.isEmpty(oldVal) && !StringUtil.isEmpty(newVal)) {
      this._fieldChangeOperationType = FieldChangeOperationType.ADD;
    } else if (!StringUtil.isEmpty(oldVal) && StringUtil.isEmpty(newVal)) {
      this._fieldChangeOperationType = FieldChangeOperationType.REMOVE;
    } else {
      this._fieldChangeOperationType = FieldChangeOperationType.CHANGE;
    }
  }

  private initLink() {
    if ([FieldChangeType.CONTACT, FieldChangeType.CUSTOMER].indexOf(this._fieldChangeType) >= 0) {
      this._link = CUSTOMER_URL.replace(':id', String(this.id));
    }
  }
}
