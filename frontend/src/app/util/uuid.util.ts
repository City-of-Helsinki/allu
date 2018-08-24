import * as uuid from 'uuid/v4';

export type Uuid = string;

export class UuidUtil {
  static create(): Uuid {
    return uuid();
  }
}
