import {FieldChangeOperationType} from './field-change';

export interface EntityDescriptor {
  ref: string | any[];
  content;
}

export class EntityChange {
  constructor(
    public operationType?: FieldChangeOperationType,
    public oldEntity?: EntityDescriptor,
    public newEntity?: EntityDescriptor
  ) {}
}
