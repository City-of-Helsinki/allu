import {FieldChange} from './field-change';
import {Dictionary, toDictionary} from '../../util/object.util';

export class EntityChange {
  constructor(
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- backend/frontend JSON payload (dynamically typed API contract)
    public oldEntity?: Dictionary<any>,
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- backend/frontend JSON payload (dynamically typed API contract)
    public newEntity?: Dictionary<any>
  ) {}
}

export function toEntityChange(fieldChanges: FieldChange[]): EntityChange {
  const dict = toDictionary(fieldChanges, item => item.fieldName);
  const oldEntity = {};
  const newEntity = {};
  Object.keys(dict).forEach(key => {
    oldEntity[key] = dict[key].oldValue;
    newEntity[key] = dict[key].newValue;
  });
  return new EntityChange(
    oldEntity,
    newEntity
  );
}
